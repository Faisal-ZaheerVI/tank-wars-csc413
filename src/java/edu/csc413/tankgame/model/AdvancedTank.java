package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class AdvancedTank extends Tank {
    private static boolean shellFired;
    private int counter;

    public AdvancedTank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
        shellFired = false;
        counter = 0;
    }

    @Override
    public void move(GameWorld gameWorld) {
        Entity playerTank = gameWorld.getEntity(Constants.PLAYER_TANK_ID);

        moveForward(Constants.TANK_MOVEMENT_SPEED / 4);

        // To figure out what angle the AI tank needs to face, we'll use the
        // change in the x and y axes between the AI and player tanks.
        double dx = playerTank.getX() - getX();
        double dy = playerTank.getY() - getY();

        // atan2 applies arctangent to the ratio of the two provided values.
        double angleToPlayer = Math.atan2(dy, dx);
        double angleDifference = getAngle() - angleToPlayer;
        // We want to keep the angle difference between -180 degrees and 180
        // degrees for the next step. This ensures that anything outside of that
        // range is adjusted by 360 degrees at a time until it is, so that the
        // angle is still equivalent.
        angleDifference -=
                Math.floor(angleDifference / Math.toRadians(360.0) + 0.5)
                        * Math.toRadians(360.0);
        // The angle difference being positive or negative determines if we turn
        // left or right. However, we don’t want the Tank to be constantly
        // bouncing back and forth around 0 degrees, alternating between left
        // and right turns, so we build in a small margin of error.
        if (angleDifference < -Math.toRadians(3.0)) {
            turnRight(Constants.TANK_TURN_SPEED);
        } else if (angleDifference > Math.toRadians(3.0)) {
            turnLeft(Constants.TANK_TURN_SPEED);
        }

        if (!shellFired) {
            fireShell(gameWorld);
            shellFired = true;
        }

        if (counter % 200 == 0) {
            shellFired = false;
        }

        if (counter >= 1000) {
            counter = 0;
        }

        counter++;
    }

    @Override
    public void checkBounds(GameWorld gameWorld) {

    }
}
