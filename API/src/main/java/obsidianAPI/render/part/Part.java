package obsidianAPI.render.part;

import obsidianAPI.render.ModelObj;

/**
 * An abstract object for tracking information about a part (limb, position of model etc).
 */
public abstract class Part
{
    protected float valueX, valueY, valueZ;
    protected float[] originalValues;
    private final String internalName;
    public final ModelObj modelObj;

    public Part(ModelObj mObj, String internalName)
    {
        modelObj = mObj;
        this.internalName = internalName.toLowerCase();
        valueX = 0.0F;
        valueY = 0.0F;
        valueZ = 0.0F;
        originalValues = new float[] {0.0F, 0.0F, 0.0F};
    }

    //------------------------------------------
    //  			Basics
    //------------------------------------------

    public String getInternalName()
    {
        return internalName;
    }

    public String getName()
    {
        return getInternalName();
    }

    public void setValues(float[] values)
    {
        valueX = values[0];
        valueY = values[1];
        valueZ = values[2];
    }

    public void setValue(float f, int i)
    {
        switch (i)
        {
            case 0:
                valueX = f;
                break;
            case 1:
                valueY = f;
                break;
            case 2:
                valueZ = f;
                break;
        }
    }

    public float getValue(int i)
    {
        switch (i)
        {
            case 0:
                return valueX;
            case 1:
                return valueY;
            case 2:
                return valueZ;
        }
        return 0.0F;
    }

    public float[] getValues()
    {
        return new float[] {valueX, valueY, valueZ};
    }

    public void setOriginalValues(float[] rot)
    {
        originalValues = rot;
    }

    public float[] getOriginalValues()
    {
        return originalValues;
    }

    public void setToOriginalValues()
    {
        setValues(originalValues);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Part part = (Part) o;

        if (!internalName.equals(part.internalName)) return false;
        return modelObj.equals(part.modelObj);
    }

    @Override
    public int hashCode()
    {
        int result = internalName.hashCode();
        result = 31 * result + modelObj.hashCode();
        return result;
    }
}
