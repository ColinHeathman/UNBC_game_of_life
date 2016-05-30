package gol;

public class GameParams {
    
    private int width = 800;
    private int height = 600;
    private int threads = 8;
    private int maximumFrameRate = 10;
    
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
    
    
}
