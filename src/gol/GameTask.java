package gol;

import java.awt.Rectangle;

/**
 *
 * @author Devin Calado
 */
public class GameTask implements Runnable {

    private final Rectangle region;
    private final Game game;
    
    private Grid currentStates;
    private Grid nextStates;
    
    public GameTask(Game game, Rectangle region) {
        this.game = game;
        this.region = region;
    }

    @Override
    public void run() {
        currentStates = game.getCurrentStates();
        nextStates = game.getNextStates();
        updateSection();
    }

    /* Updates the cells according to game rules.
     * 
     * Game Rules:
     * Any live cell with fewer than two live neighbours dies, as if caused by under-population.
     * Any live cell with two or three live neighbours lives on to the next generation.
     * Any live cell with more than three live neighbours dies, as if by overcrowding.
     * Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction. 
     */
    public void updateSection() {
        int neighbors;  // Number of neighbors this cell has

        
        for (int y = region.y; y < region.y + region.height; y++) {
            for (int x = region.x; x < region.x + region.width; x++) {
                neighbors = this.countNeighbours(y, x); // Count the surrounding neighbours
                // Apply the rules based on how many neighbors a cell has.
                if (currentStates.getState(x, y) == Grid.GREEN) {  //  rules for the living
                    if (neighbors < 2 || neighbors > 3) {      // a living cell dies from under/over population
                        nextStates.setState(x, y, Grid.DEAD);
                    }
                    if (neighbors == 2 || neighbors == 3) {  // a living cell stays alive with 2 or 3 neighbors
                        nextStates.setState(x, y, Grid.GREEN);
                    }
                } else {
                    if (currentStates.getState(x, y) == Grid.DEAD && neighbors == 3) {   // rules for waking the dead
                        nextStates.setState(x, y, Grid.GREEN);
                    }
                    if (currentStates.getState(x, y) == Grid.DEAD && neighbors != 3) {
                        nextStates.setState(x, y, Grid.DEAD);
                    }
                }
            }
        }
    }

    //Counts the number of neighbours affecting this cell.
    public int countNeighbours(int y, int x) {
        int count = 0;
        for (int neighbor_y = y - 1; neighbor_y <= y + 1; neighbor_y++) {
            for (int neighbor_x = x - 1; neighbor_x <= x + 1; neighbor_x++) {
                if (
                           neighbor_y > 0 && neighbor_y < currentStates.getHeight()
                        && neighbor_x > 0 && neighbor_x < currentStates.getWidth() //In bounds
                        && (neighbor_y != y || neighbor_x != x) //Not self
                        && currentStates.getState(neighbor_x, neighbor_y) == Grid.GREEN //Neighbor is alive
                    ) {
                    count++;  // +1 alive neighbour
                }
            }
        }
        return count;
    }
}
