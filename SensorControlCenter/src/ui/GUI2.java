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
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Color;

public class GUI2 {

	private static final int BUTAOOK = 1;
	private static final int BUTAOSAIR = -1;
	private JFrame frame;
	private AgenteGUI agGUI;
	private JTextField textField_1;
	private JTable tableHome;
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
		frame.getContentPane().add(panelEntrada, "name_81389608892175");
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
		frame.getContentPane().add(panelPrincipal, "name_81389619862659");
		panelPrincipal.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 434, 21);
		panelPrincipal.add(menuBar);
		
		JMenu mnSensorcenas = new JMenu("SensorCenas");
		menuBar.add(mnSensorcenas);
		
		JMenuItem mntmSair = new JMenuItem("Sair");
		mnSensorcenas.add(mntmSair);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, "name_81389629922512");
		
		JSplitPane splitPaneHome = new JSplitPane();
		tabbedPane.addTab("Home", null, splitPaneHome, null);
		
		JPanel panelEsquerda = new JPanel();
		splitPaneHome.setLeftComponent(panelEsquerda);
		
		tableHome = new JTable();
		tableHome.setModel(new DefaultTableModel(
			new Object[][] {
				{"M\u00E9dia Casa", null},
				{"Divis\u00E3o mais quente", null},
				{"Divis\u00E3o mais fria", null},
				{"Total Falhas", null},
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
		
		JTextPane Avisos = new JTextPane();
		Avisos.setBackground(Color.WHITE);
		Avisos.setText("Inserir aquios avisos");
		panelEsquerda.add(Avisos, "cell 0 2,grow");
		
		JPanel panel = new JPanel();
		splitPaneHome.setRightComponent(panel);
		panel.setLayout(null);
		
		JSplitPane splitPaneHistorico = new JSplitPane();
		tabbedPane.addTab("Histórico", null, splitPaneHistorico, null);
		
		JPanel panel_1 = new JPanel();
		splitPaneHistorico.setLeftComponent(panel_1);
		
		tableHistorico = new JTable();
		tableHistorico.setModel(new DefaultTableModel(
			new Object[][] {
				{"M\u00E9dia da Casa", null},
				{"Divis\u00E3o mais quente", null},
				{"Divis\u00E3o mais fria", null},
				{"Total de Falhas", ""},
				{"Total leituras erradas", null},
				{"Total leituras inconcistentes", null},
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
