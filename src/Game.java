import java.util.ArrayList;
import java.util.Random;

/**
 * Class representing the whole game.
 * It includes the interaction of 
 * @author Travis
 *
 */
public class Game {
	private boolean started;
	private GameBoard gameboard;
	private GameBoardViewController controller;
	private Player[] players;
	private int[] playerStartPositions;
	
	public GameBoard getGameBoard()
	{
		return gameboard;
	}
	public Player[] getPlayers()
	{
		return players;
	}
	public Player getPlayer(int index)
	{
		if(index >= players.length || index < 0)
		{
			return null;
		}
		return players[index];
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
	
	public int[] getPlayerStartPositions()
	{
		return playerStartPositions;
	}
	public int getNumberOfPlayers()
	{
		return players.length;
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
		for(int i = 0; i < players.length; i++)
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
		players = new Player[numplayers];
		playerStartPositions = new int[numplayers];
		for(int i = 0; i < numplayers; i++)
		{
			players[i] = new Player();
		}
		initializePlayerStartingPosition();
	}
}
