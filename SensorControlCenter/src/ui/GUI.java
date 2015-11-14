package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import controlCenter.AgenteGUI;
import jade.gui.GuiEvent;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI {

	private static final int BUTAOOK = 1;
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				butaoOK();
			}
		});
		frame.getContentPane().add(btnOk, BorderLayout.CENTER);
	}

	protected void butaoOK() {
		GuiEvent ge = new GuiEvent(this,BUTAOOK);
		ge.addParameter("Periodico");
		ge.addParameter(48);
		agGUI.postGuiEvent(ge);
		
	}

}
