package controlCenter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import ui.GUI;
import ui.GUI2;

public class AgenteGUI extends GuiAgent{

	private static final long serialVersionUID = 1L;

	private static final int BUTAOOK = 1;
	private static final int BUTAOSAIR = -1;
	
	// GUI
	transient protected GUI2 ui;
	
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
		
		// Criacao do GUI
		GUI2.main(this);
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("gui");
		dfd.addServices(sd);
		
		try{ 
			DFService.register(this, dfd );
			System.out.println("Agente["+this.getLocalName()+"] a iniciar");
		}
        catch (FIPAException fe) { 
        	fe.printStackTrace(); 
        	System.out.println("Agente["+this.getLocalName()+"] inicializacao falhou");
        }
		
		
		// Adicionar comportamentos
		this.addBehaviour(new ReceiveBehaviour());
		
	}
	
	
	// Rececao de mensagens do agente controlador
	private class ReceiveBehaviour extends CyclicBehaviour
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void action() 
		{
			ACLMessage msg = receive();
            if (msg != null) 
            {      
            	String text = msg.getContent();
       
            	if (msg.getPerformative() == ACLMessage.CONFIRM)
            	{
            		if(text.equals("resposta")){
                    	String resposta = msg.getUserDefinedParameter("resposta");
                   	 	System.out.println("Agente["+myAgent.getLocalName()+"] "+resposta);
            		}
            	}
            }
            block();
		}
	}
	
	// Processamento de eventos da GUI
	protected void onGuiEvent(GuiEvent ev){
		// Obter tipo de Comando
		int command = ev.getType();
		
		// Butao OK
		if(command == BUTAOOK) {
			//System.out.println("Agente["+this.getLocalName()+"] "+acc + " " + amount);
			
			// Enviar para o controlador um pedido de inicialização dos sensores
			AID receiver = new AID();
			receiver.setLocalName("agControlador");
			
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			long time = System.currentTimeMillis();
			
			request.setConversationId(""+time);
			request.addReceiver(receiver);
			request.addUserDefinedParameter("criar", "5");
			request.setContent("criar agentes");
			send(request);

		}
		
		// Terminar todos os agentes e suicidar-se
		else if(command == BUTAOSAIR){
			AID receiver = new AID();
			receiver.setLocalName("agControlador");
			
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			long time = System.currentTimeMillis();
			
			request.setConversationId(""+time);
			request.addReceiver(receiver);
			request.setContent("shutdown");
			send(request);
			doDelete();
		}
		
		else System.out.println("Agente["+this.getLocalName()+"] COMANDO ERRADO");
		
	}

}
