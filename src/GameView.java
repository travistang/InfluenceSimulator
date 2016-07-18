import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.sound.midi.ControllerEventListener;
import javax.swing.JComponent;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;


public class GameView extends VisualizationViewer<Node,Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -139391871706628550L;

	private GameBoardViewController controller;
	private DefaultModalGraphMouse<Node,Integer> mouse = new DefaultModalGraphMouse<Node,Integer>();
	private static Transformer<Node,Paint> paintTransformer = new Transformer<Node,Paint>()
			{
				@Override
				public Paint transform(Node arg0) {
					switch(arg0.getOwner())
					{
						case 0:
							return Color.GRAY;
						case 1:
							return Color.BLUE;
						case 2:
							return Color.GREEN;
						case 3:
							return Color.MAGENTA;
						case 4:
							return Color.RED;
						case 5:
							return Color.YELLOW;
						default:
							throw new IllegalArgumentException("The number of owner should be in [0,5]");
					}
				}
			};
	private static Transformer<Node,String> labelTransformer = new Transformer<Node,String>()
			{
				@Override
				public String transform(Node arg0) 
				{
					return String.valueOf(arg0.getNumber()) + "/" + String.valueOf(arg0.getMaxNumber());
				}			
			};
	private static Transformer<Node,Stroke> strokeTransformer = new Transformer<Node,Stroke>()
			{
				@Override
				public Stroke transform(Node arg0)
				{
					if(arg0.getMaxNumber() == 12)
						return new BasicStroke(3);
					return new BasicStroke(1);
				}
			};
	private Transformer<Node, Paint> drawPaintTransformer = new Transformer<Node,Paint>()
			{

				@Override
				public Paint transform(Node arg0) {
//					if(controller.getSelectedNode() == null) return Color.BLACK;
//					if(controller.getSelectedNode().equals(arg0))
//						return Color.ORANGE;
					return Color.BLACK;
				}
				
			};

	public GameBoardViewController getController()
	{
		return controller;
	}

	public void setController(GameBoardViewController controller)
	{
		this.controller = controller;
	}
	
	private void initializeLayout(Graph<Node,Integer> graph)
	{
		Layout<Node,Integer> layout = new StaticLayout<Node,Integer>(graph);
		// ...and any other initialization here
		ArrayList<Point2D> usedCoordinates = new ArrayList<Point2D>();
		Point2D initPoint = new Point2D.Float(100,100);
		ArrayList<Node> freeNodes = new ArrayList<Node>(graph.getVertices());
		// sort the freeNodesList so that the nodes with highest degree will be laid on the graph first.
		Collections.sort(freeNodes,new Comparator<Node>()
				{
					public int compare(Node a, Node b)
					{
						return -Integer.compare(a.getDegree(), b.getDegree());
					}
				});
			/**
			 * Suppose a node is at (x,y)
			 * Then the neighbors are at( enumerating in clockwise direction, starting from the top):
			 * 	(x - 30,y)
			 *  (x - 15,y + 15)
			 *  (x + 15, y + 15)
			 *  (x + 30, y)
			 *  (x + 15, y - 15)
			 *  (x - 15, y - 15)
			 *  Algorithm:
			 *  1. Pick a node n with highest degree (not necessarily unique)
			 *  2. Pick an initial position p
			 *  3. put n on p
			 *  4. mark n as visited
			 *  5. for each adjacent node nn of n that is also a free node:
			 *  	5.1 find a free adjacent points with respect to the position of n
			 *  	5.2 put nn to the position
			 *  	5.3 mark nn as visited
			 *  6. repeat step 5 with nn as n
			 */
			Node node = freeNodes.get(0);
			//TODO: remove this comment
//			layCellAndNeighbours(graph,node,initPoint,freeNodes,usedCoordinates,layout);
			ArrayList<Node> nodes = new ArrayList<Node>(graph.getVertices());
			Collections.sort(nodes,new Comparator<Node>()
					{
						@Override
						public int compare(Node o1, Node o2) {
							return Integer.compare(o1.id,o2.id);
						}						
					});
			/****************************************************************************************************
			 * new layout of algorithm
			 ****************************************************************************************************/
			{
			Point2D lastPoint = new Point2D.Float(100,100);
			int index = 0;
			ArrayList<Point2D> dirs = new ArrayList<Point2D>();
			dirs.add(new Point2D.Float(-15,15));
			dirs.add(new Point2D.Float(-15,-15));
			dirs.add(new Point2D.Float(0,-30));
			dirs.add(new Point2D.Float(15, -15));
			dirs.add(new Point2D.Float(15,15));
			dirs.add(new Point2D.Float(0,30));
			
			ArrayList<Integer> ringLength = new ArrayList<Integer>();
			if(nodes.size() < 6) ringLength.add(nodes.size() - 1);
			else ringLength.add(6);
			int ringsum = ringLength.get(0);
			while(ringsum < nodes.size())
			{
				int nextNum = 6 * (1 + ringLength.size()); 
				ringsum += nextNum;
				ringLength.add(nextNum);
			}
			
			for(int n = 1; n <= ringLength.size(); n++)
			{
				// first need to get right
				lastPoint = getNewPoint(lastPoint,new Point2D.Float(0,30));
				Point2D dif = dirs.get(0);
				// each ring starts at 3n(n + 1)
				int counter = 0,difIndex = 0; // counter counts the length of the edge of the ring; difIndex controls the direction of the point
				try
				{
					for(int offset = 0; offset < ringLength.get(n - 1); offset++,index++)
					{
							if(counter == n) // corner case
							{
								counter = 0;
								difIndex++;
							}else
							{
								counter++;
							}
							lastPoint = getNewPoint(lastPoint,dirs.get(difIndex));
							layout.setLocation(nodes.get(index),lastPoint);
	
					}
				}catch(IndexOutOfBoundsException e)
				{
					break;
				}
			}
			}
		this.setGraphLayout(layout);
	}
	
	private Point2D getNewPoint(Point2D current, Point2D dif)
	{
		Point2D res = new Point2D.Float();
		res.setLocation(current.getX() + dif.getX(), current.getY() + dif.getY());
		return res;
	}
	// for step 5
	private void layCellAndNeighbours(
			Graph<Node,Integer> graph,
			Node n, 
			Point2D curPoint,
			ArrayList<Node> freeNodes,
			ArrayList<Point2D> usedCoordinates,
			Layout<Node,Integer> layout)
	{
		ArrayList<Point2D> temp = new ArrayList<Point2D>();
		temp.add(new Point2D.Float((float)curPoint.getX() - 30,(float)curPoint.getY()));
		temp.add(new Point2D.Float((float)curPoint.getX() - 15,(float)curPoint.getY() + 15));
		temp.add(new Point2D.Float((float)curPoint.getX() + 15,(float)curPoint.getY() + 15));
		temp.add(new Point2D.Float((float)curPoint.getX() + 30,(float)curPoint.getY()));
		temp.add(new Point2D.Float((float)curPoint.getX() + 15,(float)curPoint.getY() - 15));
		temp.add(new Point2D.Float((float)curPoint.getX() - 15,(float)curPoint.getY() - 15));
		// get the rest of the available points
		// this list should have at least the same size of the free neighbors in the node
		final ArrayList<Point2D> adjacentPoints = (ArrayList<Point2D>)temp
						.stream()
						.filter((point) -> !usedCoordinates.contains(point))
						.collect(Collectors.toList());
		// then shuffle the adjacentPoints and hopefully the node
//		Collections.shuffle(adjacentPoints);
		List<Node> effectiveNeighbours = freeNodes.stream()
			.filter((neighbour) -> 
				graph.isNeighbor(neighbour, n) && freeNodes.contains(neighbour)).collect(Collectors.toList());
		// check if there are enough adjacent spaces for the the graph to put the neighbors of the current node
		// if not then there's a problem and an exception has to be raised
		if(adjacentPoints.size() < effectiveNeighbours.size())
			throw new IllegalArgumentException("Number of adjacent points of a given node is not enough for it's neighbours. "
					+ "The graph is probably invalid");
		try
		{
			int i = 0;
			// lay all neighbors first
			for(Node neighbour : effectiveNeighbours)
			{
				Point2D point = adjacentPoints.get(i++);
				layout.setLocation(neighbour,point);
				usedCoordinates.add(point);
				freeNodes.remove(neighbour);
			}
			// then for each neighbor, visit their unvisited neighbors
			for(i = 0; i < effectiveNeighbours.size();i++)
			{
				layCellAndNeighbours(graph,effectiveNeighbours.get(i),adjacentPoints.get(i),freeNodes,usedCoordinates,layout);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	private void lockLayout()
	{
		final Layout<Node,Integer> layout = this.getGraphLayout();
		layout.getGraph().getVertices().stream().forEach((node) -> layout.lock(node, true));
	}


	public GameView(Graph<Node,Integer> graph)
	{
		//TODO: switch back to static layout later
		super(new ISOMLayout<Node,Integer>(graph));
		this.getRenderContext().setVertexFillPaintTransformer(paintTransformer);
		this.getRenderContext().setVertexLabelTransformer(labelTransformer);
		this.getRenderContext().setVertexStrokeTransformer(strokeTransformer);
		this.getRenderContext().setVertexDrawPaintTransformer(drawPaintTransformer);
		this.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<>());
		mouse.setMode(Mode.TRANSFORMING);
		this.setGraphMouse(mouse);
		this.addGraphMouseListener(new GraphMouseListener<Node>()
		{
			@Override
			public void graphClicked(Node arg0, MouseEvent arg1) {
				lockLayout();
				controller.selectNode(arg0);
				revalidate();
				repaint();
			}

			@Override
			public void graphPressed(Node arg0, MouseEvent arg1) {
				
			}

			@Override
			public void graphReleased(Node arg0, MouseEvent arg1) {
				
			}
		
		});
//		initializeLayout(graph);
	}

}
