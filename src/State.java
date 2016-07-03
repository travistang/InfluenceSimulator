import java.util.ArrayList;
import java.util.Collections;

/**
 * An abstract representation of a state
 * The parameter of this class should be a type of game
 * This class adopts a tree-like structure and contains members for MCTS algorithm
 * @author Travis
 *
 * @param <Game>
 */
@SuppressWarnings("hiding")
public abstract class State<Game> 
{
	protected Game game;
	protected int visitedTimes = 0;
	protected State<Game> parent = null;
	protected float reward;
	protected ArrayList<State<Game>> children = new ArrayList<State<Game>>();
	private static float uctConstant = 1;
	
	private static float lowestReward = 0;
	private static float highestReward = 1;
	
	public final void setChildren(ArrayList<State<Game>> children)
	{
		//TODO: does this clone the object inside?
		Collections.copy(this.children, children);
	}
	
	public State(State<Game >parent,Game game)
	{
		this.game = game;
		this.parent = parent;
	}
	public State(Game game)
	{
		this(null,game);
	}
	public final ArrayList<State<Game>> getChildren()
	{
		return children;
	}
	public final State<Game> getParent()
	{
		return parent;
	}
	public final static void setUctConstant(float c)
	{
		uctConstant = c;
	}
	
	public final void setReward(float r)
	{
		reward = r;
	}
	
	public final float getReward()
	{
		return reward;
	}
	
	public static final void setRewardBounds(float low, float high)
	{
		if(low >= high) return;
		lowestReward = low;
		highestReward = high;
	}

	public static final float getLowestReward()
	{
		return lowestReward;
	}
	public static final float getHighestReward()
	{
		return highestReward;
	}
	
	public final float getUctConstant()
	{
		return uctConstant;
	}
	
	public final float uct()
	{
		int np = parent.visitedTimes;
		int ni = visitedTimes;
		return (float) (reward + uctConstant * Math.sqrt(Math.log(np)/ni));
	}
	
	public abstract boolean isEndState();
	public abstract boolean isWin();
	public abstract boolean isLose();
	
	public final int getVisitedTimes()
	{
		return visitedTimes;
	}
	
	public State<Game> bestChildren()
	{
		if(children == null) return null;
		if(children.size() == 0) return null;
		float bestUct = getLowestReward();
		State<Game> s = children.get(0);
		for(State<Game> child : children)
		{
			if(child.uct() > bestUct)
			{
				s = child;
				bestUct = child.uct();
			}
		}
		return s;
	}
	public final void resetVisitCounts()
	{
		visitedTimes = 0;
	}
	public abstract ArrayList<State<Game>> legalMoves();
}
