package gol;

import java.awt.Point;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Colin Heathman
 */
public class Camera {
    
    private Point offset;
    private int zoom;
    
    public Camera() {
        zoom = 1;
        offset = new Point();
    }
    
    public AffineTransform getTransform() {
        AffineTransform transform = new AffineTransform();
        transform.translate(offset.x, offset.y);
        transform.scale(zoom, zoom);
        return transform;
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
    
}
