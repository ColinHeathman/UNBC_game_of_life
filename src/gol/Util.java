package gol;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Util {
     
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
    
    public static boolean isPowerOfTwo(int number) {
        while(number % 2 == 0 && number > 1) {
            number /= 2;
        }
        return number == 1;
    }
    
}

