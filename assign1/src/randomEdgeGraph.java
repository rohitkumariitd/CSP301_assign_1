import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.io.AbstractGraphReader;
import prefuse.data.io.DataIOException;

/*
public class randomEdgeGraph extends AbstractGraphReader {

	public randomEdgeGraph() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Graph readGraph(InputStream arg0) throws DataIOException {
		// TODO Auto-generated method stub
		return null;
	}

}
*/


public class randomEdgeGraph extends AbstractGraphReader {

	@Override
	public Graph readGraph(InputStream arg0) throws DataIOException {
		// TODO Auto-generated method stub
		Graph graph=null; //////// initialized according to the value of directed 
		Node node=null;
		int i,source=0,target=0, numnodes=0;
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
								numnodes++;
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
		
		Random rand = new Random();	
		for(int i1 = 0; i1 < numnodes; i1++)
		{
		  int first = rand.nextInt(numnodes);
		  int second = rand.nextInt(numnodes-1);
		  graph.addEdge(first, second);
		}
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