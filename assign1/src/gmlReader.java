import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
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
		int i,source=0,target=0,countsame =0, countedges=0;
		int id=1;
		byte[] b=new  byte[15];
		int intread=0;
		String s;
		float ratio;
		int edge_nl=0,edge_lc=0,edge_nc=0;
		int edge_0=0,edge_1=0;
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
					{System.out.println("undirested graph");
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
								intread=arg0.read();
								while((intread!=10) && (intread!=13))
								{
									id=id*10+intread-48;
									intread=arg0.read();
									if(intread==13) intread=arg0.read();
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
							intread=arg0.read();
							while(intread!=10)
							{
								source=source*10+intread-48;
								intread=arg0.read();
								if(intread==13) intread=arg0.read();
							}
							System.out.println("source=" +source);
							while((intread=arg0.read())==32){}
							arg0.skip(6);
							intread=arg0.read();
							while(intread!=10)
							{
								target=target*10+intread-48;
								intread=arg0.read();
								if(intread==13) intread=arg0.read();
							}
							if(graph.getNodeCount()==id)
							{
								graph.addEdge((source-1), (target-1));
								countedges++;	//// count the total number of edges
								
								if(graph.getNode(source-1).get("value").equals(graph.getNode(target-1).get("value")))
								{countsame++;}		////// count the no. of edges between the nodes with same value
							}
							else
							{
								graph.addEdge(source, target);
								countedges++;	//// count the total number of edges
								if(graph.getNode(source).get("value").equals("n"))
								{
									if(graph.getNode(target).get("value").equals("l")) edge_nl++;
									if(graph.getNode(target).get("value").equals("c")) edge_nc++;
								}
								if(graph.getNode(source).get("value").equals("l"))
								{
									if(graph.getNode(target).get("value").equals("n")) edge_nl++;
									if(graph.getNode(target).get("value").equals("c")) edge_lc++;
								}
								if(graph.getNode(source-1).get("value").equals("c"))
								{
									if(graph.getNode(target).get("value").equals("l")) edge_lc++;
									if(graph.getNode(target).get("value").equals("n")) edge_nc++;
								}
								if(graph.getNode(source).get("value").equals(graph.getNode(target).get("value")))
								{countsame++;}		////// count the no. of edges between the nodes with same value
							}
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
		ratio = (float)countsame/countedges;
		System.out.println("ratio= " +ratio);
		System.out.println("nl= " +edge_nl +"nc= " +edge_nc +"lc= " +edge_lc +"countedges= " +countedges +"countsame= " +countsame);
		return graph;
	}


	public Graph randomGraphGen(Graph graph,int[] rarr)
	{		  
		Graph randGraph= new Graph();
		Random rand = new Random();	
		int numnodes=graph.getNodeCount();
		int numedges=graph.getEdgeCount();
		int countsame =0;
		float ratio=0;
		randGraph=graph;
		randGraph.getEdgeTable().clear();		
		System.out.println(" coutn "+graph.getEdgeCount());
		for(int i1 = 0; i1 < numedges; i1++)
		{
		  int first = rand.nextInt(numnodes);
		  int second = rand.nextInt(numnodes);
		  if(first!=second)
		  graph.addEdge(first, second);
		  else i1--;
		  if(graph.getNode(first).get("value").equals(graph.getNode(second).get("value")))
			countsame++;	
		}
		ratio=(float)countsame/numedges;
		System.out.println(" randomratio= "+ratio);
		/*ratio=ratio*1000;
		ratio=ratio-320;
		if(ratio>0)
		rarr[(int) ratio]++;*/
		return randGraph;
	}
	public static int gotoNextline(InputStream arg0)
	{
		int intread=0;
		try {
			while((intread=arg0.read())!=10){if(intread==-1) break;}
			while((intread=arg0.read())==32){if(intread==-1) break;}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return intread;
		
	}
}