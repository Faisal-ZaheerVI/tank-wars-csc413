package edu.csc413.tankgame.model;

public class Wall extends Entity {
    private static int uniqueID = 0;
    private String imageFile;

    public Wall(double x, double y) {
        super("wall-" + uniqueID, x, y, 0);
        uniqueID++;
        imageFile = "";
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
    public void move(GameWorld gameWorld) {

    }

    @Override
    public void checkBounds(GameWorld gameWorld) {

    }
}
