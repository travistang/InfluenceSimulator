import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class SandBoxTest {
	GameBoard gb = new GameBoard(4);
	SandBox sandbox;
	
	@Before
	public void setUp() throws Exception {
		int conn[][] = {{0,1},{1,2},{2,1},{2,3}};
		for(int[] pair : conn)
		{
			gb.getNodes().get(pair[0]).connectTo(gb.getNodes().get(pair[1]));
		}
		for(int i =0 ; i < 4;i++)
		{
			gb.getNodes().get(i).setOwner(1 + i % 2);
		}
		sandbox = new SandBox(gb);
	}

	@Test
	public void constructor()
	{
		int i = 0;
		for(Node n : gb.getNodes())
		{
			assertTrue("construction ok",n.getOwner() == 1 + i++ % 2);
		}
		GameBoard cur = sandbox.getGameBoard();
		i = 0;
		for(Node n : cur.getNodes())
		{
			assertTrue("Sandbox construction ok",n.getOwner() == 1 + i++ % 2);
		}
	}
	@Test
	public void cloneBoard()
	{
		GameBoard clone = SandBox.cloneBoard(gb);
		int i = 0;
		for(Node n : clone.getNodes())
		{
			assertTrue("clone ok",n.getOwner() == 1 + i++ % 2);
			assertTrue("clone is a deep copy", n != gb.getNodes().get(i - 1));
			assertTrue("clone is a correct copy",n.equals(gb.getNodes().get(i - 1)));
		}
		
	}
	
	@Test
	public void history()
	{
		assertTrue("history works",sandbox.getHistory().size() == 1);
	}
	
	@Test
	public void add()
	{
		sandbox.add(sandbox.getGameBoard().getNodes().get(0));
		assertTrue("Adding works",sandbox.getGameBoard().getNodes().get(0).getNumber() == 1);
		sandbox.add(sandbox.getGameBoard().getNodes().get(0));
		assertTrue("Adding works",sandbox.getGameBoard().getNodes().get(0).getNumber() == 2);
		
	}
	
	@Test
	public void log()
	{
		sandbox.add(sandbox.getGameBoard().getNodes().get(0));
		sandbox.add(sandbox.getGameBoard().getNodes().get(1));
		assertEquals("logging works", sandbox.getHistory().size(),3);
	}
	
	@Test
	public void undo()
	{
		sandbox.add(sandbox.getGameBoard().getNodes().get(3));
		sandbox.undo();
		assertEquals("undo can remove history",1,sandbox.getHistory().size());
		assertEquals("undo works",0,sandbox.getGameBoard().getNodes().get(3).getNumber());
	}
	@Test
	public void attack()
	{
		sandbox.add(sandbox.getGameBoard().getNodes().get(0));
		sandbox.add(sandbox.getGameBoard().getNodes().get(0));
		sandbox.getGameBoard().getNodes().get(0).setOwner(1);
		sandbox.add(sandbox.getGameBoard().getNodes().get(1));
		sandbox.add(sandbox.getGameBoard().getNodes().get(1));
		sandbox.getGameBoard().getNodes().get(1).setOwner(2);
		
		sandbox.attack(sandbox.getGameBoard().getNodes().get(0), sandbox.getGameBoard().getNodes().get(1));
		assertTrue("sandbox attack",sandbox.getGameBoard().getNodes().get(0).getNumber() != 2);
	}
	@Test
	public void hasPlayer()
	{
		sandbox.getGameBoard().getNodes().get(0).setOwner(9);
		assertTrue("has player says yes",sandbox.hasPlayer(9));
		assertTrue("has player says no",!sandbox.hasPlayer(-1));
	}
	@Test
	public void getNodesOfPlayer()
	{
		List<Node> nodes = sandbox.getNodesOfPlayer(1);
		assertEquals("get nodes of player size",2,nodes.size());
	}
	@Test
	public void hasWinner()
	{
		assertFalse("no winners ",sandbox.hasWinner());
		for(Node n : sandbox.getGameBoard().getNodes())
		{
			n.setOwner(1);
		}
		assertTrue("has winner",sandbox.hasWinner());
	}
	@Test
	public void getWinner()
	{
		assertTrue("no winners",sandbox.getWinner() == 0);
		for(Node n : sandbox.getGameBoard().getNodes())
		{
			n.setOwner(2);
		}
		assertTrue("has winner",sandbox.getWinner() == 2);	
	}
	@Test
	public void getBoundaryNodes()
	{
		for(Node n : sandbox.getGameBoard().getNodes())
		{
			n.setOwner(1);
		}
		for(int i = 0; i < 3; i++)
			assertEquals("no boundary",0,sandbox.getBoundaryNodesOfPlayer(i).size());
		for(int i = 0; i < 3; i++)
			sandbox.getGameBoard().getNodes().get(i).setOwner(i % 2 + 1);
		assertEquals("has boundary",2,sandbox.getBoundaryNodesOfPlayer(1).size());
	}
	
}
