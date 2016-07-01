import java.util.ArrayList;
import java.util.HashMap;


public class ComputerPlayer extends Player
{
	private GameBoardViewController controller;
	private Policy policy;
	
	public final boolean isAI;
	
	public void setPolicy(Policy policy)
	{
		this.policy = policy;
	}
	
	ComputerPlayer()
	{
		controller = null;
		policy = null;
		isAI = true;
	}
	public void setController(GameBoardViewController controller)
	{
		this.controller = controller;
	}
	/**
	 * React to the controller
	 * This method should be the "main" function of computer player
	 * It decides what to do according to the given policy
	 * 
	 * TODO: needs to be tested
	 */
	public HashMap<Node,Node> attack(ArrayList<Node> board, ArrayList<Node> nodes)
	{
		if(policy == null)
		{
			throw new NullPointerException("No policy is given to computer player");
		}
		return policy.attackingPolicy(board, nodes);
	}
	/**
	 * React to the controller when it's the current player's turn and the game is in adding mode
	 * @param board
	 * @param nodes
	 * @param quota
	 * @return
	 */
	public ArrayList<Node> add(ArrayList<Node> board, ArrayList<Node> nodes, int quota)
	{
		if(policy == null)
		{
			throw new NullPointerException("No policy is given to computer player");
		}
		return policy.addingPolicy(board, nodes, quota);
	}
}
