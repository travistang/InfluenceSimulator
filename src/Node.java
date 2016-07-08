import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

public class Node implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3678123098589215925L;
	private static final int max_connection = 6;
	private static int numNodes = 0; 
	private final int max_number;
	private int number;
	private int owner;
//	private int numberOfConnectedNodes;
	//reference to other nodes: do not modify it directly!!
	private Vector<Node> connections;
	
	public final int id;
	
	public int getOwner()
	{
		return owner;
	};
	public boolean isFull()
	{
		return number == max_number;
	}
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
		return this.owner != n.owner;
	}
	public void add()
	{
		if(!isFull())
			number++;
	}
	public void sub()
	{
		if(number > 1)
			number --;
	}
	public void fill()
	{
		number = max_number;
	}
	
	public int spaces()
	{
		return max_number - number;
	}
	
	public void empty()
	{
		if(owner == 0)
			number = 1;
		else
			number = 0;
			
	}
	
	/**
	 * this function order the owner of this cell to attack another node "target"
	 * @param target node to be attacked
	 * @return true if the target cell is captured. False otherwise.
	 */
	public boolean attack(Node target)
	{

		if(!isNotTheSameOwnerAs(target) || !canAttack() || !isConnectedTo(target))return false;
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
			return true;
		}else
		{

			int dif = this.number - target.getNumber();
			
			float prob = (float)(this.number)/(this.number + target.number);
			
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
			
			//if the target cell has number 1
			//the attack must succeed
			if(enemyNumber == 1)
			{
				
			}
			//loss
			if(selfNumber - enemyNumber < 0)
			{
				enemyNumber -= selfNumber;
				target.setNumber(enemyNumber);
				this.setNumber(1);
				return false;
			}else if(selfNumber == enemyNumber)
			{
				//draw: 1 to 1
				//ok
				target.setNumber(1);
				if(willWin)
				{
					target.setOwner(this.owner);
				}
				this.setNumber(1);
				return true;
			}else
			{
				target.setOwner(this.owner);
				dif = selfNumber - enemyNumber;
				// handling overflow
				if(dif > target.max_number)
				{
					target.setNumber(target.max_number);
					this.setNumber( 1 + dif - target.max_number);
				}else
				{
					target.setNumber(dif);
				}
				this.setNumber(1);
				return true;
			}			
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
		if(connections.size() == max_connection)
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
		connections = new Vector<Node>();
	};
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Node other = (Node)o;
        return max_number == other.max_number &&
        		owner == other.owner &&
        		number == other.number;
	}
	@Override
	public int hashCode()
	{
		int prime = 127;
		return (max_number * 51 + owner * 17 - number * 3) % prime;
	}
}
