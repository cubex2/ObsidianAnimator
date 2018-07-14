package obsidianAPI.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.animation.PartGrouping;
import obsidianAPI.data.ModelDefinition;
import obsidianAPI.data.ParentingDefinition;
import obsidianAPI.data.PartData;
import obsidianAPI.data.PartRotationDefinition;
import obsidianAPI.render.bend.Bend;
import obsidianAPI.render.part.*;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ModelObj extends ModelBase
{
    public static final float initRotFix = 180.0F;
    public static final float offsetFixY = -1.5F;

    public final String entityName;
    public final ModelDefinition definition;
    public WavefrontObject model;
    private List<Part> parts;
    private final ArrayList<Bend> bends = new ArrayList<Bend>();

    public final PartGrouping grouping;

    public ModelObj(ModelDefinition definition)
    {
        this.definition = definition;
        this.entityName = definition.getModelName();
        this.model = definition.getObjModel();
        this.grouping = new PartGrouping(definition);

        initParts();
        initRotationPoints(definition);
        initParenting();
    }

    private void initRotationPoints(ModelDefinition definition)
    {
        for (PartRotationDefinition rotationPoint : definition.getRotationPoints())
        {
            PartObj part = getPartObjFromName(rotationPoint.getPartName());
            if (part != null)
            {
                part.setRotationPoint(rotationPoint.getRotationPoint());
                part.setOriginalValues(rotationPoint.getDefaultValues());
                part.setValues(rotationPoint.getDefaultValues());
            }
        }
    }

    private void initParts()
    {
        parts = createPartObjList(model.groupObjects);
        for (PartData partData : definition.getPartData())
        {
            PartObj part = getPartObjFromName(partData.getInternalName());
            if (part != null)
            {
                part.setDisplayName(partData.getName());
            }
        }
        parts.sort(Comparator.comparing(part -> definition.getPartOrder().indexOf(part.getName())));

        parts.add(new PartEntityPos(this));
        if (definition.getHasProps() || entityName.equals("player"))
        {
            addProps();
        }
    }

    private void initParenting()
    {
        for (ParentingDefinition definition : definition.getParenting())
        {
            PartObj parent = getPartObjFromName(definition.getParent());
            PartObj child = getPartObjFromName(definition.getChild());
            if (parent != null && child != null)
            {
                setParent(child, parent, definition.getHasBend());
            }
        }
    }

    public void updatePartOrder()
    {
        definition.getPartOrder().clear();
        getPartObjs().forEach(part -> definition.getPartOrder().add(part.getName()));
    }

    public void updatePartDisplayName(PartObj part, String newDisplayName)
    {
        Optional<PartData> result = definition.getPartData().stream().filter(p -> p.getName().equals(part.getName())).findAny();

        part.setDisplayName(newDisplayName);

        if (result.isPresent())
        {
            result.get().setName(newDisplayName);
        } else
        {
            definition.getPartData().add(new PartData(part.getInternalName(), part.getName()));
        }
    }

    public void updateParenting(PartObj child, @Nullable PartObj parent, boolean addBend)
    {
        if (parent == null && child.getParent() != null)
        {
            definition.getParenting().removeIf(p -> p.getChild().equals(child.getName()) && p.getParent().equals(child.getParent().getName()));
        }

        if (parent != null)
        {
            Optional<ParentingDefinition> result = definition.getParenting().stream()
                                                             .filter(p -> p.getChild().equals(child.getName()))
                                                             .findAny();

            if (result.isPresent())
            {
                result.get().setParent(parent.getName());
                result.get().setHasBend(addBend);
            } else
            {
                definition.getParenting().add(new ParentingDefinition(parent.getName(), child.getName(), addBend));
            }
        }

        setParent(child, parent, addBend);
    }

    public List<Part> getParts()
    {
        return parts;
    }

    public Iterable<Bend> getBends()
    {
        return bends;
    }

    public boolean hasProps()
    {
        return definition.getHasProps();
    }

    public void setHasProps(boolean value)
    {
        if (value)
        {
            definition.setHasProps(true);

            addProps();
        } else
        {
            definition.setHasProps(false);
        }
    }

    private void addProps()
    {
        if (parts.stream().anyMatch(p -> p instanceof PartPropRotation))
            return;

        parts.add(new PartPropRotation(this));
        parts.add(new PartPropTranslation(this));
        parts.add(new PartPropScale(this));

        parts.add(new PartPropRotation(this, "prop_rot_l"));
        parts.add(new PartPropTranslation(this, "prop_trans_l"));
        parts.add(new PartPropScale(this, "prop_scale_l"));
    }

    //----------------------------------------------------------------
    //				     Parts and Groups
    //----------------------------------------------------------------

    public List<PartObj> getPartObjs()
    {
        List<PartObj> partObjs = new ArrayList<PartObj>();
        for (Part part : parts)
        {
            if (part instanceof PartObj)
                partObjs.add((PartObj) part);
        }
        return partObjs;
    }

    //----------------------------------------------------------------
    //						Parenting
    //----------------------------------------------------------------

    private void setParent(PartObj child, @Nullable PartObj parent, boolean addBend)
    {
        if (addBend)
        {
            for (Part p : parts)
            {
                if (p instanceof PartObj)
                {
                    PartObj obj = (PartObj) p;
                    obj.updateTextureCoordinates(null, false, false, false);
                }
            }

            if (!child.hasBend())
            {
                Bend b = createBend(parent, child);
                bends.add(b);
                child.setBend(b);
            }
        } else if (child.hasBend())
        {
            child.removeBend();
        }

        if (child.hasParent())
            child.getParent().removeChild(child);

        child.setParent(parent);
        if (parent != null)
        {
            parent.addChild(child);
        } else
        {
            child.removeBend();
        }
    }

    protected Bend createBend(PartObj parent, PartObj child)
    {
        return new Bend(parent, child);
    }

    public void removeBend(Bend bend)
    {
        bends.remove(bend);
    }

    //----------------------------------------------------------------
    //							Rotation
    //----------------------------------------------------------------

    public void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    //----------------------------------------------------------------
    //							Rendering
    //----------------------------------------------------------------

    @Override
    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale)
    {
        super.render(entity, time, distance, loop, lookY, lookX, scale);

        setRotationAngles(time, distance, loop, lookY, lookX, scale, entity);

        GL11.glPushMatrix();
        GL11.glRotatef(initRotFix, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, offsetFixY, 0.0F);

        for (Part p : this.parts)
        {
            if (p instanceof PartObj)
            {
                PartObj part = (PartObj) p;
                if (!part.hasParent())
                    part.render(entity);
            }
            //TODO entity movement via PartEntityPos
//			else if(p instanceof PartEntityPos)
//				((PartEntityPos) p).move(entity);
        }

        for (Bend bend : bends)
        {
            bend.render(entity);
        }

        GL11.glPopMatrix();
    }

    //----------------------------------------------------------------
    //							Utils
    //----------------------------------------------------------------

    public ArrayList<Part> createPartObjList(ArrayList<GroupObject> groupObjects)
    {
        ArrayList<Part> parts = new ArrayList<Part>();
        for (GroupObject gObj : groupObjects)
            parts.add(createPart(gObj));
        return parts;
    }

    protected PartObj createPart(GroupObject group)
    {
        return new PartObj(this, group);
    }

    @Nullable
    public Part getPartFromName(String name)
    {
        for (Part part : parts)
        {
            if (part.getInternalName().equalsIgnoreCase(name)
                || part.getName().equalsIgnoreCase(name))
            {
                return part;
            }
        }

        return null;
    }

    @Nullable
    public PartObj getPartObjFromName(String name)
    {
        for (Part p : parts)
        {
            if (p instanceof PartObj)
            {
                PartObj part = (PartObj) p;
                if (part.getInternalName().equalsIgnoreCase(name)
                    || part.getName().equalsIgnoreCase(name))
                {
                    return part;
                }
            }
        }

        return null;
    }

}