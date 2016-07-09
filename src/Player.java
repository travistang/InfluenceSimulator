import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
public class Player {
	public final int id;
	private static int numPlayers;
	// Because the objects are passed by value, the id of the nodes should be stored in this player instead.
	private ArrayList<Node> ownedCells;
	public final boolean isAI = false;
	
	public int getNumberOfCellsOwned()
	{
		return ownedCells.size();
	}
	
	public void addCells(Node n)
	{
		ownedCells.add(n);
	}
	public ArrayList<Node> getOwnedCells()
	{
		// reference http://stackoverflow.com/questions/3700971/immutable-array-in-java
		return ownedCells;
	}
	
	public boolean isAllCellFull()
	{
		for(Node c : ownedCells)
		{
			if(!c.isFull())
				return false;
		}
		return true;
	}
	//draw a node, specify if a fulled cell should be drawn by passing the flag into it.
	public Node drawNode(boolean full)
	{
		if(full && getNonFullCells().size() == getNumberOfCellsOwned())
			return null;
		if(!full && getNonFullCells().size() == 0)
			return null;
		
		Random random = new Random();

		while(true)
		{
			Node node = ownedCells.get(random.nextInt(ownedCells.size()));
			if((node.isFull() && full) || (!node.isFull() && !full))
				return node;
		}
	}
	public ArrayList<Node> getNonFullCells()
	{
		ArrayList<Node> list = new ArrayList<Node>();
		for(Node n : ownedCells)
		{
			if(!n.isFull())
				list.add(n);
		}
		return list;
	}
	private int getTotalNumber()
	{
		int total = 0;
		for(int i = 0; i < ownedCells.size(); i++)
		{
			total += ownedCells.get(i).getNumber();
		}
		return total;
	}
	public void removeCells(Node n)
	{
		if(ownedCells.contains(n))
		{
			ownedCells.remove(n);
		}
	}
	public void removeAllCells()
	{
		ownedCells.clear();
	}
	public boolean isLoss()
	{
		return ownedCells.size() == 0;
	}
	
	public int getQuota()
	{
		return ownedCells.size();
	}
	public Player()
	{
		numPlayers++;
		id = numPlayers;
		ownedCells = new ArrayList<Node>();
	}
}
