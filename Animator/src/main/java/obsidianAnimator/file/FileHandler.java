package obsidianAnimator.file;

import net.minecraft.nbt.CompressedStreamTools;
import obsidianAPI.animation.AnimationSequence;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler
{

    public static final String modelExtension = "obm";
    public static final String animationExtension = "oba";
    public static final String textureExtension = "png";


    public static final FileNameExtensionFilter modelFilter = new FileNameExtensionFilter("Obsidian Models", modelExtension);
    public static final FileNameExtensionFilter animationFilter = new FileNameExtensionFilter("Obsidian Animations", animationExtension);
    public static final FileNameExtensionFilter textureFilter = new FileNameExtensionFilter("PNG", textureExtension);

    public static File lastModelDirectory;
    public static File lastAnimationDirectory;

    public static void saveAnimationSequence(File animationFile, AnimationSequence sequence)
    {
        try
        {
            CompressedStreamTools.writeCompressed(sequence.getSaveData(), new FileOutputStream(animationFile));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
