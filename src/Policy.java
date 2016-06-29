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
public interface Policy {

	public HashMap<Node,Node> attackingPolicy(ArrayList<Node> board, ArrayList<Node> nodes);
	public ArrayList<Node> addingPolicy(ArrayList<Node> board, ArrayList<Node> nodes, int quota);
}
