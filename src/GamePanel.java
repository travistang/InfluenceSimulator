import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
public class GamePanel extends JPanel{

	private Cell[] cells;
	private GameBoardViewController controller;
	private HashMap<Cell,Coordinate> cellCoordinateMap;
	private Coordinate highlightCoordinate;
	
	public void draw(Graphics graphics)
	{
		int dl = (int)Cell.CELL_DIMENSION.getWidth()/2;
		for(int w = 0; w < cells.length;w++)
		{
			Cell c = cells[w];
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
			c.setBounds(co.x + inset.left, co.y + inset.top,Cell.CELL_DIMENSION.width,Cell.CELL_DIMENSION.height);
			c.setPreferredSize(Cell.CELL_DIMENSION);
			c.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					controller.selectCell(c);
				}
			});
			c.setVisible(true);

			
		}
		this.revalidate();
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
		highlight(cellCoordinateMap.get(c));
	}
	public void unhighlight()
	{
		highlightCoordinate = null;
		revalidate();
		repaint();
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

		Cell.setController(controller);
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
		draw(g);

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
