package obsidianAnimator.data;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.data.ModelDefinition;
import obsidianAPI.io.ModelExporter;
import obsidianAPI.io.ModelImporter;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.render.entity.ModelObj_Animator;
import obsidianAnimator.render.entity.RenderObj_Animator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelHandler
{
    private static Map<String, ModelObj_Animator> models = new HashMap<>();

    public static RenderObj_Animator modelRenderer = new RenderObj_Animator();

    public static String importModel(File modelFile, File textureFile)
    {
        ModelDefinition definition = ModelImporter.INSTANCE.importModel(modelFile);
        File persistedFile = new File(Persistence.modelFolder, modelFile.getName());
        ModelExporter.INSTANCE.save(definition, persistedFile);

        ModelObj_Animator model = loadModelFromFile(persistedFile);
        copyFileToPersistentMemory(textureFile, model.entityName + ".png");
        updateRenderer(model.entityName);
        return model.entityName;
    }

    public static void loadFileFromPersistence(File file)
    {
        loadModelFromFile(file);
    }

    private static ModelObj_Animator loadModelFromFile(File modelFile)
    {
        String fileName = modelFile.getName();
        String entityName = fileName.substring(0, fileName.indexOf("."));
        ModelObj_Animator model = new ModelObj_Animator(entityName, modelFile, generateTextureResourceLocation(entityName));
        models.put(model.entityName, model);
        return model;
    }

    /**
     * Generates a resource location for a png texture file that is in the external
     * animation folder.
     */
    private static ResourceLocation generateTextureResourceLocation(String entityName)
    {
        return new ResourceLocation(String.format("animation:models/%s.png", entityName));
    }

    public static void updateRenderer(String entityName)
    {
        modelRenderer.setModel(models.get(entityName));
    }

    public static boolean isModelImported(String entityName)
    {
        return models.containsKey(entityName);
    }

    public static ModelObj_Animator getModel(String entityName)
    {
        return models.get(entityName);
    }

    public static Set<String> getModelList()
    {
        return models.keySet();
    }

    public static void copyFileToPersistentMemory(File file, String destFileName)
    {
        File copy = new File(Persistence.modelFolder, destFileName);
        try
        {
            if (copy.exists())
                copy.delete();
            FileUtils.copyFile(file, copy);
        } catch (IOException e) {e.printStackTrace();}
    }

    public static void saveFiles()
    {
        for (ModelObj model : models.values())
            makeModelFile(model);
    }

    public static void updateModel(ModelObj_Animator oldModel, ModelObj_Animator newModel)
    {
        List<Part> parts = Lists.newArrayList();
        parts.addAll(oldModel.getPartObjs());
        newModel.getPartObjs().stream().filter(p -> !parts.contains(p))
                .forEach(parts::add);

        oldModel.getParts().stream().filter(p -> !(p instanceof PartObj))
                .filter(p -> !parts.contains(p))
                .forEach(parts::add);

        newModel.getParts().stream().filter(p -> !(p instanceof PartObj))
                .filter(p -> !parts.contains(p))
                .forEach(parts::add);

        for (PartObj partObj : newModel.getPartObjs())
        {
            int index = oldModel.getPartObjs().indexOf(partObj);
            if (index >= 0)
            {
                oldModel.getPartObjs().get(index).updateValues(partObj);
            }
        }

        oldModel.getParts().clear();
        oldModel.getParts().addAll(parts);

        oldModel.model = newModel.model;

        saveFiles();
    }

    private static void makeModelFile(ModelObj model)
    {
        File f = new File(Persistence.modelFolder, model.entityName + "." + FileHandler.modelExtension);

        ModelExporter.INSTANCE.save(model.definition, f);
    }
}
