import java.util.ArrayList;
import java.util.Random;

/**
 * Class representing the whole game.
 * It includes the interaction of 
 * @author Travis
 *
 */
public class Game {
	private GameBoard gameboard;
	private GameBoardViewController controller;
	private Player[] players;
	private Node[] playerStartPositions;
	
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
	
	//initialize player starting positions
	public void initializePlayerStartingPosition()
	{
		Random random = new Random();
		ArrayList<Integer> drawn = new ArrayList<Integer>();
		for(int i = 0; i < players.length; i++)
		{
			int pos = random.nextInt(gameboard.getNumberOfNodes());
			if(!drawn.contains(pos));
			playerStartPositions[i] = gameboard.getNodes().get(pos);
			drawn.add(pos);
		}
	}
	
	// rowLen means number of cells in a row
	public Game(int numplayers,int numcells,int rowLen)
	{
		gameboard = new GameBoard(numcells);
		controller = null;
		players = new Player[numplayers];
		playerStartPositions = new Node[numplayers];
		for(int i = 0; i < numplayers; i++)
		{
			players[i] = new Player();
		}
		initializePlayerStartingPosition();
	}
}
