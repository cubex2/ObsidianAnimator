package obsidianAnimator.gui.entitySetup;

import javax.swing.JOptionPane;

import net.minecraft.client.Minecraft;
import obsidianAPI.animation.AnimationParenting;
import obsidianAPI.animation.PartGroups;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.gui.frames.HomeFrame;
import obsidianAnimator.render.entity.ModelObj_Animator;

public class EntitySetupController 
{
	
	private PartGroups partGroups;
	private EntitySetupGui gui;
	private EntitySetupFrame frame;
	
	public EntitySetupController(String entityName)
	{
		gui = new EntitySetupGui(entityName, this);
		this.partGroups = gui.entityModel.partGroups;
		
		frame = new EntitySetupFrame(this);
	}
	
	public void display()
	{
		Minecraft.getMinecraft().displayGuiScreen(gui);
		frame.setVisible(true);
	}
	
	public void attemptParent(PartObj parent, PartObj child)
	{
		if(parent.getName().equals(child.getName()))
		{
			JOptionPane.showMessageDialog(frame, "Cannot parent a part to itself.", "Parenting issue", JOptionPane.ERROR_MESSAGE);
		} 
		else if (!AnimationParenting.areUnrelated(child, parent) || !AnimationParenting.areUnrelated(parent, child))
		{
			JOptionPane.showMessageDialog(frame, "Parts are already related.", "Parenting issue", JOptionPane.ERROR_MESSAGE);
		}
		else if(child.getParent() != null)
		{
			Object[] options = {"OK", "Remove parenting"};
			int n = JOptionPane.showOptionDialog(frame, child.getDisplayName() + " already has a parent.", "Parenting issue",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
			if(n == 1)
			{
				parent(null,child);
			}
		}
		else
			parent(parent, child);
	}

	private void parent(PartObj parent, PartObj child)
	{
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(frame, "Add bend?", "Bend",
											 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null, options, options[1]);
		boolean bend = n == 0;
		getEntityModel().setParent(child, parent, bend);
		frame.refreshParentingPanel();
	}
	
	public void close()
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
		frame.dispose();
		new HomeFrame().display();
	}
	
	public ModelObj_Animator getEntityModel()
	{
		return gui.entityModel;
	}
	
	public void setSelectedPart(Part part)
	{
		gui.selectedPart = part;
	}
	
	public PartGroups getPartGroups()
	{
		return partGroups;
	}
	
}
