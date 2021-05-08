package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

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
    private final List<Entity> destroyedEntities;

    public GameWorld() {
        // TODO: Implement.
        entities = new ArrayList<>();
        newShells = new ArrayList<>();
        destroyedEntities = new ArrayList<>();
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

    /*
        --- CHECKING FOR NO OVERLAP ---
        entity2.getX() > entity1.getXBound() --> e2's left side is greater than e1's right side
        entity1.getX() > e2.getXBound() --> if true, entities dont overlap
        entity2.getY() > e1.getYBound()
        entity1.getY() > e2.getYBound() -->
     */

    public boolean entitiesOverlap(Entity entity1, Entity entity2) {
        if (entity1.getX() < entity2.getXBound()
        && entity1.getXBound() > entity2.getX()
        && entity1.getY() < entity2.getYBound()
        && entity1.getYBound() > entity2.getY()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void handleCollision(Entity entity1, Entity entity2) {
        // Sorts through and calculates shortest distance when two entities collide,
        // whether it's the shortest on the top or bottom and left or right between the two entities.
        double[] distances = {entity1.getXBound() - entity2.getX(), entity2.getXBound() - entity1.getX(),
                entity1.getYBound() - entity2.getY(), entity2.getYBound() - entity1.getY()};
        Arrays.sort(distances);
        // Used without modification for Tank on static entity collisions such as Tank on Wall collisions.
        double shortestDistance = distances[0];
        double distanceMoved = shortestDistance / 2; // Used only for Tank on Tank collisions.

        // Tank colliding with another Tank.
        if (entity1 instanceof Tank && entity2 instanceof Tank) {
            // e1 right hitting e2 left -> e1.getXBound() - e2.getX()
            // e2 right hitting e1 left -> e2.getXBound() - e1.getX()
            // e1 bottom hitting e2 top -> e1.getYBound() - e2.getY()
            // e2 bottom hitting e1 top -> e2.getYBound() - e1.getY()

            // Moving tank to right = add to X, moving tank to left = subtract from X
            // Moving tank up = subtract from Y, moving tank down = add to Y

            if (entity1.getXBound() - entity2.getX() == shortestDistance) {
                entity1.x = entity1.getX() - distanceMoved;
                entity2.x = entity2.getX() + distanceMoved;
            }
            else if (entity2.getXBound() - entity1.getX() == shortestDistance) {
                entity1.x = entity1.getX() + distanceMoved;
                entity2.x = entity2.getX() - distanceMoved;
            }
            else if (entity1.getYBound() - entity2.getY() == shortestDistance) {
                entity1.y = entity1.getY() - distanceMoved;
                entity2.y = entity2.getY() + distanceMoved;
            }
            else if (entity2.getYBound() - entity1.getY() == shortestDistance) {
                entity1.y = entity1.getY() + distanceMoved;
                entity2.y = entity2.getY() - distanceMoved;
            }
        }

        // Tank colliding with a Shell. (And Shell colliding with a Tank).
        else if (entity1 instanceof Tank && entity2 instanceof Shell) {
            destroyEntity(entity2.getId());
            ((Tank) entity1).reduceHealth();
            if (((Tank) entity1).getHealth() == 0) {
                destroyEntity(entity1.getId());
            }
        } else if (entity1 instanceof Shell && entity2 instanceof Tank) {
            destroyEntity(entity1.getId());
            ((Tank) entity2).reduceHealth();
            if (((Tank) entity2).getHealth() == 0) {
                destroyEntity(entity2.getId());
            }
        }

        // Tank colliding with a Wall. (And Wall being collided by a Tank).
        else if (entity1 instanceof Wall && entity2 instanceof Tank) {
            if (entity2.getXBound() - entity1.getX() == shortestDistance) {
                entity2.x = entity2.getX() - shortestDistance;
            }
            else if (entity1.getXBound() - entity2.getX() == shortestDistance) {
                entity2.x = entity2.getX() + shortestDistance;
            }
            else if (entity2.getYBound() - entity1.getY() == shortestDistance) {
                entity2.y = entity2.getY() - shortestDistance;
            }
            else if (entity1.getYBound() - entity2.getY() == shortestDistance) {
                entity2.y = entity2.getY() + shortestDistance;
            }
        }
        else if (entity1 instanceof Tank && entity2 instanceof Wall) {
            if (entity1.getXBound() - entity2.getX() == shortestDistance) {
                entity1.x = entity1.getX() - shortestDistance;
            }
            else if (entity2.getXBound() - entity1.getX() == shortestDistance) {
                entity1.x = entity1.getX() + shortestDistance;
            }
            else if (entity1.getYBound() - entity2.getY() == shortestDistance) {
                entity1.y = entity1.getY() - shortestDistance;
            }
            else if (entity2.getYBound() - entity1.getY() == shortestDistance) {
                entity1.y = entity1.getY() + shortestDistance;
            }
        }

        // Shell colliding with a Wall. (And Wall being collided by a Shell).
        else if (entity1 instanceof Shell && entity2 instanceof Wall) {
            destroyEntity(entity1.getId());
            ((Wall) entity2).reduceHealth();
            if (((Wall) entity2).getHealthPoints() == 0) {
                destroyEntity(entity2.getId());
            }
        } else if (entity2 instanceof Shell && entity1 instanceof Wall) {
            destroyEntity(entity2.getId());
            ((Wall) entity1).reduceHealth();
            if (((Wall) entity1).getHealthPoints() == 0) {
                destroyEntity(entity1.getId());
            }
        }

        // Shell colliding with a Shell.
        else if (entity1 instanceof Shell && entity2 instanceof Shell) {
            destroyEntity(entity1.getId());
            destroyEntity(entity2.getId());
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

    public void destroyEntity(String id) {
        destroyedEntities.add(getEntity(id));
    }

    public List<Entity> getDestroyedEntities() {
        return destroyedEntities;
    }

    public void clearDestroyedEntities() {
        destroyedEntities.clear();
    }
}
