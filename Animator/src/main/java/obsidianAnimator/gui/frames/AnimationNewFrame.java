package obsidianAnimator.gui.frames;

import obsidianAPI.animation.AnimationPart;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.ModelObj;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.file.FileNotChosenException;
import obsidianAnimator.gui.timeline.TimelineController;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AnimationNewFrame extends BaseFrame
{

    private JComboBox<String> entityDropDown;
    private JTextField nameTextField;
    private JLabel locationLabel;
    private JLabel templateLabel;

    private String[] entites = ModelHandler.getModelList().toArray(new String[0]);

    private File animationFolder;
    private File templateFile;

    public AnimationNewFrame()
    {
        super("New Animation", 300, 250);

        addComponents();
    }

    @Override
    protected void addComponents()
    {
        entityDropDown = new JComboBox<>(entites);
        nameTextField = new JTextField();
        locationLabel = new JLabel("No location set");
        templateLabel = new JLabel("No template set");

        entityDropDown.setPreferredSize(new Dimension(100, 25));
        locationLabel.setPreferredSize(new Dimension(100, 25));
        templateLabel.setPreferredSize(new Dimension(100, 25));

        JButton chooseFolder = new JButton("Choose folder");
        chooseFolder.addActionListener(e -> chooseFolderPressed());

        JButton chooseTemplate = new JButton("Choose template");
        chooseTemplate.addActionListener(e -> chooseTemplatePressed());

        JButton create = new JButton("Create");
        create.addActionListener(e -> createPressed());

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> cancelPressed());

        create.setPreferredSize(chooseFolder.getPreferredSize());

        int y = 0;

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = y++;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 5, 2, 5);

        //Entity
        mainPanel.add(new JLabel("Entity"), c);
        c.gridy = y++;
        mainPanel.add(entityDropDown, c);

        c.gridy = y++;
        mainPanel.add(new JSeparator(), c);

        //Folder location
        c.gridy = y++;
        mainPanel.add(new JLabel("Location"), c);
        c.gridwidth = 1;
        c.gridy = y++;
        mainPanel.add(locationLabel, c);
        c.gridx = 1;
        mainPanel.add(chooseFolder, c);

        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth = 2;
        mainPanel.add(new JSeparator(), c);

        // Template
        c.gridx = 0;
        c.gridy = y++;
        mainPanel.add(new JLabel("Template (optional)"), c);
        c.gridwidth = 1;
        c.gridy = y++;
        mainPanel.add(templateLabel, c);
        c.gridx = 1;
        mainPanel.add(chooseTemplate, c);

        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth = 2;
        mainPanel.add(new JSeparator(), c);

        //Animation name
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth = 2;
        mainPanel.add(new JLabel("Name"), c);
        c.gridy = y++;
        mainPanel.add(nameTextField, c);

        //Buttons
        c.gridwidth = 1;
        c.gridy = y++;
        mainPanel.add(create, c);
        c.gridx = 1;
        mainPanel.add(cancel, c);

    }

    private void chooseFolderPressed()
    {
        try
        {
            animationFolder = FileChooser.chooseAnimationFolder(frame);
            String path = animationFolder.getAbsolutePath();
            locationLabel.setText(path);
            locationLabel.setToolTipText(path);
            frame.revalidate();
            frame.repaint();
        } catch (FileNotChosenException e) {}
    }

    private void chooseTemplatePressed()
    {
        try
        {
            templateFile = FileChooser.loadAnimationFile(frame);
            String fileName = templateFile.getName();
            templateLabel.setText(fileName);
            templateLabel.setToolTipText(fileName);
            frame.revalidate();
            frame.repaint();
        } catch (FileNotChosenException ignored) {}
    }

    private void createPressed()
    {
        String animationName = nameTextField.getText();
        String entityName = (String) entityDropDown.getSelectedItem();
        if (animationName.equals(""))
            return;

        File animationFile = new File(animationFolder, animationName + "." + FileHandler.animationExtension);
        if (animationFile.exists())
        {
            JOptionPane.showMessageDialog(frame, "An animation with that name already exists.");
            return;
        }

        AnimationSequence sequence = new AnimationSequence(entityName, animationName);

        if (templateFile != null)
        {
            ModelObj model = ModelHandler.getModel(entityName);
            AnimationSequence template = FileHandler.getAnimationFromFile(templateFile);
            if (!isValidTemplate(template, model))
            {
                JOptionPane.showMessageDialog(frame, "Invalid template for this model");
                return;
            }

            sequence = applyTemplate(template, sequence);
        }

        frame.dispose();
        new TimelineController(animationFile, sequence).display();
    }

    private AnimationSequence applyTemplate(AnimationSequence template, AnimationSequence dest)
    {
        dest.setFPS(template.getFPS());
        dest.setAnimations(template.getAnimationList());

        return dest;
    }

    private boolean isValidTemplate(@Nullable AnimationSequence template, ModelObj model)
    {
        if (template == null)
            return false;

        for (AnimationPart animPart : template.getAnimationList())
        {
            if (model.getPartFromName(animPart.getPartName()) == null)
            {
                return false;
            }
        }

        return true;
    }

    private void cancelPressed()
    {
        frame.dispose();
        new HomeFrame().display();
    }

}
