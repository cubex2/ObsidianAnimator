package obsidianAPI.io

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import obsidianAPI.animation.AnimationPart
import obsidianAPI.animation.AnimationSequence
import obsidianAPI.render.ModelObj
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object AnimationFileLoader
{
    fun load(file: File, model: ModelObj? = null): AnimationSequence
    {
        return load(FileInputStream(file), model)
    }

    fun load(inputStream: InputStream, model: ModelObj? = null): AnimationSequence
    {
        return load(CompressedStreamTools.readCompressed(inputStream), model)
    }

    fun load(nbt: NBTTagCompound, model: ModelObj? = null): AnimationSequence
    {
        val sequence = AnimationSequence(nbt)

        model?.let { fixAnimationPartNames(sequence, it) }

        return sequence
    }

    fun fixAnimationPartNames(sequence: AnimationSequence, model: ModelObj)
    {
        val animationParts = sequence.animationList
        sequence.clearAnimations()

        for (animationPart in animationParts)
        {
            val part = model.getPartFromName(animationPart.partName)
            val newAnimationPart = if (part != null)
                AnimationPart(
                        animationPart.startTime,
                        animationPart.endTime,
                        animationPart.startPosition,
                        animationPart.endPosition,
                        part
                )
            else
                animationPart

            sequence.addAnimation(newAnimationPart)
        }
    }
}