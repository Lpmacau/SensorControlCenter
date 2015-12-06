package controlCenter;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteSensorTemperatura extends Agent {
	private static final long serialVersionUID = 1L;
	private boolean sensorState = false;
	private boolean finished = false;

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
		sd.setType("sensorTemperatura");
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

							// System.out.println("Agente["+getLocalName()+"]
							// recebi pedido de temperatura");

							if (randomNum < 5) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							// Valor invalido
							else if (randomNum < 10 && randomNum >= 5) {
								reply.setContent("XXXXX");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							
							// 5-30
							else if (randomNum >= 10 && randomNum < 70) {
								randomNum = 5 + (int) (Math.random() * ((30-5)+1));
								reply.setContent(randomNum + "");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							}
							
							// -5-5
							else if (randomNum >= 70 && randomNum < 80) {
								randomNum = -5 + (int) (Math.random() * ((10+5)+1));
								reply.setContent(randomNum + "");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							
							// 30-40
							else if (randomNum >= 80 && randomNum < 90) {
								randomNum = 30 + (int) (Math.random() * ((40-30)+1));
								reply.setContent(randomNum + "");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							}
							
							// -20--5
							else if (randomNum >= 90 && randomNum < 95) {
								randomNum = 5 + (int) (Math.random() * ((20-5)+1));
								randomNum *= -1;
								reply.setContent(randomNum + "");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							
							// 40-50
							else if (randomNum >= 95 && randomNum < 100) {
								randomNum = 40 + (int) (Math.random() * ((50-40)+1));
								reply.setContent(randomNum + "");
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
