package controlCenter;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteSensorMovimento extends Agent {
	private static final long serialVersionUID = 1L;
	private boolean sensorState = false;
	private boolean finished = false;

	@Override
	protected void takeDown() {
		// C�digo gen�rico para todos os agentes (tirar do registo determinado
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
		sd.setType("sensorMovimento");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
			System.out.println("Agente[" + this.getLocalName() + "] a iniciar");
			this.setSensorState(true);
		} catch (FIPAException fe) {
			fe.printStackTrace();
			System.out.println("Agente[" + this.getLocalName() + "] inicializacao falhou");
		}

		this.addBehaviour(new ReceiveBehaviour());

	}

	public boolean isSensorState() {
		return sensorState;
	}

	public void setSensorState(boolean sensorState) {
		this.sensorState = sensorState;
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
						if (isSensorState()) {
							reply.setPerformative(ACLMessage.FAILURE);
							myAgent.send(reply);
						} else {
							System.out.println("Agente[" + myAgent.getLocalName() + "] esta agora online");
							reply.setPerformative(ACLMessage.CONFIRM);
							myAgent.send(reply);
							setSensorState(true);
						}
					}

					if (msg.getContent().equals("offline")) {
						if (isSensorState()) {
							System.out.println("Agente[" + myAgent.getLocalName() + "] esta agora offline");
							reply.setPerformative(ACLMessage.CONFIRM);
							myAgent.send(reply);
							setSensorState(false);
						} else {
							reply.setPerformative(ACLMessage.FAILURE);
							myAgent.send(reply);
						}

					}

					if (msg.getContent().equals("value")) {
						if (isSensorState()) {
							int randomNum = new Random().nextInt(100);
							
							if (randomNum < 10) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							// Valor invalido
							else if (randomNum < 20 && randomNum >= 10) {
								reply.setContent("XXXXX");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							
							// H� movimento
							else if (randomNum < 40 && randomNum >= 20) {
								reply.setContent("1");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							
							// N�o h� movimento
							else if (randomNum < 100 && randomNum >= 40) {
								reply.setContent("0");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							
							
						} else {
							reply.setPerformative(ACLMessage.FAILURE);
							myAgent.send(reply);
						}
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
