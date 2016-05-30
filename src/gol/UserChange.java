package gol;

import java.awt.Point;

/**
 *
 * @author Colin Heathman
 */
class UserChange {
    
    public final Point point;
    public final int color;
    
    public UserChange(Point point, int color) {
        this.point = new Point(point);
        this.color = color;
    }
}
