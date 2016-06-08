package ca.unbc.gol;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class
 */
public class Util {
    
    /**
     * Subdivides a rectangle into smaller rectangles
     * @param rect Rectangle to subdivide, must have an even numbered width and height
     * @param regions number of regions to create, must be a power of two
     * @return List of Rectangle regions
     * @throws IllegalArgumentException 
     */
    public static List<Rectangle> subdivideArea(Rectangle rect, int regions) throws IllegalArgumentException {
        
        if(!isPowerOfTwo(regions)) {
            throw new IllegalArgumentException("Regions to subdivide must be a power of two");
        }
        
        //Rows and column count of regions
        int rows = regions / 2;
        int columns = regions / rows;
        
        if(rect.width % columns != 0 || rect.height % rows != 0) {
            throw new IllegalArgumentException("Cannot divide region evenly (use even numbered size)");
        }
        
        int regionWidth = rect.width / columns;
        int regionHeight = rect.height / rows;
        
        List<Rectangle> list = new ArrayList<>(regions);
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                list.add(new Rectangle(
                        x * regionWidth, 
                        y * regionHeight, 
                        regionWidth, 
                        regionHeight
                ));
            }
        }
        
        return list;
    }
    
    /**
     * Calculates if a number is a power of two
     * @param number number to test
     * @return true if number is a power of two, false otherwise
     */
    public static boolean isPowerOfTwo(int number) {
        while(number % 2 == 0 && number > 1) {
            number /= 2;
        }
        return number == 1;
    }
    
    /**
     * Swaps two elements in an array
     * @param array array to modify
     * @param first first element index
     * @param second second element index
     */
    public static void swap(int[] array, int first, int second) {
        int swap = array[first];
        array[first] = array[second];
        array[second] = swap;
    }
    
}
