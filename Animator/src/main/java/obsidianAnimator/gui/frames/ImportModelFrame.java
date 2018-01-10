package obsidianAnimator.gui.frames;

import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.file.FileNotChosenException;
import obsidianAnimator.gui.entitySetup.EntitySetupController;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImportModelFrame extends BaseFrame
{

    private File modelFile;
    private File textureFile;
    private JButton importButton;
    private FileSelectionPanel modelSelectionPanel;
    private FileSelectionPanel textureSelectionPanel;

    public ImportModelFrame()
    {
        super("Import Model");
        addComponents();
    }

    @Override
    protected void addComponents()
    {
        importButton = new JButton("Import");
        importButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                importPressed();
            }
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cancelPressed();
            }
        });

        importButton.setPreferredSize(cancel.getPreferredSize());

        modelSelectionPanel = new FileSelectionPanel("Model File", FileHandler.modelFilter);
        textureSelectionPanel = new FileSelectionPanel("Texture File", FileHandler.textureFilter);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 2;

        mainPanel.add(modelSelectionPanel, c);

        c.gridy = 1;
        mainPanel.add(textureSelectionPanel, c);

        c.insets = new Insets(20, 5, 5, 5);

        c.fill = GridBagConstraints.BOTH;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 1;
        mainPanel.add(importButton, c);

        c.gridx = 1;
        mainPanel.add(cancel, c);

        refresh();
    }

    private void refresh()
    {
        modelFile = modelSelectionPanel.targetFile;
        textureFile = textureSelectionPanel.targetFile;
        if (modelFile == null || textureFile == null)
            importButton.setEnabled(false);
        else
            importButton.setEnabled(true);
        frame.revalidate();
        frame.repaint();
    }

    private void importPressed()
    {
        String entityName = ModelHandler.importModel(modelFile, textureFile);
        frame.dispose();
        new EntitySetupController(entityName).display();
    }

    private void cancelPressed()
    {
        frame.dispose();
        new HomeFrame().display();
    }

    private class FileSelectionPanel extends JPanel
    {
        JLabel titleLabel;
        JLabel locationLabel;
        JButton chooseFileButton;
        File targetFile;

        private FileSelectionPanel(String title, final FileNameExtensionFilter filter)
        {
            setLayout(new GridBagLayout());

            titleLabel = new JLabel(title);
            titleLabel.setHorizontalAlignment(JLabel.HORIZONTAL);

            locationLabel = new JLabel("No location set");
            locationLabel.setPreferredSize(new Dimension(150, 25));

            chooseFileButton = new JButton("Choose File");
            chooseFileButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {

                    try
                    {
                        targetFile = FileChooser.loadImportFile(frame, filter);
                        String path = targetFile.getAbsolutePath();
                        locationLabel.setText(path);
                        locationLabel.setToolTipText(path);
                        refresh();
                    } catch (FileNotChosenException e1) {}
                }
            });

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);


            c.gridwidth = 2;
            add(titleLabel, c);

            c.gridwidth = 1;
            c.gridy = 1;
            add(locationLabel, c);

            c.gridx = 1;
            add(chooseFileButton, c);
        }

    }

}
