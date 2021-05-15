package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.KeyboardReader;
import edu.csc413.tankgame.view.HealthBar;
import edu.csc413.tankgame.view.Sounds;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class PlayerTank extends Tank {
    private boolean spacePressed;
    private int cooldown;
    private boolean powerUpActive;
    private int powerUpCounter;

    public PlayerTank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
        healthPoints = 4; // Can change to differentiate health values between PlayerTank and other tanks.
        spacePressed = false;
        cooldown = 0;
        powerUpActive = false;
        powerUpCounter = 0;
    }

    public void activatePowerUp() {
        if (!powerUpActive) {
            powerUpActive = true;
            powerUpCounter = 500;
        }
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

        if (powerUpActive) {
            if (keyboardReader.spacePressed() && !spacePressed) {
                fireShell(gameWorld);
                Sounds.playSound(Constants.PLAYER_TANK_FIRE);
                spacePressed = true;
            }
            if (!keyboardReader.spacePressed()) {
                spacePressed = false;
            }
            if (powerUpCounter == 0) {
                powerUpActive = false;
            }
            powerUpCounter--;
        }

        else {
            // * Fixed Keyboard input so that when space key is pressed, only one shell fires at a time.
            // * Added cooldown system so that player can fire a shell every 200 iterations.
            if (keyboardReader.spacePressed() && !spacePressed && cooldown == 0) {
                fireShell(gameWorld);
                Sounds.playSound(Constants.PLAYER_TANK_FIRE);
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

        gameWorld.updateHealthBar(healthPoints);
    }

    @Override
    public void reduceHealth() {
        if (healthPoints > 0) {
            healthPoints--;
        }
    }
}
