package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.view.Sounds;

public class AdvancedTank extends Tank {
    //private boolean tankStopped;

    public AdvancedTank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
        //tankStopped = true;
        counter = 200;
    }

    @Override
    public void move(GameWorld gameWorld) {
        // Create a reference/copy of the playerTank.
        Entity playerTank = gameWorld.getEntity(Constants.PLAYER_TANK_ID);

        // While the playerTank is alive, perform AI movement based on location of the playerTank.
        if (playerTank != null) {
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

            moveForward(Constants.TANK_MOVEMENT_SPEED / 4);

            // When the countdown of 200 iterations reaches 0, fire a shell, then reset the countdown.
            if (counter == 0) {
                fireShell(gameWorld);
                Sounds.playSound(Constants.AI_TANK_FIRE);
                counter = 200;
            }

            // Decrements counter.
            counter--;
        } else {
            moveForward(0);
        }
    }

    @Override
    public void checkBounds(GameWorld gameWorld) {

    }
}
