package obsidianAnimator.file;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class FileChooser
{

    private static JFileChooser fc = new JFileChooser();

    public static File loadModelFile(Component parent) throws FileNotChosenException
    {
        File modelFile = getLoadLocationFromUser(parent, FileHandler.lastModelDirectory, FileHandler.modelFilter, JFileChooser.FILES_ONLY);
        FileHandler.lastModelDirectory = fc.getCurrentDirectory();
        return modelFile;
    }

    public static File loadAnimationFile(Component parent) throws FileNotChosenException
    {
        File animationFile = getLoadLocationFromUser(parent, FileHandler.lastAnimationDirectory, FileHandler.animationFilter, JFileChooser.FILES_ONLY);
        FileHandler.lastAnimationDirectory = fc.getCurrentDirectory();
        return animationFile;
    }

    public static File loadImportFile(Component parent, FileNameExtensionFilter filter) throws FileNotChosenException
    {
        File modelFile = getLoadLocationFromUser(parent, FileHandler.lastModelDirectory, filter, JFileChooser.FILES_ONLY);
        FileHandler.lastModelDirectory = fc.getCurrentDirectory();
        return modelFile;
    }

    public static File chooseAnimationFolder(Component parentComponent) throws FileNotChosenException
    {
        File animationFolder = getLoadLocationFromUser(parentComponent, FileHandler.lastAnimationDirectory, null, JFileChooser.DIRECTORIES_ONLY);
        FileHandler.lastAnimationDirectory = fc.getCurrentDirectory();
        return animationFolder;
    }

    private static File getLoadLocationFromUser(Component parentComponent, File parentDirectory, FileNameExtensionFilter fileExtensionFilter, int fileSelectionMode) throws FileNotChosenException
    {
        File file = null;

        if (parentDirectory != null)
            fc.setCurrentDirectory(parentDirectory);

        fc.setFileFilter(fileExtensionFilter);
        fc.setFileSelectionMode(fileSelectionMode);

        int returnVal = fc.showOpenDialog(parentComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            throw new FileNotChosenException();

        return file;
    }

}

