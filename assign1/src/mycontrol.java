import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;


public class mycontrol extends ControlAdapter implements Control {

	public mycontrol() {
		// TODO Auto-generated constructor stub
	}
	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		if(item instanceof NodeItem)
		{
			String name = ((String) item.get("name"));
			int id = (Integer) item.get("id");
			
			JPopupMenu jpub = new JPopupMenu();
			jpub.add(name);
			jpub.add("id: " + id);
			jpub.show(e.getComponent(),(int) item.getX(),
                            (int) item.getY());
		}
	}

}
