package gol;

import java.awt.Canvas;
import java.util.Random;
import javax.swing.JFrame;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) throws ParseException {

        GameParams params = new GameParams();
        
        JFrame frame = new JFrame("Game Of Life Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, params.getWidth(), params.getHeight());
        frame.setVisible(true);
        
        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, params.getWidth(), params.getHeight());
        canvas.setVisible(true);
        
        frame.add(canvas);
        
        Game game = new Game(params);
        Camera camera = new Camera();
        Renderer renderer = new Renderer(game, canvas, camera);
        game.setUpdateDisplayTask(renderer.renderTask);
        
        Random rand = new Random();
        for(int y = 0; y < game.getHeight(); y++) {
            for(int x = 0; x < game.getWidth(); x++) {
                if(rand.nextBoolean()) {
                    game.getCurrentStates().setState(x, y, Grid.GREEN);
                }
            }
        }
        
        Controller controller = new Controller(game, canvas, camera);
        
        game.start();
        
        //TODO Command Line options
//        //Define Options
//        Options options = new Options();
//        options.addOption("m", "mode", true, "Run mode");
//        options.addOption("rh", "remotehost", true, "Client's remote host ip address");
//        options.addOption("h", "help", false, "Print help");
//        
//        //Parse arguments
//        CommandLineParser parser = new DefaultParser();
//        CommandLine cmd;
//        cmd = parser.parse(options, args);
//                
//        //Poll options
//        if(cmd.hasOption("m"))
//        {
//            String mode = cmd.getOptionValue("mode").toLowerCase();
//            if(mode.equals("server") | mode.equals("s")) {
//                Server server = new Server();
//                server.start();
//                
//            } else if(mode.equals("client") | mode.equals("c")) {
//                String ip = cmd.getOptionValue("remotehost");
//                Client client = new Client(ip, 1222);
//                client.start();
//                
//            } else if(mode.equals("standalone") | mode.equals("sa")) {
//                
//                Server server = new Server();
//                server.start();
//                
//                Client client = new Client("localhost", 1222);
//                client.start();
//            }
//        }
    }
}
