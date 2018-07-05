package obsidianAPI.data

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.model.obj.WavefrontObject
import obsidianAPI.io.NBTSerializer
import obsidianAPI.io.getStringList
import obsidianAPI.io.setStringList
import java.io.ByteArrayInputStream

class ModelDefinition(val modelName: String, val objModelBytes: ByteArray, val rotationPoints: List<PartRotationDefinition>,
                      val partData: MutableList<PartData>, val parenting: List<ParentingDefinition>,
                      val partOrder: List<String>, val hasProps: Boolean)
{
    val objModel = WavefrontObject(modelName, ByteArrayInputStream(objModelBytes))

    companion object : NBTSerializer<ModelDefinition>
    {
        override fun toNBT(value: ModelDefinition): NBTTagCompound
        {
            return NBTTagCompound().apply {
                setString("ModelName", value.modelName)
                setByteArray("ObjModelData", value.objModelBytes)
                setTag("RotationPoints", PartRotationDefinition.writeList(value.rotationPoints))
                setTag("PartData", PartData.writeList(value.partData))
                setTag("Parenting", ParentingDefinition.writeList(value.parenting))
                setStringList("PartOrder", value.partOrder)
                setBoolean("HasProps", value.hasProps)
            }
        }

        override fun fromNBT(tag: NBTTagCompound): ModelDefinition
        {
            val modelName = tag.getString("ModelName")
            val objModelBytes = tag.getByteArray("ObjModelData")
            val rotationPoints = PartRotationDefinition.readList(tag, "RotationPoints")
            val partData = PartData.readList(tag, "PartData").toMutableList()
            val parenting = ParentingDefinition.readList(tag, "Parenting")
            val partOrder = tag.getStringList("PartOrder")
            val hasProps = tag.getBoolean("HasProps")

            return ModelDefinition(modelName, objModelBytes, rotationPoints, partData, parenting, partOrder, hasProps)
        }
    }
}