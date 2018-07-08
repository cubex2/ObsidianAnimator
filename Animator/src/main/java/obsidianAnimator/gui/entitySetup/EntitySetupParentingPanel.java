package obsidianAnimator.gui.entitySetup;

import obsidianAPI.render.part.PartObj;
import obsidianAnimator.render.entity.ModelObj_Animator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EntitySetupParentingPanel extends JPanel
{

    private EntitySetupController controller;

    private JScrollPane scrollPane;

    public EntitySetupParentingPanel(EntitySetupController controller)
    {
        this.controller = controller;

        setLayout(new BorderLayout());

        addScrollPane();

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> {
            ModelObj_Animator model = controller.getEntityModel();
            for (PartObj partObj : model.getPartObjs())
            {
                model.updateParenting(partObj, null, false);
            }

            refreshScrollPane();
        });

        add(clearButton, BorderLayout.SOUTH);
    }

    private void addScrollPane()
    {
        JPanel parentingTreePanel = new JPanel();
        parentingTreePanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        parentingTreePanel.add(new JLabel("PARENTS", SwingConstants.CENTER), c);
        c.gridx = 1;
        c.weightx = 1;
        parentingTreePanel.add(new JLabel("CHILDREN", SwingConstants.LEFT), c);

        List<PartObj> partObjs = controller.getEntityModel().getPartObjs();
        for (int i = 0; i < partObjs.size(); i++)
        {
            PartObj p = partObjs.get(i);

            c.gridx = 0;
            c.gridy = 1 + i;
            c.weightx = 0;
            parentingTreePanel.add(new JLabel(p.getName(), SwingConstants.CENTER), c);
            c.gridx = 1;
            c.weightx = 1;
            parentingTreePanel.add(new JLabel(createChildList(p), SwingConstants.LEFT), c);
        }

        scrollPane = new JScrollPane(parentingTreePanel);

        add(scrollPane, BorderLayout.CENTER);
    }

    protected void refreshScrollPane()
    {
        remove(scrollPane);
        addScrollPane();
        revalidate();
        repaint();
    }

    private String createChildList(PartObj parent)
    {
        String childList = "";

        for (PartObj child : parent.getChildren())
            childList += child.getName() + ", ";

        if (parent.getChildren().size() > 0)
            childList = childList.substring(0, childList.length() - 2);

        return childList;
    }


}
