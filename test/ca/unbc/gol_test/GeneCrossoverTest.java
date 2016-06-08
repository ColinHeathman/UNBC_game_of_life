package ca.unbc.gol_test;

import ca.unbc.gol.GeneCrossover;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Colin Heathman
 */
public class GeneCrossoverTest {
    
    @Test
    public void testGeneCrossoverTest1() {
        int first =  0xFFFFFFFF;
        int second = 0x00000000;
        
        ArrayList<Integer> genes = new ArrayList<>();
        genes.add(0, first);
        genes.add(1, second);
        
        int child = GeneCrossover.StochasticCrossOver(genes);
        
        System.out.println(first + " + " + second + " = " + child);
        Assert.assertTrue(child != 0);
    }
    
    @Test
    public void testGeneCrossoverTest2() {
        int first =  100;
        int second = 0;
        
        ArrayList<Integer> genes = new ArrayList<>();
        genes.add(0, first);
        genes.add(1, second);
        
        int child = GeneCrossover.StochasticCrossOver(genes);
        
        System.out.println(first + " + " + second + " = " + child);
        Assert.assertTrue(child > 0 && child < 100);
    }
    
    @Test
    public void testGeneCrossoverTest3() {
        int first =  0;
        int second = 0;
        
        ArrayList<Integer> genes = new ArrayList<>();
        genes.add(0, first);
        genes.add(1, second);
        
        int child = GeneCrossover.StochasticCrossOver(genes);
        
        System.out.println(first + " + " + second + " = " + child);
        Assert.assertTrue(child == 0);
    }
    
    @Test
    public void testGeneCrossoverTest4() {
        int first =  -1;
        int second = -1;
        
        ArrayList<Integer> genes = new ArrayList<>();
        genes.add(0, first);
        genes.add(1, second);
        
        int child = GeneCrossover.StochasticCrossOver(genes);
        
        System.out.println(first + " + " + second + " = " + child);
        Assert.assertTrue(child == -1);
    }
}
