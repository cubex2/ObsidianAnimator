package obsidianAPI.animation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.PartObj;

import java.util.Set;

public class AnimationParenting
{
    /**
     * To check that a child is not related to a parent in anyway ie(not a grandchild or great grandchild etc.)
     * Returns true is there is no relation
     */
    public static boolean areUnrelated(PartObj child, PartObj parent)
    {
        PartObj c = child;
        while (c.hasParent())
        {
            PartObj p = c.getParent();
            if (p.equals(parent))
                return false;
            c = p;
        }
        return true;
    }

    public static NBTTagCompound getSaveData(ModelObj model)
    {
        NBTTagCompound parentNBT = new NBTTagCompound();
        NBTTagList parentNBTList = new NBTTagList();

        for (PartObj part : model.getPartObjs())
        {
            Set<PartObj> children = part.getChildren();
            if (!children.isEmpty())
            {
                NBTTagCompound parentCompound = new NBTTagCompound();
                parentCompound.setString("Parent", part.getName());

                int i = 0;
                for (PartObj child : children)
                {
                    String name = child.getName();
                    if (child.hasBend())
                    {
                        name += "*";
                    }
                    parentCompound.setString("Child" + i, name);
                    i++;
                }

                parentNBTList.appendTag(parentCompound);
            }

        }

        parentNBT.setTag("Parenting", parentNBTList);
        return parentNBT;
    }
}
