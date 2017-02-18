package ca.unbc.gol;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;

/**
 *
 * @author Colin Heathman
 */
class Controller {

    private final Game game;
    private final Canvas canvas;
    private final Camera camera;
    
    private int color = Grid.RED;
    private Random random;
    private boolean randomColor = false;
    
    Controller(Game game) {
        
        this.game = game;
        this.canvas = game.canvas;
        this.camera = game.camera;
        this.random = new Random();
        
        this.canvas.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent evt) {
                mousePressedHandler(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                mouseReleasedHandler(evt);
            }
            
        });
        
        this.canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                mouseDraggedHandler(evt);
            }
        });
        
        this.canvas.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent evt) {
                mouseWheelMovedHandler(evt);
            }
        });
        
        this.canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        
        
    }
    
    //Mouse clicking biz
    private Point lastPoint;
    private int mouseButton;
    
    private void mousePressedHandler(MouseEvent evt) {
        mouseButton = evt.getButton();
        lastPoint = evt.getPoint();
        if(mouseButton == MouseEvent.BUTTON3) {
            paintCells(evt);
        }
    }
    
    private void mouseReleasedHandler(MouseEvent evt) {
        mouseButton = 0;
        lastPoint = null;
    }
    
    //Mouse draggin biz
    private void mouseDraggedHandler(MouseEvent evt) {
        if(mouseButton == MouseEvent.BUTTON1) {
            moveCamera(evt);
        } else if (mouseButton == MouseEvent.BUTTON3) {
            paintCells(evt);
        }
    }
    
    private void moveCamera(MouseEvent evt) {
        if(lastPoint != null) {
            
            camera.getOffset().translate(
                    evt.getPoint().x - lastPoint.x,
                    evt.getPoint().y - lastPoint.y
            );
            
            lastPoint = evt.getPoint();
        }
    }
    
    private void paintCells(MouseEvent evt) {
        Point mouse = globalToLocal(evt.getPoint());
        try {
            if(
                    mouse.x >= 0
                 && mouse.x < game.getWidth()
                 && mouse.y >= 0
                 && mouse.y < game.getHeight()
                    ) {
                if(randomColor) game.getUserChangesQueue().put(new UserChange(mouse, random.nextInt()));
                else            game.getUserChangesQueue().put(new UserChange(mouse, color));
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("User input interupted unexpectedly");
        }
    }

    private void mouseWheelMovedHandler(MouseWheelEvent evt) {
        
        Point mouse = globalToLocal(evt.getPoint());
        int mouseWheelRotation = evt.getWheelRotation();
        
        //mouse is the origin from which to zoom
        if(mouseWheelRotation < 0 && camera.getZoom() < 10) {
            camera.getOffset().setLocation(
                camera.getOffset().x - mouse.x,
                camera.getOffset().y - mouse.y
            );
            
            camera.setZoom(camera.getZoom() + 1);
            
        } else if (mouseWheelRotation > 0 && camera.getZoom() > 1) {
            
            camera.getOffset().setLocation(
                camera.getOffset().x + mouse.x,
                camera.getOffset().y + mouse.y
            );
            camera.setZoom(camera.getZoom() - 1);
        }

    }
    
    private Point globalToLocal(Point global) {
        
        Point local = new Point(
                (global.x - camera.getOffset().x) / camera.getZoom(),
                (global.y - camera.getOffset().y) / camera.getZoom()
        );
        
        return local;
    }
    
    private void keyPressedHandler(KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                game.togglePaused();
                break;
            case KeyEvent.VK_1:
                randomColor = false;
                color = Grid.RED;
                break;
            case KeyEvent.VK_2:
                randomColor = false;
                color = Grid.GREEN;
                break;
            case KeyEvent.VK_3:
                randomColor = false;
                color = Grid.BLUE;
                break;
            case KeyEvent.VK_4:
                randomColor = false;
                color = Grid.WHITE;
                break;
            case KeyEvent.VK_5:
                randomColor = false;
                color = Grid.DEAD;
                break;
            case KeyEvent.VK_6:
                randomColor = true;
                break;
            case KeyEvent.VK_R:
                //Randomize
                game.randomize(true);
                break;
            case KeyEvent.VK_C:
                //Clear
                game.clear();
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * @return Current selected color
     */
    public int getColor() {
        if(randomColor) return random.nextInt();
        else            return color;
    }
    
    /**
     * 
     * @return If the color will be random
     */
    public boolean randomColor() {
        return randomColor;
    }
}
