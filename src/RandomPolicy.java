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
		// give a slight chances that the policy attacks nobody 
		if(new Random().nextInt(100000) % 100 == 0) return null;
		// This tries to get the references of the nodes inside the board to the sandbox.
		// So that the methods from the sandbox will return the same references to the map.
		SandBox sb = new SandBox(new GameBoard(board),false);
		
		List<Pair<Node,Node>> pairs = sb.getPossibleActions(player);
		// nothing left to move

		if(pairs.size() == 0) return null;
		pairs = sample(pairs,1);
		
		return pairs.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<Node,Integer> addingPolicy(ArrayList<Node> board,
			ArrayList<Node> nodes, int quota) {
		HashMap<Node,Integer> res = new HashMap<Node,Integer>();
		fullSample(nodes)
			.stream()
			.forEach(n -> res.put(n,1)); // each node add one
		return res;
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
	
	private <T> List<T> sample(List<T> list)
	{
		return sample(list,1);
	}
	private <T> List<T> fullSample(List<T> list)
	{
		return sample(list,list.size());
	}
}
