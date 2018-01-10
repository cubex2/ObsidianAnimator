package obsidianAnimator.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block
{

    public BlockBase()
    {
        super(Material.glass);
        this.setBlockTextureName("mod_obsidian_animator:base");
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

}
