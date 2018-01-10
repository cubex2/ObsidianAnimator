package obsidianAnimator.gui.entitySetup;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class EntitySetupMiscPanel extends JPanel
{
    private EntitySetupController controller;

    public EntitySetupMiscPanel(EntitySetupController controller)
    {
        this.controller = controller;

        setLayout(new BorderLayout());
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
        add(hasProps, BorderLayout.NORTH);
    }
}
