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

    KeyboardReader keyboardReader = KeyboardReader.instance();

    public static boolean paused = false;
    public static boolean restartGame = false;
    private boolean gameOver = false;
    private int endGameCounter = 200;

    public GameDriver() {
        mainView = new MainView(this::startMenuActionPerformed, this::pauseMenuActionPerformed);
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

    private void pauseMenuActionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case PauseMenuView.RESUME_BUTTON_ACTION_COMMAND -> {
                paused = false;
                mainView.setScreen(MainView.Screen.RUN_GAME_SCREEN);
            }
            case PauseMenuView.RESTART_BUTTON_ACTION_COMMAND -> {
                paused = false;
                restartGame = true;
            }
            case PauseMenuView.EXIT_BUTTON_ACTION_COMMAND -> mainView.closeGame();
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
        Entity simpleTank = new SimpleTank(
                Constants.AI_TANK_1_ID,
                Constants.AI_TANK_1_INITIAL_X,
                Constants.AI_TANK_1_INITIAL_Y,
                Constants.AI_TANK_1_INITIAL_ANGLE);
        Entity advancedTank = new AdvancedTank(
                Constants.AI_TANK_2_ID,
                Constants.AI_TANK_2_INITIAL_X,
                Constants.AI_TANK_2_INITIAL_Y,
                Constants.AI_TANK_2_INITIAL_ANGLE);
        Entity powerUp = new PowerUp(
                Constants.POWER_UP_ID,
                Constants.POWER_UP_INITIAL_X,
                Constants.POWER_UP_INITIAL_Y);

        // Adds all initial entities to the gameWorld.
        gameWorld.addEntity(playerTank);
        gameWorld.addEntity(simpleTank);
        gameWorld.addEntity(advancedTank);
        gameWorld.addEntity(powerUp);

        // View part (Adds sprites to initial entities):
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
        runGameView.addSprite(
                powerUp.getId(),
                RunGameView.POWER_UP_IMAGE_FILE,
                powerUp.getX(),
                powerUp.getY(),
                powerUp.getAngle()
        );

        // Adds different iterations of healthBar (0 to 4) to gameWorld.
        for (int i = 0; i < 5; i++) {
            HealthBar healthBar = new HealthBar(
                    Constants.HEALTH_BAR_INITIAL_X,
                    Constants.HEALTH_BAR_INITIAL_Y);
            gameWorld.addHealthBar(healthBar);
        }

        // Only adds sprite for start of game 4 health points Health Bar.
        HealthBar healthBar = gameWorld.getHealthBar();
        runGameView.addSprite(
                healthBar.getId(),
                healthBar.getImageFile(),
                healthBar.getX(),
                healthBar.getY(),
                healthBar.getAngle()
        );
    }

    /**
     * updateGame is repeatedly called in the gameplay loop. The code in this method should run a single frame of the
     * game. As long as it returns true, the game will continue running. If the game should stop for whatever reason
     * (e.g. the player tank being destroyed, escape being pressed), it should return false.
     */
    private boolean updateGame() {
        // TODO: Implement.

        if (paused) {
            return true;
        } else {
            if (restartGame) {
                return false;
            }

            // Pause game conditions:
            if (keyboardReader.escapePressed() && !paused) {
                // If game is running and ESC is pressed, game will pause.
                mainView.setScreen(MainView.Screen.PAUSE_MENU_SCREEN);
                paused = true;
            }

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

            for (Entity shell : gameWorld.getShells()) {
                gameWorld.addEntity(shell);
                runGameView.addSprite(
                        shell.getId(),
                        RunGameView.SHELL_IMAGE_FILE,
                        shell.getX(),
                        shell.getY(),
                        shell.getAngle());
            }

            gameWorld.clearShells();

            for (Entity entity : gameWorld.getEntities()) {
                runGameView.setSpriteLocationAndAngle(
                        entity.getId(), entity.getX(), entity.getY(), entity.getAngle());
            }

            clearDestroyedEntities();

            // Update Health Bar images to match playerHealth.
            List<HealthBar> copyHealthBars = gameWorld.getHealthBars();
            for (HealthBar healthBar : copyHealthBars) {
                runGameView.removeSprite(healthBar.getId());
                if (healthBar.getBarID() == gameWorld.getHealthBar().getBarID()) {
                    runGameView.addSprite(
                            healthBar.getId(),
                            healthBar.getImageFile(),
                            healthBar.getX(),
                            healthBar.getY(),
                            healthBar.getAngle());
                    runGameView.setSpriteLocationAndAngle(
                            healthBar.getId(),
                            healthBar.getX(),
                            healthBar.getY(),
                            healthBar.getAngle());
                }
            }

            // Check for endgame conditions:
            // If playerTank dies, or if all AI Tanks die, the game ends. If game ends, it goes to restart screen.
            if (gameWorld.getEntity(Constants.PLAYER_TANK_ID) == null) {
                gameOver = true;
            } else if (gameWorld.getEntity(Constants.AI_TANK_1_ID) == null
                    && gameWorld.getEntity(Constants.AI_TANK_2_ID) == null) {
                gameOver = true;
            }

            if (gameOver) {
                endGameCounter--;
                if (endGameCounter == 0) {
                    return false;
                }
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
        endGameCounter = 200;
        gameOver = false;
        paused = false;
        restartGame = false;
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

    --- IN PROGRESS EXTRA FEATURES (3 pts or 1 Small Feature needed) ---


    --- DONE EXTRA FEATURES ---
    * (M) Pause Screen
        - Works okay, goes to end game screen when restart is clicked, needs fixing.
        - Ideally if Restart button pressed, it would immediately reset/restart game.
    * (S) Animations
    * (S) Sound
        - Could use volume adjusting for all or specific sounds; can add more sounds.
        - Add Sound Effects for playerTank reload (indicating to player when they can fire their next shell),
        explosions (Tank, hitting walls, other collision) and possibly more.
    * (S or M) Power ups
        - Currently have one just for shooting limitless shells for a small duration for playerTank = Small feature.
    * (S) Game UI
        - Shows playerHealth on top right of the screen and updates as playerTank health goes down.

    --- OPTIONAL FIXES ---
    -Look into preventing friendly fire among enemy AI tanks (if AI tank shoots other AI tank, take no damage?)
    -Look into avoiding code duplication (Ex: similar code between AI tank .move() methods)
    -Fix if anything weird with shell collision with playerTank (seems different than Shell collision with other Tanks).
    -Look into potential fix for weird collision between Tanks and Walls.
    -Look into fixing the case when one Shell fired destroys two Walls at a time (fix to only hit one at a time?).
    -Fix clunky/messy or figure out more efficient way to implement Health Bar for playerTank.

    NOTE: Concurrent error happens when trying to modify same list as you're iterating it!
    NOTE: NullPtrException happens when i.e. something is trying to be called/reference when it is null/doesn't exist.
 */
