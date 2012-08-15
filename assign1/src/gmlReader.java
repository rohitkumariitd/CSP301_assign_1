import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.swing.JFrame;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.RandomLayout;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.io.AbstractGraphReader;
import prefuse.data.io.DataIOException;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;


public class gmlReader extends AbstractGraphReader {

	public gmlReader() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Graph readGraph(InputStream arg0) throws DataIOException {
		// TODO Auto-generated method stub
		try {
			System.out.println("check" + arg0.read());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Graph graph = new Graph();
		int numNodes = 20;
		    	
		for (int i = 0; i < numNodes; i++)
			graph.addNode();
		    	
		// Create random connections
		Random rand = new Random();	
		for(int i = 0; i < numNodes; i++)
		{
		  int first = rand.nextInt(numNodes);
		  int second = rand.nextInt(numNodes);
		  graph.addEdge(first, second);
		}
	
		ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(0, 200, 0));
		ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
		       
		ActionList color = new ActionList();
		color.add(fill);
		color.add(edges);
		       
		ActionList layout = new ActionList();   	
		layout.add(new RandomLayout("graph"));   	
		layout.add(new RepaintAction()); 
		
		Visualization vis = new Visualization();
		vis.add("graph", graph);
		// once actions have been created (see above)
		// they are added to the Visualization.
		vis.putAction("color", color);
		vis.putAction("layout", layout);
		
		ShapeRenderer r = new ShapeRenderer();
		vis.setRendererFactory(new DefaultRendererFactory(r));
		
		Display d = new Display(vis);
		d.setSize(720, 500); 
		
		d.addControlListener(new DragControl());
		d.addControlListener(new PanControl());
		d.addControlListener(new ZoomControl());
		           	
		JFrame frame = new JFrame("prefuse example");   	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    	
		frame.add(d);    	
		frame.pack();              	
		frame.setVisible(true);
		    	
		vis.run("color");
		vis.run("layout");
		
		return null;
	}
}