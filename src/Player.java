import java.util.ArrayList;
public class Player {
	public final int id;
	private static int numPlayers;
	// Because the objects are passed by value, the id of the nodes should be stored in this player instead.
	private ArrayList<Node> ownedCells;
	
	public int getNumberOfCellsOwned()
	{
		return ownedCells.size();
	}
	
	public void addCells(Node n)
	{
		ownedCells.add(n);
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
