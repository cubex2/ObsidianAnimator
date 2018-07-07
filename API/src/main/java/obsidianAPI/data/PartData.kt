package obsidianAPI.data

import net.minecraft.nbt.NBTTagCompound
import obsidianAPI.io.NBTSerializer

class PartData @JvmOverloads constructor(val internalName: String, var name: String, val group: String = "Default")
{
    companion object : NBTSerializer<PartData>
    {
        override fun toNBT(data: PartData): NBTTagCompound
        {
            return NBTTagCompound().apply {
                setString("PartName", data.internalName)
                setString("DisplayName", data.name)
                setString("Group", data.group)
            }
        }

        override fun fromNBT(tag: NBTTagCompound): PartData
        {
            val internalName = tag.getString("PartName")
            val name = tag.getString("DisplayName")
            val group = tag.getString("Group")

            return PartData(internalName, name, group)
        }
    }
}