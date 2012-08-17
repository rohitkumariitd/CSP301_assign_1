/*import java.io.IOException;
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
}*/


import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

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
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
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
		Graph graph=null; //////// initialized according to the value of directed 
		Node node=null;
		int i,source=0,target=0;
		int id=1;
		byte[] b=new  byte[15];
		int intread=0;
		String s;
	
		try {
			intread=gotoNextline(arg0);
			if(intread!=103)
			{
				intread=gotoNextline(arg0);
			}
			///////end of comment
			///// 103=g  starting of graph
			if(intread==103) 
			{
				intread=gotoNextline(arg0);
				///go to the next line till directed
				if(intread!=100)
				{
					intread=gotoNextline(arg0);
				}
				///100=d  directed variable
				if(intread==100)
				{
					arg0.skip(8);
					intread=arg0.read();
					if(intread==48)
					{
						graph=new Graph(); 						///////undirected  graph
						graph.addColumn("id",int.class);
						graph.addColumn("name",String.class);
						graph.addColumn("value",String.class);
						graph.addColumn("source",String.class);
					}
					else										//// directed graph
					{
						graph=new Graph(true);
						graph.addColumn("id",int.class);
						graph.addColumn("name",String.class);
						graph.addColumn("value",String.class);
						graph.addColumn("source",String.class);
					}
				}
			}
			else											//// if graph is not found
			{
				System.out.println("Problem");
				System.exit(0);
			}
			// go to next line
			intread=gotoNextline(arg0);
			while(intread!=-1)
			{
			//////either node or edge  110=node  
				if(intread==110)
				{
					node=graph.addNode();
					intread=gotoNextline(arg0);
					// read all the variables
					while(true)
						{
							if(intread==91)							// 91 = "[" go to next line
							{
								intread=gotoNextline(arg0);
							}
							else if(intread==105)					// 105= id variable
							{
								arg0.skip(2);
								id=0;
								while((intread=arg0.read())!=10)
								{
									id=id*10+intread-48;
								}
								node.setInt("id",id);
								System.out.println("idd " + id);
								while((intread=arg0.read())==32){}
							}
							
							else if(intread==108)			// label
							{
								i=0;
								arg0.skip(6);
								char[] chararray=new char[100];
								while((intread=arg0.read())!=34)
								{
									chararray[i]=(char) intread;
									i++;
								}
								s=new String(chararray);
								System.out.println("string read= " + s);
								node.setString("name",s);
								intread=gotoNextline(arg0);
							}
							else if(intread==118)			//118=v  value
							{
								arg0.skip(5);
								if((intread=arg0.read())==34)
								{
									intread=arg0.read();
									char[] tempc=new char[1];
									tempc[0]=(char) intread;
									s=new String(tempc);
									node.setString("value",s);
									System.out.println("  value read= " + s);
								}
								else
								{
									char[] tempc=new char[1];
									tempc[0]=(char) intread;
									s=new String(tempc);
									node.setString("value",s);
									//System.out.println("  value read= " + s);
								}
								intread=gotoNextline(arg0);
							}
							else if(intread==115)							// 115=s source
							{
								arg0.skip(7);
								i=0;
								char[] chararray=new char[100];
								while((intread=arg0.read())!=34)
								{
									chararray[i]=(char) intread;
									i++;
								}
								s=new String(chararray);
								System.out.println("source read= " + s);
								node.setString("source",s);
								intread=gotoNextline(arg0);
							}
							else if(intread==93)							// 93 = "]" go to next line
							{
								intread=gotoNextline(arg0);
							}
							else
							{
								break;
							}
						}
				}
				else if(intread==101)						// edge 
				{
					intread=gotoNextline(arg0);
					while(true)
					{
						if(intread==91)							// 91 = "[" go to next line
						{
							intread=gotoNextline(arg0);
						}
						else if(intread==115)					//115 source
						{
							arg0.skip(6);
							source=0;
							target=0;
							while((intread=arg0.read())!=10)
							{
								source=source*10+intread-48;
							}
							System.out.println("source=" +source);
							while((intread=arg0.read())==32){}
							arg0.skip(6);
							while((intread=arg0.read())!=10)
							{
								target=target*10+intread-48;
							}
							if(graph.getNodeCount()==id)
							graph.addEdge((source-1), (target-1));
							else
								graph.addEdge(source, target);
							while((intread=arg0.read())==32){}
							System.out.println("s= " +source +" t= " +target);
							//break;
						}
						else if(intread==93)							// 91 = "]" go to next line
						{
							intread=gotoNextline(arg0);
						}
						else
						{
							break;
						}
					}
				}
				else
				{
					System.out.println("There is some problem in file");
					System.exit(0);
				}
			}
			
			

			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		Node node=null;
		int numNodes = 20;
		 Class type;
		graph.addColumn("name",String.class);
		graph.addColumn("value",String.class);
		for (int i = 0; i < numNodes; i++)
			{
				node=graph.addNode();
				//System.out.println(node.canSetString("name") +" " + node.getColumnCount() +" "+ node.isValid());
				node.setString("name", "abcd");
				//node.set(1,"l");
			}
		    	
		// Create random connections
		Random rand = new Random();	
		for(int i = 0; i < numNodes; i++)
		{
		  int first = rand.nextInt(numNodes);
		  int second = rand.nextInt(numNodes);
		  graph.addEdge(first, second);
		}
	*/
		/*int[] palette = {ColorLib.rgb(200, 0, 0), ColorLib.rgb(0,0, 200),ColorLib.rgb(0,200, 0)}; 
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
		*/
		return graph;
	}


	public static int gotoNextline(InputStream arg0)
	{
		int intread=0;
		try {
			while((intread=arg0.read())!=10){}
			while((intread=arg0.read())==32){}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return intread;
		
	}
}