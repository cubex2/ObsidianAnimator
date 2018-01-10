package obsidianAnimator.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockGrid extends Block
{

    public BlockGrid()
    {
        super(Material.glass);
        this.setBlockTextureName("mod_obsidian_animator:grid");
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

}
