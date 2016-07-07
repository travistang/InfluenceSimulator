import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main MCTS Policy that an AI can choose to adopt.
 * @author Travis
 *
 */
public class MCTSPolicy extends Policy implements MCTS<State<Game>> 
{

	@Override
	public Pair<Node, Node> attackingPolicy(ArrayList<Node> board,
			ArrayList<Node> nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Node,Integer> addingPolicy(ArrayList<Node> board,
			ArrayList<Node> nodes, int quota) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State defaultPolicy(State s) {
		// TODO This is the most interesting stuff...
		// TODO the default policy is determined by the opponent's behaviour
		return null;
	}

}
