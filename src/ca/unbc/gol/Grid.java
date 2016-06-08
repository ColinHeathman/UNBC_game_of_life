package ca.unbc.gol;

import java.awt.Color;

/**
 * Game grid 
 */
public class Grid {
    
    public static final int DEAD = Color.BLACK.getRGB();
    public static final int GREEN = Color.GREEN.getRGB();
    public static final int RED = Color.RED.getRGB();
    public static final int BLUE = Color.BLUE.getRGB();
    public static final int WHITE = Color.WHITE.getRGB();
    
    private final int[] states;
    private final int width;
    private final int height;
    
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        states = new int[width * height];
    }
    
    private int map(int x, int y) {
        if(x < 0 || x >= width) throw new IndexOutOfBoundsException("Board x index out of bounds");
        if(y < 0 || y >= height) throw new IndexOutOfBoundsException("Board y index out of bounds");
        return x + (y * width);
    }
    
    public int getState(int x, int y) {
        return states[map(x, y)];
    }
    
    public void setState(int x, int y, int state) {
        states[map(x, y)] = state;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
}
