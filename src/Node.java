import java.util.Random;
import java.util.Vector;

public class Node {

	private static final int max_connection = 6;
	private static int numNodes = 0; 
	private final int max_number;
	private int number;
	private int owner;
	private int numberOfConnectedNodes;
	//reference to other nodes: do not modify it directly!!
	private Vector<Node> connections;
	
	public final int id;
	
	public int getOwner()
	{
		return owner;
	};
	
	public void setOwner(int owner)
	{
		this.owner = owner;
	}
	
	public int getMaxNumber()
	{
		return max_number;
	}
	
	public void setNumber(int n)
	{
		number = n;
	}
	public int getNumber()
	{
		return number;
	}
	public Vector<Node> getConnections()
	{
		return connections;
	}
	public int getDegree()
	{
		return connections.size();
	}
	// functions related to attack
	public boolean isNotTheSameOwnerAs(Node n)
	{
		return this.owner == n.owner;
	}

	// this function order the owner of this cell to attack another node "target"
	// 
	public void attack(Node target)
	{
		if(!isNotTheSameOwnerAs(target) || !canAttack() || !isConnectedTo(target))return;
		if(target.isNotOwned())
		{
			//maximum cell moving to a cell that is owned by nobody.
			if(number == 12)
			{
				number = 4;
				target.setNumber(8);
			}else
			{
				target.setNumber(number - 1);
				number = 1;
			}
			// since the target cell is not occupied. The ownership of the target cell is changed for sure.
			target.setOwner(owner);
			
		}else
		{
			//TODO: dealing with attack sequence
			int dif = this.number - target.getNumber();
			//TODO: how to generate winning chances following certain distributions?
			float prob = 0;
			switch(Math.abs(dif))
			{
				case 0:
					prob = 0.5f;
					break;
				case 1:
					prob = 0.75f;
					break;
				case 2:
					prob = 0.875f;
					break;
				case 3:
					prob = 0.9375f;
					break;
				case 4:
					prob = 0.96875f;
					break;
				default:
					prob = 1;
					break;
			}
			//generate result based on probability
			boolean willWin = Math.random() < prob;
			if(dif < 0)
			{
				willWin = !willWin;
			}
			
			if(willWin)
			{
				this.number += 1;
			}
			// evaluate the result
			int selfNumber = this.number - 1
				,enemyNumber = target.getNumber();
			
			//loss
			if(selfNumber - enemyNumber < 0)
			{
				enemyNumber -= selfNumber;
			}else
			{
				//win 
				selfNumber -= enemyNumber;
				target.setNumber(selfNumber);
				target.setOwner(this.owner);
			}
			this.setNumber(1);
			
		}
	}
	// this function tells whether the cell itself can attack the other cell
	public boolean canAttack()
	{
		return number > 1 && !isNotOwned();
	}
	public boolean isNotOwned()
	{
		return owner == 0;
	}
	public boolean isConnectedTo(Node n)
	{
		return connections.contains(n);
	}

	// connection issue
	// this function tries to connect this node to another node n. true is returned on success, false is returned otherwise.
	public boolean connectTo(Node n)
	{
		if(numberOfConnectedNodes == max_connection)
			return false;
		if(isConnectedTo(n))
			return false;
		
		// now node n can be connected
		connections.addElement(n);
		// check if n is connected to this cell or there will be an infinite loop.
		if(!n.isConnectedTo(this))
		{
			n.connectTo(this);
		}
		numberOfConnectedNodes++;
		return true;
	}
	public void disconnectFromAllNodes()
	{
		connections.clear();
	}
	/**
	 * This method should ensure disconnection in both directions
	 * @param n
	 * @return
	 */
	public boolean disconnectFrom(Node n)
	{
		if(!isConnectedTo(n))
		{
			return false;
		}
		// node n can be disconnected from this node.
		connections.remove(n);
		numberOfConnectedNodes--;
		//bidirectional disconnection
		if(n.isConnectedTo(this))
		{
			n.disconnectFrom(this);
		}
		return true;
	}
	//aux. function
	public boolean isValid()
	{
		if(owner == 0) return number == 0;
		else return number != 0;
	}
	
	//constructor
	public Node()
	{
		id = numNodes;
		numNodes++;
		
		Random r = new Random();
		int c = r.nextInt() % 3;
		if(c == 0 )
			max_number = 12;
		else
			max_number = 8;
		
		number = 0;
		owner = 0;
		// connection issue
		numberOfConnectedNodes = 0;
		connections = new Vector<Node>();
	};
	
	
}
