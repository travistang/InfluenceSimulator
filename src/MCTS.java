import java.util.ArrayList;
import java.util.Collection;
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
	@SuppressWarnings("unchecked")
	public default S select(S s)
	{
		try
		{
			S curState = s;
			
			while(curState != null)
				curState = selectionPolicy(curState);
			
			return curState;
		}catch(UnsupportedOperationException e)
		{
			if(s.getVisitedTimes() == 0) return s;
			
			for(State<Game> child : s.getChildren())
			{
				if(child.getVisitedTimes() == 0) return (S)child;
			}
			
			return select((S)s.bestChildren());
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
	@SuppressWarnings("unchecked")
	public default ArrayList<S> expand(S s,int moves)
	{
		ArrayList<S> expanded = new ArrayList<S>();
		// get all possible moves
		ArrayList<S> children = (ArrayList<S>) s.legalMoves();
		// filter the legal moves so no moves are duplicated 
		for(S child : children)
		{
			if(s.getChildren().contains(child))
			{
				children.remove(child);
			}
		}
		// add the required number of moves.
		// children now contains only unvisited nodes
		// if there are no such nodes then nothing to do here(i.e. fully expanded)
		if(children.size() > 1)
		{
			ArrayList<State<Game>> newChildren = new ArrayList<State<Game>>(s.getChildren());
			// determine moves to add.
			// moves to add = max(given moves, number of legal moves left)
			int movesToAdd = (moves > children.size()?children.size():moves);
			
			// draw *moves to add* number of nodes
			for(int i = 0; i < movesToAdd; i++)
			{
				int ind = new Random().nextInt(children.size());
				S child = children.get(ind);
				newChildren.add(child);
				expanded.add(child);
				children.remove(ind);
			}
		}
		// update children of the given node
		s.setChildren((ArrayList<State<Game>>) children);
		// finally return the newly added states
		return expanded;
	};
	/**
	 * Step 2: expand the given node by adding all possible moves to the given nodes
	 * @param s
	 */
	public default ArrayList<S> expand(S s)
	{
		return expand(s,s.legalMoves().size());
	}
	
	/**
	 * Step 3:
	 * Perform simulation starting from the current node to estimate the reward
	 * @param s
	 * @return
	 */
	public default float simulate(S s,int times,float rewards,float penalties)
	{
		S curState = s;
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
	@SuppressWarnings("unchecked")
	public default void backPropagate(S s)
	{
		S curState = s;
		while(curState.getParent() != null)
		{
			S parent = (S) curState.getParent();
			try
			{
				// user-provided back-propagate policy
				parent.setReward(this.backPropagatePolicy(curState));
			}catch(UnsupportedOperationException e)
			{
				// default back-propagate policy
				int total = 0,size = parent.getChildren().size();
				for(State<Game> child : parent.getChildren())
				{
					total += child.getReward();
				}
				parent.setReward(total/size);
			}finally
			{
				curState = parent;			
			}
		}
	}
	
	/**
	 * Main MCTS Algorithm
	 *  
	 * @return the prefered next state
	 */
	@SuppressWarnings("unchecked")
	public default S mcts(S root,float reward,float penalty,int expandNodesPerTime,int iterationTimes,int simulationTimes)
	{
		//0. setup
		root.resetVisitCounts();
		root.getChildren().clear();
		
		for(int i = 0; i < iterationTimes; i++)
		{
			//1. recursively select the root according to given selection policy
			// or just base on uct score if the selection policy is not overridden
			S node = (S)select(root);
			//2. expansion
			ArrayList<S> expanded = expand(node);
			for(S substates : expanded)
			{
				float r = this.simulate(substates,simulationTimes,reward,penalty);
				substates.setReward(r);
			}
			this.backPropagate(node);
		}
		//finally, return the most promising moves
		return (S)root.bestChildren();
	}
	
	@SuppressWarnings("unchecked")
	public default S defaultPolicy(S s)
	{
		if(s.isEndState()) return s;
		ArrayList<State<Game>> moves = s.legalMoves();
		int ind = new Random().nextInt(moves.size());
		return (S)(moves.get(ind));
	}
	
	public default S selectionPolicy(S s)
	{
		throw new UnsupportedOperationException();
	};
	
	public default float backPropagatePolicy(S s)
	{
		throw new UnsupportedOperationException();
	}
}
