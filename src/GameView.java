import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.sound.midi.ControllerEventListener;
import javax.swing.JComponent;

import org.apache.commons.collections15.Transformer;

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
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
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
					if(arg0.getMaxNumber() == 8)
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
	
//	public GameView(Graph<Node,Integer> graph,Dimension preferredSize)
//	{
//		super(new ISOMLayout<Node,Integer>(graph),preferredSize);
//		this.getRenderContext().setVertexFillPaintTransformer(paintTransformer);
//		this.getRenderContext().setVertexLabelTransformer(labelTransformer);
//	}

	public GameView(Graph<Node,Integer> graph)
	{
		super(new SpringLayout<Node,Integer>(graph));
		
		this.getRenderContext().setVertexFillPaintTransformer(paintTransformer);
		this.getRenderContext().setVertexLabelTransformer(labelTransformer);
		this.getRenderContext().setVertexStrokeTransformer(strokeTransformer);
		this.getRenderContext().setVertexDrawPaintTransformer(drawPaintTransformer);
		
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
