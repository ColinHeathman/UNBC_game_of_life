package gol;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game extends Thread {

    private final int height;
    private final int width;
    private final ExecutorService pool;
    private ScheduledExecutorService mainExecutor;
    private final long frameDelay;
    private final List<Callable<Object>> tasks;
    private Runnable updateDisplayTask;

    private Grid currentStates;
    private Grid nextStates;
    private final BlockingQueue<UserChange> userChangesQueue;
    private boolean paused;
    private boolean togglePause;
    
    public Game(GameParams params) {
        this.height = params.getHeight();
        this.width = params.getWidth();
        this.pool = Executors.newFixedThreadPool(params.getThreads());
        this.frameDelay = (long) (TimeUnit.SECONDS.toNanos(1) * (1 / (double) params.getMaximumFrameRate()));
        
        currentStates = new Grid(width, height);
        nextStates = new Grid(width, height);
        userChangesQueue = new LinkedBlockingQueue();
        paused = false;
        
        List<Rectangle> regions = Util.subdivideArea(
                new Rectangle(width, height),
                ((int) (params.getThreads() / 2)) * 2
        );
        
        tasks = new ArrayList(regions.size());
        
        for(int i = 0; i < regions.size(); i++) {
            tasks.add(Executors.callable(new GameTask(this, regions.get(i))));
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
            //Apply any user changes
            while(!userChangesQueue.isEmpty()) {
                UserChange change = userChangesQueue.take();
                currentStates.setState(change.point.x, change.point.y, change.color);
            }
            
            List<Future> futures = new ArrayList<>();
            //Display grid
            if(updateDisplayTask != null) {
                futures.add(
                    pool.submit(updateDisplayTask)
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
    
    public void setUpdateDisplayTask(Runnable updateDisplayTask) {
        this.updateDisplayTask = updateDisplayTask;
    }

    BlockingQueue getUserChangesQueue() {
        return userChangesQueue;
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