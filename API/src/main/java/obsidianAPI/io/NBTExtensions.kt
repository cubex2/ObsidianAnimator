package obsidianAPI.io

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.common.util.Constants

internal fun NBTTagCompound.getStringList(name: String): List<String>
{
    val nbtTagList = getTagList(name, Constants.NBT.TAG_STRING)

    return (0 until nbtTagList.tagCount())
        .map(nbtTagList::getStringTagAt)
}

internal fun NBTTagCompound.setStringList(name: String, value: List<String>)
{
    val nbtTagList = NBTTagList()
    value.forEach { nbtTagList.appendTag(NBTTagString(it)) }

    setTag(name, nbtTagList)
}
