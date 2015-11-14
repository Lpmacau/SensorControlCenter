package controlCenter;

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
import ui.GUI;

public class AgenteGUI extends GuiAgent{

	private static final long serialVersionUID = 1L;
	
	private static final int BUTAOOK = 1;
	
	// GUI
	transient protected GUI ui;
	
	@Override
	protected void takeDown() {
		// Código genérico para todos os agentes (tirar do registo determinado agente)
		super.takeDown();
		try { 
			 DFService.deregister(this); 
			 System.out.println("Agente["+this.getLocalName()+"] removido do registo de servicos");
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
		GUI.main(this);
		
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
		
		
	}

	protected void onGuiEvent(GuiEvent ev){
		// Process the event according to it's type
		int command = ev.getType();
		if (command == BUTAOOK) {
			String acc = (String) ev.getParameter(0);
			int amount = ((int)ev.getParameter(1));
			System.out.println("Agente["+this.getLocalName()+"] "+acc + " " + amount);
		}
		else System.out.println("Agente["+this.getLocalName()+"] COMANDO ERRADO");
		
	}

}
