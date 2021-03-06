import java.util.ArrayList;import java.util.Collections;
import java.util.List;
import java.util.stream.*;

/**
 * Store the state of a game of which the given player is given chances to attack nodes of other player
 * This is meant to be an implementation of the states of the game "Influence" when it is in attack mode.
 * GameBoard is chosen to be the parameter of the state instead of Game or SandBox
 * as this class concerns about the layout of the board more than the player inside.
 * @author Travis
 *
 */
public final class AttackGameState extends State<GameBoard> {

	/**
	 * This player references shall come from the SandBox
	 */
	private int player;
	private SandBox sandbox;
	public AttackGameState(GameBoard board,int player) 
	{
		super(null,SandBox.cloneBoard(board));
		sandbox = new SandBox(board);
		this.player = player;
	}

	@Override
	public boolean isEndState() {
		return sandbox.hasWinner();
	}

	@Override
	public boolean isWin() {
		return sandbox.getWinner() == player;
	}

	@Override
	public boolean isLose() 
	{
		return isEndState() && sandbox.getWinner() != player;
	}
	@Override
	/**
	 * The legal moves would actually means possible outcomes of EACH attack ( but not all)
	 * To do this a sandbox is needed to play around with EACH attack
	 * then collect all observed outcomes
	 * This does not guarantee that all possible outcomes will be returned
	 * But since there are many samples, the unobserved states are therefore unlikely to happen
	 * and they can be ignored.
	 */
	//TODO: this needs to be tested
	public ArrayList<State<GameBoard>> legalMoves() {
		return (ArrayList<State<GameBoard>>)sandbox.getPossibleActions(this.player)
				.stream()
				.map((pair) ->
				{
					List<State<GameBoard>> gameBoard = new ArrayList<State<GameBoard>>();
					for(int i = 0; i < 100; i++)
					{
						gameBoard.add(new AttackGameState(tryAttack(pair),player));
						sandbox.undo();
					}
					return gameBoard;
				}).reduce(new ArrayList<State<GameBoard>>(),(list,gbs) ->
				{
					list.addAll(gbs);
					return list;
				});
	}

	private GameBoard tryAttack(Pair<Node,Node> pair)
	{
		sandbox.attack(pair.first, pair.second);
		return sandbox.getGameBoard();
	}
}
