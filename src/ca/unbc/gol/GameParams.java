package ca.unbc.gol;

public class GameParams {
    
    private int width = 800;
    private int height = 600;
    private int threads = 8;
    private int maximumFrameRate = 10;
    
    private boolean randomized = true;
    private boolean randomInitialColor = true;
    private int defaultInitialColor = Grid.GREEN;
    
    private boolean visible = true;
    
    /**
     * Parameters bean for Game of Life
     */
    public GameParams() {}

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getMaximumFrameRate() {
        return maximumFrameRate;
    }

    public void setMaximumFrameRate(int maximumFrameRate) {
        this.maximumFrameRate = maximumFrameRate;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isRandomized() {
        return randomized;
    }

    public void setRandomized(boolean randomize) {
        this.randomized = randomize;
    }

    public boolean isRandomInitialColor() {
        return randomInitialColor;
    }

    public void setRandomInitialColor(boolean randomInitialColor) {
        this.randomInitialColor = randomInitialColor;
    }

    public int getDefaultInitialColor() {
        return defaultInitialColor;
    }

    public void setDefaultInitialColor(int defaultInitialColor) {
        this.defaultInitialColor = defaultInitialColor;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    
}
