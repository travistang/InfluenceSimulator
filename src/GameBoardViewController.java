import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;

public class GameBoardViewController{

//	private GameBoard gameboard;
	private Game game;
	private GamePanel panel;
	private Cell selectedCell;
	private HashMap<Cell,Node> cellNodeMap;
	// currentPlayer means the player playing the round
	private int currentPlayer;
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
		return cellNodeMap.get(cell);
	}
	public void setGame(Game game)
	{
		this.game = game;
	}
	
	public Cell getCell(Node node)
	{
		for(Map.Entry<Cell,Node> e : cellNodeMap.entrySet())
		{
			if(node == e.getValue())
				return e.getKey();
		}

		return null;
	}
	//restore default
	public void reset()
	{
		currentPlayer = 1;
		panel.unhighlight();
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
		currentPlayer = 1;
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
				//un-select cell from previous round 
				selectedCell = null;
				if(!adding)
				{
					quota = game.getPlayer(currentPlayer).getNumberOfCellsOwned();
					stateLabel.setVisible(true);
				}else
				{
					//handle remaining quota
					if(quota != 0)
					{
						Random random = new Random();
						while(quota > 0 && ! game.getPlayer(currentPlayer).isAllCellFull())
						{
							game.getPlayer(currentPlayer).drawNode(false).add();
						}
					}
					
					//current player transition
					switchToNextPlayer();
				}
				adding = !adding;
				reportOnStateLabel();
			}
			
		});
	}
	
	private void switchToNextPlayer()
	{
		if(game.hasEnded()) return;
		
		currentPlayer++;
		
		if(currentPlayer == game.getNumberOfPlayers())
		{
			currentPlayer = 0;
		}
		
		if(game.getPlayer(currentPlayer).isLoss())
			switchToNextPlayer();
	
		// I know this is not appropriate but the "MVC" structure of this project is broken anyway...
		Player player;
		if((player = game.getPlayers()[currentPlayer]).isAI)
		{
			ComputerPlayer cp = (ComputerPlayer)player;
			//TODO: verify the followings are immutable
			ArrayList<Node> board = (ArrayList<Node>)Collections
					.unmodifiableList(game.getGameBoard().getNodes());
			ArrayList<Node> playerOwnedCells = game.getPlayers()[currentPlayer].getOwnedCells();
			if(!adding)
			{
				HashMap<Node,Node> attacks = cp.attack(board,playerOwnedCells);
			}else
			{
				ArrayList<Node> adds = cp.add(board, playerOwnedCells, this.quota);
			}
		}
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

		if(adding)
		{
			if(quota > 0)
				msg = "Player " + currentPlayer + " adding. " + quota + " move(s) left";
			else
				msg = "Player " + currentPlayer + "has no quotas left.";
		}else
		{
			msg = "Player " + currentPlayer + "'s move";
		}
		
		if(game.hasEnded())
		{
			msg = "Player " + game.getWinner() + " wins";
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
		}
		update();
	}
	private void setCellAppearence(Cell c,int owner, int number)
	{
		if(c != null)
		{
			c.setAppearance(owner, number);
		}
	}
	/**
	 * Update game panels
	 */
	public void update()
	{
		//clear player owned cells
		for(int i = 0; i < game.getNumberOfPlayers(); i++)
		{
			game.getPlayer(i + 1).removeAllCells();
		}
		for(int i = 0; i < game.getGameBoard().getNodes().size();i++)
		{
			Node n = game.getGameBoard().getNodes().get(i);
			Cell c = this.getCell(n);
			c.setAppearance(n.getOwner(), n.getNumber());
			
			//update player statistics
			if(n.getOwner() > 0)
				game.getPlayer(n.getOwner()).addCells(n);
		}
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
	/**
	 * This function handles cells selection rule
	 * 1. If no cells are selected and the selected Cell does not belong to the owner, then do nothing
	 * 2. If no cells are selected and the selected Cell belongs to the owner, then the cell will be selected
	 * 2. If a cell was selected previously and it did not connect to the cell, the cell will be the selected cell
	 * 3. If a cell was selected previously ant it connects to the cell, then the attack sequence will be performed
	 * 4. If the game is in adding mode, this method will check if there's any quota left
	 * 	  If there are still quotas available, the selected cell would be incremented by 1 and the quota will decremented by 1
	 * 5. If there are still quota but the number of the selected cell is at its maximum, this function will do nothing
	 * 	  and the quota will not decrease.
	 * 6. If there are no quotas left, the function will do nothing and return immediately ( which is not supposed to happen)
	 * @param cell that is newly selected
	 */
	public void selectCell(Cell cell)
	{
		Node node = getNode(cell);
		
		if(node == null)
		{
			System.out.println("incomplete cell-node mapping");
			return;
		}
		
		//do not allow the selection to proceed if the game isn't even started.
		if(!game.hasStarted())return;

		// add mode
		if(adding)
		{
			//reset selected cell to prepare for the next round
			selectedCell = null;
			
			panel.unhighlight();
			
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
			if(node.getOwner() == currentPlayer)
			{
				node.setNumber(node.getNumber() + 1);
				quota--;
			}
			//show quota(s) left
			reportOnStateLabel();
			update();
			return;
		}
		
		//no cell is selected previously
		if(selectedCell == null)
		{
			if(currentPlayer == node.getOwner())
			{
				panel.highlight(cell);
				selectedCell = cell;
			}
		}else
		{
			Node target = getNode(selectedCell);

			if(node.getOwner() != target.getOwner())
			{
				// the connectivity would be checked when attack
				// so no need to check again
				boolean result = target.attack(node);
				if(result)
				{
					panel.highlight(getCell(node));
					selectedCell = getCell(node);
				}else
				{
					panel.unhighlight();
					selectedCell = null;
				}
			}else
			{
				selectedCell = cell;
				panel.highlight(cell);
			}
		}
		update();
		//check winning condition
		if(game.hasEnded())
		{
			this.reportOnStateLabel();
		}
	}
	GameBoardViewController(Game game, GamePanel dp)
	{
		// construct all mappings here
		this.game = game;
		game.setController(this);
		panel = dp;

		cellNodeMap = new HashMap<Cell,Node>();
		HashMap<Cell,Coordinate> cellCoordMap = new HashMap<Cell,Coordinate>();
		if(game.getGameBoard().getNumberOfNodes() != panel.getCells().length)
		{
			System.out.println("number of nodes in gameboard does not match number of cells in panel.");
			return;
		}
		// construct cellNodeMap
		int i = 0;
		for(Node n : game.getGameBoard().getNodes())
		{
			cellNodeMap.put(panel.getCells()[i++],n);
		}
		//associate panel controller to this only after cellNodeMap construction
		panel.setController(this);
		
		/**
		 * Construct cellCoordMap for gamePanel
		 * Each cell is of size 20*20. Each connection is 20 pixels long
		 */
		i = 0;
		int z = 0;
		int cellsPerCol = (int)Math.ceil(Math.sqrt(game.getGameBoard().getNumberOfNodes()));
		int len = (int)Cell.CELL_DIMENSION.getWidth()*2;
		for(int y = len/2; y < cellsPerCol*len + len/2; y+=len)
		{
			if(i == panel.getCells().length)
			{
				break;
			}
			for(int x = len/2,k = 0; x < cellsPerCol*len + len/2;x += len,k++)
			{
				if(i == panel.getCells().length)
				{
					break;
				}
				//upper
				if(k%2 == 0)
				{
					 cellCoordMap.put(panel.getCells()[i++],new Coordinate(x,y));
				}else //lower
				{
					cellCoordMap.put(panel.getCells()[i++],new Coordinate(x,y + 20));
				}
			}

		}
		panel.setCellCoordinateMap(cellCoordMap);
		panel.layCells();
		
		currentPlayer = 0;
	}
}
