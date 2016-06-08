package ca.unbc.gol;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Task updates a section of the grid
 * @author Devin Calado
 */
class GameTask implements Runnable {

    private final Rectangle region;
    private final Game game;
    
    private Grid currentStates;
    private Grid nextStates;
    
    GameTask(Game game, Rectangle region) {
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
    private void updateSection() {
        
        List<Integer> neighbors;  // Number of neighbors this cell has

        for (int y = region.y; y < region.y + region.height; y++) {
            for (int x = region.x; x < region.x + region.width; x++) {
                neighbors = this.countNeighbours(y, x); // Count the surrounding neighbours
                // Apply the rules based on how many neighbors a cell has.
                if (currentStates.getState(x, y) != Grid.DEAD) {  //  rules for the living
                    if (neighbors.size() < 2 || neighbors.size() > 3) {      // a living cell dies from under/over population
                        nextStates.setState(x, y, Grid.DEAD);
                    }
                    if (neighbors.size() == 2 || neighbors.size() == 3) {  // a living cell stays alive with 2 or 3 neighbors
                        nextStates.setState(x, y, GeneCrossover.StochasticCrossOver(neighbors));
                    }
                } else {
                    if (currentStates.getState(x, y) == Grid.DEAD && neighbors.size() == 3) {   // rules for waking the dead
                        nextStates.setState(x, y, GeneCrossover.StochasticCrossOver(neighbors));
                    }
                    if (currentStates.getState(x, y) == Grid.DEAD && neighbors.size() != 3) {
                        nextStates.setState(x, y, Grid.DEAD);
                    }
                }
            }
        }
    }

    //Counts the number of neighbours affecting this cell.
    private ArrayList<Integer> countNeighbours(int y, int x) {
        
        ArrayList<Integer> neighbors = new ArrayList<>(3);
        for (int neighbor_y = y - 1; neighbor_y <= y + 1; neighbor_y++) {
            for (int neighbor_x = x - 1; neighbor_x <= x + 1; neighbor_x++) {
                if (
                           neighbor_y > 0 && neighbor_y < currentStates.getHeight()
                        && neighbor_x > 0 && neighbor_x < currentStates.getWidth() //In bounds
                        && (neighbor_y != y || neighbor_x != x) //Not self
                        && currentStates.getState(neighbor_x, neighbor_y) != Grid.DEAD //Neighbor is alive
                    ) {
                    neighbors.add(currentStates.getState(neighbor_x, neighbor_y));
                }
            }
        }
        
        return neighbors;
    }
}
