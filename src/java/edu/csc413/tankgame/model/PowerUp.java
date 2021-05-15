package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class PowerUp extends Entity {

    public PowerUp(String id, double x, double y) {
        super(id, x, y, 0);
    }

    @Override
    public double getXBound() {
        return getX() + Constants.POWER_UP_WIDTH;
    }

    @Override
    public double getYBound() {
        return getY() + Constants.POWER_UP_HEIGHT;
    }

    @Override
    public void move(GameWorld gameWorld) {

    }

    @Override
    public void checkBounds(GameWorld gameWorld) {

    }
}
