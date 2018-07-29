package obsidianAnimator.gui.timeline.swing.subsection;

import javax.swing.*;
import java.awt.*;

public class TimelinePartPanel extends JPanel
{

    protected JLabel partName, partX, partY, partZ;

    public TimelinePartPanel(TimelinePartController controller)
    {
        partName = new JLabel();
        partX = new JLabel();
        partY = new JLabel();
        partZ = new JLabel();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.anchor = c.LINE_START;
        c.insets = new Insets(1, 1, 1, 1);
        add(partName, c);
        c.gridwidth = 1;
        c.weightx = 1;
        c.gridy = 1;
        c.gridx = 0;
        add(partX, c);
        c.gridx = 1;
        add(partY, c);
        c.gridx = 2;
        add(partZ, c);

        setBorder(BorderFactory.createTitledBorder("Part"));
    }


}
