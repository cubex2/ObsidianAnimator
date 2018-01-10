package obsidianAnimator.data;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import obsidianAnimator.ObsidianAnimator;
import obsidianAnimator.file.FileHandler;

import java.io.*;

public class Persistence
{

    private static final File saveFile = new File(ObsidianAnimator.animationPath, "save.data");
    public static final File modelFolder = new File(ObsidianAnimator.animationPath + "/models");

    private static FilenameFilter modelFileFilter = new FilenameFilter()
    {
        public boolean accept(File dir, String name)
        {
            return name.toLowerCase().endsWith(".obm");
        }
    };

    public static void save()
    {
        ModelHandler.saveFiles();

        NBTTagCompound nbt = new NBTTagCompound();
        if (FileHandler.lastModelDirectory != null)
            nbt.setString("lastModelDir", FileHandler.lastModelDirectory.getAbsolutePath());
        if (FileHandler.lastAnimationDirectory != null)
            nbt.setString("lastAnimationDir", FileHandler.lastAnimationDirectory.getAbsolutePath());
        writeNBTToFile(nbt, saveFile);
    }

    public static void load()
    {
        loadModels();

        if (saveFile.exists())
        {
            NBTTagCompound nbt = getNBTFromFile(saveFile);
            if (nbt.hasKey("lastModelDir"))
                FileHandler.lastModelDirectory = new File(nbt.getString("lastModelDir"));
            if (nbt.hasKey("lastAnimationDir"))
                FileHandler.lastAnimationDirectory = new File(nbt.getString("lastAnimationDir"));
        }
    }

    private static void loadModels()
    {
        //Load cached imported models
        if (modelFolder.exists())
        {
            for (File f : modelFolder.listFiles(modelFileFilter))
                ModelHandler.loadFileFromPersistence(f);
        } else
            modelFolder.mkdirs();
    }


    /**
     * Write an NBTTagCompound to a file.
     */
    public static void writeNBTToFile(NBTTagCompound nbt, File file)
    {
        try
        {
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
        } catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
    }

    /**
     * Read an NBTTagCompound from a file.
     */
    private static NBTTagCompound getNBTFromFile(File file)
    {
        try
        {
            return CompressedStreamTools.readCompressed(new FileInputStream(file));
        } catch (FileNotFoundException e) {throw new RuntimeException(e);} catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
