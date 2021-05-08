package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.*;

/** Entity class representing all tanks in the game. */
public abstract class Tank extends Entity {
    // TODO: Implement. A lot of what's below is relevant to all Entity types, not just Tanks. Move it accordingly to
    //       Entity class.
    protected int counter;
    protected int healthPoints;

    public Tank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
        healthPoints = 3;
    }

    @Override
    public double getXBound() {
        return getX() + Constants.TANK_WIDTH;
    }

    @Override
    public double getYBound() {
        return getY() + Constants.TANK_HEIGHT;
    }

    @Override
    public void checkBounds(GameWorld gameWorld) {
        double tankX = Constants.TANK_WIDTH / 2;
        double tankY = Constants.TANK_HEIGHT / 2;

        if (getX() < Constants.TANK_X_LOWER_BOUND - tankX) {
            x = Constants.TANK_X_LOWER_BOUND - tankX;
        }

        else if (getX() > Constants.TANK_X_UPPER_BOUND + tankX) {
            x = Constants.TANK_X_UPPER_BOUND + tankX;
        }

        if (getY() < Constants.TANK_Y_LOWER_BOUND - tankY) {
            y = Constants.TANK_Y_LOWER_BOUND - tankY;
        }

        else if (getY() > Constants.TANK_Y_UPPER_BOUND + tankY) {
            y = Constants.TANK_Y_UPPER_BOUND + tankY;
        }
    }

    // TODO: The methods below are provided so you don't have to do the math for movement. You should call these methods
    //       from the various subclasses of Entity in their implementations of move. --- DONE

    // The following methods will be useful for determining where a shell should be spawned when it
    // is created by this tank. It needs a slight offset so it appears from the front of the tank,
    // even if the tank is rotated. The shell should have the same angle as the tank.

    protected double getShellX() {
        return getX() + Constants.TANK_WIDTH / 2 + 45.0 * Math.cos(getAngle()) - Constants.SHELL_WIDTH / 2;
    }

    protected double getShellY() {
        return getY() + Constants.TANK_HEIGHT / 2 + 45.0 * Math.sin(getAngle()) - Constants.SHELL_HEIGHT / 2;
    }

    protected double getShellAngle() {
        return getAngle();
    }

    protected void fireShell(GameWorld gameWorld) {
        Entity shell = new Shell(getShellX(), getShellY(), getShellAngle());
        gameWorld.addShell(shell);
    }

    public int getHealth() {
        return healthPoints;
    }

    public void reduceHealth() {
        if (healthPoints > 0) {
            healthPoints--;
        }
    }
}
