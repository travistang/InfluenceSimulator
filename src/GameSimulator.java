import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JSplitPane;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;


public class GameSimulator {

	private JFrame frame;
	private Game game;
//	private GamePanel gamePanel;
	private GameView view;
	private GameBoard gameboard;
	private GameBoardViewController controller;
	private JLabel stateLabel;
	private JButton btnProceed;
	private JButton resetButton;
	private JButton startButton;
	private final int GAMEPANEL_WIDTH = 500;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameSimulator window = new GameSimulator();
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
	public GameSimulator() {
		game = new Game(5,70,GAMEPANEL_WIDTH/(int)Cell.CELL_DIMENSION.getWidth());
		//set AI player
		for(int i = 2; i < 5; i++)
		{
			ComputerPlayer player = new ComputerPlayer();
			player.setPolicy(new RandomPolicy(i));
			game.setPlayer(i, player);
		}

		/**
		 * Initialize the game panel with the nodes in the Game.
		 * Now all the cells should be associated with exactly one node
		 */
//		gamePanel = new GamePanel(game.getGameBoard().getNodes());
		view = new GameView(game.getGameBoard().getGraph());
		controller = new GameBoardViewController(game,view);

		initialize();
		//associate stateLabel and proceedBtn after initialization
		controller.setStateLabel(stateLabel);
		controller.setProceedBtn(btnProceed);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 620, 454);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		controlPanel.setBounds(0, 0, 118, 426);
		frame.getContentPane().add(controlPanel);
		
		resetButton = new JButton("Reset");
		resetButton.setEnabled(false);

		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.reset();
				startButton.setEnabled(true);
				resetButton.setEnabled(false);
			}
		});
		
		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!game.hasStarted())
				{
					game.start();
				}
				startButton.setEnabled(false);
				resetButton.setEnabled(true);
			}
		});
		
		JCheckBox chckbxPlayer_1 = new JCheckBox("Player 1");
		chckbxPlayer_1.setSelected(true);
		chckbxPlayer_1.setForeground(Color.BLUE);
		
		JCheckBox chckbxPlayer_2 = new JCheckBox("Player 2");
		chckbxPlayer_2.setSelected(true);
		chckbxPlayer_2.setForeground(Color.GREEN);
		
		JCheckBox chckbxPlayer_3 = new JCheckBox("Player 3");
		chckbxPlayer_3.setSelected(true);
		chckbxPlayer_3.setForeground(Color.MAGENTA);
		
		JCheckBox chckbxPlayer_4 = new JCheckBox("Player 4");
		chckbxPlayer_4.setSelected(true);
		chckbxPlayer_4.setForeground(Color.RED);
		
		JCheckBox chckbxPlayer_5 = new JCheckBox("Player 5");
		chckbxPlayer_5.setSelected(true);
		chckbxPlayer_5.setForeground(Color.YELLOW);
		
		btnProceed = new JButton("Proceed");
		btnProceed.setEnabled(false);
		
		stateLabel = new JLabel("");
		stateLabel.setEnabled(true);
		stateLabel.setVisible(false);
		
		GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
		gl_controlPanel.setHorizontalGroup(
			gl_controlPanel.createParallelGroup(Alignment.TRAILING)
				.addComponent(chckbxPlayer_1, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
				.addGroup(gl_controlPanel.createSequentialGroup()
					.addComponent(chckbxPlayer_2, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_controlPanel.createSequentialGroup()
					.addComponent(chckbxPlayer_4, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_controlPanel.createSequentialGroup()
					.addComponent(chckbxPlayer_3, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_controlPanel.createSequentialGroup()
					.addComponent(chckbxPlayer_5, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_controlPanel.createSequentialGroup()
					.addGroup(gl_controlPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(startButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
						.addComponent(resetButton, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
					.addContainerGap())
				.addGroup(gl_controlPanel.createSequentialGroup()
					.addComponent(btnProceed, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(Alignment.LEADING, gl_controlPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(stateLabel, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(14, Short.MAX_VALUE))
		);
		gl_controlPanel.setVerticalGroup(
			gl_controlPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_controlPanel.createSequentialGroup()
					.addGap(27)
					.addComponent(chckbxPlayer_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxPlayer_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxPlayer_3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxPlayer_4)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxPlayer_5)
					.addGap(39)
					.addComponent(resetButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(startButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(stateLabel)
					.addGap(9)
					.addComponent(btnProceed)
					.addContainerGap(95, Short.MAX_VALUE))
		);
		controlPanel.setLayout(gl_controlPanel);

//		gamePanel.setBackground(Color.WHITE);
//		gamePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
//		gamePanel.setBounds(118, 0, 502, 426);
//		gamePanel.setPreferredSize(new Dimension(502,426));
//		gamePanel.setSize(502,426);
//		frame.getContentPane().add(gamePanel);
		/*
		 * GameView configurations
		 */
		view.setBackground(Color.WHITE);
		view.setBorder(new LineBorder(new Color(0, 0, 0)));
		view.setBounds(118, 0, 502, 426);
		view.setPreferredSize(new Dimension(502,426));
		view.setSize(502,426);
		frame.getContentPane().add(view);
	}
}