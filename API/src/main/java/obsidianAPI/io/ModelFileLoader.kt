package obsidianAPI.io

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.util.ResourceLocation
import obsidianAPI.data.ModelDefinition
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object ModelFileLoader
{
    fun load(file: File): ModelDefinition
    {
        return load(FileInputStream(file))
    }

    fun load(location: ResourceLocation): ModelDefinition
    {
        return load(Minecraft.getMinecraft().resourceManager.getResource(location).inputStream)
    }

    fun load(inputStream: InputStream): ModelDefinition
    {
        val nbt = CompressedStreamTools.readCompressed(inputStream)

        return ModelDefinition.fromNBT(nbt)
    }
}

