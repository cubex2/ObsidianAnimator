package obsidianAnimator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.resources.IResourcePack;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.render.AnimationResourcePack;
import obsidianAnimator.render.entity.EntityObj;

import java.util.List;

public class ClientProxy extends CommonProxy
{

    public void registerRendering()
    {
        //Entities
        RenderingRegistry.registerEntityRenderingHandler(EntityObj.class, ModelHandler.modelRenderer);

        //Register animator resource pack, where models and textures are stored, externally from the jar
        List<IResourcePack> resourcePackList = ObfuscationReflectionHelper.getPrivateValue(FMLClientHandler.class, FMLClientHandler.instance(), "resourcePackList");
        resourcePackList.add(new AnimationResourcePack());
    }
}

