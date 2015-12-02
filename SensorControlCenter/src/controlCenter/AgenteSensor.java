package controlCenter;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteSensor extends Agent 
{
	private static final long serialVersionUID = 1L;
	private boolean sensorState = false;
	private boolean finished = false;
	
	@Override
	protected void takeDown() {
		// Código genérico para todos os agentes (tirar do registo determinado agente)
		super.takeDown();
		try { 
			 DFService.deregister(this); 
			 System.out.println("Agente["+this.getLocalName()+"] a terminar");
		 }
         catch (Exception e) {
        	 e.printStackTrace();
        	 System.out.println("Agente["+this.getLocalName()+"] falha na remocao");
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
		sd.setType("sensor");
		dfd.addServices(sd);
		
		try{ 
			DFService.register(this, dfd );
			System.out.println("Agente["+this.getLocalName()+"] a iniciar");
			this.setSensorState(true);
		}
        catch (FIPAException fe) { 
        	fe.printStackTrace(); 
        	System.out.println("Agente["+this.getLocalName()+"] inicializacao falhou");
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

	private class ReceiveBehaviour extends CyclicBehaviour
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void action() 
		{
			ACLMessage msg = receive();

            if (msg != null) 
            {            	
            	ACLMessage reply = msg.createReply();

           	 	System.out.println("Agente["+getLocalName()+"] Recebi mensagem "+msg.getContent());
            	
            	if (msg.getPerformative() == ACLMessage.REQUEST)
            	{
            		if (msg.getContent().equals("shutdown"))
            		{
            			System.out.println("Agente["+myAgent.getLocalName()+"] a terminar");
            			setFinished(true);
            		}
            		
            		if (msg.getContent().equals("online"))
            		{
            			if (isSensorState())
            			{
            				reply.setPerformative(ACLMessage.FAILURE);
            				myAgent.send(reply);
            			}
            			else
            			{
                			System.out.println("Agente["+myAgent.getLocalName()+"] esta agora online");
            				reply.setPerformative(ACLMessage.CONFIRM);
            				myAgent.send(reply);
            				setSensorState(true);
            			}
            		}
            		
            		if (msg.getContent().equals("offline"))
            		{
            			if (isSensorState())
            			{
                			System.out.println("Agente["+myAgent.getLocalName()+"] esta agora offline");
            				reply.setPerformative(ACLMessage.CONFIRM);
            				myAgent.send(reply);
            				setSensorState(false);
            			}
            			else
            			{
            				reply.setPerformative(ACLMessage.FAILURE);
            				myAgent.send(reply);
            			}
            			
            		}
            		
            		if (msg.getContent().equals("value"))
            		{
            			if (isSensorState())
            			{
            				int randomNum = Math.abs(new Random().nextInt() % 100);

            	        	 System.out.println("Agente["+getLocalName()+"] recebi pedido de temperatura");
            				if (randomNum < 5)
            				{
            					reply.setContent("XXXXX");
            					reply.setPerformative(ACLMessage.INFORM);
            					myAgent.send(reply);
            				}
            				else if (randomNum >= 10 && randomNum < 20)
            				{
            					reply.setContent(-randomNum+"");
            					reply.setPerformative(ACLMessage.INFORM);
            					myAgent.send(reply);
            				}
            				else if (randomNum >= 20 && randomNum < 90)
            				{
            					reply.setContent(randomNum+"");
            					reply.setPerformative(ACLMessage.INFORM);
            					myAgent.send(reply);
            				}
            			}
            			else
            			{
            				reply.setPerformative(ACLMessage.FAILURE);
            				myAgent.send(reply);
            			}
            		}
            	}
            	else
            	{
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

