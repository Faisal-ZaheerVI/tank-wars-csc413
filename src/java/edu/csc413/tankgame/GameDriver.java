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

        // List<WallInformation> wallInfo = WallInformation.readWalls();

        // Create entities in the game world based on entity type.
        Entity playerTank = new PlayerTank(
                Constants.PLAYER_TANK_ID,
                Constants.PLAYER_TANK_INITIAL_X,
                Constants.PLAYER_TANK_INITIAL_Y,
                Constants.PLAYER_TANK_INITIAL_ANGLE);
//        SimpleTank simpleTank = new SimpleTank(
//                Constants.AI_TANK_1_ID,
//                Constants.AI_TANK_1_INITIAL_X,
//                Constants.AI_TANK_1_INITIAL_Y,
//                Constants.AI_TANK_1_INITIAL_ANGLE);
        Entity advancedTank = new AdvancedTank(
                Constants.AI_TANK_2_ID,
                Constants.AI_TANK_2_INITIAL_X,
                Constants.AI_TANK_2_INITIAL_Y,
                Constants.AI_TANK_2_INITIAL_ANGLE
        );
        gameWorld.addEntity(playerTank);
//        gameWorld.addEntity(simpleTank);
        gameWorld.addEntity(advancedTank);

        // View part:
        runGameView.addSprite(
                playerTank.getId(),
                RunGameView.PLAYER_TANK_IMAGE_FILE,
                playerTank.getX(),
                playerTank.getY(),
                playerTank.getAngle());
//        runGameView.addSprite(
//                simpleTank.getId(),
//                RunGameView.AI_TANK_IMAGE_FILE,
//                simpleTank.getX(),
//                simpleTank.getY(),
//                simpleTank.getAngle());
        runGameView.addSprite(
                advancedTank.getId(),
                RunGameView.AI_TANK_IMAGE_FILE,
                advancedTank.getX(),
                advancedTank.getY(),
                advancedTank.getAngle());
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

        for (Entity entity : originalEntities) {
            entity.checkBounds(gameWorld);
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
        for (Entity entity : gameWorld.getEntities()) {
            runGameView.removeSprite(entity.getId());
        }
        for (Entity entity : originalEntities) {
            gameWorld.removeEntity(entity.getId());
        }
    }

    public static void main(String[] args) {
        GameDriver gameDriver = new GameDriver();
        gameDriver.start();
    }
}
