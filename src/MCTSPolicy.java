import java.util.ArrayList;
import java.util.HashMap;


public class MCTSPolicy implements Policy,MCTS {

	@Override
	public HashMap<Node, Node> attackingPolicy(ArrayList<Node> board,
			ArrayList<Node> nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Node> addingPolicy(ArrayList<Node> board,
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
