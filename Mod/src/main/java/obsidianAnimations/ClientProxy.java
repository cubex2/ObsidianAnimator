package obsidianAnimations;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import obsidianAnimations.entity.EntityDummyPlayer;
import obsidianAnimations.entity.RenderDummyPlayer;
import obsidianAnimations.entity.RenderPlayerAnimated;

public class ClientProxy extends CommonProxy
{

    public void registerRendering()
    {
        // RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, new RenderDummyPlayer());
        // RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderPlayerAnimated());
    }
}

