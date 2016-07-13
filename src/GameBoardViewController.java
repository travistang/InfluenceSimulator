import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.stream.*;

import javax.swing.JButton;
import javax.swing.JLabel;

public class GameBoardViewController{

//	private GameBoard gameboard;
	private Game game;
//	private GamePanel panel;
//	private Cell selectedCell;
	private Node selectedNode;
	private GameView view;
//	private HashMap<Cell,Node> cellNodeMap;
	// currentPlayer means the player playing the round
	private Player currentPlayer;
	// adding mode
	private boolean adding;
	private int quota;
	private JLabel stateLabel;
	private JButton proceedBtn;
	
	public void setStateLabel(JLabel lbl)
	{
		stateLabel = lbl;
	}
	public void setProceedBtn(JButton btn)
	{
		proceedBtn = btn;
	}

	public Node getNode(Cell cell)
	{
		return cell.getNode();
	}
	public void setGame(Game game)
	{
		this.game = game;
	}
	
//	public Cell getCell(Node node)
//	{
//		for(Cell c :panel.getCells())
//		{
//			if(c.getNode().equals(node)) return c;
//		}
//
//		return null;
//	}
	//restore default
	public void reset()
	{
		currentPlayer = game.getPlayer(1);
//		panel.unhighlight();
		game.getGameBoard().reset();
		
		adding = false;
		
		initializePlayerStartingPosition();
	}
	public void start()
	{
		if(game.getNumberOfPlayers() < 1)
		{
			System.out.println("Error with number of players in game");
		}
		currentPlayer = game.getPlayer(1);
		this.initializePlayerStartingPosition();
		//handling start logic
		if(proceedBtn == null)
		{
			System.out.println("procced button is not associated to controller.");
			return;
		}
		if(stateLabel == null)
		{
			System.out.println("State label is not associated to controller.");
			return;
		}
		//enable proceed button
		proceedBtn.setEnabled(true);
		
		reportOnStateLabel();
		proceedBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				handleProceed();
			}
			
		});
	}
	
	private void handleProceed()
	{
		//un-select cell from previous round 
//		selectedCell = null;
		selectedNode = null;
		if(!adding)
		{
			quota = currentPlayer.getNumberOfCellsOwned();
			adding = !adding;
			stateLabel.setVisible(true);
		}else
		{
			//handle remaining quota
			if(quota != 0)
			{
				while(quota > 0 && ! currentPlayer.isAllCellFull())
				{
					addNode(currentPlayer.drawNode(false));
				}
			}
			// invert adding flag here so that the correct instruction will be given to the AI
			adding = !adding;
			//current player transition
			switchToNextPlayer();
		}

		reportOnStateLabel();
	}
	private void addNode(Node n)
	{
		if(quota == 0) return;
		if(quota < 0)
			throw new IllegalArgumentException("Not enough quota");
		n.add();
		quota--;
	}
	private Player getCurrentPlayer()
	{
		return currentPlayer;
	}

	/**
	 * range of current player : [1,*numPlayer*]
	 */
	private void switchToNextPlayer()
	{
		if(game.hasEnded()) return;
		
		do
		{
			currentPlayer = game.getNextPlayer(currentPlayer);
		}while(currentPlayer.isLoss());
		
		// I know this is not appropriate but the "MVC" structure of this project is broken anyway...
		try
		{
			ComputerPlayer cp = (ComputerPlayer) getCurrentPlayer();
			ArrayList<Node> board = game.getGameBoard().getNodes();
			
			ArrayList<Node> playerOwnedCells = getCurrentPlayer().getOwnedCells();
				// repeatedly ask the AI to give an attack decision until it has enough
				//TODO: check the validity of the following order given by the AI
			Pair<Node,Node> attack = cp.attack(game.getGameBoard().getNodes(), playerOwnedCells);
			while(attack != null && attack.first != null && attack.second != null)
			{
				this.selectNode(attack.first);
				this.selectNode(attack.second);
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				update();
				attack = cp.attack(board, playerOwnedCells);
			}
			
			handleProceed();
			//now the AI applies adding policy
			//TODO: check the validity of the following order given by the AI
			HashMap<Node,Integer> adds = cp.add(board, playerOwnedCells, this.quota);
			this.executeAddingOrder(adds);
			
			handleProceed();
		}catch(Exception e)
		{
			//TODO: remove me
			e.printStackTrace();
			// is not an AI
		}
	}
	private void updateOwnership()
	{
		for(Player p : game.getPlayers())
		{
			p.getOwnedCells().clear();
		}
		for(Node n : game.getGameBoard().getNodes())
		{
			Player p = this.game.getPlayer(n.getOwner());
			if(p != null)
				p.addCells(n);
		}
	}
	
	private boolean attack(Node from, Node to)
	{
		boolean res = from.attack(to);
		updateOwnership();
		return res;
	}
	/**
	 * Responsible for updating the message on stateLabel
	 * according to the game status.
	 */
	private void reportOnStateLabel()
	{
		if(!stateLabel.isVisible())
			stateLabel.setVisible(true);
		String msg = null;
		int ind = game.getIndexOfPlayer(currentPlayer);
		if(adding)
		{
			if(quota > 0)
				msg = "Player " + ind + " adding. " + quota + " move(s) left";
			else
				msg = "Player " + ind + "has no quotas left.";
		}else
		{
			msg = "Player " + ind + "'s move";
		}
		
		if(game.hasEnded())
		{
			msg = "Player " + game.getIndexOfPlayer(game.getWinner()) + " wins";
		}
		msg = "<html>" + msg + "</html>";
		stateLabel.setText(msg);
	}
	/**
	 * initialize player starting positions
	 * according to the nodes recorded in game
	 * update() will be called at the end
	 */
	public void initializePlayerStartingPosition()
	{
		if(game.getNumberOfPlayers() > game.getGameBoard().getNumberOfNodes())
		{
			System.out.println("Insufficient number of nodes for initialization");
			return;
		}
		int[] sp = game.getPlayerStartPositions();
		for(int i = 0; i < sp.length; i++)
		{
			//update info in gameboard
			Node n = game.getGameBoard().getNodes().get(sp[i]);
			n.setOwner(i + 1);
			n.setNumber(2);
			game.getPlayer(i + 1).addCells(n);
		}
		updateOwnership();
	}

	/**
	 * Return the status of the controller
	 * "Adding" represents the mode the game is in. 
	 * If it is set to be true, the behavior of selectCell(Cell) would be changed.
	 * The selected cell would be incremented by one when pressed.
	 * If the number in cell is not at the maximum the cell can hold,
	 * the number will increment by one and the quota would be decremented by 1
	 * @return adding
	 */
	public boolean isAdding()
	{
		return adding;
	}
	public void setAdding(boolean flag)
	{
		adding = flag;
	}
	public void setQuota(int quota)
	{
		this.quota = quota;
	}
	public int getQuota()
	{
		return quota;
	}
	public boolean isLargeCell(Cell c)
	{
		return getNode(c).getMaxNumber() == 12;
	}

	GameBoardViewController(Game game,GameView view)
	{
		this.game = game;
		this.view = view;
		game.setController(this);
		view.setController(this);
	}
