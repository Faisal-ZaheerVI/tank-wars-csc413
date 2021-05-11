package edu.csc413.tankgame;

import edu.csc413.tankgame.model.*;
import edu.csc413.tankgame.view.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GameDriver {
    private final MainView mainView;
    private final RunGameView runGameView;
    private final GameWorld gameWorld = new GameWorld();
    private int counter = 200;
    private boolean gameOver = false;

    public GameDriver() {
        mainView = new MainView(this::startMenuActionPerformed);
        runGameView = mainView.getRunGameView();
    }

    public void start() {
        mainView.setScreen(MainView.Screen.START_GAME_SCREEN);
    }

    private void startMenuActionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case StartMenuView.START_BUTTON_ACTION_COMMAND -> runGame();
            case StartMenuView.EXIT_BUTTON_ACTION_COMMAND -> mainView.closeGame();
            default -> throw new RuntimeException("Unexpected action command: " + actionEvent.getActionCommand());
        }
    }

    private void runGame() {
        mainView.setScreen(MainView.Screen.RUN_GAME_SCREEN);
        Runnable gameRunner = () -> {
            setUpGame();
            while (updateGame()) {
                runGameView.repaint();
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }
            mainView.setScreen(MainView.Screen.END_MENU_SCREEN);
            resetGame();
        };
        new Thread(gameRunner).start();
    }

    /**
     * setUpGame is called once at the beginning when the game is started. Entities that are present from the start
     * should be initialized here, with their corresponding sprites added to the RunGameView.
     */
    private void setUpGame() {
        // TODO: Implement.

        // Initializes walls (location+sprites) based on WallInformation.
        List<WallInformation> wallInfo = WallInformation.readWalls();
        for (WallInformation wall : wallInfo) {
            Entity newWall = new Wall(wall.getX(), wall.getY());
            gameWorld.addEntity(newWall);
            runGameView.addSprite(newWall.getId(), wall.getImageFile(), newWall.getX(), newWall.getY(), newWall.getAngle());
        }

        // Create entities in the game world based on entity type.
        Entity playerTank = new PlayerTank(
                Constants.PLAYER_TANK_ID,
                Constants.PLAYER_TANK_INITIAL_X,
                Constants.PLAYER_TANK_INITIAL_Y,
                Constants.PLAYER_TANK_INITIAL_ANGLE);
        SimpleTank simpleTank = new SimpleTank(
                Constants.AI_TANK_1_ID,
                Constants.AI_TANK_1_INITIAL_X,
                Constants.AI_TANK_1_INITIAL_Y,
                Constants.AI_TANK_1_INITIAL_ANGLE);
        AdvancedTank advancedTank = new AdvancedTank(
                Constants.AI_TANK_2_ID,
                Constants.AI_TANK_2_INITIAL_X,
                Constants.AI_TANK_2_INITIAL_Y,
                Constants.AI_TANK_2_INITIAL_ANGLE
        );

        gameWorld.addEntity(playerTank);
        gameWorld.addEntity(simpleTank);
        gameWorld.addEntity(advancedTank);

        // View part:
        runGameView.addSprite(
                playerTank.getId(),
                RunGameView.PLAYER_TANK_IMAGE_FILE,
                playerTank.getX(),
                playerTank.getY(),
                playerTank.getAngle());
        runGameView.addSprite(
                simpleTank.getId(),
                RunGameView.AI_TANK_IMAGE_FILE,
                simpleTank.getX(),
                simpleTank.getY(),
                simpleTank.getAngle());
        runGameView.addSprite(
                advancedTank.getId(),
                RunGameView.AI_TANK_IMAGE_FILE,
                advancedTank.getX(),
                advancedTank.getY(),
                advancedTank.getAngle()
        );
    }

    /**
     * updateGame is repeatedly called in the gameplay loop. The code in this method should run a single frame of the
     * game. As long as it returns true, the game will continue running. If the game should stop for whatever reason
     * (e.g. the player tank being destroyed, escape being pressed), it should return false.
     */
    private boolean updateGame() {
        // TODO: Implement.
        KeyboardReader keyboardReader = KeyboardReader.instance();

        // Check for endgame conditions: If ESC pressed, playerTank dies, or if all AI Tanks die, the game ends.
        // If game ends, it goes to restart screen.
        if (keyboardReader.escapePressed() || gameWorld.getEntity(Constants.PLAYER_TANK_ID) == null) {
            gameOver = true;
        } else if (gameWorld.getEntity(Constants.AI_TANK_1_ID) == null
                && gameWorld.getEntity(Constants.AI_TANK_2_ID) == null) {
            gameOver = true;
        }

        // Concurrent error happens when trying to modify same list as you're iterating it!

        ArrayList<Entity> originalEntities = new ArrayList<>(gameWorld.getEntities());
        for (Entity entity : originalEntities) {
            entity.move(gameWorld);
        }

        // Check bounds for each entity in gameWorld.
        for (Entity entity : originalEntities) {
            entity.checkBounds(gameWorld);
        }

        // Collision detection and handling between entities.
        for (int i = 0; i < originalEntities.size(); i++) {
            for (int j = i + 1; j < originalEntities.size(); j++) {
                boolean entitiesOverlap = gameWorld.entitiesOverlap(gameWorld.getEntities().get(i), gameWorld.getEntities().get(j));
                if (entitiesOverlap) {
                    gameWorld.handleCollision(gameWorld.getEntities().get(i), gameWorld.getEntities().get(j));
                }
            }
        }

        // 1. Make a copy of the entities list gameWorld.getEntities()
        // After moving all entities, gameWorld might have a few extra entities in it.

        // 2. (Better option) Keep track of all the new stuff separately
        // Don't make a copy of gameWorld.entities()
        // When shells are added, don't add them to the entities list directly.
        // Put them in a separate temp list instead.
        // Process (addSprite) that separate temp list, and then move them all to the main list.

        for (Entity shell : gameWorld.getShells()) {
            runGameView.addSprite(shell.getId(), RunGameView.SHELL_IMAGE_FILE, shell.getX(), shell.getY(), shell.getAngle());
            gameWorld.addEntity(shell);
        }

        gameWorld.clearShells();

        for (Entity entity : gameWorld.getEntities()) {
            runGameView.setSpriteLocationAndAngle(
                    entity.getId(), entity.getX(), entity.getY(), entity.getAngle());
        }

        clearDestroyedEntities();

        if (gameOver) {
            counter--;
            if (counter == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * resetGame is called at the end of the game once the gameplay loop exits. This should clear any existing data from
     * the game so that if the game is restarted, there aren't any things leftover from the previous run.
     */
    private void resetGame() {
        // TODO: Implement.
        runGameView.reset();
        ArrayList<Entity> originalEntities = new ArrayList<>(gameWorld.getEntities());
        for (Entity entity : originalEntities) {
            gameWorld.removeEntity(entity.getId());
        }
        counter = 200;
        gameOver = false;
    }

    private void clearDestroyedEntities() {
        // Iterates through destroyed entities to add the explosion animation and remove the entities from the game.
        ArrayList<Entity> destroyedEntities = new ArrayList<>(gameWorld.getDestroyedEntities());
        if (destroyedEntities.size() > 0) {
            for (Entity entity : destroyedEntities) {
                // If a Shell is destroyed, add Shell explosion animation.
                if (entity instanceof Shell) {
                    runGameView.addAnimation(
                            RunGameView.SHELL_EXPLOSION_ANIMATION,
                            5,
                            entity.getX(),
                            entity.getY());
                }
                // If Tank or Wall is destroyed, add big explosion animation.
                else if (entity instanceof Tank) {
                    runGameView.addAnimation(
                            RunGameView.BIG_EXPLOSION_ANIMATION,
                            10,
                            entity.getX(),
                            entity.getY());
                }
                else if (entity instanceof Wall) {
                    runGameView.addAnimation(
                            RunGameView.BIG_EXPLOSION_ANIMATION,
                            10,
                            entity.getX(),
                            entity.getY());
                }
            }

            // For each destroyed shell, remove it's sprite, and then remove from gameWorld.
            for (Entity entity : gameWorld.getDestroyedEntities()) {
                runGameView.removeSprite(entity.getId());
            }

            for (Entity entity : destroyedEntities) {
                gameWorld.removeEntity(entity.getId());
            }
        }

        gameWorld.clearDestroyedEntities();
    }

    public static void main(String[] args) {
        GameDriver gameDriver = new GameDriver();
        gameDriver.start();
    }
}

/*
    --- TO-DO LIST ---
    -Choose at least 15 points worth of Extra Features (Small = 3 pts, Medium = 6 pts, Large = 10 pts).

    --- EXTRA FEATURES ---
    -Game UI (Showing playerTank health, score bar, etc.) -> Small = 3 pts
    -Add a Pause screen? -> Medium = 6 pts
    -Animations -> Small = 3 pts

    --- OPTIONAL FIXES ---
    -Look into preventing friendly fire among enemy AI tanks (if AI tank shoots other AI tank, take no damage?)
    -Look into avoiding code duplication (Ex: similar code between AI tank .move() methods)
    -Fix if anything weird with shell collision with playerTank (seems different than Shell collision with other Tanks).
    -Look into potential fix for collision between Tanks and Walls.
    -Look into fixing the case when one Shell fired destroys two Walls at a time (fix to only hit one at a time?).
 */
