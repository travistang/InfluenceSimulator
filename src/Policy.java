import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides general instruction for computer players to make decisions 
 * This class only provide directions instead of taking actions directly.
 * This class should give suggestions based on statistics and simulations
 * 
 * @author Travis
 *
 */
public abstract class Policy {

	public State<Game> state;
	public HashMap<Node,Node> getPossibleMoves()
	{
		HashMap<Node,Node> res = new HashMap<Node,Node>();
		return res;
	}
	public final void setPolices()
	{
		
	}
	/**
	 * The method for the AI to tell the game which steps it is going to make.
	 * Since it is a bit difficult to tell which nodes refers to which between two copies of game board,
	 * the game would assume that the HashMap returned by this function stores the references of the nodes in the real game board.
	 * A check will be performed to verify this when the game receives the map from this method.
	 * If the results fails the check an exception will be raised.
	 * @param board
	 * @param nodes
	 * @return
	 */
	public abstract Pair<Node,Node> attackingPolicy(ArrayList<Node> board, ArrayList<Node> nodes);
	/**
	 * Similar method for the AI to apply it's algorithm to tell the game which nodes it is going to add.
	 * 
	 * @param board
	 * @param nodes
	 * @param quota
	 * @return
	 */
	public abstract HashMap<Node,Integer> addingPolicy(ArrayList<Node> board, ArrayList<Node> nodes, int quota);
}
