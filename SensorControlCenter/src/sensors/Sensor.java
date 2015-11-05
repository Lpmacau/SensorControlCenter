package sensors;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Sensor extends Agent {
	private static final long serialVersionUID = 1L;
	private boolean sensorState = false;
	private boolean finished = false;

	@Override
	protected void takeDown() {
		// Eliminar sensor
		super.takeDown();
		
		// Retirar o sensor do Directory Facilitator
		try {
			DFService.deregister(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("A remover registo de serviços...");
	}

	@Override
	protected void setup() {
		// Criar o agente
		super.setup();
		
		// Criar descrição e tipo de serviço do agente
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("sensor");
		
		// Adicionar serviço ao Directory Facilitator
		dfd.addServices(sd);

		try {
			// Registar o agente 
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		System.out.println("Agente " + this.getLocalName() + " a iniciar...");
		
		// Adicionar comportamento de receção
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

	
	// Comportamento de receção de mensagens
	private class ReceiveBehaviour extends CyclicBehaviour {  
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			// Receber mensagem
			ACLMessage msg = receive();
			if (msg != null) {
				// Criar mensagem de resposta
				ACLMessage reply = msg.createReply();
				
				// Tratamento da mensagem
				if (msg.getPerformative() == ACLMessage.REQUEST) {
					
					// Pedido de término do agente
					if (msg.getContent().equals("shutdown")) {
						System.out.println("Sensor " + myAgent.getLocalName() + " a terminar...");
						setFinished(true);
					}
					
					// Ativar agente
					if (msg.getContent().equals("online")) {
						// Caso ja esteja ativado, falha
						if (isSensorState()) {
							reply.setPerformative(ACLMessage.FAILURE);
							myAgent.send(reply);
						} 
						
						// Caso contrário, ativa-o e confirma ativação
						else {
							System.out.println("Sensor " + myAgent.getLocalName() + " estÃ¡ agora online.");
							reply.setPerformative(ACLMessage.CONFIRM);
							myAgent.send(reply);
							setSensorState(true);
						}
					}
					
					// Desligar agente
					if (msg.getContent().equals("offline")) {
						
						// Caso esteja online, desliga-o
						if (isSensorState()) {
							System.out.println("Sensor " + myAgent.getLocalName() + " estÃ¡ agora offline.");
							reply.setPerformative(ACLMessage.CONFIRM);
							myAgent.send(reply);
							setSensorState(false);
						} 
						
						// Falha no caso de já estar offline
						else {
							reply.setPerformative(ACLMessage.FAILURE);
							myAgent.send(reply);
						}

					}
					
					// Pedir valor de temperatura
					if (msg.getContent().equals("value")) {
						
						// Verificar se o sensor está ativo
						if (isSensorState()) {
							// Geração de numero aleatorio
							int randomNum = Math.abs(new Random().nextInt() % 100);
							
							// Valor irrealista
							if (randomNum < 10) {
								reply.setContent("XXXXX");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							else if (randomNum >= 10 && randomNum < 20) {
								reply.setContent(-randomNum + "");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							} 
							else if (randomNum >= 20 && randomNum < 90) {
								reply.setContent(randomNum + "");
								reply.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply);
							}
						} 
						
						// Falha, sensor offline
						else {
							reply.setPerformative(ACLMessage.FAILURE);
							myAgent.send(reply);
						}
					}
				} 
				
				// Mensagem de erro caso a prerrogativa seja diferente de REQUEST
				else {
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					myAgent.send(reply);
				}
			}
			
			// Caso esteja finalizado, apagar
			if (isFinished())
				myAgent.doDelete();
			block();
		}
	}
}
