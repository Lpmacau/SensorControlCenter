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
import java.awt.CardLayout;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class GUI2 {

	private static final int BUTAOOK = 1;
	private static final int BUTAOSAIR = -1;
	private JFrame frame;
	private AgenteGUI agGUI;
	private JTextField textField;
	private JTextField textField_1;
	private JTable tableInformacoes;
	private JTable tableHistorico;

	/**
	 * Launch the application.
	 */
	public static void main(AgenteGUI ag) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI2 window = new GUI2(ag);
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
	public GUI2(AgenteGUI ag) {
		this.agGUI = ag;
		initialize();	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JPanel panelEntrada = new JPanel();
		frame.getContentPane().add(panelEntrada, "name_74011905447920");
		panelEntrada.setLayout(null);
		
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setBounds(-5032, -5000, 103, 46);
		panelEntrada.add(popupMenu);
		
		JTextArea textArea = new JTextArea();
		textArea.setText("SensorCenas");
		textArea.setBounds(173, 44, 92, 22);
		panelEntrada.add(textArea);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setText("N\u00FAmero de Divis\u00F5es:");
		textArea_1.setBounds(142, 124, 156, 22);
		panelEntrada.add(textArea_1);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(177, 151, 86, 20);
		panelEntrada.add(textField_1);
		
		JButton button = new JButton("OK");
		button.setBounds(135, 201, 75, 37);
		panelEntrada.add(button);
		
		JButton button_1 = new JButton("Exit");
		button_1.setBounds(235, 201, 75, 37);
		panelEntrada.add(button_1);
		
		JPanel panelPrincipal = new JPanel();
		frame.getContentPane().add(panelPrincipal, "name_74013502066179");
		panelPrincipal.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 434, 21);
		panelPrincipal.add(menuBar);
		
		JMenu mnSensorcenas = new JMenu("SensorCenas");
		menuBar.add(mnSensorcenas);
		
		JMenuItem mntmSair = new JMenuItem("Sair");
		mnSensorcenas.add(mntmSair);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setBounds(0, 21, 434, 240);
		panelPrincipal.add(splitPane_1);
		
		JPanel panelEsquerda = new JPanel();
		splitPane_1.setLeftComponent(panelEsquerda);
		
		tableInformacoes = new JTable();
		tableInformacoes.setModel(new DefaultTableModel(
			new Object[][] {
				{"M\u00E9dia Casa", null},
				{"Divis\u00E3o mais quente", null},
				{"Divis\u00E3o mais fria", null},
				{"Total Falhas", null},
				{"Leituras errada", null},
				{"Leitura inconsistente", null},
			},
			new String[] {
				"New column", "New column"
			}
		));
		tableInformacoes.getColumnModel().getColumn(0).setPreferredWidth(103);
		panelEsquerda.add(tableInformacoes);
		
		JPanel panel = new JPanel();
		splitPane_1.setRightComponent(panel);
		panel.setLayout(null);
		
		JButton btnHistorico = new JButton("Hist\u00F3rico");
		btnHistorico.setBounds(128, 211, 89, 23);
		panel.add(btnHistorico);
		
		JPanel panelPrincipalHistorico = new JPanel();
		frame.getContentPane().add(panelPrincipalHistorico, "name_77564883011518");
		panelPrincipalHistorico.setLayout(null);
		
		tableHistorico = new JTable();
		tableHistorico.setBounds(108, 76, 228, 112);
		panelPrincipalHistorico.add(tableHistorico);
		tableHistorico.setModel(new DefaultTableModel(
			new Object[][] {
				{"M\u00E9dia da casa", null},
				{"Temperatura mais alta", null},
				{"Temperatutra mais baixa", null},
				{"Total de falhas", null},
				{"Total de leituras erradas", null},
				{"Total de leituras inconsistentes", null},
				{"Total de avisos", null},
			},
			new String[] {
				"New column", "New column"
			}
		));
		
		JDesktopPane desktopPaneEntradaPopUp = new JDesktopPane();
		frame.getContentPane().add(desktopPaneEntradaPopUp, "name_77764873901482");
		desktopPaneEntradaPopUp.setLayout(null);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(109, 115, 97, 20);
		desktopPaneEntradaPopUp.add(textPane);
		textPane.setText("Nome da x divis\u00E3o:");
		
		textField = new JTextField();
		textField.setBounds(218, 115, 97, 20);
		desktopPaneEntradaPopUp.add(textField);
		textField.setColumns(10);
		tableHistorico.getColumnModel().getColumn(0).setPreferredWidth(153);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
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
	}
}
