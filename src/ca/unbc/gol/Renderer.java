package ca.unbc.gol;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

//Renders the game onto the canvas
class Renderer {

    private static final Font FONT = new Font("Terminal",Font.BOLD,10);
    
    private final Game game;
    private final BufferStrategy strategy;
    private final BufferedImage image;
            
    final Runnable renderTask;
    
    Renderer(Game game) {
        
        this.renderTask = () -> {update();};
        
        this.game = game;
        
        game.canvas.createBufferStrategy(2);
        this.strategy = game.canvas.getBufferStrategy();
        this.image = new BufferedImage(game.getWidth(), game.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    }
        
    private void update() {
        updateFPS();
        
        // Render single frame
        do {
            // The following loop ensures that the contents of the drawing buffer
            // are consistent in case the underlying surface was recreated
            do {
                // Get a new graphics context every time through the loop
                // to make sure the strategy is validated
                Graphics2D graphics = (Graphics2D) strategy.getDrawGraphics();

                graphics.setColor(Color.GRAY);
                graphics.fill(game.canvas.getBounds());
                
                // Blit image
                for(int y = 0; y < game.getHeight(); y++) {
                    for(int x = 0; x < game.getWidth(); x++) {
                        image.setRGB(x, y, game.getCurrentStates().getState(x, y));
                    }
                }
                
                //Draw image
                graphics.drawImage(image,game.camera.getTransform(),game.canvas);
                
                //Draw controls
                graphics.setColor(Color.DARK_GRAY);
                int bottom = game.canvas.getBounds().height;
                int right = game.canvas.getBounds().width;
                graphics.fillRect(0, bottom - 20, right, 15);
                graphics.setFont(FONT);
                
                graphics.setColor(Color.RED);
                if(!game.controller.randomColor() && game.controller.getColor() == Grid.RED) {
                    graphics.drawString("( 1 - Red )", 10, bottom - 10);
                } else {
                    graphics.drawString("  1 - Red  ", 10, bottom - 10);
                }
                graphics.setColor(Color.GREEN);
                if(!game.controller.randomColor() && game.controller.getColor() == Grid.GREEN) {
                    graphics.drawString("(2 - Green)", 70, bottom - 10);
                } else {
                    graphics.drawString(" 2 - Green ", 70, bottom - 10);
                }
                graphics.setColor(Color.BLUE);
                if(!game.controller.randomColor() && game.controller.getColor() == Grid.BLUE) {
                    graphics.drawString("(3 - Blue )", 130, bottom - 10);
                } else {
                    graphics.drawString(" 3 - Blue  ", 130, bottom - 10);
                }
                graphics.setColor(Color.WHITE);
                if(!game.controller.randomColor() && game.controller.getColor() == Grid.WHITE) {
                    graphics.drawString("(4 - White)", 190, bottom - 10);
                } else {
                    graphics.drawString(" 4 - White ", 190, bottom - 10);
                }
                graphics.setColor(Color.BLACK);
                if(!game.controller.randomColor() && game.controller.getColor() == Grid.DEAD) {
                    graphics.drawString("(5 - Black)", 250, bottom - 10);
                } else {
                    graphics.drawString(" 5 - Black ", 250, bottom - 10);
                }
                if(game.controller.randomColor()) {
                    graphics.setColor(new Color(game.controller.getColor()));
                    graphics.drawString("(6 - Random)", 310, bottom - 10);
                } else {
                    graphics.setColor(Color.WHITE);
                    graphics.drawString(" 6 - Random ", 310, bottom - 10);
                }
                      
                
                
                graphics.setColor(Color.white);
                StringBuilder statusString = new StringBuilder();
                statusString.append(" [R]andomize   ");
                statusString.append(" [C]lear       ");
                statusString.append(" [Space] pause ");
                statusString.append("  FPS: ").append(String.format("%.3g%n", FrameRate));
                
                graphics.drawString(statusString.toString(), 375, bottom - 10);
                
                
                                
                // Dispose the graphics
                graphics.dispose();

                // Repeat the rendering if the drawing buffer contents
                // were restored
            } while (strategy.contentsRestored());

            // Display the buffer
            strategy.show();

            // Repeat the rendering if the drawing buffer was lost
        } while (strategy.contentsLost());
        
    }
    
    //FPS business
    private float FrameRate = 0.0f;
    private long lastFrameRateCheckTime = 0;
    
    private void updateFPS() {
        long thisCheck = System.nanoTime();
        long elapsedTime = thisCheck - lastFrameRateCheckTime;
        FrameRate =  (float) TimeUnit.SECONDS.toNanos(1) / elapsedTime;
        lastFrameRateCheckTime = thisCheck;
    }
    
    
}
