package obsidianAPI.render.part;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.bend.Bend;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

/**
 * One partObj for each 'part' of the model.
 */
public class PartObj extends PartRotation
{
    private float[] rotationPoint;
    public GroupObject groupObj;
    private String displayName;
    private String displayNameLowerCase;

    protected Map<Face, TextureCoordinate[]> defaultTextureCoords = Maps.newHashMap();

    private PartObj parent;
    private Set<PartObj> children = Sets.newHashSet();
    private Bend bend = null;

    public PartObj(ModelObj modelObject, GroupObject groupObj)
    {
        super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
        this.groupObj = groupObj;
        this.displayName = getInternalName();
        this.displayNameLowerCase = displayName.toLowerCase();
        setDefaultTCsToCurrentTCs();
    }

    public void updateValues(PartObj part)
    {
        if (part.rotationPoint != null)
            rotationPoint = part.rotationPoint;
        defaultTextureCoords = part.defaultTextureCoords;
        groupObj = part.groupObj;
        setDefaultTCsToCurrentTCs();
    }

    public void setParent(PartObj parent)
    {
        this.parent = parent;
    }

    public PartObj getParent()
    {
        return parent;
    }

    public void addChild(PartObj child)
    {
        children.add(child);
    }

    public void removeChild(PartObj child)
    {
        children.remove(child);
    }

    public Set<PartObj> getChildren()
    {
        return children;
    }

    public void setBend(Bend bend)
    {
        this.bend = bend;
    }

    public boolean hasBend()
    {
        return bend != null;
    }

    public void removeBend()
    {
        if (bend != null)
        {
            modelObj.removeBend(bend);
            bend.remove();
            bend = null;
        }
    }

    //------------------------------------------
    //              Basics
    //------------------------------------------

    @Override
    @Nonnull
    public String getName()
    {
        return displayNameLowerCase == null ? getInternalName() : displayNameLowerCase;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
        this.displayNameLowerCase = displayName == null ? null : displayName.toLowerCase();
    }

    public void setRotationPoint(float[] rot)
    {
        rotationPoint = rot;
    }

    @Override
    public float getRotationPoint(int i)
    {
        return rotationPoint[i];
    }

    public float[] getRotationPoint()
    {
        return rotationPoint;
    }

    //------------------------------------------
    //         Rendering and Rotating
    //------------------------------------------

    /**
     * Stores the current texture coordinates in default texture coords.
     * This is required in case a bend is removed, then the texture coords can be restored.
     * XXX
     */
    public void setDefaultTCsToCurrentTCs()
    {
        for (Face f : groupObj.faces)
        {
            if (f.textureCoordinates == null)
            {
                f.textureCoordinates = new TextureCoordinate[3];
                for (int i = 0; i < 3; i++)
                {
                    f.textureCoordinates[i] = new TextureCoordinate(0, 0);
                }
            }

            TextureCoordinate[] coordsToStore = new TextureCoordinate[3];
            for (int i = 0; i < 3; i++)
            {
                coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
            }

            defaultTextureCoords.put(f, coordsToStore);
        }
    }

    public void updateTextureCoordinates(Entity entity)
    {
        updateTextureCoordinates(entity, false, false, true);
    }

    /**
     * Change the texture coordinates and texture if the part is highlighted.
     */
    public void updateTextureCoordinates(Entity entity, boolean mainHighlight, boolean otherHighlight, boolean bindTexture)
    {
        for (Face f : groupObj.faces)
        {
            f.textureCoordinates = defaultTextureCoords.get(f);
        }
    }

    public void render(Entity entity)
    {
        GL11.glPushMatrix();
        move();
        updateTextureCoordinates(entity);
        groupObj.render();

        //Do for children - rotation for parent compensated for!
        for (PartObj child : getChildren())
            child.render(entity);

        GL11.glPopMatrix();
    }

    @Nullable
    @Override
    public IPart getParentPart()
    {
        return parent;
    }

    public float[] createRotationMatrixFromAngles()
    {
        double sx = Math.sin(-valueX);
        double sy = Math.sin(-valueY);
        double sz = Math.sin(-valueZ);
        double cx = Math.cos(-valueX);
        double cy = Math.cos(-valueY);
        double cz = Math.cos(-valueZ);

        float m0 = (float) (cy * cz);
        float m1 = (float) (sx * sy * cz - cx * sz);
        float m2 = (float) (cx * sy * cz + sx * sz);
        float m3 = (float) (cy * sz);
        float m4 = (float) (sx * sy * sz + cx * cz);
        float m5 = (float) (cx * sy * sz - sx * cz);
        float m6 = (float) -sy;
        float m7 = (float) (sx * cy);
        float m8 = (float) (cx * cy);

        return new float[] {m0, m1, m2, m3, m4, m5, m6, m7, m8};
    }
}
