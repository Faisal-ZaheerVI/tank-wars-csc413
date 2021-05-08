package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class Wall extends Entity {
    private static int uniqueID = 0;
    private String imageFile;
    private int healthPoints;

    public Wall(double x, double y) {
        super("wall-" + uniqueID, x, y, 0);
        uniqueID++;
        imageFile = "";
        healthPoints = 1;
    }

    public void setX(int posX) {
        x = posX;
    }

    public void setY(int posY) {
        y = posY;
    }

    public void setImageFile(String fileName) {
        imageFile = fileName;
    }

    @Override
    public double getXBound() {
        return getX() + Constants.WALL_WIDTH;
    }

    @Override
    public double getYBound() {
        return getY() + Constants.WALL_HEIGHT;
    }

    @Override
    public void move(GameWorld gameWorld) {

    }

    @Override
    public void checkBounds(GameWorld gameWorld) {

    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void reduceHealth() {
        if (healthPoints > 0) {
            healthPoints--;
        }
    }
}
