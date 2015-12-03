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

public class GUI {

	private static final int BUTAOOK = 1;
	private static final int BUTAOSAIR = -1;
	private JFrame frame;
	private AgenteGUI agGUI;

	/**
	 * Launch the application.
	 */
	public static void main(AgenteGUI ag) {
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
		initialize();	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(130, 203, 75, 37);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				butaoOK();
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnOk);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				butaoSair();
			}
		});
		btnExit.setBounds(230, 203, 75, 37);
		frame.getContentPane().add(btnExit);
	}

	// Sair do programa
	protected void butaoSair() {
		GuiEvent ge = new GuiEvent(this,BUTAOSAIR);
		agGUI.postGuiEvent(ge);
		frame.dispose();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	protected void butaoOK() {
		GuiEvent ge = new GuiEvent(this,BUTAOOK);
		ge.addParameter(10);
		agGUI.postGuiEvent(ge);
		
	}
}
