import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
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
import prefuse.action.layout.graph.BalloonTreeLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.FruchtermanReingoldLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.io.DataIOException;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;


public class mainClass {
	static int triadcount=0;
	static Visualization vis = new Visualization();
	
	public static void main(String args []) throws IOException
	{
		//initializing the array to store the data of random graphs
		int[] rarr=new int[300];
		for(int i=0;i<100;i++)
		{
			rarr[i]=0;
		}
		
		//Initializing the buffer reader for writing in text file
		FileWriter fstream = new FileWriter("out.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		
		Node node=null;
		gmlReader reader = new gmlReader();
		Graph graph = new Graph();
		
		try 
		{
			// reading the graph from the gml file
			graph = reader.readGraph("polblogs.gml");
		} 
		catch (DataIOException e) 
		{
			e.printStackTrace();
		}
		
		//generate random graphs from that graph
		for(int i=0;i<500;i++)
		{
			System.out.println("i= " +i);
			graph=reader.randomGraphGen(graph,rarr);
			graph=triadCount(graph,rarr);
		}
		
		//writing the output to a text file to plot the graph
		for(int i=150;i<200;i++)
		{
			out.write("" +rarr[i] +",");
		}
		out.close();
	
		//counting th triad in a given graph
		//graph=triadCount(graph,rarr);
	
		// initializing the color actions for visualization
		int[] palette = {ColorLib.rgb(200, 0, 0), ColorLib.rgb(0,0, 200),ColorLib.rgb(0,200, 0)}; 
		DataColorAction fill = new DataColorAction("graph.nodes", "value",Constants.NOMINAL,VisualItem.FILLCOLOR,palette);
		ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
		ColorAction disappear_edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.rgb(255,255,255));       
		
		//adding this list to the action lists
		ActionList color_node = new ActionList();
		color_node.add(fill);
		ActionList color_edge = new ActionList();
		color_edge.add(edges);
		ActionList disappear_edge = new ActionList();
		disappear_edge.add(disappear_edges);
		
		//layout actions
		ActionList layout_FDL = new ActionList(50000);
		layout_FDL.add(new ForceDirectedLayout("graph",false,false));   	
		ActionList layout_RA = new ActionList(Activity.INFINITY);
		layout_RA.add(new RepaintAction()); 
		
		// adding graph to the visualization
		vis.add("graph", graph);
		
		// Adding these list to the visualization
		vis.putAction("color_node", color_node);
		vis.putAction("color_edge", color_edge);
		vis.putAction("disappear_edge", disappear_edge);
		vis.putAction("layout_FDL", layout_FDL);
		vis.putAction("layout_RA", layout_RA);
		
		//reducing the graph by combining the clusters
		//graph=reducedGraphnew(graph);
		
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
		    	
		vis.run("color_node");
		vis.run("color_edge");
		vis.run("layout_FDL");
		vis.run("layout_RA");
		
	}
	
