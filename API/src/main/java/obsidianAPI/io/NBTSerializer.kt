package obsidianAPI.io

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants

interface NBTSerializer<T>
{
    fun toNBT(value: T): NBTTagCompound

    fun fromNBT(tag: NBTTagCompound): T

    fun readList(tag: NBTTagCompound, listName: String): List<T>
    {
        return readList(tag.getTagList(listName, Constants.NBT.TAG_COMPOUND))
    }

    fun readList(nbtTagList: NBTTagList): List<T>
    {
        return (0 until nbtTagList.tagCount())
            .map(nbtTagList::getCompoundTagAt)
            .map(this::fromNBT)
    }

    fun writeList(list: List<T>): NBTTagList
    {
        val nbtTagList = NBTTagList()

        list.map(this::toNBT)
            .forEach { nbtTagList.appendTag(it) }

        return nbtTagList
    }
}