//	GameBoardViewController(Game game, GamePanel dp)
//	{
//		// construct all mappings here
//		this.game = game;
//		game.setController(this);
//		panel = dp;
//
//		if(game.getGameBoard().getNumberOfNodes() != panel.getCells().size())
//		{
//			System.out.println("number of nodes in gameboard does not match number of cells in panel.");
//			return;
//		}
//		//associate panel controller to this only after cellNodeMap construction
//		panel.setController(this);
//		
//		/**
//		 * Construct cellCoordMap for gamePanel
//		 * Each cell is of size 20*20. Each connection is 20 pixels long
//		 */
//		int i = 0;
//		int z = 0;
//		int cellsPerCol = (int)Math.ceil(Math.sqrt(game.getGameBoard().getNumberOfNodes()));
//		int len = (int)Cell.CELL_DIMENSION.getWidth()*2;
//		for(int y = len/2; y < cellsPerCol*len + len/2; y+=len)
//		{
//			if(i == panel.getCells().size())
//			{
//				break;
//			}
//			for(int x = len/2,k = 0; x < cellsPerCol*len + len/2;x += len,k++)
//			{
//				if(i == panel.getCells().size())
//				{
//					break;
//				}
//				//upper
//				if(k%2 == 0)
//				{
//					panel.getCells().get(i++).setPosition(new Pair<Integer,Integer>(x,y));
//				}else //lower
//				{
//					panel.getCells().get(i++).setPosition(new Pair<Integer,Integer>(x,y));
//				}
//			}
//
//		}
//		panel.layCells();
//		
//		currentPlayer = game.getFirstPlayer();
//		if(currentPlayer == null) throw new NullPointerException("there are no players in game");
//	}
	public void selectNode(Node node)
	{
		if(node == null) throw new IllegalArgumentException("No such node in cell");
		
		//do not allow the selection to proceed if the game isn't even started.
		if(!game.hasStarted())return;

		// add mode
		if(adding)
		{
			//reset selected cell to prepare for the next round
			selectedNode = null;
			
			//6.
			if(quota == 0)
			{
				System.out.println("No quotas left");
				return;
			}
			//5.
			if(node.getNumber() == node.getMaxNumber())
			{
				return;
			}
			//4.
			if(currentPlayer.getOwnedCells().contains(node))
			{
				node.setNumber(node.getNumber() + 1);
				quota--;
			}
			//show quota(s) left
			reportOnStateLabel();
//			update();
			return;
		}
		
		//no cell is selected previously
		if(selectedNode == null)
		{
			// and the current player owns the node
			// then the given node is now a selected node
			if(currentPlayer.getOwnedCells().contains(node))
			{
				selectedNode = node;
			}
		}else
		{
			// a node is selected already
			// in this case an attack action will be performed

			// if the currently selected node has a different owner than the previous one
			// then an  attack action will be performed
			if(selectedNode.getOwner() != node.getOwner())
			{
				// the connectivity would be checked when attack
				// so no need to check again
				boolean result = attack(selectedNode,node);
				if(result)
				{
					// a successful attack
					// highlight the target node
					selectedNode = node;
				}else
				{
					// a failed attack
					// then no nodes are selected
					selectedNode = null;
				}
			}else
			{
				// the player is trying to attack the same node (or he just wants to select another node of his)
				// then update the selected node
				selectedNode = node;
			}
		}
		// finally update the view
//		update();
		//check winning condition
		if(game.hasEnded())
		{
			this.reportOnStateLabel();
		}

	}
	public Node getSelectedNode()
	{
		return selectedNode;
	}
	//TODO: test this
	private void executeAddingOrder(HashMap<Node,Integer> order)
	{
		order.entrySet().stream().forEach((entry) ->
		{
			Node node = entry.getKey();
			int val = entry.getValue();
			if(node.getNumber() + val > node.getMaxNumber())
				val = node.getMaxNumber() - node.getNumber();
			for(int i =0 ; i < val; i++)
				addNode(node);
		});
	}
}
