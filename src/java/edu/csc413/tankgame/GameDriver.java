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

        gameWorld.addEntity(playerTank);
        gameWorld.addEntity(simpleTank);

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
    }

    /**
     * updateGame is repeatedly called in the gameplay loop. The code in this method should run a single frame of the
     * game. As long as it returns true, the game will continue running. If the game should stop for whatever reason
     * (e.g. the player tank being destroyed, escape being pressed), it should return false.
     */
    private boolean updateGame() {
        // TODO: Implement.
        KeyboardReader keyboardReader = KeyboardReader.instance();

        if (keyboardReader.escapePressed()) {
            return false;
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

        // Iterates through destroyed shells to add the explosion animation and remove the shells from the game.
        ArrayList<Entity> destroyedEntities = new ArrayList<>(gameWorld.getDestroyedEntities());
        if (destroyedEntities.size() > 0) {
            for (Entity entity : destroyedEntities) {
                if (entity instanceof Shell) {
                    runGameView.addAnimation(RunGameView.SHELL_EXPLOSION_ANIMATION, 10, entity.getX(), entity.getY());
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

        return true;
    }

    /**
     * resetGame is called at the end of the game once the gameplay loop exits. This should clear any existing data from
     * the game so that if the game is restarted, there aren't any things leftover from the previous run.
     */
    private void resetGame() {
        // TODO: Implement.
        runGameView.reset();
    }

    public static void main(String[] args) {
        GameDriver gameDriver = new GameDriver();
        gameDriver.start();
    }
}
