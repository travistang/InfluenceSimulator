import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

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
		game.getGameBoard().reset();
		initializePlayerStartingPosition();
		//repaint the game panel
		update();
	}
	/**
	 * initialize player starting positions
	 * according to the nodes recorded in game
	 */
	public void initializePlayerStartingPosition()
	{
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
		for(int i = 0; i < game.getGameBoard().getNodes().size();i++)
		{
			Node n = game.getGameBoard().getNodes().get(i);
			Cell c = this.getCell(n);
			c.setAppearance(n.getOwner(), n.getNumber());
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
	public boolean isLargeCell(int i)
	{
		return game.getGameBoard().getLargeCellsList().contains(i);
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
		// add mode
		if(adding)
		{
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
			node.setNumber(node.getNumber() + 1);
			quota--;
			return;
		}
		int nodeOwner = node.getOwner();
		//1 & 2.
		if(selectedCell == null)
		{
			if(nodeOwner == currentPlayer)
				selectedCell = cell;
			return;
		}else
		{
			Node selectedNode = getNode(selectedCell);
			
			if(selectedNode == null)
			{
				System.out.println("incomplete cell-node mapping");
				return;				
			}
			
			//3.
			if(node.isConnectedTo(selectedNode))
			{
				node.attack(selectedNode);
			}else
			{
				//4.
				selectedCell = cell;
			}
		}

		if(selectedCell != null)
		{
			//2.
			if(getNode(selectedCell).isConnectedTo(getNode(cell)))
			{
				selectedCell = cell;
			}else
			{
				
			}
		}else
		{
		}
		update();
	}
	GameBoardViewController(Game game, GamePanel dp)
	{
		// construct all mappings here
		this.game = game;
		game.setController(this);
		panel = dp;
		panel.setController(this);
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
			System.out.println(z++);
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
		currentPlayer = 0;
	}
}