	public static Graph reducedGraphnew(Graph graph)
	{
		Iterator nbor_itr1=null;
		Iterator nbor_itr2=null;
		Iterator nodes_itr=null;
		Node node=null;
		Node tmp_node=null;
		Node nnode1=null,nnode2=null;
		int count=0,flag=0,limit=0;
		Random rand=new Random();
		int node_num=0,reduced_count=0;
		while(reduced_count!=400)
		{
			node_num=rand.nextInt(graph.getNodeCount());
			node=graph.getNode(node_num);
			System.out.println("ch0 node " +node.getRow());
			nbor_itr1=node.neighbors();
			while(nbor_itr1.hasNext())
			{	
				nnode1=(Node) nbor_itr1.next();
				System.out.println("ch1 nnode1 " +nnode1.getRow());
				nbor_itr2=node.neighbors();
				while(nbor_itr2.hasNext())
				{
					nnode2=(Node) nbor_itr2.next();
					System.out.println("ch2 nnode2 " +nnode2.getRow());
					if(node.getString("value").equals(nnode1.getString("value")))
					{
						if(nnode2.getString("value").equals(nnode1.getString("value")))
						{
					if(((graph.getEdge(nnode1, nnode2))!=null) || ((graph.getEdge(nnode2, nnode1))!=null))
					{System.out.println("ch3");
						nodes_itr=graph.neighbors(nnode1);
						while(nodes_itr.hasNext())
						{
							tmp_node=(Node) nodes_itr.next();
							System.out.println("ch4 tmp_node " +tmp_node.getRow());
							if(tmp_node.getRow()!=node.getRow())
							graph.addEdge(node,tmp_node);
							
						}
						nodes_itr=graph.neighbors(nnode2);
						while(nodes_itr.hasNext())
						{
							tmp_node=(Node) nodes_itr.next();
							System.out.println("ch5 tmp_node " +tmp_node.getRow());
							if(tmp_node.getRow()!=node.getRow())
							graph.addEdge(node,tmp_node);
							
						}
						System.out.println("r " +node.getRow() +" " +nnode1.getRow() + " " + nnode2.getRow());
						System.out.println("reduced " +reduced_count);
						reduced_count++;
						limit=reduced_count;
						flag=1;
						deleteNode(graph, nnode2);
						deleteNode(graph, nnode1);
						break;
					}
						}
					}
					
				}
				if(flag==1)
				{
					count=0;
					break;
				}
			}
			limit++;
			if(limit-reduced_count>40) 
				{
					System.out.println("limit reduced= " +reduced_count);
					break;
				}
		}
		return graph;
	}
	public static Graph triadCount(Graph graph,int[] rarr)
	{
		Iterator nbor_itr1=null;
		Iterator nbor_itr2=null;
		Iterator nodes_itr=null;
		nodes_itr=graph.nodes();
		Node node=null;
		Node nnode1=null,nnode2=null;
		float clust_coeff=0;
		int node_triad=0,num_neigh=0;
		int count=0;
		int leave = 0,samenode_triad=0,twodiff_triad=0;
		while(nodes_itr.hasNext())
		{	
			node=(Node) nodes_itr.next();
			nbor_itr1=node.neighbors();
			while(nbor_itr1.hasNext())
			{	num_neigh++;
				nnode1=(Node) nbor_itr1.next();
				nbor_itr2=node.neighbors();
				while(nbor_itr2.hasNext())
				{
					nnode2=(Node) nbor_itr2.next();
					if(((graph.getEdge(nnode1, nnode2))!=null) || ((graph.getEdge(nnode2, nnode1))!=null))
					{
						node_triad++;
						triadcount++;
						if(node.getString("value").equals(nnode1.getString("value")))
						{
							if(nnode2.getString("value").equals(nnode1.getString("value")))
							{
								samenode_triad++;
							}
						}
						if(node.getString("value").equals(nnode1.getString("value")) || node.getString("value").equals(nnode2.getString("value")) || nnode1.getString("value").equals(nnode2.getString("value")))
						{
							twodiff_triad++;
						}
					}
				}
			}
			node_triad=node_triad/2;
			
			if(num_neigh>1)
			{
				clust_coeff=(((float)node_triad*2)/(num_neigh*(num_neigh-1)))+clust_coeff;
			}
			else leave++;
			node_triad=0;num_neigh=0;
		}
		clust_coeff=clust_coeff/(graph.getNodeCount()-leave);
		System.out.println("clust coeff= " +clust_coeff);
		clust_coeff=clust_coeff*10000;
		rarr[(int) clust_coeff]++;
		System.out.println("triadcount= " +triadcount/6 +"samenode_triad= " +samenode_triad/6 +"twodiff_triad= " +twodiff_triad/6);
		triadcount=triadcount/6;
		return graph;
	}
public static Graph reduceGraph(Graph graph)
{
		Iterator nbor_itr1=null;
		Iterator nbor_itr2=null;
		Iterator nodes_itr=null;
		Iterator nbor_itr=null;
		Iterator edge_itr=null;
		nodes_itr=graph.nodes();
		Node node=null;
		Node nnode1=null,nnode2=null,newNode=null,nbor_node=null;
		int triadcount=0;
		int count=0;
		while(nodes_itr.hasNext())
		{
			node=(Node) nodes_itr.next();
			nbor_itr1=node.neighbors();
			while(nbor_itr1.hasNext())
			{
				nnode1=(Node) nbor_itr1.next();
				nbor_itr2=node.neighbors();
				while(nbor_itr2.hasNext())
				{
					nnode2=(Node) nbor_itr2.next();
					if(node.getString("value").equals(nnode1.getString("value")))
					{
						if(nnode2.getString("value").equals(nnode1.getString("value")))
						{
					if((node.getRow()==nnode1.getRow()) || (node.getRow()==nnode2.getRow()) || (nnode1.getRow()==nnode2.getRow())) break;
					if(((graph.getEdge(nnode1, nnode2))!=null) || ((graph.getEdge(nnode2, nnode1))!=null))
					{	System.out.println("node= " +node.getRow() +"nnode1= " +nnode1.getRow() +"nnode2= " + nnode2.getRow());
						newNode=graph.addNode();
						newNode.setInt("id",node.getInt("id"));
						newNode.setString("name",node.getString("name"));
						newNode.setString("value", node.getString("value"));
						nbor_itr=node.neighbors();
						while(nbor_itr.hasNext())
						{
							nbor_node=(Node) nbor_itr.next();
							graph.addEdge(newNode, nbor_node);	
						}
						nbor_itr=nnode1.neighbors();
						while(nbor_itr.hasNext())
						{
							nbor_node=(Node) nbor_itr.next();
							graph.addEdge(newNode, nbor_node);	
						}
						nbor_itr=nnode2.neighbors();
						while(nbor_itr.hasNext())
						{
							nbor_node=(Node) nbor_itr.next();
							graph.addEdge(newNode, nbor_node);	
						}
						
						/*graph.removeNode(node);
						graph.removeNode(nnode1);
						graph.removeNode(nnode2);*/
						
						deleteNode(graph,node);
						deleteNode(graph,nnode1);
						deleteNode(graph,nnode2);
						return graph;
					}
						}
					}
				}
			}
			
		}
		return graph;
	}
public static Graph reducedGraph(Graph graph)
{
	int count=0;
	int check=1;
	//graph= reduceGraph(graph);
	
	
	while(count!=300 )
	{System.out.println("count " +count);
		graph=reduceGraph(graph);
		count++;
	}
	return graph;
}
	public static void deleteNode(Graph graph,Node node)
	{
		vis.getVisualItem("graph", node).setVisible(false);
		Iterator edge_itr=node.edges();
		Edge edge=null;
		while(edge_itr.hasNext())
		{
			edge=(Edge) edge_itr.next();
			graph.removeEdge(edge);
			edge_itr=node.edges();
		}
	}

}
