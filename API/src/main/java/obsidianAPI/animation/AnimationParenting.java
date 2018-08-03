package obsidianAPI.animation;

import obsidianAPI.render.part.IPart;

public class AnimationParenting
{
    /**
     * To check that a child is not related to a parent in anyway ie(not a grandchild or great grandchild etc.)
     * Returns true is there is no relation
     */
    public static boolean areUnrelated(IPart child, IPart parent)
    {
        IPart c = child;
        while (c.hasParent())
        {
            IPart p = c.getParentPart();
            if (p.equals(parent))
                return false;
            c = p;
        }
        return true;
    }
}
