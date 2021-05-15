package edu.csc413.tankgame.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class PauseMenuView extends JPanel {
    public static final Dimension SCREEN_DIMENSIONS = new Dimension(510, 550);
    private static final String START_MENU_IMAGE_FILE = "title.png";

    private static final Dimension BUTTON_SIZE = new Dimension(200, 100);
    private static final Font BUTTON_FONT = new Font("Consolas", Font.BOLD, 22);
    private static final Rectangle RESUME_BUTTON_BOUNDS = new Rectangle(150, 300, 200, 50);
    private static final Rectangle RESTART_BUTTON_BOUNDS = new Rectangle(150, 375, 200, 50);
    private static final Rectangle EXIT_BUTTON_BOUNDS = new Rectangle(150, 450, 200, 50);

    public static final String RESUME_BUTTON_ACTION_COMMAND = "resume_ac";
    public static final String RESTART_BUTTON_ACTION_COMMAND = "restart_ac";
    public static final String EXIT_BUTTON_ACTION_COMMAND = "exit_ac";

    private final BufferedImage menuBackground;

    public PauseMenuView(String startButtonText, String resumeButtonText, ActionListener pauseMenuListener) {
        URL imageUrl = getClass().getClassLoader().getResource(START_MENU_IMAGE_FILE);
        if (imageUrl == null) {
            throw new RuntimeException("Background image file not found: " + START_MENU_IMAGE_FILE);
        }
        try {
            menuBackground = ImageIO.read(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setBackground(Color.BLACK);
        setLayout(null);

        addButton(resumeButtonText, RESUME_BUTTON_BOUNDS, RESUME_BUTTON_ACTION_COMMAND, pauseMenuListener);
        addButton(startButtonText, RESTART_BUTTON_BOUNDS, RESTART_BUTTON_ACTION_COMMAND, pauseMenuListener);
        addButton("Exit", EXIT_BUTTON_BOUNDS, EXIT_BUTTON_ACTION_COMMAND, pauseMenuListener);
    }

    private void addButton(
            String buttonText, Rectangle buttonBounds, String buttonActionCommand, ActionListener actionListener) {
        JButton button = new JButton(buttonText);
        button.setSize(BUTTON_SIZE);
        button.setFont(BUTTON_FONT);
        button.setBounds(buttonBounds);
        button.setActionCommand(buttonActionCommand);
        button.addActionListener(actionListener);
        add(button);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(menuBackground, 0, 0, null);
    }
}
