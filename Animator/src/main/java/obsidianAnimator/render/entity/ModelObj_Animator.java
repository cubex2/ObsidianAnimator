package obsidianAnimator.render.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.GroupObject;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.bend.Bend;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.render.Bend_Animator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModelObj_Animator extends ModelObj
{

    private PartObj mainHighlight = null;
    private List<PartObj> hightlightedParts;
    private ResourceLocation texture;

    public static final ResourceLocation pinkResLoc = new ResourceLocation("mod_obsidian_animator:defaultModelTextures/pink.png");
    public static final ResourceLocation whiteResLoc = new ResourceLocation("mod_obsidian_animator:defaultModelTextures/white.png");

    public ModelObj_Animator(String entityName, ResourceLocation model, ResourceLocation texture)
    {
        this(entityName, null, model, texture);
    }

    public ModelObj_Animator(String entityName, File modelFile, ResourceLocation texture)
    {
        this(entityName, modelFile, null, texture);
    }

    private ModelObj_Animator(String entityName, File modelFile, ResourceLocation model, ResourceLocation texture)
    {
        super(entityName, modelFile, model);
        hightlightedParts = new ArrayList<PartObj>();
        this.texture = texture;
    }

    public ResourceLocation getTexture()
    {
        return texture;
    }

    @Override
    protected PartObj createPart(GroupObject group)
    {
        return new PartObj_Animator(this, group);
    }

    @Override
    protected Bend createBend(PartObj parent, PartObj child)
    {
        return new Bend_Animator(parent, child);
    }

    //----------------------------------------------------------------
    // 							 Selection
    //----------------------------------------------------------------

    public PartObj testRay()
    {
        PartObj closestPart = null;
        Double min = null;
        synchronized ( parts )
        {
            for (Part part : parts)
            {
                if (part instanceof PartObj_Animator)
                {
                    PartObj_Animator p = (PartObj_Animator) part;
                    Double d = p.testRay();
                    if (d != null && (min == null || d < min))
                    {
                        closestPart = p;
                        min = d;
                    }
                }
            }
        }
        for (Bend bend : bends)
        {
            if (bend instanceof Bend_Animator)
            {
                Bend_Animator b = (Bend_Animator) bend;

                Double d = b.testRayChild();
                if (d != null && (min == null || d < min))
                {
                    closestPart = bend.child;
                    min = d;
                }
                Double d2 = b.testRayParent();
                if (d2 != null && (min == null || d2 < min))
                {
                    closestPart = bend.parent;
                    min = d2;
                }
            }
        }

        return closestPart;
    }

    //----------------------------------------------------------------
    // 							Highlighting
    //----------------------------------------------------------------

    /**
     * Highlight a part.
     *
     * @param part - Part to highlight.
     * @param main - True if main highlight (pink).
     */
    public void hightlightPart(PartObj part, boolean main)
    {
        if (part != null)
        {
            if (main)
                mainHighlight = part;
            else
                hightlightedParts.add(part);
        }
    }

    public void clearHighlights()
    {
        this.hightlightedParts.clear();
        this.mainHighlight = null;
    }

    public boolean isMainHighlight(PartObj partObj)
    {
        return mainHighlight == partObj;
    }

    /**
     * Highlighted but not main highlight (white).
     */
    public boolean isPartHighlighted(PartObj partObj)
    {
        return hightlightedParts.contains(partObj);
    }
}