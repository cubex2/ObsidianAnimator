package obsidianAnimator.gui.timeline.swing.subsection;

import net.minecraft.client.Minecraft;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.GuiInventoryChooseItem;

import javax.swing.*;
import java.awt.*;

public class TimelineItemPanel extends JPanel
{

    private final TimelineItemController controller;

    public TimelineItemPanel(TimelineItemController controller)
    {
        this.controller = controller;
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2, 2, 2, 2);
        c.ipadx = 10;
        c.fill = GridBagConstraints.BOTH;

        addButtons(c, 0, "Right", GuiInventoryChooseItem.Type.RIGHT,
                   () -> controller.getEntityToRender().setCurrentItem(null));

        addButtons(c, 1, "Left", GuiInventoryChooseItem.Type.LEFT,
                   () -> ModelHandler.modelRenderer.setLeftItem(null));

        if (controller.getCurrentAnimation().getName().toLowerCase().contains("shop"))
        {
            addButtons(c, 2, "Shop", GuiInventoryChooseItem.Type.SHOP,
                       () -> ModelHandler.modelRenderer.setShopItem(null));
        }

        setBorder(BorderFactory.createTitledBorder("Item"));
    }

    private void addButtons(GridBagConstraints c, int y, String text, GuiInventoryChooseItem.Type type,
                            Runnable emptyClicked)
    {
        JButton itemButton;
        JButton emptyItemButton;
        c.gridy = y;
        c.gridx = 0;
        itemButton = new JButton("Choose " + text);
        itemButton.addActionListener(e -> {
            controller.getTimelineFrame().setVisible(false);
            Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryChooseItem(type, controller, controller.getEntityToRender()));
        });
        add(itemButton, c);

        c.gridy = y;
        c.gridx = 1;
        emptyItemButton = new JButton("Empty");
        emptyItemButton.addActionListener(e -> emptyClicked.run());
        add(emptyItemButton, c);
    }

}
