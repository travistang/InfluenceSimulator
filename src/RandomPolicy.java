import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomPolicy extends Policy{

	private int player;
	private Random random;
	public RandomPolicy(int player)
	{
		this.player = player;
		random = new Random();
	}
	//TODO: test this
	@Override
	public Pair<Node, Node> attackingPolicy(ArrayList<Node> board,
			ArrayList<Node> nodes) {
		// This tries to get the references of the nodes inside the board to the sandbox.
		// So that the methods from the sandbox will return the same references to the map.
		SandBox sb = new SandBox(new GameBoard(board),false);
		
		List<Pair<Node,Node>> pairs = sb.getPossibleActions(player);
		pairs = sample(pairs,0);
		
		return pairs.get(0);
	}

	@Override
	public HashMap<Node,Integer> addingPolicy(ArrayList<Node> board,
			ArrayList<Node> nodes, int quota) {
		// TODO Auto-generated method stub
		
		return null;
	}

	/**
	 * return a list of sample of the given list.
	 * The resultant list holds the reference to the given list
	 * This methods guarantees that the references of the result are unique.
	 * @param list
	 * @param count
	 * @return
	 */
	//TODO: test this
	private <T>	List<T> sample(List<T> list,int count)
	{
		int size = list.size();
		if(count < 0)
			throw new IllegalArgumentException("Number of samples of a list cannot be negative");
		if(count >= size) return list;
		return list
				.stream()
				.sorted((l,r) -> new Random().nextBoolean()? 1 : (-1)) // rearrange the list with a random comparator
				.collect(Collectors.toList())
				.subList(0, count); // then take the first "count" items ( which should have the same effect as drawing )
	}
}
