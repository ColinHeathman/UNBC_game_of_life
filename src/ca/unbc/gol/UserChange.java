package ca.unbc.gol;

import java.awt.Point;

/**
 * Encapsulates a user's change to the game
 */
class UserChange {
    
    final Point point;
    final int color;
    
    UserChange(Point point, int color) {
        this.point = new Point(point);
        this.color = color;
    }
}
