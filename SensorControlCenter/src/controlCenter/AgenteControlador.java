package controlCenter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import FIPA.DateTime;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
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

public class AgenteControlador extends Agent {

	private static final long serialVersionUID = 1L;
	private Map<String, List<String>> agentes;
	private Map<String, Integer> lastValues;
	private Map<String, String> lastErrors;
	private Map<String, List<SensorValue>> history;
	private Map<String, List<SensorError>> sensorErrors;

	private class SensorValue {
		private Date tempo;
		private int valor;

		public SensorValue(int valor) {
			this.tempo = Calendar.getInstance().getTime();
			this.valor = valor;
		}

		public Date getTempo() {
			return this.tempo;
		}

		public int getValor() {
			return this.valor;
		}
	}

	private class SensorError { 
		private Date tempo;
		private String tipo;

		public SensorError(String tipo) {
			this.tempo =  Calendar.getInstance().getTime();
			this.tipo = tipo;
		}

		public String getTipo() {
			return this.tipo;
		}

		public Date getTempo() {
			return this.tempo;
		}
	}

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
		sd.setType("controlador");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
			System.out.println("Agente[" + this.getLocalName() + "] a iniciar");
		} catch (FIPAException fe) {
			fe.printStackTrace();
			System.out.println("Agente[" + this.getLocalName() + "] inicializacao falhou");
		}

		// Inicializar estruturas
		this.agentes = new HashMap<String, List<String>>();
		this.history = new HashMap<String, List<SensorValue>>();
		this.lastErrors = new HashMap<String, String>();
		this.lastValues = new HashMap<String, Integer>();
		this.sensorErrors = new HashMap<String, List<SensorError>>();

		// Procurar todos os agentes na rede e adicionar comportamentos
		this.getAllAgents();
		this.addBehaviour(new ReceiveBehaviour());
		this.addBehaviour(new RequestSensorBehaviour(this, 3000));
	}
	

	// Comportamento de pedido de sensores
	private class RequestSensorBehaviour extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		public RequestSensorBehaviour(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			// Verificar se existe sensores detetados
			if (agentes.get("sensor") != null) {
				// Limpar valores anteriores
				lastErrors.clear();
				lastValues.clear();

				// Obter listas de pedidos e recetores
				List<ACLMessage> requests = new ArrayList<ACLMessage>();
				List<AID> receivers = new ArrayList<AID>();

				// Preparar pedido e preencher listas
				for (String agent : agentes.get("sensor")) {
					AID receiver = new AID();
					receiver.setLocalName(agent);

					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					long time = System.currentTimeMillis();

					request.setConversationId("" + time);
					request.addReceiver(receiver);
					request.setContent("value");

					requests.add(request);
					receivers.add(receiver);
				}

				// Realizar o pedido aos sensores
				for (int i = 0; i < requests.size(); i++) {
					ACLMessage request = requests.get(i);
					AID receiver = receivers.get(i);
					String agente = receiver.getLocalName();
					try {
						// Fazer pedido e esperar resposta
						ACLMessage answer = DFService.doFipaRequestClient(myAgent, request, 3000);

						// Caso o sensor tenha demorado mais de 3000 ms a
						// responder
						if (answer == null || answer.getContent().equals("XXXXX")) {

							// Determinar o erro
							if (answer == null)
								System.out.println(
										"Agente[" + getLocalName() + "] " + agente + " não enviou temperatura");
							else
								System.out.println(
										"Agente[" + getLocalName() + "] " + agente + " enviou temperatura inválida");

							// Caso já tenha uma lista de erros
							if (sensorErrors.containsKey(agente)) {
								if (answer == null)
									sensorErrors.get(agente).add(new SensorError("timeout"));
								else
									sensorErrors.get(agente).add(new SensorError("XXXXX"));
							}

							// Criar lista de erros
							else {
								ArrayList<SensorError> lista = new ArrayList<SensorError>();
								if (answer == null)
									lista.add(new SensorError("timeout"));
								else
									lista.add(new SensorError("XXXXX"));
								sensorErrors.put(agente, lista);
							}

							if(answer==null) lastErrors.put(agente, "timeout");
							else lastErrors.put(agente, "XXXXX");
						}

						// Resposta do sensor
						else {
							System.out.println("Agente[" + getLocalName() + "] " + answer.getContent());

							int valor = Integer.parseInt(answer.getContent());
							
							// Validar valor para ver se nao é improvavel
							int valido = validaValor(agente,valor);
							
							// Inferior
							if(valido == -1){
								lastErrors.put(agente, "Valor Improvável Inferior "+valor);
							}
							// Superior
							if(valido == 1){
								lastErrors.put(agente, "Valor Improvável Superior "+valor);
							}
							
							// Inserir no historico e nos ultimos valores
							if (history.containsKey(agente)) {
								history.get(agente).add(new SensorValue(valor));
							}

							else {
								ArrayList<SensorValue> lista = new ArrayList<SensorValue>();
								lista.add(new SensorValue(valor));
								history.put(agente, lista);
							}

							lastValues.put(agente, valor);
						}
					} catch (FIPAException e) {
						// TODO Auto-generated catch block
						System.out.println("ESCAXOU");
						e.printStackTrace();
					}
				}
				
				myAgent.addBehaviour(new OneShotBehaviour() {
					@Override
					public void action() {
						if(agentes.containsKey("gui")){
							String gui = agentes.get("gui").get(0);
							if(gui!=null){
								AID receiver = new AID();
								receiver.setLocalName(gui);

								ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
								long time = System.currentTimeMillis();

								inform.setConversationId("" + time);
								inform.addReceiver(receiver);
								inform.setContent("updateValores");
								
								for(Map.Entry<String,Integer> l : lastValues.entrySet()){
									inform.addUserDefinedParameter(l.getKey(), ""+l.getValue());
								}
								
								send(inform);
							}
							else System.out.println("NULISSIMO");
						}
					}
				});
				

				myAgent.addBehaviour(new OneShotBehaviour() {
					@Override
					public void action() {
						if(agentes.containsKey("gui")){
							String gui = agentes.get("gui").get(0);
							if(gui!=null){
								AID receiver = new AID();
								receiver.setLocalName(gui);

								ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
								long time = System.currentTimeMillis();

								inform.setConversationId("" + time);
								inform.addReceiver(receiver);
								inform.setContent("updateErros");
								

								for(Map.Entry<String,String> l : lastErrors.entrySet()){
									inform.addUserDefinedParameter(l.getKey(), l.getValue());
								}
								
								send(inform);
							}
							else System.out.println("NULISSIMO");
						}
					}
				});
				

				
				System.out.println("----------------------------------------");
				System.out.println("ERROS ATUAIS:");
				for (Map.Entry<String, List<SensorError>> entrada : sensorErrors.entrySet()) {
					String nome = entrada.getKey();
					for (SensorError s : entrada.getValue()) {
						Date data = s.getTempo();
						System.out.println(data.getHours()+":"+data.getMinutes()+":"+data.getSeconds()+" -> " + s.getTipo());
					}
				}

				System.out.println("----------------------------------------");

			} else
				System.out.println("Agente[" + getLocalName() + "] não há sensores disponíveis");
			block();
		}
	}

	// Comportamento de rececao de mensagens
	private class ReceiveBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			// Receber Mensagem
			ACLMessage msg = receive();
			if (msg != null) {
				// Criar resposta e obter conteudo da msg
				ACLMessage reply = msg.createReply();
				String text = msg.getContent();

				if (msg.getPerformative() == ACLMessage.REQUEST) {

					// Pedido de criação de agentes
					if (text.equals("criar agentes")) {

						String nome = msg.getUserDefinedParameter("nome");

						 //int nAgentes = Integer.parseInt(msg.getUserDefinedParameter("criar"));
						try {
							// Criar agentes e preparacao de resposta
							createSensorAgent(nome);
							reply.setPerformative(ACLMessage.CONFIRM);
							reply.addUserDefinedParameter("resposta",
									"Criação de Agente de senor:" + nome + " bem sucedida");
							reply.setContent("resposta");
						} catch (StaleProxyException e) {
							reply.setPerformative(ACLMessage.FAILURE);
							reply.addUserDefinedParameter("resposta", e.getMessage());
							reply.setContent("resposta");
						}
						myAgent.send(reply);
						
					}

					// Desligar sensores
					if (text.equals("shutdown")) {
						shutdownSensors();
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("resposta");
						myAgent.send(reply);
						myAgent.doDelete();
					}
				}

				else if (msg.getPerformative() == ACLMessage.INFORM) {
					System.out.println(text);
				}
			}
			block();
		}
	}

	// Procura de agentes (gui e sensor) na rede
	private void getAllAgents() {

		Map<String, List<String>> agentes = new HashMap<String, List<String>>();

		// Procurar por agentes
		try {
			// Busca de todos os agentes
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();

			// Procurar pela gui
			sd.setType("gui");
			dfd.addServices(sd);
			DFAgentDescription[] result = DFService.search(this, dfd);

			// Insercao na estrutura
			if (result.length > 0) {
				System.out.println(
						"Agente[" + this.getLocalName() + "] Encontrei " + result.length + " agentes do tipo gui");
				for (int i = 0; i < result.length; i++) {
					System.out.println("Agente[" + this.getLocalName() + "] " + result[i].getName().getLocalName());
					if (agentes.containsKey("gui")) {
						agentes.get("gui").add(result[i].getName().getLocalName());
					} else {
						List<String> listaAgentes = new ArrayList<String>();
						listaAgentes.add(result[i].getName().getLocalName());
						agentes.put("gui", listaAgentes);
					}
				}

			} else {
				System.out.println("Agente[" + this.getLocalName() + "] Não encontrei qualquer agente do tipo gui");
			}

			// Procurar pelas sensores
			sd.setType("sensor");
			result = DFService.search(this, dfd);

			// Insercao na estrutura
			if (result.length > 0) {
				System.out.println(
						"Agente[" + this.getLocalName() + "] Encontrei " + result.length + " agentes do tipo sensor");
				for (int i = 0; i < result.length; i++) {
					System.out.println("Agente[" + this.getLocalName() + "] " + result[i].getName().getLocalName());
					if (agentes.containsKey("sensor")) {
						agentes.get("sensor").add(result[i].getName().getLocalName());
					} else {
						List<String> listaAgentes = new ArrayList<String>();
						listaAgentes.add(result[i].getName().getLocalName());
						agentes.put("sensor", listaAgentes);
					}
				}

			} else {
				System.out.println("Agente[" + this.getLocalName() + "] Não encontrei qualquer agente do tipo sensor");
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.agentes = agentes;
	}

	public int validaValor(String agente, int valor) {
		if(history.containsKey(agente)){
			int tamanho = history.get(agente).size();
			if(tamanho>3){
				int valor1,valor2;
				valor1 = history.get(agente).get(tamanho-1).valor;
				valor2 = history.get(agente).get(tamanho-2).valor;
				
				// Valor improvável inferior
				if((valor>-20 && valor<-6) && (valor1>-20 && valor1<-6) && (valor2>-20 && valor2<-6)){
					return -1;
				}
				
				// Valor imporovavel superior
				if((valor>40 && valor<50) && (valor1>40 && valor1<50) && (valor2>40 && valor2<50)){
					return 1;
				}
			}
		}
		return 0;
	}

	public void createSensorAgent(String nome) throws StaleProxyException {
		ContainerController cc = getContainerController();

		AgentController ac = cc.createNewAgent(nome, AgenteSensor.class.getName(), null);
		ac.start();

		this.getAllAgents();
	}

	// Terminar os sensores
	private void shutdownSensors() {
		// Atualizar agentes
		this.getAllAgents();

		for (String s : this.agentes.get("sensor")) {
			AID receiver = new AID();
			receiver.setLocalName(s);

			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			long time = System.currentTimeMillis();
			String pedido = "shutdown";

			request.setConversationId("" + time);
			request.addReceiver(receiver);
			request.setContent(pedido);
			send(request);
		}
	}

	// Criacao de n agentes do tipo sensor
	private void createSensorAgents(int n) throws StaleProxyException {
		ContainerController cc = getContainerController();
		for (int i = 0; i < n; i++) {
			AgentController ac = cc.createNewAgent("agSensor" + i, AgenteSensor.class.getName(), null);
			ac.start();
		}
		this.getAllAgents();
	}
}