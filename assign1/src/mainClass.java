/*import prefuse.data.Graph;
import prefuse.data.io.DataIOException;


public class mainClass {
	
	public static void main(String args [])
	{
		gmlReader reader = new gmlReader();
		try {
			Graph graph = new Graph();
			graph = reader.readGraph("xyz.gml");
		} catch (DataIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
*/

import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.RandomLayout;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;


public class mainClass {
	
	public static void main(String args [])
	{
		gmlReader reader = new gmlReader();
		Graph graph = new Graph();
		try {
			
			graph = reader.readGraph("polbooks.gml");
		} catch (DataIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		int[] palette = {ColorLib.rgb(200, 0, 0), ColorLib.rgb(0,0, 200),ColorLib.rgb(0,200, 0)}; 
		DataColorAction fill = new DataColorAction("graph.nodes", "value",Constants.NOMINAL,VisualItem.FILLCOLOR,palette);
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
		d.addControlListener(new mycontrol());
		           	
		JFrame frame = new JFrame("prefuse example");   	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    	
		frame.add(d);    	
		frame.pack();              	
		frame.setVisible(true);
		    	
		vis.run("color");
		vis.run("layout");

	}

}
