package obsidianAPI.io

import obsidianAPI.data.ModelDefinition
import obsidianAPI.data.PartRotationDefinition
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Imports models exported from blender
 */
object ModelImporter
{
    fun importModel(file: File): ModelDefinition
    {
        return importModel(file.name.replace(".obm", ""), file)
    }

    fun importModel(modelName: String, file: File): ModelDefinition
    {
        return importModel(modelName, FileInputStream(file))
    }

    fun importModel(modelName: String, stream: InputStream): ModelDefinition
    {
        val lines = readAllLines(stream)

        val objModelBytes = readModelBytes(lines)
        val rotations = createRotationDefinitions(lines)

        return ModelDefinition(modelName, objModelBytes, rotations, mutableListOf(), listOf(), listOf(), false)
    }

    private fun readAllLines(stream: InputStream): MutableList<String>
    {
        val reader = InputStreamReader(stream)
        val lines = reader.readLines().toMutableList()
        reader.close()
        return lines
    }

    private fun readModelBytes(lines: MutableList<String>): ByteArray
    {
        val modelLines = mutableListOf<String>()

        while (true)
        {
            val line = lines.removeAt(0)

            if (line.trim() == "# Part #")
                break

            modelLines.add(line)
        }

        val modelString = modelLines.reduce { acc, s -> acc + "\n" + s }
        return modelString.toByteArray(Charsets.UTF_8)
    }

    private fun createRotationDefinitions(lines: List<String>): List<PartRotationDefinition>
    {
        val definitions = mutableListOf<PartRotationDefinition>()

        var partName = ""
        var rotationPoint = floatArrayOf()
        var defaultValues: FloatArray

        lines.filter { it.isNotBlank() }
            .map { it.trim() }
            .forEach { line ->
                when
                {
                    partName.isEmpty() -> partName = line
                    rotationPoint.isEmpty() -> rotationPoint = lineToFloatArray(line)
                    else ->
                    {
                        defaultValues = lineToFloatArray(line)
                        definitions.add(PartRotationDefinition(partName, rotationPoint, defaultValues))

                        partName = ""
                        rotationPoint = floatArrayOf()
                        defaultValues = floatArrayOf()
                    }
                }
            }

        return definitions
    }

    private fun lineToFloatArray(line: String): FloatArray
    {
        return line.split(",", limit = 3)
            .map(String::trim)
            .map(String::toFloat)
            .toFloatArray()
    }
}