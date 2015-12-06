package controlCenter;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteAC extends Agent {
	private static final long serialVersionUID = 1L;
	private boolean finished = false;
	private boolean onOff = false;
	private int state;
	

	private static final int QUENTE = 1;
	private static final int FRIO = 0;

	@Override
	protected void takeDown() {
		// Código genérico para todos os agentes (tirar do registo determinado
		// agente)
		super.takeDown();
		try {
			DFService.deregister(this);
			System.out.println("Agente[" + this.getLocalName() + "] a terminar");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Agente[" + this.getLocalName() + "] falha na remocao");
		}

	}

	@Override
	protected void setup() {
		// Codigo de inicializacao do agente
		super.setup();

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("AC");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
			System.out.println("Agente[" + this.getLocalName() + "] a iniciar");
		} catch (FIPAException fe) {
			fe.printStackTrace();
			System.out.println("Agente[" + this.getLocalName() + "] inicializacao falhou");
		}

		this.addBehaviour(new ReceiveBehaviour());

	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	private class ReceiveBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = receive();

			if (msg != null) {
				ACLMessage reply = msg.createReply();

				// System.out.println("Agente["+getLocalName()+"] Recebi
				// mensagem "+msg.getContent());

				if (msg.getPerformative() == ACLMessage.REQUEST) {
					if (msg.getContent().equals("shutdown")) {
						System.out.println("Agente[" + myAgent.getLocalName() + "] a terminar");
						setFinished(true);
					}

					if (msg.getContent().equals("online")) {
						System.out.println("Agente[" + myAgent.getLocalName() + "] esta agora online");
						reply.setPerformative(ACLMessage.CONFIRM);
						myAgent.send(reply);
					}

					if (msg.getContent().equals("offline")) {
						if(onOff==true) onOff=false;
						reply.setPerformative(ACLMessage.CONFIRM);
						myAgent.send(reply);
					}
					
					if (msg.getContent().equals("quente")) {
						if(onOff==false)onOff=true;
						state = QUENTE;
						reply.setPerformative(ACLMessage.CONFIRM);
						myAgent.send(reply);
					}
					
					if (msg.getContent().equals("frio")) {
						if(onOff==false)onOff=true;
						state = FRIO;
						reply.setPerformative(ACLMessage.CONFIRM);
						myAgent.send(reply);
					}

					else {
						reply.setPerformative(ACLMessage.FAILURE);
						myAgent.send(reply);
					}
				} else {
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					myAgent.send(reply);
				}
			}

			if (isFinished())
				myAgent.doDelete();
			block();
		}
	}
}
