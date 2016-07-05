import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A class that let players to obtain insights of a given game by playing around with it.
 * This guarantees that the original game will not be changed.
 * This class also contains useful algorithm for game board manipulation and statistics
 * and is meant to let the AI players to obtain such info of the board without consuming the main Game instance 
 * @author Travis
 *
 */
public class SandBox 
{
	private GameBoard gameboard;
	Stack<GameBoard> history = new Stack<GameBoard>();
	
	SandBox(GameBoard gameboard)
	{
		// Note: Do NOT assign the gameboard directly to an external instance as the simulation afterwards will change it.
		setGameBoard(gameboard);
		
	}
	public void undo()
	{
		if(!history.empty())
		{
			// is ok to assign the reference directly to the current gameboard because the one in "history" is already cloned and will not be in use in the real game
			GameBoard last = history.pop();
			this.gameboard = last;
		}
	}
	public GameBoard getGameBoard()
	{
		// ok to return reference here (unless you try to set this gameboard to the main game directly)
		return gameboard;
	}
	public void reset()
	{
		if(!history.empty())
		{
			GameBoard first = history.get(history.size() - 1);
			this.gameboard = first;
			history.clear();
		}
	}
	//TODO: can this really give a deep copy?
	public static GameBoard cloneBoard(GameBoard board)
	{
		try
		{
	      ObjectOutputStream oos = null;
	      ObjectInputStream ois = null;
	         ByteArrayOutputStream bos = 
	               new ByteArrayOutputStream(); // A
	         oos = new ObjectOutputStream(bos); // B
	         // serialize and pass the object
	         oos.writeObject(board);   // C
	         oos.flush();               // D
	         ByteArrayInputStream bin = 
	               new ByteArrayInputStream(bos.toByteArray()); // E
	         ois = new ObjectInputStream(bin);                  // F
	         // return the new object
	         
	         
	         GameBoard newBoard =  (GameBoard)ois.readObject(); // G
	         oos.close();
	         ois.close();
	         
	         return newBoard;
		}catch(Exception e)
		{
			return null;
		}
	}
	public List<GameBoard> getHistory()
	{
		return history;
	}
	/**
	 * Set the base of the game be the DEEP COPY of the given objects
	 * @param game
	 * @throws IOException 
	 */
	public void setGameBoard(GameBoard gameboard) throws NullPointerException
	{
		GameBoard cb = cloneBoard(gameboard);
		if(cb == null)
			throw new NullPointerException("The given game cannot be cloned"); 
		this.gameboard = cb;
		log();
	}
	public void log()
	{
		if(gameboard == null) throw new NullPointerException("Cannot log the gameboard if it is null");
		history.add(cloneBoard(gameboard));
	}
	/*********************************************************
	 * Operational methods
	 *********************************************************/
	/**
	 * Note: please pass the reference of nodes from the DEEP COPY of the sandbox
	 * This method performs an attack operation on the gameboard
	 * @param from
	 * @param to
	 */
	public boolean attack(Node from, Node to)
	{
		if(gameboard == null)
			throw new NullPointerException("The game is not properly set");
		if(!from.isConnectedTo(to))
			throw new IllegalArgumentException("Non-adjacent nodes cannot attack each other");
		log();
		return from.attack(to);
	}
	/**
	 * Again please pass the reference of the nodes from the DEEP COPY of the sandbox
	 * This method performs an adding operation on the gameboard in this SandBox
	 * @param n
	 * @return
	 */
	public boolean add(Node n)
	{
		if(n.isFull()) return false;
		log();
		n.add();
		return true;
	}
	/*********************************************************
	 * Statistical methods
	 *********************************************************/
	public boolean hasPlayer(int p)
	{
		return getNodesOfPlayer(p).size() > 0;

	}
	public List<Node> getNodesOfPlayer(int player)
	{
		return this.filterNode((node) ->
		{
			return node.getOwner() == player;
		});
	}
	public int getPointsOfPlayer(int player)
	{
		List<Node> ns = getNodesOfPlayer(player);
		int sum = 0;
		for(Node n : ns)
		{
			sum += n.getNumber();
		}
		return sum;
	}
	//TODO: this really needs to be tested..
	public List<Node> getAdjacentNodesOfPlayer(int p)
	{
		return this.getNodesOfPlayer(p)
				.stream()
				.flatMap((node) -> 
				{
					return node.getConnections().stream();
				})
				.distinct()
				.collect(Collectors.toList());
	}
	public List<Node> getBoundaryNodesOfPlayer(int p)
	{
		return this.filterNode((node) ->
		{
			return node.getOwner() == p 
					&& node.getConnections()
					.stream()
					.filter((neighbour) -> 
					{
						return neighbour.getOwner() != p;
					}).count() > 0;
		});
	}
	public boolean hasWinner()
	{
		return this.filterNode((node) ->
		{
			return node.getOwner() != 0;
		})
		.stream()
		.map((node) ->
		{
			return node.getOwner();
		})
		.distinct()
		.count() == 1;
	}
	
	public int getWinner()
	{
		if(hasWinner())
		{
			return this.filterNode((node) ->
			{
				return node.getOwner() != 0;
			}).get(0).getOwner();
		}else
			return 0;
	}
	/*********************************************************
	 * Functional methods
	 *********************************************************/
	/**
	 * Apply a user-defined function to each of the nodes in the gameboard of the sandbox
	 * @param func
	 */
	public void forEachNode(Consumer<Node> func)
	{
		gameboard.getNodes().forEach(func);
	}
	public <T> List<T> mapEachNode(Function<Node,T> mapper)
	{
		return gameboard.getNodes()
				.stream()
				.map(mapper)
				.collect(Collectors.toList());
	}
	public List<Node> filterNode(Predicate<Node> filter)
	{
		return gameboard.getNodes().stream().filter(filter).collect(Collectors.toList());
	}
	/*********************************************************
	 * Utility methods
	 *********************************************************/
	/**
	 * Get a patch of node ( sub-graph ) from the given center.
	 * Each node in the resulting patch are at most n steps away from center 
	 * @param center
	 * @param n
	 * @return patch of node
	 */
	public ArrayList<Node> getPatch(Node center, int n)
	{
		ArrayList<Node> res = new ArrayList<Node>();
		res.add(center);
		if(n <= 0) return res;
		for(Node nei : center.getConnections())
		{
			res.addAll(getPatch(nei,n - 1));
		}
		// make the list unique
		HashSet<Node> set = new HashSet<Node>();
		set.addAll(res);
		res.clear();
		res.addAll(set);
		return res;
	}
}
