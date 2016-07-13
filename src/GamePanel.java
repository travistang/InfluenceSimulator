import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JPanel;

/*************************************************************
 * -Provide interfaces for other classes to controlLayout
 * -Determine the position of each cell
 * -Draw connections between cells
 * 
 *************************************************************/
public class GamePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Cell> cells;
	private GameBoardViewController controller;
	private Coordinate highlightCoordinate;
	
	public void draw(Graphics graphics)
	{
		int dl = (int)Cell.CELL_DIMENSION.getWidth()/2;
		cells.stream().forEach((cell) ->
		{
			Pair<Integer,Integer> selfcoord = cell.getPosition();
			cell.getNode().getConnections().stream().forEach((neighbour) -> {
				Pair<Integer,Integer> coord = this.getCell(neighbour).getPosition();
				graphics.drawLine(coord.first + dl, coord.second + dl, selfcoord.first + dl, selfcoord.second + dl);
			});
		});
//		for(int w = 0; w < cells.size();w++)
//		{
//			Cell c = cells.get(w);
//			for(Node g : controller.getNode(c).getConnections())
//			{
//	
//				Cell cc = controller.getCell(g);
//				Pair<Integer,Integer> cood = cc.getPosition();
//				Pair<Integer,Integer> selfcood = c.getPosition();
//				if(cood == null || selfcood == null)
//				{
//					System.out.println("Problem with the cellCoordinateMap in GamePanel");
//					continue;
//				}
//				
//				graphics.drawLine(cood.first + dl, cood.second + dl, selfcood.first + dl, selfcood.second + dl);
//			}
//		}
		/**
		 * Highlighting
		 */
		if(highlightCoordinate != null)
		{
			graphics.setColor(Color.RED);
			graphics.drawRect(highlightCoordinate.x , highlightCoordinate.y, Cell.CELL_DIMENSION.width + 10, Cell.CELL_DIMENSION.height + 10);
			graphics.setColor(Color.BLACK);
		}
	}
	/**
	 * Draw all cells according to their infos and coordinate
	 */
	public void layCells()
	{
		this.setLayout(null);
		Insets inset = this.getInsets();
		cells.stream().forEach((cell) ->
		{
			Coordinate co = cell.getCoordinate();
			this.add(cell);
			cell.setBounds(co.x + inset.left, co.y + inset.top,Cell.CELL_DIMENSION.width,Cell.CELL_DIMENSION.height);
			cell.setPreferredSize(Cell.CELL_DIMENSION);
			cell.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					//TODO restore the following line to recover the previous feature
					//controller.selectCell(cell);
				}
			});
			cell.setVisible(true);
		});

		this.revalidate();
	}
	public Cell getCell(Node n)
	{
		return cells.stream().filter((cell) -> n.equals(cell.getNode())).collect(Collectors.toList()).get(0);
	}
	public void highlight(Coordinate c)
	{
		highlightCoordinate = c;
		// perhaps not necessary
		revalidate();
		repaint();
	}
	public void highlight(Cell c)
	{
		highlight(new Coordinate(c.getPosition()));
	}
	public void unhighlight()
	{
		highlightCoordinate = null;
		revalidate();
		repaint();
	}
	public ArrayList<Cell> getCells()
	{
		return cells;
	}
	
	public void setController(GameBoardViewController controller)
	{
		this.controller = controller;
		for(int i = 0; i < cells.size(); i++)
		{
			if(controller.isLargeCell(cells.get(i)))
			{
				cells.get(i).setLarge(true);
			}
		}

		Cell.setController(controller);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);

	}

	public GamePanel(ArrayList<Node> nodes)
	{
		cells = new ArrayList<Cell>();
		for(Node n: nodes)
		{
			cells.add(new Cell(n));
		}
		controller = null;
	}
}
