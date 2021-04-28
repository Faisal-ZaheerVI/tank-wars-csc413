package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.KeyboardReader;

public class PlayerTank extends Tank {
    private static boolean spacePressed;

    public PlayerTank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
        spacePressed = false;
    }

    @Override
    public void move(GameWorld gameWorld) {
        KeyboardReader keyboardReader = KeyboardReader.instance();

        if (keyboardReader.upPressed()) {
            moveForward(Constants.TANK_MOVEMENT_SPEED);
        }
        if (keyboardReader.downPressed()) {
            moveBackward(Constants.TANK_MOVEMENT_SPEED);
        }
        if (keyboardReader.leftPressed()) {
            turnLeft(Constants.TANK_TURN_SPEED);
        }
        if (keyboardReader.rightPressed()) {
            turnRight(Constants.TANK_TURN_SPEED);
        }

        // Fixed Keyboard input so that when space key
        // is pressed, only one shell fires at a time.
        if (keyboardReader.spacePressed() && !spacePressed) {
            fireShell(gameWorld);
            spacePressed = true;
        }
        if (!keyboardReader.spacePressed()) {
            spacePressed = false;
        }
    }

//    private void fireShell(GameWorld gameWorld) {
//        Shell shell = new Shell(getShellX(), getShellY(), getShellAngle());
//        gameWorld.addShell(shell);
//    }

    // The following methods will be useful for determining where a shell should be spawned when it
    // is created by this tank. It needs a slight offset so it appears from the front of the tank,
    // even if the tank is rotated. The shell should have the same angle as the tank.

//    private double getShellX() {
//        return getX() + Constants.TANK_WIDTH / 2 + 45.0 * Math.cos(getAngle()) - Constants.SHELL_WIDTH / 2;
//    }
//
//    private double getShellY() {
//        return getY() + Constants.TANK_HEIGHT / 2 + 45.0 * Math.sin(getAngle()) - Constants.SHELL_HEIGHT / 2;
//    }
//
//    private double getShellAngle() {
//        return getAngle();
//    }
}
