package edu.csc413.tankgame.model;

import java.util.*;

/**
 * GameWorld holds all of the model objects present in the game. GameWorld tracks all moving entities like tanks and
 * shells, and provides access to this information for any code that needs it (such as GameDriver or entity classes).
 */
public class GameWorld {
    // TODO: Implement. There's a lot of information the GameState will need to store to provide contextual information.
    //       Add whatever instance variables, constructors, and methods are needed.
    private final List<Entity> entities;
    private final List<Entity> newShells;
    private final List<Entity> destoryedShells;

    public GameWorld() {
        // TODO: Implement.
        entities = new ArrayList<>();
        newShells = new ArrayList<>();
        destoryedShells = new ArrayList<>();
    }

    /** Returns a list of all entities in the game. */
    public List<Entity> getEntities() {
        // TODO: Implement.
        return entities;
    }

    /** Adds a new entity to the game. */
    public void addEntity(Entity entity) {
        // TODO: Implement.
        entities.add(entity);
    }

    /** Returns the Entity with the specified ID. */
    public Entity getEntity(String id) {
        // TODO: Implement.
        for (Entity entity : entities) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }
        return null;
    }

    /** Removes the entity with the specified ID from the game. */
    public void removeEntity(String id) {
        // TODO: Implement.
        List<Entity> tempList = new ArrayList<>(entities);
        for (Entity entity : tempList) {
            if (entity.getId().equals(id)) {
                entities.remove(entity);
            }
        }
    }

    // Can expands to more than just shells, like if the game will have regenerating walls
    // or something similar. Expand to Entity type instead of Shell.

    public void addShell(Entity shell) {
        newShells.add(shell);
    }

    public List<Entity> getShells() {
        return newShells;
    }

    public void clearShells() {
        newShells.clear();
    }

    public void destroyShell(String id) {
        destoryedShells.add(getEntity(id));
    }

    public List<Entity> getDestroyedShells() {
        return destoryedShells;
    }

    public void clearDestroyedShells() {
        destoryedShells.clear();
    }
}
