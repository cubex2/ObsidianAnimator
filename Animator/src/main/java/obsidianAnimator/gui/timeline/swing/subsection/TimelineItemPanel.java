package obsidianAnimator.gui.timeline.swing.subsection;

import net.minecraft.client.Minecraft;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.GuiInventoryChooseItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimelineItemPanel extends JPanel
{

    public TimelineItemPanel(TimelineItemController controller)
    {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2, 2, 2, 2);
        c.ipadx = 10;
        c.fill = GridBagConstraints.BOTH;

        JButton itemButton = new JButton("Choose Right");
        itemButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.getTimelineFrame().setVisible(false);
                Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryChooseItem(false, controller, controller.getEntityToRender()));
            }
        });
        add(itemButton, c);

        c.gridx = 1;
        JButton emptyItemButton = new JButton("Empty");
        emptyItemButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.getEntityToRender().setCurrentItem(null);
            }
        });
        add(emptyItemButton, c);

        c.gridy = 1;
        c.gridx = 0;
        itemButton = new JButton("Choose Left");
        itemButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.getTimelineFrame().setVisible(false);
                Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryChooseItem(true, controller, controller.getEntityToRender()));
            }
        });
        add(itemButton, c);

        c.gridy = 1;
        c.gridx = 1;
        emptyItemButton = new JButton("Empty");
        emptyItemButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ModelHandler.modelRenderer.setLeftItem(null);
            }
        });
        add(emptyItemButton, c);
        setBorder(BorderFactory.createTitledBorder("Item"));
    }

}
