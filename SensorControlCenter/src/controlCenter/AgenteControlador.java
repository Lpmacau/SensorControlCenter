package controlCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class AgenteControlador extends Agent{

	private static final long serialVersionUID = 1L;
	private Map<String,List<String>> agentes;
	
	
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
		sd.setType("controlador");
		dfd.addServices(sd);
		
		try{ 
			DFService.register(this, dfd );
			System.out.println("Agente["+this.getLocalName()+"] a iniciar");
		}
        catch (FIPAException fe) { 
        	fe.printStackTrace(); 
        	System.out.println("Agente["+this.getLocalName()+"] inicializacao falhou");
        }
		
		
		// Procurar todos os agentes na rede e adicionar comportamentos
		this.getAllAgents();
		this.addBehaviour(new ReceiveBehaviour());
	}

	// Comportamento de rececao de mensagens
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
            	String text = msg.getContent();
            	
            	if (msg.getPerformative() == ACLMessage.REQUEST)
            	{
            		
            		if(text.equals("criar agentes")){
                    	int nAgentes = Integer.parseInt(msg.getUserDefinedParameter("criar"));
            			try {
            				// Criar agentes e preparacao de resposta
							createSensorAgents(nAgentes);
							reply.setPerformative(ACLMessage.INFORM);
							reply.addUserDefinedParameter("resposta","Criação de "+nAgentes+" agentes de sensor bem sucedida");
							reply.setContent("resposta");
						} catch (StaleProxyException e) {
							reply.setPerformative(ACLMessage.INFORM);
							reply.addUserDefinedParameter("resposta",e.getMessage());
							reply.setContent("resposta");
						}
            			myAgent.send(reply);
            		}
            	}
            }
            block();
		}
	}
	
	// Procura de agentes (gui e sensor) na rede
	private void getAllAgents(){
		
		Map<String,List<String>> agentes = new HashMap<String,List<String>>();
		
		// Procurar por agentes
		try {
			// Busca de todos os agentes
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd  = new ServiceDescription();
			
			
			// Procurar pela gui
			sd.setType( "gui" );
			dfd.addServices(sd);
			DFAgentDescription[] result = DFService.search(this, dfd);

			// Insercao na estrutura
        	if(result.length > 0){
        		System.out.println("Agente["+this.getLocalName()+"] Encontrei "+result.length+" agentes do tipo gui");
        		for(int i=0;i<result.length;i++){
        			System.out.println("Agente["+this.getLocalName()+"] "+ result[i].getName().getLocalName());	
        			if(agentes.containsKey("gui")){
        				agentes.get("gui").add(result[i].getName().getLocalName());
        			}
        			else{
        				List<String> listaAgentes = new ArrayList<String>();
        				listaAgentes.add(result[i].getName().getLocalName());
        				agentes.put("gui",listaAgentes);
        			}
            	}
        		
        	}
        	else{
        		System.out.println("Agente["+this.getLocalName()+"] Não encontrei qualquer agente do tipo gui");
        	}
        	
        	// Procurar pelas sensores
			sd.setType( "sensor" );
			result = DFService.search(this, dfd);
	
			// Insercao na estrutura
			if(result.length > 0){
        		System.out.println("Agente["+this.getLocalName()+"] Encontrei "+result.length+" agentes do tipo sensor");
        		for(int i=0;i<result.length;i++){
        			System.out.println("Agente["+this.getLocalName()+"] "+ result[i].getName().getLocalName());	
        			if(agentes.containsKey("sensor")){
        				agentes.get("sensor").add(result[i].getName().getLocalName());
        			}
        			else{
        				List<String> listaAgentes = new ArrayList<String>();
        				listaAgentes.add(result[i].getName().getLocalName());
        				agentes.put("sensor",listaAgentes);
        			}
            	}
        		
        	}
	    	else{
	    		System.out.println("Agente["+this.getLocalName()+"] Não encontrei qualquer agente do tipo sensor");
	    	}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		
		this.agentes = agentes;
	}

	// Criacao de n agentes do tipo sensor
	protected void createSensorAgents(int n) throws StaleProxyException{
		ContainerController cc = getContainerController();
		for(int i = 0; i<n ; i++){
			AgentController ac = cc.createNewAgent("agSensor"+i,AgenteSensor.class.getName(),null);
			ac.start();
		}
		
	}
	
	

}
