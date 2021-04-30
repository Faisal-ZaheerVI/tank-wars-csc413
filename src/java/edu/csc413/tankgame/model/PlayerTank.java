package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.KeyboardReader;

public class PlayerTank extends Tank {
    private static boolean spacePressed;
    private int cooldown;

    public PlayerTank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
        spacePressed = false;
        cooldown = 0;
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

        // * Fixed Keyboard input so that when space key is pressed, only one shell fires at a time.
        // * Added cooldown system so that player can fire a shell every 200 iterations.
        if (keyboardReader.spacePressed() && !spacePressed && cooldown == 0) {
            fireShell(gameWorld);
            spacePressed = true;
            cooldown = 200;
        }
        if (!keyboardReader.spacePressed()) {
            spacePressed = false;
        }

        if (cooldown != 0) {
            cooldown--;
        }
    }
}
