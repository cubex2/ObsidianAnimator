package obsidianAnimator.gui.timeline.swing.subsection;

import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.TimelineControllerSub;

import java.awt.*;
import java.text.DecimalFormat;

public class TimelinePartController extends TimelineControllerSub
{

    public final TimelinePartPanel panel;

    private DecimalFormat df = new DecimalFormat("#.##");

    public TimelinePartController(TimelineController controller)
    {
        super(controller);

        this.panel = new TimelinePartPanel(this);

        updatePartLabels();
    }

    public void updatePartLabels()
    {
        String name = "No part selected";
        String x = "-", y = "-", z = "-";
        if (getSelectedPart() != null)
        {
            Part part = getSelectedPart();
            name = part.getName();
            x = df.format(part.getValue(0));
            y = df.format(part.getValue(1));
            z = df.format(part.getValue(2));
        }
        //if (name.length() > 10)
            //name = name.substring(0, 10);
        panel.partName.setText(name);
        //panel.partName.setPreferredSize(new Dimension(70, panel.partName.getPreferredSize().height));
        panel.partX.setText("X: " + x);
        panel.partY.setText("Y: " + y);
        panel.partZ.setText("Z: " + z);
    }

}
