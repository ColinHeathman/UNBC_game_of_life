package gol;

/**
 *
 * @author Devin Calado
 */
public class BoardThread extends Thread {

    private int firstRowIndex;
    private int lastRowIndex;
    private int firstColIndex;
    private int lastColIndex;
    private static boolean[][] myCurrentStates;

    /*
     * Initialize the thread by passing: the current board, and the area of the board to be computed. (indexes start at 0)
     */
    public BoardThread(int startRowIndex, int endRowIndex, int startColIndex, int endColIndex) {
        firstRowIndex = startRowIndex;
        lastRowIndex = endRowIndex;
        firstColIndex = startColIndex;
        lastColIndex = endColIndex;
    }

    /*
     * The run method for the Thread Object, Running will compute the changes in a section of the board.
     */
    @Override
    public void run() {
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
        byte neighbors;  // Number of neighbors this cell has

        for (int i = firstRowIndex; i <= lastRowIndex; i++) {
            for (int j = firstColIndex; j <= lastColIndex; j++) {
                neighbors = this.countNeighbours(i, j); // Count the surrounding neighbours
                // Apply the rules based on how many neighbors a cell has.
                if (myCurrentStates[i][j] == true) {  //  rules for the living
                    if (neighbors < 2 || neighbors > 3) {      // a living cell dies from under/over population
                        Client.nextStates[i][j] =false;
                    }
                    if (neighbors == 2 || neighbors == 3) {  // a living cell stays alive with 2 or 3 neighbors
                        Client.nextStates[i][j] =true;
                    }
                } else {
                    if (myCurrentStates[i][j] == false && neighbors == 3) {   // rules for waking the dead
                        Client.nextStates[i][j] = true;
                    }
                    if (myCurrentStates[i][j] == false && neighbors != 3) {
                        Client.nextStates[i][j] = false;
                    }
                }
            }
        }
    }

    //Counts the number of neighbours affecting this cell.
    public byte countNeighbours(int row, int col) {
        byte count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                try {
                    if (myCurrentStates[i][j] == true && (i != row || j != col)) {
                        count++;  // +1 neighbour
                    }
                } catch (ArrayIndexOutOfBoundsException f) {
                    continue;  // ignore indices that are outside of the board
                }
            }
        }
        return count;
    }

    public static void setMyCurrentStates(boolean[][] myCurrentStates) {
        BoardThread.myCurrentStates = myCurrentStates;
        myCurrentStates = null;
    }
        
}
