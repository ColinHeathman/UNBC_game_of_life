package ca.unbc.gol;

import java.util.List;
import java.util.Random;

/**
 * Breeds colors together using a random crossover algorithm
 * @author Colin Heathman
 */
public class GeneCrossover {
    public static int StochasticCrossOver(List<Integer> genes) {
        
        int maskSize = 32;
        while(maskSize % genes.size() != 0) {
            maskSize++;
        }
        
        int[] mask = new int[maskSize];
        for(int i = 0; i < mask.length; i++) {
            mask[i] = i % genes.size(); //1,2,3,4,1,2,3,....
        }
        
        //Scramble the mask
        Random random = new Random();
        for(int i = 0; i < maskSize * 2; i++) {
            int first = random.nextInt(maskSize);
            int second = random.nextInt(maskSize);
            Util.swap(mask, first, second);
        }
        
        int child = 0;
        
        for(int i = 0; i < 32; i++) {
            child |= genes.get(mask[i]) & (1 << i);
        }
        
        return child;
    }
    
}
