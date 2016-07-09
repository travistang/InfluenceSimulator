import java.util.ArrayList;
import java.util.Random;

public class Game {
	private boolean started;
	private GameBoard gameboard;
	private GameBoardViewController controller;
	private ArrayList<Player> players;
	private int[] playerStartPositions;
	
	public GameBoard getGameBoard()
	{
		return gameboard;
	}
	public int[] getNumberOfCellsOwnedStatistics()
	{
		int[] res = new int[players.size()];
		for(int i = 0; i < 5; i++)
		{
			res[i] = players.get(i).getNumberOfCellsOwned();
		}
		return res;
	}
	
	public ArrayList<Player> getPlayers()
	{
		return players;
	}
	public void setPlayer(int i,Player player)
	{
		players.set(i,player);
	}
	public boolean hasEnded()
	{
		int effectivePlayer = 0;
		for(Player p : players)
		{
			if(!p.isLoss())
			{
				effectivePlayer++;
			}
		}
		return effectivePlayer <= 1 && started;
	}
	public Player getWinner()
	{
		if(!hasEnded()) return null;
		for(Player p : players)
			if(!p.isLoss())
				return p;
		return null;
	}
	/**
	 * The player number is 1-based !!
	 * @param index
	 * @return reference of the Player or null if index is invalid
	 */
	public Player getPlayer(int index)
	{
		if(index > players.size()|| index <= 0)
		{
			return null;
		}
		return players.get(index - 1);
	}
	public boolean hasStarted()
	{
		return started;
	}
	public void start()
	{
		started = true;
		controller.start();
	}
	public void reset()
	{
		started = false;
		for(Player p : players)
		{
			p.removeAllCells();
		}
		controller.reset();
	}
	public int[] getPlayerStartPositions()
	{
		return playerStartPositions;
	}
	public int getNumberOfPlayers()
	{
		return players.size();
	}
	
	public void setGameBoard(GameBoard gb)
	{
		this.gameboard = gb;
	}
	public void setController(GameBoardViewController con)
	{
		controller = con;
		controller.setGame(this);
	}
	
	/**
	 * initialize player starting positions
	 * 
	 * because this is a model class and I don't want to change other models here
	 * The positions will be saved instead of other references
	 * The controller should make the changes.
	 */
	public void initializePlayerStartingPosition()
	{
		Random random = new Random();
		ArrayList<Integer> drawn = new ArrayList<Integer>();
		for(int i = 0; i < players.size(); i++)
		{
			int pos = random.nextInt(gameboard.getNumberOfNodes());
			if(!drawn.contains(pos))
				playerStartPositions[i] = pos;
			drawn.add(pos);
		}
	}
	
	// rowLen means number of cells in a row
	public Game(int numplayers,int numcells,int rowLen)
	{
		started = false;
		gameboard = new GameBoard(numcells);
		controller = null;
		players = new ArrayList<Player>();
		playerStartPositions = new int[numplayers];
		for(int i = 0; i < numplayers; i++)
		{
			players.add(new Player());
		}
		initializePlayerStartingPosition();
	}
}