package ca.unbc.gol;

import java.math.BigInteger;
import javax.swing.JFrame;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) throws ParseException {

        //Define Options
        Options options = new Options();
        options.addOption("w", "width", true, "Grid width, default = 800");
        options.addOption("h", "height", true, "Grid height, default = 600");
        options.addOption("r", "randomize", false, "Randomize the game grid, (true, false, t ,f ) default true");
        options.addOption("fr", "framerate", true, "Game max frame rate (frames per second), default 10");
        options.addOption("c", "color", true, "Default color of initial cells, (RED, GREEN, BLUE, WHITE, or RANDOM) or hex code of color #FF33AACC, default GREEN");
        options.addOption("t", "threads", true, "Thread count");
        options.addOption("h", "help", false, "Print this help menu");
        
        //Parse arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        cmd = parser.parse(options, args);
        
        
        GameParams params = new GameParams();
        
        //Poll options
        if(cmd.hasOption("w"))
        {
            String width = cmd.getOptionValue("width");
            params.setWidth(Integer.parseInt(width));
        }
                        
        if(cmd.hasOption("h"))
        {
            String height = cmd.getOptionValue("height");
            params.setWidth(Integer.parseInt(height));
        }
        
        if(cmd.hasOption("r"))
        {
            String randomize = cmd.getOptionValue("randomize").toLowerCase();
            
            if(randomize.charAt(0) == 'f') {
                params.setRandomized(false);
            } else if(randomize.charAt(0) == 't') {
                params.setRandomized(true);
            }
        }
        
        if(cmd.hasOption("fr"))
        {
            String frameRate = cmd.getOptionValue("framerate").toLowerCase();
            params.setMaximumFrameRate(Integer.parseInt(frameRate));
        }
        
        if(cmd.hasOption("c"))
        {
            String color = cmd.getOptionValue("color").toUpperCase();
            
            if(color.equals("RED")) {
                params.setDefaultInitialColor(Grid.RED);
            } else if(color.equals("GREEN")) {
                params.setDefaultInitialColor(Grid.GREEN);
            } else if(color.equals("BLUE")) {
                params.setDefaultInitialColor(Grid.BLUE);
            } else if(color.equals("RANDOM")) {
                params.setRandomInitialColor(true);
            } else if(color.charAt(0) == '#') {
                BigInteger bigint = new BigInteger(color, 16); //Handle > 0FFFFFFF
                params.setDefaultInitialColor(bigint.intValue()); //Truncate
            }
        }
        
        if(cmd.hasOption("t"))
        {
            String threadcount = cmd.getOptionValue("threads");
            params.setThreads(Integer.parseInt(threadcount));
        }
        
        JFrame frame = new JFrame("Game Of Life Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
//        frame.setSize(params.getWidth(), params.getHeight());
        
        Game game = new Game(params, frame);
        
        game.start();
        
    }
}
