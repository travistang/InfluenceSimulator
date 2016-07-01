import java.util.ArrayList;
import java.util.Collections;
public abstract class State<Game> 
{
	private Game game;
	private int visitedTimes = 0;
	private State<Game> parent = null;
	private float reward;
	private ArrayList<State<Game>> children = new ArrayList<State<Game>>();
	private float uctConstant = 1;
	
	private static float lowestReward = 0;
	private static float highestReward = 1;
	
	public void setChildren(ArrayList<State<Game>> children)
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
	public ArrayList<State<Game>> getChildren()
	{
		return children;
	}
	public State<Game> getParent()
	{
		return parent;
	}
	public void setUctConstant(float c)
	{
		uctConstant = c;
	}
	
	public void setReward(float r)
	{
		reward = r;
	}
	
	public float getReward()
	{
		return reward;
	}
	
	public static void setRewardBounds(float low, float high)
	{
		if(low >= high) return;
		lowestReward = low;
		highestReward = high;
	}

	public static float getLowestReward()
	{
		return lowestReward;
	}
	public static float getHighestReward()
	{
		return highestReward;
	}
	
	public float getUctConstant()
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
	
	public int getVisitedTimes()
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
	public void resetVisitCounts()
	{
		visitedTimes = 0;
	}
	public abstract ArrayList<State<Game>> legalMoves();
}
