package obsidianAnimator.gui.frames;

import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame
{

    protected JFrame frame;
    protected JPanel mainPanel;

    public BaseFrame(String title)
    {
        this(title, 300, 200);
    }

    public BaseFrame(String title, int width, int height)
    {
        frame = new JFrame(title);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(width, height));

        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLocation(Display.getX() + Display.getWidth() / 2 - frame.getWidth() / 2, Display.getY() + Display.getHeight() / 2 - frame.getHeight() / 2);
    }

    protected abstract void addComponents();

    public void display()
    {
        frame.setVisible(true);
    }

}
