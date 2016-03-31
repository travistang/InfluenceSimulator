
public class ComputerPlayer extends Player
{
	private GameBoardViewController controller;
	private Policy policy;
	
	
	public void setPolicy(Policy policy)
	{
		this.policy = policy;
	}
	
	ComputerPlayer()
	{
		controller = null;
		policy = null;
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
	public void react()
	{
		if(policy == null)
		{
			throw new NullPointerException("No policy is given to computer player");
		}
		
	}
}
