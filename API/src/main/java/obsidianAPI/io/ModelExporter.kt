package obsidianAPI.io

import net.minecraft.nbt.CompressedStreamTools
import obsidianAPI.data.ModelDefinition
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ModelExporter
{
    fun save(definition: ModelDefinition, file: File)
    {
        makeBackup(file)

        save(definition, FileOutputStream(file))
    }

    private fun save(definition: ModelDefinition, stream: OutputStream)
    {
        val nbt = ModelDefinition.toNBT(definition)

        CompressedStreamTools.writeCompressed(nbt, stream)
    }

    private fun makeBackup(file: File)
    {
        if (file.exists())
        {
            file.copyTo(file.parentFile.resolve(file.name + ".backup"), overwrite = true)
        }
    }
}