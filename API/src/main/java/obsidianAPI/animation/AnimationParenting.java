package obsidianAPI.animation;

import obsidianAPI.render.part.PartObj;

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
}
