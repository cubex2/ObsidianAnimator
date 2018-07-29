package obsidianAnimator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.timeline.swing.subsection.TimelineItemController;
import obsidianAnimator.render.entity.EntityObj;

@SideOnly(Side.CLIENT)
public class GuiInventoryChooseItem extends GuiInventory
{

    private final Type type;
    private TimelineItemController controller;
    private EntityObj entity;

    public GuiInventoryChooseItem(Type type, TimelineItemController controller, EntityObj entity)
    {
        super(Minecraft.getMinecraft().thePlayer);
        this.allowUserInput = true;
        this.controller = controller;
        this.entity = entity;
        this.type = type;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.mc.displayGuiScreen(new GuiContainerChooseItem(this.mc.thePlayer, this));
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.mc.displayGuiScreen(new GuiContainerChooseItem(this.mc.thePlayer, this));
    }

    public void setItemStack(ItemStack itemStack)
    {
        if (type == Type.LEFT)
            ModelHandler.modelRenderer.setLeftItem(itemStack);
        else if (type == Type.RIGHT)
            this.entity.setCurrentItem(itemStack);
        else if (type == Type.SHOP)
            ModelHandler.modelRenderer.setShopItem(itemStack);
        controller.display();
    }

    public enum Type
    {
        LEFT, RIGHT, SHOP
    }
}