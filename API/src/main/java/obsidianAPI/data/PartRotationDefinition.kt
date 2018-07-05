package obsidianAPI.data

import net.minecraft.nbt.NBTTagCompound
import obsidianAPI.io.NBTSerializer

class PartRotationDefinition(val partName: String, val rotationPoint: FloatArray, val defaultValues: FloatArray)
{
    companion object : NBTSerializer<PartRotationDefinition>
    {
        override fun toNBT(definition: PartRotationDefinition): NBTTagCompound
        {
            return NBTTagCompound().apply {
                setString("PartName", definition.partName)
                setFloat("RotPointX", definition.rotationPoint[0])
                setFloat("RotPointY", definition.rotationPoint[1])
                setFloat("RotPointZ", definition.rotationPoint[2])
                setFloat("DefaultRotPointX", definition.defaultValues[0])
                setFloat("DefaultRotPointY", definition.defaultValues[1])
                setFloat("DefaultRotPointZ", definition.defaultValues[2])
            }
        }

       override fun fromNBT(tag: NBTTagCompound): PartRotationDefinition
        {
            val partName = tag.getString("PartName")
            val rotationPoint = floatArrayOf(
                    tag.getFloat("RotPointX"),
                    tag.getFloat("RotPointY"),
                    tag.getFloat("RotPointZ")
            )
            val defaultValues = floatArrayOf(
                    tag.getFloat("DefaultRotPointX"),
                    tag.getFloat("DefaultRotPointY"),
                    tag.getFloat("DefaultRotPointZ")
            )

            return PartRotationDefinition(partName, rotationPoint, defaultValues)
        }
    }
}