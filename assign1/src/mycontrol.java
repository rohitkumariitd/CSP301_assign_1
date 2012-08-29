import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JPopupMenu;

import prefuse.Visualization;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;


public class mycontrol extends ControlAdapter implements Control {
	int in=0;
	Visualization vis=null;
	public mycontrol() {
		// TODO Auto-generated constructor stub
	}
	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		if(item instanceof NodeItem)
		{
			String name = ((String) item.get("name"));
			String value = ((String) item.get("value"));
			int id = (Integer) item.getRow();
			
			JPopupMenu jpub = new JPopupMenu();
			jpub.add(name);
			jpub.add(value);
			jpub.add("id: " + id);
			jpub.show(e.getComponent(),(int) item.getX(),
                            (int) item.getY());
		}
	}
	public void itemEntered(VisualItem item, MouseEvent e)
	{
		if(item instanceof NodeItem){
			vis=item.getVisualization();
			Iterator itr=((Node) item).neighbors();
			Node node=null;
			while(itr.hasNext())
			{
				node=(Node) itr.next();
				//vis.getVisualItem("graph",(Tuple) node ).setFillColor(ColorLib.rgb(255, 255, 3));
				vis.getVisualItem("graph",(Tuple) node ).setSize(2);
			}
	
		}
	}
	public void itemExited(VisualItem item, MouseEvent e)
	{
		if(item instanceof NodeItem){
			vis=item.getVisualization();
			vis.run("color_node");
			Iterator itr=((Node) item).neighbors();
			Node node=null;
			while(itr.hasNext())
			{
				node=(Node) itr.next();
				//vis.getVisualItem("graph",(Tuple) node ).setFillColor(ColorLib.rgb(255, 255, 3));
				vis.getVisualItem("graph",(Tuple) node ).setSize(1);
			}
			}
	}
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode()==65)
		{
			if(in==0)
				{
					vis.run("disappear_edge");
					in=1;
				}
			
		}
	}
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode()==65)
		{
			vis.run("color_edge");
			in=0;			
		}
	}

}
