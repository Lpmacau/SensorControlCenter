package controlCenter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

public class AgenteGUI extends GuiAgent {

	private static final long serialVersionUID = 1L;

	private static final int BUTAOOK = 1;
	private static final int BUTAOSAIR = -1;
	private static final int TEMPERATURAS = 2;

	private List<String> sensores;
	private Map<String,List<Integer>> graficos;
	// GUI
	transient protected GUI ui;

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
		this.sensores = new ArrayList<String>();
		this.graficos = new HashMap<String,List<Integer>>();
		
		
		// Criacao do GUI
		GUI.main(this);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("gui");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
			System.out.println("Agente[" + this.getLocalName() + "] a iniciar");
		} catch (FIPAException fe) {
			fe.printStackTrace();
			System.out.println("Agente[" + this.getLocalName() + "] inicializacao falhou");
		}

		// Adicionar comportamentos
		this.addBehaviour(new ReceiveBehaviour());

	}

	// Rececao de mensagens do agente controlador
	private class ReceiveBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				String text = msg.getContent();

				if (msg.getPerformative() == ACLMessage.CONFIRM) {
					if (text.equals("resposta")) {
						String resposta = msg.getUserDefinedParameter("resposta");
						System.out.println("Agente[" + myAgent.getLocalName() + "] " + resposta);
					}
				}
				
				else if(msg.getPerformative() == ACLMessage.INFORM){
					if (text.equals("update")){
						System.out.println("Agente[" + myAgent.getLocalName() + "]  recebi update dos valores");
						Properties nomes = msg.getAllUserDefinedParameters();
						
						for(String a : sensores){
							if(nomes.get(a)!=null){
								int valor = Integer.parseInt((String)nomes.get(a));
								if(graficos.get(a)!=null){
									graficos.get(a).add(valor);
								}
								System.out.println("Agente[" + myAgent.getLocalName() + "] "+a+" -> "+valor);
							}
							else{
								System.out.println("Agente[" + myAgent.getLocalName() + "] "+a+" -> erro");
							}
						}
					}
				}
			}
			block();
		}
	}

	// Processamento de eventos da GUI
	protected void onGuiEvent(GuiEvent ev) {
		// Obter tipo de Comando
		int command = ev.getType();
		// Butao OK
		if (command == BUTAOOK) {
			// System.out.println("Agente["+this.getLocalName()+"] "+acc + " " +
			// amount);
			Iterator it = ev.getAllParameter();
			while (it.hasNext()) {
				String nome = (String) it.next();
				this.sensores.add(nome);
				this.graficos.put(nome,new ArrayList<Integer>());
				System.out.println(nome);
				// Enviar para o controlador um pedido de inicialização dos
				// sensores
				AID receiver = new AID();
				receiver.setLocalName("agControlador");

				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				long time = System.currentTimeMillis();

				request.setConversationId("" + time);
				request.addReceiver(receiver);
				request.addUserDefinedParameter("nome", nome);
				request.setContent("criar agentes");
				send(request);
			}
		}

		// Terminar todos os agentes e suicidar-se
		else if (command == BUTAOSAIR) {
			AID receiver = new AID();
			receiver.setLocalName("agControlador");

			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			long time = System.currentTimeMillis();

			request.setConversationId("" + time);
			request.addReceiver(receiver);
			request.setContent("shutdown");
			
			for(Map.Entry<String,List<Integer>> l : graficos.entrySet()){
				System.out.print(l.getKey()+": ");
				for(int i : l.getValue()){
					System.out.print(i+",");
				}
				System.out.println("");
			}
			send(request);
			doDelete();
		}
		
		//Pedido de temperaturas para chart actual
		else if (command == TEMPERATURAS) {
			System.out.println("Olha um pedido Fresquinho!!");
			GUI g = (GUI) ev.getSource();
			System.out.println("Olha um pedido Fresquinho2!!");
			g.chartTempAct(graficos);
		}
		
		
		
		/* COmo passar cenas para a GUI
		 * Obter objeto e invocar metodos do GUI 
		 

		GUI g = (GUI2) ev.getSource();
		g.ola();*/
		
		
		
		
		
		
		

		else
			System.out.println("Agente[" + this.getLocalName() + "] COMANDO ERRADO");

	}

}