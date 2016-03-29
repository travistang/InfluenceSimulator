import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

/*************************************************************
 * -Provide interfaces for other classes to controlLayout
 * -Determine the position of each cell
 * -Draw connections between cells
 * 
 *************************************************************/
public class GamePanel extends JPanel {

	private Cell[] cells;
	private GameBoardViewController controller;
	private HashMap<Cell,Coordinate> cellCoordinateMap;
	private Coordinate highlightCoordinate;
	
	/**
	 * Draw all cells according to their infos and coordinate
	 */
	public void layCells(Graphics graphics)
	{
		this.removeAll();
		//TODO: remove me
		int z = 0;
		for(int w = 0; w < cells.length;w++)
		{
			Cell c = cells[w];
			Coordinate co = cellCoordinateMap.get(c);
			if(co == null)
			{
				System.out.println("Problem with the cellCoordinateMap in GamePanel");
				return;
			}
			this.add(c);
			c.setLocation(co.x, co.y);
			c.setPreferredSize(Cell.CELL_DIMENSION);
			c.setVisible(true);
			int dl = (int)Cell.CELL_DIMENSION.getWidth()/2;
			for(Node g : controller.getNode(c).getConnections())
			{

				Cell cc = controller.getCell(g);
				Coordinate cood = cellCoordinateMap.get(cc);
				Coordinate selfcood = cellCoordinateMap.get(c);
				if(cood == null || selfcood == null)
				{
					System.out.println("Problem with the cellCoordinateMap in GamePanel");
					continue;
				}
				
				graphics.drawLine(cood.x + dl, cood.y + dl, selfcood.x + dl, selfcood.y + dl);
			}
			
		}
		/**
		 * Highlighting
		 */
		if(highlightCoordinate != null)
		{
			graphics.drawRect(highlightCoordinate.x, highlightCoordinate.y, Cell.CELL_DIMENSION.width, Cell.CELL_DIMENSION.height);
		}
		this.revalidate();
	}
	public void hightlight(Coordinate c)
	{
		highlightCoordinate = c;
	}
	public void unhighlight()
	{
		highlightCoordinate = null;
	}
	public Cell[] getCells()
	{
		return cells;
	}
	
	public void setController(GameBoardViewController controller)
	{
		this.controller = controller;
		for(int i = 0; i < cells.length; i++)
		{
			if(controller.isLargeCell(i))
			{
				cells[i].setLarge(true);
			}
		}
	}
	
	public void setCellCoordinateMap(HashMap<Cell,Coordinate> map)
	{
		cellCoordinateMap = map;
	}
	public HashMap<Cell,Coordinate> getCellCoordinateMap()
	{
		return cellCoordinateMap;
	}
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		layCells(g);
	}
	public GamePanel(int cellnum) {
		cells = new Cell[cellnum];
		highlightCoordinate = null;
		for(int i = 0 ; i < cells.length; i++)
		{
			cells[i] = new Cell();
		}
		cellCoordinateMap = null;
		controller = null;
	}

}
