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
	private static Point2D UP = new Point2D.Float(0,-30);
	private static Point2D DOWN = new Point2D.Float(0,30);
	private static Point2D LEFT = new Point2D.Float(-30,0);
	private static Point2D RIGHT = new Point2D.Float(30,0);
	private static Point2D UP_LEFT = new Point2D.Float(-15,-15);
	private static Point2D UP_RIGHT = new Point2D.Float(15,-15);
	private static Point2D DOWN_LEFT = new Point2D.Float(-15, 15);
	private static Point2D DOWN_RIGHT = new Point2D.Float(15, 15);
	
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
			ArrayList<Node> nodes = new ArrayList<Node>(graph.getVertices());
			Collections.sort(nodes,new Comparator<Node>()
					{
						@Override
						public int compare(Node o1, Node o2) {
							return Integer.compare(o1.id,o2.id);
						}						
					});
			Point2D lastPoint = new Point2D.Float(100,100);
			int index = 0;
			
			//set the center point
			layout.setLocation(nodes.get(index++),lastPoint);
			layout.lock(nodes.get(index - 1), true);
			lastPoint = this.getNewPoint(lastPoint, RIGHT);
			
			ArrayList<Point2D> dirs = new ArrayList<Point2D>();
			
			dirs.add(UP_RIGHT);
			dirs.add(UP_LEFT);
			dirs.add(LEFT);
			dirs.add(DOWN_LEFT);
			dirs.add(DOWN_RIGHT);
			dirs.add(RIGHT);
			
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
			//TODO: the arraylist of length of rings need not to be constructed
			// iterate each rings
			for(int n = 1; n <= ringLength.size(); n++)
			{
				int dirIndex = 0;
				int count = 1;
				// iterate each nodes in a ring
				try
				{
					for(int i = 1; i <= 6 * n; i++,index++)
					{
						layout.setLocation(nodes.get(index), lastPoint);
						layout.lock(nodes.get(index), true);
						// change side at the beginning
						// but this should be done after laying the current cell

						if(count == n)
						{
							count = 1;
							dirIndex++;
						}else count++;
						lastPoint = this.getNewPoint(lastPoint, dirIndex == 6? RIGHT: dirs.get(dirIndex));
					}
				}catch(IndexOutOfBoundsException e)
				{
					break;
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
	
	private void lockLayout()
	{
		final Layout<Node,Integer> layout = this.getGraphLayout();
		layout.getGraph().getVertices().stream().forEach((node) -> layout.lock(node, true));
	}


	public GameView(Graph<Node,Integer> graph)
	{
		super(new StaticLayout<Node,Integer>(graph));
		initializeLayout(graph);
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
				controller.selectNode(arg0);
			}

			@Override
			public void graphPressed(Node arg0, MouseEvent arg1) {
				
			}

			@Override
			public void graphReleased(Node arg0, MouseEvent arg1) {
				
			}
		
		});
	}

}
