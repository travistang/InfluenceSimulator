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
	public abstract HashMap<Node,Node> attackingPolicy(ArrayList<Node> board, ArrayList<Node> nodes);
	public abstract ArrayList<Node> addingPolicy(ArrayList<Node> board, ArrayList<Node> nodes, int quota);
}
