package obsidianAPI.data

import net.minecraft.nbt.NBTTagCompound
import obsidianAPI.io.NBTSerializer

class PartData @JvmOverloads constructor(val partName: String, var displayName: String, val group: String = "Default")
{
    companion object : NBTSerializer<PartData>
    {
        override fun toNBT(data: PartData): NBTTagCompound
        {
            return NBTTagCompound().apply {
                setString("PartName", data.partName)
                setString("DisplayName", data.displayName)
                setString("Group", data.group)
            }
        }

        override fun fromNBT(tag: NBTTagCompound): PartData
        {
            val partName = tag.getString("PartName")
            val displayName = tag.getString("DisplayName")
            val group = tag.getString("Group")

            return PartData(partName, displayName, group)
        }
    }
}