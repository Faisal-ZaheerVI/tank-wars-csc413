package edu.csc413.tankgame.view;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.model.Entity;
import edu.csc413.tankgame.model.GameWorld;

public class HealthBar extends Entity {
    private static final String HEALTH_BAR_IMAGE_FILE_PREFIX = "health-bar-";
    private static final String HEALTH_BAR_IMAGE_FILE_SUFFIX = ".png";
    private static int healthID = 4;
    private int barID;
    private String id;

    public HealthBar(double x, double y) {
        super("health-bar-" + healthID, x, y, 0);
        barID = healthID;
        if (healthID > 0) {
            healthID--;
        }
    }

    public String getImageFile() {
        return HEALTH_BAR_IMAGE_FILE_PREFIX + barID + HEALTH_BAR_IMAGE_FILE_SUFFIX;
    }

    public int getBarID() {
        return barID;
    }

    public void reduceHealth() {
        if (healthID != 0) {
            healthID--;
        }
    }

    @Override
    public double getXBound() {
        return getX() + Constants.HEALTH_BAR_WIDTH;
    }

    @Override
    public double getYBound() {
        return getX() + Constants.HEALTH_BAR_HEIGHT;
    }

    @Override
    public void move(GameWorld gameWorld) {

    }

    @Override
    public void checkBounds(GameWorld gameWorld) {

    }
}
