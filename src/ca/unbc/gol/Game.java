package ca.unbc.gol;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Game of Life Game class
 */
public class Game extends Thread {

    private final int height;
    private final int width;
    private final ExecutorService pool;
    private ScheduledExecutorService mainExecutor;
    private final long frameDelay;
    private final List<Callable<Object>> tasks;

    private Grid currentStates;
    private Grid nextStates;
    private final BlockingQueue<UserChange> userChangesQueue;
    private boolean paused;
    private boolean togglePause;
    private final Semaphore gameLock;
    
    final Canvas canvas;
    final Camera camera;
    final Controller controller;
    final Renderer renderer;
    
    public Game(GameParams params, Container container) {
        this.height = params.getHeight();
        this.width = params.getWidth();
        this.pool = Executors.newFixedThreadPool(params.getThreads());
        this.frameDelay = (long) (TimeUnit.SECONDS.toNanos(1) * (1 / (double) params.getMaximumFrameRate()));
        
        currentStates = new Grid(width, height);
        nextStates = new Grid(width, height);
        userChangesQueue = new LinkedBlockingQueue();
        gameLock = new Semaphore(1);
        paused = false;
        
        List<Rectangle> regions = Util.subdivideArea(
                new Rectangle(width, height),
                ((int) (params.getThreads() / 2)) * 2
        );
        
        tasks = new ArrayList(regions.size());
        
        for(int i = 0; i < regions.size(); i++) {
            tasks.add(Executors.callable(new GameTask(this, regions.get(i))));
        }
        
        if(params.isRandomized()) {
            this.randomize(params.getDefaultInitialColor(), params.isRandomInitialColor());
        }
        
        if(params.isVisible()) {
            
            this.canvas = new Canvas();
            canvas.setVisible(true);
            container.add(canvas);
            
            this.camera = new Camera();
            this.renderer = new Renderer(this);

            this.controller = new Controller(this);
            container.setSize(params.getWidth(), params.getHeight());
            container.requestFocus();
        } else {
            
            this.canvas = null;
            this.camera = null;
            this.renderer = null;
            this.controller = null;
        }
    }
    
    @Override
    public void start() {
        mainExecutor = Executors.newSingleThreadScheduledExecutor();
        mainExecutor.scheduleAtFixedRate(this, 0, frameDelay, TimeUnit.NANOSECONDS);
    }
    
    @Override
    public void run() {
        try {
            if(togglePause) {
                togglePause = false;
                paused = !paused;
            }
            
            gameLock.acquire();
            
            //Apply any user changes
            while(!userChangesQueue.isEmpty()) {
                UserChange change = userChangesQueue.take();
                currentStates.setState(change.point.x, change.point.y, change.color);
            }
            
            List<Future> futures = new ArrayList<>();
            //Display grid
            if(renderer != null) {
                futures.add(
                    pool.submit(renderer.renderTask)
                );
            }

            //Compute next frame
            if(!paused) {
                futures.addAll(
                        pool.invokeAll(tasks)
                );
            }

            //Await threads
            for(Future f : futures) {
                f.get();
            }
            futures.clear();

            //Swap grids
            if(!paused) swapGrid();
            
            gameLock.release();
            
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
            pool.shutdownNow();
            System.exit(1);
        }
    }
    
    private void swapGrid() {
        Grid swap = nextStates;
        nextStates = currentStates;
        currentStates = swap;
    }
    
    public void randomize(int color) {
        this.randomize(color, false);
    }
    
    public void randomize(boolean randomColor) {
        this.randomize(0, true);
    }
    
    private void randomize(int color, boolean randomColor) {
        
        try {
            gameLock.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        Random rand = new Random();
        for(int y = 0; y < this.getHeight(); y++) {
            for(int x = 0; x < this.getWidth(); x++) {
                if(rand.nextBoolean()) {
                    if(randomColor) {
                        this.getCurrentStates().setState(x, y, rand.nextInt());
                    }
                    else {
                        this.getCurrentStates().setState(x, y, color);
                    }
                } else {
                    this.getCurrentStates().setState(x, y, Grid.DEAD);
                }
            }
        }
        gameLock.release();
    }
    
    public void clear() {
        try {
            gameLock.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int y = 0; y < this.getHeight(); y++) {
            for(int x = 0; x < this.getWidth(); x++) {
                this.getCurrentStates().setState(x, y, Grid.DEAD);
            }
        }
        gameLock.release();
    }
    
    public Grid getCurrentStates() {
        return currentStates;
    }

    public Grid getNextStates() {
        return nextStates;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
    
    BlockingQueue getUserChangesQueue() {
        return userChangesQueue;
    }
    
    public Canvas getCanvas() {
        return canvas;
    }

    public Camera getCamera() {
        return camera;
    }
    
    public void togglePaused() {
        togglePause = true;
    }
    
    public boolean isPaused() {
        return paused;
    }

    void dispose() {
        pool.shutdown();
        mainExecutor.shutdown();
    }
    
}