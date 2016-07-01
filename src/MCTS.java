import java.util.ArrayList;
import java.util.Random;
public interface MCTS<S extends State<Game>>
{	
	/**
	 * Step 1:
	 * Select the state.
	 * 
	 * @param s
	 * @return
	 */
	public default State<Game> select(State<Game> s)
	{
		try
		{
			State<Game> curState = s;
			
			while(curState != null)
				curState = selectionPolicy(curState);
			
			return curState;
		}catch(UnsupportedOperationException e)
		{
			if(s.getVisitedTimes() == 0) return s;
			
			for(State<Game> child : s.getChildren())
			{
				if(child.getVisitedTimes() == 0) return child;
			}
			
			return select(s.bestChildren());
		}
	}
	
	/**
	 * Step 2: 
	 * Expand the given states by appending legal moves to it.
	 * Number of states appended can be specified here.
	 * If the given number of moves is larger than what is available
	 * then the following will add as much as it can.
	 * @param s
	 * @param moves
	 */
	public default void expand(State<Game> s,int moves)
	{
		ArrayList<State<Game>> children = (ArrayList<State<Game>>) s.legalMoves();
		// filter the legal moves so no moves are duplicated 
		for(State<Game> child : children)
		{
			if(s.getChildren().contains(child))
			{
				children.remove(child);
			}
		}
		// add the required number of moves.
		if(children.size() > 1)
		{
			ArrayList<State<Game>> newChildren = new ArrayList<State<Game>>();
			int movesToAdd = (moves > children.size()?children.size():moves);
			
			for(int i = 0; i < movesToAdd; i++)
			{
				int ind = new Random().nextInt(children.size());
				State<Game> child = children.get(ind);
				newChildren.add(child);
				children.remove(ind);
			}
		}
		s.setChildren((ArrayList<State<Game>>) children);
	};
	/**
	 * Step 2: expand the given node by adding all possible moves to the given nodes
	 * @param s
	 */
	public default void expand(State<Game> s)
	{
		expand(s,s.legalMoves().size());
	}
	
	/**
	 * Step 3:
	 * Perform simulation starting from the current node to estimate the reward
	 * @param s
	 * @return
	 */
	public default float simulate(S s,int times,float rewards,float penalties)
	{
		State<Game> curState = s;
		float totalReward = 0;
		for(int i = 0; i < times; i++)
		{
			while(!curState.isEndState())
			{
				curState = defaultPolicy(s);
			}
			if(curState.isWin())
			{
				totalReward += rewards;
			}else if(curState.isLose())
			{
				totalReward -= penalties;
			}	
		}
		return totalReward/(float)times;	
	}
	/**
	 * Last step: back-propagate the reward
	 * @param s
	 */
	public default void backPropagate(S s)
	{
		State<Game> curState = s;
		while(curState.getParent() != null)
		{
			State<Game> parent = curState.getParent();
			int total = 0,size = parent.getChildren().size();
			for(State<Game> child : parent.getChildren())
			{
				total += child.getReward();
			}
			parent.setReward(total/size);
			curState = parent;
		}
	}
	
	public S defaultPolicy(State<Game> s);
	public default S selectionPolicy(State<Game> s)
	{
		throw new UnsupportedOperationException();
	};
	
}
