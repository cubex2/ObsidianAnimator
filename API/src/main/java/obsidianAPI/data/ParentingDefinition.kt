package obsidianAPI.data

import net.minecraft.nbt.NBTTagCompound
import obsidianAPI.io.NBTSerializer

class ParentingDefinition(var parent: String, val child: String, var hasBend: Boolean)
{
    companion object : NBTSerializer<ParentingDefinition>
    {
        override fun toNBT(value: ParentingDefinition): NBTTagCompound
        {
            return NBTTagCompound().apply {
                setString("Parent", value.parent)
                setString("Child", value.child)
                setBoolean("HasBend", value.hasBend)
            }
        }

        override fun fromNBT(tag: NBTTagCompound): ParentingDefinition
        {
            val parentPartName = tag.getString("Parent")
            val childPartName = tag.getString("Child")
            val hasBend = tag.getBoolean("HasBend")

            return ParentingDefinition(parentPartName, childPartName, hasBend)
        }
    }
}