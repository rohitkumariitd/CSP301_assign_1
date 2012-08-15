import prefuse.data.Graph;
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
