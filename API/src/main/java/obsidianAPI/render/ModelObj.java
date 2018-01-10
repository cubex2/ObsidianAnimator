package obsidianAPI.render;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.animation.AnimationParenting;
import obsidianAPI.animation.PartGroups;
import obsidianAPI.render.bend.Bend;
import obsidianAPI.render.part.*;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelObj extends ModelBase
{

    public final String entityName;
    public WavefrontObject model;
    public List<Part> parts;
    protected ArrayList<Bend> bends = new ArrayList<Bend>();
    public PartGroups partGroups;
    private Map<PartObj, float[]> defaults;

    public static final float initRotFix = 180.0F;
    public static final float offsetFixY = -1.5F;

    public ModelObj(String entityName, ResourceLocation modelLocation)
    {
        this(entityName, null, modelLocation);
    }

    public ModelObj(String entityName, File modelFile)
    {
        this(entityName, modelFile, null);
    }

    protected ModelObj(String entityName, File modelFile, ResourceLocation modelLocation)
    {
        this.entityName = entityName;
        defaults = Maps.newHashMap();
        if (modelFile != null)
            load(modelFile);
        else
            load(modelLocation);
        init();
    }

    public void init()
    {
        for (Part p : this.parts)
        {
            if (p instanceof PartObj)
            {
                PartObj obj = (PartObj) p;
                float[] arr = new float[3];
                arr[0] = obj.getRotationPoint(0);
                arr[1] = obj.getRotationPoint(1);
                arr[2] = obj.getRotationPoint(2);
                defaults.put(obj, arr);
            }
        }
    }

    public float[] getDefaults(PartObj part)
    {
        return defaults.get(part).clone();
    }

    private void load(ResourceLocation modelLocation)
    {
        try
        {
            load(Minecraft.getMinecraft().getResourceManager().getResource(modelLocation).getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void load(File modelFile)
    {
        try
        {
            load(new FileInputStream(modelFile));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void load(InputStream stream)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String nbtData = "";
            String modelData = "";
            String partData = "";

            int targetString = 0;
            String currentLine = "";
            while ((currentLine = reader.readLine()) != null)
            {
                if (currentLine.contains("# Model #"))
                    targetString = 1;
                else if (currentLine.contains("# Part #"))
                    targetString = 2;
                else
                {
                    switch (targetString)
                    {
                        case 0:
                            nbtData += currentLine + "\n";
                            break;
                        case 1:
                            modelData += currentLine + "\n";
                            break;
                        case 2:
                            partData += currentLine + "\n";
                            break;
                    }
                }
            }

            loadModel(modelData);
            loadAdditionalPartData(partData);

            //Only load setup if it exists - it won't if file is fresh from Blender.
            if (!nbtData.equals(""))
            {
                //Write nbt data to temp file so it can be read by compressed stream tools.
                File tmp = new File("tmp");
                if (!tmp.exists())
                    tmp.createNewFile();

                FileWriter fw = new FileWriter(tmp);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(nbtData);
                bw.close();
                fw.close();

                loadSetup(CompressedStreamTools.read(tmp));

                tmp.delete();
            }
            reader.close();
        } catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
    }

    private void loadModel(String modelData) throws ModelFormatException, UnsupportedEncodingException
    {
        model = new WavefrontObject(this.entityName, new ByteArrayInputStream(modelData.getBytes("UTF-8")));
        parts = createPartObjList(model.groupObjects);
        parts.add(new PartEntityPos(this));
        if (entityName.equals("player"))
        {
            parts.add(new PartPropRotation(this));
            parts.add(new PartPropTranslation(this));
            parts.add(new PartPropScale(this));

            parts.add(new PartPropRotation(this, "prop_rot_l"));
            parts.add(new PartPropTranslation(this, "prop_trans_l"));
            parts.add(new PartPropScale(this, "prop_scale_l"));
        }
        partGroups = new PartGroups(this);
    }

    private void loadAdditionalPartData(String partData)
    {
        PartObj currentPart = null;
        int i = 0;
        boolean readingRotation = true;

        for (String currentLine : partData.split("\n"))
        {
            if (currentLine.equals("PART SETUP"))
                readingRotation = false;

            if (readingRotation)
            {
                switch (i)
                {
                    case 0:
                        for (Part p : parts)
                        {
                            if (p instanceof PartObj)
                            {
                                PartObj obj = (PartObj) p;
                                if (obj.getName().equals(currentLine))
                                {
                                    currentPart = obj;
                                    break;
                                }
                            }
                        }
                        i++;
                        break;
                    case 1:
                        currentPart.setRotationPoint(read3Floats(currentLine));
                        i++;
                        break;
                    case 2:
                        float[] rot = read3Floats(currentLine);
                        currentPart.setOriginalValues(rot);
                        currentPart.setValues(rot);
                        i = 0;
                        break;
                }
            }
        }
    }

    private void loadSetup(NBTTagCompound nbt)
    {
        AnimationParenting.loadData(nbt.getCompoundTag("Parenting"), this);
        partGroups.loadData(nbt.getCompoundTag("Groups"), this);
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

    public NBTTagList getPartOrderAsList()
    {
        NBTTagList list = new NBTTagList();
        for (PartObj p : getPartObjs())
        {
            list.appendTag(new NBTTagString(p.getName()));
        }
        return list;
    }

    public void setPartOrderFromList(NBTTagList order)
    {
        ArrayList<Part> newPartList = new ArrayList<Part>();
        for (int i = 0; i < order.tagCount(); i++)
        {
            newPartList.add(getPartFromName(order.getStringTagAt(i)));
        }
        for (Part part : parts)
        {
            if (!newPartList.contains(part))
                newPartList.add(part);
        }
        parts = newPartList;
    }

    //----------------------------------------------------------------
    //						Parenting
    //----------------------------------------------------------------

    public void setParent(PartObj child, @Nullable PartObj parent, boolean addBend)
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

    private static float[] read3Floats(String str)
    {
        float[] arr = new float[3];
        for (int i = 0; i < 3; i++)
        {
            if (str.contains(","))
            {
                arr[i] = Float.parseFloat(str.substring(0, str.indexOf(",")));
                str = str.substring(str.indexOf(",") + 2);
            } else
            {
                arr[i] = Float.parseFloat(str);
            }
        }
        return arr;
    }

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

    public NBTTagCompound createNBTTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Parenting", AnimationParenting.getSaveData(this));
        nbt.setTag("Groups", partGroups.getSaveData());
        return nbt;
    }

    public Part getPartFromName(String name)
    {
        for (Part part : parts)
        {
            if (part.getName().equals(name) || part.getDisplayName().equals(name))
            {
                return part;
            }
        }
        throw new RuntimeException("No part found for '" + name + "'");
    }

    public PartObj getPartObjFromName(String name)
    {
        for (Part p : parts)
        {
            if (p instanceof PartObj)
            {
                PartObj part = (PartObj) p;
                if (part.getName().equals(name) || part.getDisplayName().equals(name))
                {
                    return part;
                }
            }
        }
        throw new RuntimeException("No part obj found for " + name + ".");
    }

}