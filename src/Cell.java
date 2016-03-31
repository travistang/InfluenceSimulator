import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;


public class Cell extends JButton{
	//pre-load images
	private int owner;
	private int number;
	private static ArrayList<Icon> images;
	private static GameBoardViewController controller;
	public static final Dimension CELL_DIMENSION;
	private boolean isLarge;
	private final URL path = this.getClass().getResource("images/cell");
	static
	{
		controller = null;
		CELL_DIMENSION = new Dimension(20,20);
		images = new ArrayList<Icon>();
		
		try 
		{
			String[] colors = {"empty","blue","green","purple","red","yellow"};
			String ls = "-large";
			
			for(String color : colors)
			{
				if(!new File("images/cell-" + color + ".png").exists())
				{
					System.out.println("missing image file" + "images/cell-" + color + ".png");
				}
			    images.add(new ImageIcon("images/cell-" + color + ".png"));
			}
			for(String color : colors)
			{
				images.add(new ImageIcon("images/cell-" + color + ls + ".png"));
			}

		} 
		catch (Exception e) 
		{
		    e.printStackTrace();
		}
	}
	
	
	public void setAppearance(int owner, int number)
	{
		this.owner = owner;
		this.number = number;
		
		int index = 0;
		if(!isLarge)
			index = owner;
		else
			// skip all the regular cell image
			index = owner + 6;
		this.setOpaque(false);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setVerticalTextPosition(SwingConstants.CENTER);
		this.setHorizontalTextPosition(SwingConstants.CENTER);
		this.setIcon(Cell.images.get(index));
		this.setText(Integer.toString(number));
	}

	public static void setController(GameBoardViewController controller)
	{
		Cell.controller = controller;
	}

	public void setLarge(boolean isLarge)
	{
		this.isLarge = isLarge;
		//reload appearance
		reload();
	}
	public void reload()
	{
		this.setAppearance(this.owner, this.number);
	}
	
	Cell()
	{
		owner = number = 0;
		isLarge = false;
		controller = null;
		this.setFont(new Font("Arial", Font.PLAIN,8));
		this.setAppearance(0,0);
		this.setPreferredSize(CELL_DIMENSION);
	}
}
