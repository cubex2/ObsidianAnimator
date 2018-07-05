package obsidianAnimator.gui.entitySetup;

import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.file.FileNotChosenException;
import obsidianAnimator.render.entity.ModelObj_Animator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class EntitySetupMiscPanel extends JPanel
{
    private EntitySetupController controller;


    public EntitySetupMiscPanel(EntitySetupFrame frame, EntitySetupController controller)
    {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JCheckBox hasProps = new JCheckBox("Has Props (disabling requires restart)");
        hasProps.setSelected(controller.getEntityModel().hasProps());
        hasProps.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                controller.getEntityModel().setHasProps(hasProps.isSelected());
            }
        });
        add(hasProps);

        JButton selectNewTexture = new JButton("Select New Texture");
        selectNewTexture.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    File targetFile = FileChooser.loadImportFile(frame, FileHandler.textureFilter);
                    ModelHandler.copyFileToPersistentMemory(targetFile, controller.getEntityModel().entityName + ".png");
                    controller.scheduleTextureReload();
                } catch (FileNotChosenException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        add(selectNewTexture);

        JButton updateModel = new JButton("Update Model");
        updateModel.setToolTipText("Update the model. This will only update the mesh. Parenting, part names, etc. remain unchanged.");
        updateModel.addActionListener(e -> {
            /*try
            {
                File targetFile = FileChooser.loadImportFile(frame, FileHandler.modelFilter);
                ModelObj_Animator newModel = new ModelObj_Animator(controller.getEntityModel().entityName, targetFile, controller.getEntityModel().getTexture());
                ModelHandler.updateModel(controller.getEntityModel(), newModel);
            } catch (FileNotChosenException e1)
            {
                e1.printStackTrace();
            }*/
        });
        updateModel.setEnabled(false);
        add(updateModel);
    }
}
