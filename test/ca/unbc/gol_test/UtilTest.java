package ca.unbc.gol_test;

import ca.unbc.gol.Util;
import java.awt.Rectangle;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Colin Heathman
 */
public class UtilTest {
    
     @Test
     public void testPowerOfTwo1() {
         Assert.assertTrue(ca.unbc.gol.Util.isPowerOfTwo(1));
     }
     
     @Test
     public void testPowerOfTwo2() {
         Assert.assertTrue(ca.unbc.gol.Util.isPowerOfTwo(2));
     }
     
     @Test
     public void testPowerOfTwo3() {
         Assert.assertTrue(ca.unbc.gol.Util.isPowerOfTwo(4));
     }
     
     @Test
     public void testPowerOfTwo4() {
         Assert.assertTrue(ca.unbc.gol.Util.isPowerOfTwo(256));
     }
     
     @Test
     public void testNotPowerOfTwo1() {
         Assert.assertFalse(ca.unbc.gol.Util.isPowerOfTwo(0));
     }
     
     @Test
     public void testNotPowerOfTwo2() {
         Assert.assertFalse(ca.unbc.gol.Util.isPowerOfTwo(3));
     }
     
     @Test
     public void testNotPowerOfTwo3() {
         Assert.assertFalse(ca.unbc.gol.Util.isPowerOfTwo(7));
     }
     
     @Test
     public void testNotPowerOfTwo4() {
         Assert.assertFalse(ca.unbc.gol.Util.isPowerOfTwo(255));
     }
     
     @Test
     public void testSubdivisionWidths() {
         
         int width = 800;
         int height = 600;
         Rectangle rect = new Rectangle(0,0,width,height);
         List<Rectangle> regions = ca.unbc.gol.Util.subdivideArea(rect, 8);
         
         int widthSum = 0;
         
         for(Rectangle region : regions) {
             if(region.y == 0) {
                 widthSum += region.width;
             }
         }
         
         Assert.assertEquals(widthSum, width);
     }
     
     @Test
     public void testSubdivisionHeights() {
         
         int width = 800;
         int height = 600;
         Rectangle rect = new Rectangle(0,0,width,height);
         List<Rectangle> regions = ca.unbc.gol.Util.subdivideArea(rect, 8);
         
         int heightSum = 0;
         
         for(Rectangle region : regions) {
             if(region.x == 0) {
                 heightSum += region.height;
             }
         }
         
         Assert.assertEquals(heightSum, height);
     }
     
     @Test
     public void testSubdivisionArea() {
         
         int width = 800;
         int height = 600;
         int area = width * height;
         Rectangle rect = new Rectangle(0,0,width,height);
         List<Rectangle> regions = ca.unbc.gol.Util.subdivideArea(rect, 8);
         
         int areaSum = 0;
         
         for(Rectangle region : regions) {
             areaSum += region.width * region.height;
         }
         
         Assert.assertEquals(areaSum, area);
     }
     
     @Test
     public void testSubdivisionWidthBounds() {
         
         int width = 800;
         int height = 600;
         int area = width * height;
         Rectangle rect = new Rectangle(0,0,width,height);
         List<Rectangle> regions = ca.unbc.gol.Util.subdivideArea(rect, 8);
         
         for(Rectangle region : regions) {
            Assert.assertTrue(region.x + region.width <= width);
         }
     }
     
     @Test
     public void testSubdivisionHeightBounds() {
         
         int width = 800;
         int height = 600;
         int area = width * height;
         Rectangle rect = new Rectangle(0,0,width,height);
         List<Rectangle> regions = ca.unbc.gol.Util.subdivideArea(rect, 8);
         
         for(Rectangle region : regions) {
            Assert.assertTrue(region.y + region.height <= height);
         }
     }
     
     @Test
     public void testSwap() {
         int[] test = {1,2};
         
         Util.swap(test, 0, 1);
         
         Assert.assertTrue(test[0] == 2 && test[1] == 1);
     }
     
}
