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

	/**
	 * Determine which adjacent Node should be attacked base on the given node
	 * @param node that initiates the attack.
	 * @return Node that should be attacked.
	 */
	Node localPolicy(final Node node);
	/**
	 * 
	 */
	ArrayList<Node> attackPolicy(final GameBoard gameboard);
	/**
	 * Determine which node to add based on given gameboard
	 * The hashmap tells which nodes should be added by what amount of number
	 * The quota should also be provided because this class
	 * is not responsible for acquiring such info.
	 * @return HashMap of Nodes and Integers.
	 * 	Integers are the number that should be added to the node
	 * 
	 * Note that the sum of valueSet of the returned map should be equal to quota.
	 */
	HashMap<Node,Integer> addingPolicy(final GameBoard gameboard,int quota);
	
	/**
	 * Determine which node should initiate attack.
	 * @param gameboard to be examined
	 * @return pair of nodes. Left is the node that initiate the attack.
	 * 		   Right is the node that being attacked.
	 */
	Pair<Node,Node> shouldInitiateAttackNext(final GameBoard gameboard);
	
	/**
	 * Determine the importance of a given node.
	 * @param  node to be examined.
	 * @return importance of the node, which is expected to be between [0,1]
	 */
	float importance(final Node node);
	
	/**
	 * Determine the probability of losing control of given node
	 * @param node interested
	 * @return the required probability
	 */
	float probLosingAfterThisRound(final Node node);
	
	/**
	 * Determine the probability of gaining control of given node
	 * @param node interested
	 * @return the required probability
	 */
	float probGettingControlAfterThisRound(final Node node);
	
	/**
	 * Determine the chances of winning
	 * @param game board for the current situation
	 * @return probability of winning this game given the game board
	 */
	float probWinning(final GameBoard gameboard);
	/**
	 * Determine the chances of winning
	 * @param GameBoard interested and the player to be evaluated.
	 * @return the required probability
	 */

	default float probWinning(final GameBoard gameboard,int player)
	{
		return probWinning(gameboard);
	};
}
