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
                
                //Show FPS
                graphics.setColor(Color.DARK_GRAY);
                int bottom = game.canvas.getBounds().height;
                graphics.fillRect(0, bottom - 20, 75, 15);
                graphics.setColor(Color.white);
                graphics.setFont(FONT);
                graphics.drawString("FPS: " + String.format("%.3g%n", FrameRate), 10, bottom - 10);
                
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
