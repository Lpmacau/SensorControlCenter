package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import controlCenter.AgenteGUI;
import jade.gui.GuiEvent;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.JDesktopPane;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.CardLayout;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Panel;
import javax.swing.JComboBox;
import javax.swing.Timer;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class GUI {
	

	private static final int BUTAOOK = 1;
	private static final int BUTAOSAIR = -1;
	private static final int TEMPERATURAS = 2;

	private static JFrame frame;
	private AgenteGUI agGUI;
	private JTextField textField_1;
	private JTable tableHome;
	private JTable tableHistorico;
	private ArrayList<String> divisoes;
	private Timer rtemperaturas;
	private JFreeChart chart;
	private Panel panel_3;
	private DefaultCategoryDataset dataset;
	private JTextArea textErros;

	private String divisaoMaisQuente, divisaoMaisFria;
	private int totalTimeout,totalErrada,totalIncon,numDivisoes,totalTemp,mediaCasa,maisQuente, maisFria;
	
	private int iteracoes = 0;
	
	/**
	 * Launch the application.
	 */
	public static void main(AgenteGUI ag) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
			}
		} catch (UnsupportedLookAndFeelException e) {
		       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI(ag);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI(AgenteGUI ag) {
		this.agGUI = ag;
		this.divisoes = new ArrayList<String>();
		initialize();	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JPanel panelEntrada = new JPanel();
		frame.getContentPane().add(panelEntrada, "Entrada");
		panelEntrada.setLayout(null);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(177, 151, 86, 20);
		panelEntrada.add(textField_1);
		
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				butaoOK();
			}
		});
		button.setBounds(135, 201, 75, 37);
		panelEntrada.add(button);
		
		JButton button_1 = new JButton("Exit");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				butaoSair();
			}
		});
		button_1.setBounds(235, 201, 75, 37);
		panelEntrada.add(button_1);
		
		JLabel lblSensorcenas = new JLabel("SensorCenas");
		lblSensorcenas.setBounds(185, 48, 63, 14);
		panelEntrada.add(lblSensorcenas);
		
		JLabel lblNmeroDeDivises = new JLabel("Número de Divisões:");
		lblNmeroDeDivises.setBounds(168, 126, 107, 14);
		panelEntrada.add(lblNmeroDeDivises);
		
		JPanel panelPrincipal = new JPanel();
		frame.getContentPane().add(panelPrincipal, "Principal");
		panelPrincipal.setLayout(null);
		
		
		
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 434, 21);
		panelPrincipal.add(menuBar);
		
		JMenu mnSensorcenas = new JMenu("SensorCenas");
		menuBar.add(mnSensorcenas);
		
		JMenuItem mntmSair = new JMenuItem("Sair");
		mnSensorcenas.add(mntmSair);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 21, 434, 240);
		panelPrincipal.add(tabbedPane);
		
		JSplitPane splitPaneHome = new JSplitPane();
		tabbedPane.addTab("Home", null, splitPaneHome, null);
		
		JPanel panelEsquerda = new JPanel();
		splitPaneHome.setLeftComponent(panelEsquerda);
		
		tableHome = new JTable();
		tableHome.setModel(new DefaultTableModel(
			new Object[][] {
				{"M\u00E9dia da Casa", null},
				{"Divis\u00E3o mais quente", null},
				{"Divis\u00E3o mais fria", null},
				{"Leituras sem resposta", null},
				{"Leituras erradas", null},
				{"Leituras inconsistentes", null},
				{null, null},
			},
			new String[] {
				"New column", "New column"
			}
		));
		tableHome.getColumnModel().getColumn(0).setPreferredWidth(113);
		panelEsquerda.setLayout(new MigLayout("", "[188px,grow][6px]", "[93.00px][][grow]"));
		panelEsquerda.add(tableHome, "cell 0 0,aligny center");
		
		JLabel lblAvisos = new JLabel("Avisos:");
		panelEsquerda.add(lblAvisos, "cell 0 1");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelEsquerda.add(scrollPane, "cell 0 2,grow");
		
		textErros = new JTextArea();
		scrollPane.setViewportView(textErros);
		
		JPanel panel = new JPanel();
		splitPaneHome.setRightComponent(panel);
		panel.setLayout(null);
		
		panel_3 = new Panel();
		chart = ChartFactory.createLineChart("Temperaturas Casa", "Graus Celsius", "Segundos", null, PlotOrientation.VERTICAL, true, false, false);
		dataset = new DefaultCategoryDataset();
		rtemperaturas = new Timer(1000,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTemperaturas();
				
			}
		});
		
		panel_3.setBounds(10, 10, 190, 150);
		panel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JLabel label = new JLabel("Escolha a divis\u00E3o:");
		label.setBounds(10, 182, 91, 14);
		panel.add(label);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(95, 179, 105, 20);
		panel.add(comboBox_1);
		
		JSplitPane splitPaneHistorico = new JSplitPane();
		tabbedPane.addTab("Hist\u00F3rico", null, splitPaneHistorico, null);
		
		JPanel panel_1 = new JPanel();
		splitPaneHistorico.setLeftComponent(panel_1);
		
		tableHistorico = new JTable();
		tableHistorico.setModel(new DefaultTableModel(
			new Object[][] {
				{"M\u00E9dia da Casa", null},
				{"Divis\u00E3o mais quente", null},
				{"Divis\u00E3o mais fria", null},
				{"Leituras sem resposta", ""},
				{"Leituras erradas", null},
				{"Leituras inconcistentes", null},
				{null, null},
			},
			new String[] {
				"New column", "New column"
			}
		));
		tableHistorico.getColumnModel().getColumn(0).setPreferredWidth(147);
		panel_1.setLayout(new MigLayout("", "[222px]", "[222.00px]"));
		panel_1.add(tableHistorico, "cell 0 0,alignx left,aligny top");
		
		JPanel panel_2 = new JPanel();
		splitPaneHistorico.setRightComponent(panel_2);
		panel_2.setLayout(null);
		
		Panel panel_4 = new Panel();
		panel_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int s1 = 1;
				int s2 = 10;
				int s3 = 20;
				int s4 = 4;
				
				DefaultCategoryDataset dataset = new DefaultCategoryDataset();
				dataset.setValue(s1, "", "Sala");
				dataset.setValue(s2, "", "Cozinha");
				dataset.setValue(s3, "", "divisao3");
				dataset.setValue(s4, "", "divisao4");
				
				JFreeChart chart = ChartFactory.createBarChart("Temperaturas Casa", "", "", dataset, PlotOrientation.VERTICAL, false, false, false);
				CategoryPlot catPlot = chart.getCategoryPlot();
				catPlot.setRangeMinorGridlinePaint(Color.BLACK);
				
				ChartPanel chartPanel = new ChartPanel(chart);
				panel_4.removeAll();
				panel_4.add(chartPanel, BorderLayout.CENTER);
				panel_4.validate();
			}
		});
		panel_4.setBounds(10, 10, 166, 151);
		panel_2.add(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JLabel lblEscolhaADiviso = new JLabel("Escolha a divis\u00E3o:");
		lblEscolhaADiviso.setBounds(10, 185, 91, 14);
		panel_2.add(lblEscolhaADiviso);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(95, 182, 81, 20);
		panel_2.add(comboBox);
		
		//Mostrar menu principal
		//CardLayout c = (CardLayout) frame.getContentPane().getLayout();
		//c.show(frame.getContentPane(), "Principal");
		

		divisaoMaisQuente = divisaoMaisFria = "";
		maisFria = 1000;
		totalTemp = 0;
		maisQuente = -1000;
		totalTimeout = totalErrada = totalIncon = 0;
		numDivisoes=0;
	}
	

	protected void updateTemperaturas() {
		GuiEvent ge = new GuiEvent(this,TEMPERATURAS);
		agGUI.postGuiEvent(ge);
		
	}

	public void ola(){
		System.out.println("OLA");
	}
	// Sair do programa
	protected void butaoSair() {
		GuiEvent ge = new GuiEvent(this,BUTAOSAIR);
		agGUI.postGuiEvent(ge);
		
		frame.dispose();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		System.exit(0);
	}
	protected void butaoOK() {
		
		GuiEvent ge = new GuiEvent(this,BUTAOOK);
		int divi = Integer.parseInt(textField_1.getText());
		for(int i=1;i<=divi;i++)
		{
			String nome = JOptionPane.showInputDialog(null,"Insira o nome da divisão");
			divisoes.add(nome);
			ge.addParameter(nome);
		}
		agGUI.postGuiEvent(ge);
		//Mostrar menu principal
		CardLayout c = (CardLayout) frame.getContentPane().getLayout();
		c.show(frame.getContentPane(), "Principal");
		//Iniciar timer para as temperaturas
		rtemperaturas.start();
	}
	
	//refresh chart panel3
	public void chartTempAct(Map<String,List<Integer>> graficos){
		String sens;
		int temp;
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(Map.Entry<String,List<Integer>> l : graficos.entrySet()){
			sens = l.getKey();
			if(!l.getValue().isEmpty()){
				temp = l.getValue().get(l.getValue().size()-1);
				dataset.setValue(temp,"",sens);
			}
		}
		JFreeChart chart = ChartFactory.createBarChart("Temperaturas Casa", "", "Graus Celsius", dataset, PlotOrientation.VERTICAL, false, false, false);
		CategoryPlot catPlot = chart.getCategoryPlot();
		catPlot.setRangeMinorGridlinePaint(Color.BLACK);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		
		
		panel_3.removeAll();
		panel_3.add(chartPanel, BorderLayout.CENTER);
		panel_3.validate();
		
	}
	
	public void chartTempActLinhas(Map<String,List<Integer>> graficos){
		String sens;
		int temp;
		for(Map.Entry<String,List<Integer>> l : graficos.entrySet()){
			sens = l.getKey();
			if(!l.getValue().isEmpty()){
				for(int valor : l.getValue()){
					temp = valor;
					dataset.addValue(temp, sens, String.valueOf(iteracoes));
				}
			}
			else {
				temp = -1;
				dataset.addValue(temp, sens, String.valueOf(iteracoes));
			}
		}

		chart = ChartFactory.createLineChart("Temperaturas Casa", "Segundos", "Graus Celsius", dataset, PlotOrientation.VERTICAL, true, false, false);
		
		CategoryPlot catPlot = chart.getCategoryPlot();
		catPlot.setRangeMinorGridlinePaint(Color.BLACK);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		iteracoes+=3;
		
		panel_3.removeAll();
		panel_3.add(chartPanel, BorderLayout.CENTER);
		panel_3.validate();
		
	}

	public void ultimosErros(Map<String, String> errosSensores) {
		
		for(Map.Entry<String, String> e : errosSensores.entrySet()){
			textErros.setText(textErros.getText()+e.getKey()+" -> "+e.getValue()+"\n");
		}
	}
	
	public void showStats(Map<String,List<Integer>> graficos, Map<String, String> errosSensores){
		// Stats
		int it=0;
		numDivisoes += graficos.keySet().size();
		
		
		for(String erro : errosSensores.values()){
			if(erro=="timeout") totalTimeout++;
			if(erro=="XXXXX") totalErrada++;
			else totalIncon++;
		}
		
		for(Map.Entry<String,List<Integer>> l : graficos.entrySet()){
			String sens = l.getKey();
			if(!l.getValue().isEmpty() && l.getValue().size()>0){
				int valor = l.getValue().get(l.getValue().size()-1);
				if(valor<maisFria){
					maisFria = valor;
					divisaoMaisFria = sens;
				}
				if(valor>maisQuente){
					maisQuente = valor;
					divisaoMaisQuente = sens;
				}
				if(it==0){
					it = l.getValue().size();
				}
				totalTemp+=valor;
			}
		}
		
		if(it!=0 || totalTemp != 0){
			mediaCasa = totalTemp/numDivisoes;
		}
		
		

		tableHistorico.setModel(new DefaultTableModel(
			new Object[][] {
				{"M\u00E9dia da Casa", mediaCasa},
				{"Divis\u00E3o mais quente", divisaoMaisQuente + " -> "+maisQuente},
				{"Divis\u00E3o mais fria", divisaoMaisFria+ " -> "+maisFria},
				{"Leituras sem resposta", totalTimeout},
				{"Leituras erradas", totalErrada},
				{"Leituras inconcistentes", totalIncon},
			},
			new String[] {
				"New column", "New column"
			}
		));
		
	}

	/*private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}*/
}
