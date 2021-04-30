package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class Shell extends Entity {
    private static int uniqueID = 0;
    private boolean isDestroyed;

    public Shell(double x, double y, double angle) {
        super("shell-" + uniqueID, x, y, angle);
        uniqueID++;
        isDestroyed = false;
    }

    @Override
    public void move(GameWorld gameWorld) {
        if (!isDestroyed) {
            moveForward(Constants.SHELL_MOVEMENT_SPEED);
        }
        else {
            moveForward(0);
        }
    }

    @Override
    public void checkBounds(GameWorld gameWorld) {
        // Out of bounds if:
        // x < lowest acceptable x value OR x > highest acceptable x value
        // y < lowest acceptable x value OR x > highest acceptable y value

        double shellX = 40;
        double shellY = 60;

        // Continue checking bounds of shell while it is not destroyed.
        if (!isDestroyed) {
            if (getX() < Constants.SHELL_X_LOWER_BOUND) {
                x = Constants.SHELL_X_LOWER_BOUND;
                isDestroyed = true;
            }
            else if (getX() > Constants.SHELL_X_UPPER_BOUND - shellX) {
                x = Constants.SHELL_X_UPPER_BOUND - shellX;
                isDestroyed = true;
            }

            if (getY() < Constants.SHELL_Y_LOWER_BOUND) {
                y = Constants.SHELL_Y_LOWER_BOUND;
                isDestroyed = true;
            }
            else if (getY() > Constants.SHELL_Y_UPPER_BOUND - shellY) {
                y = Constants.SHELL_Y_UPPER_BOUND - shellY;
                isDestroyed = true;
            }

            // If a shell makes contact with the boundaries, add shell to destroyed shells list in gameWorld.
            if (isDestroyed) {
                gameWorld.destroyShell(getId());
            }
        }
    }
}
