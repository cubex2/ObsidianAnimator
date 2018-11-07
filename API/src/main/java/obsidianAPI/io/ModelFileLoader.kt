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

    @JvmOverloads
    fun load(location: ResourceLocation, onServer: Boolean = false): ModelDefinition
    {
        return if (onServer)
        {
            val classLoader = ModelFileLoader::class.java.classLoader
            val path = "assets/" + location.resourceDomain + "/" + location.resourcePath
            load(classLoader.getResourceAsStream(path))
        } else
        {
            load(Minecraft.getMinecraft().resourceManager.getResource(location).inputStream)
        }
    }

    fun load(inputStream: InputStream): ModelDefinition
    {
        val nbt = CompressedStreamTools.readCompressed(inputStream)

        return ModelDefinition.fromNBT(nbt)
    }
}

