package gol;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author calado
 */
public class Server extends Thread {

    public static boolean IO = true;
    public static boolean[][] currentStates;
    public static int rows;
    public static int cols;
    public static CyclicBarrier barrier;
    public static CyclicBarrier barrier2;
    public static Display W;
    public static ServerSocket serverSocket;
    public static ArrayList<MiniServer> clientList = new ArrayList();
    private static Scanner input = new Scanner(System.in);
    public static int connections;
    private static ExecutorService pool;
    private static boolean connecting = true;
    public static int numOfThreadsPerClient;
    //Timer Variables
    public static int generationCounter = 0;
    private static long genTime;
    private static long elapsedTime;
    private static long currentTime;
    private static long startTime;
    private static boolean isVisible;
    private static int preset;
 

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        getStartUpData();
        getConnections();
        initializeVariables();
        runSimulation();
    }

    
    /*
     * Internal Functionality
     */
    // Fills the Cells[][] with random entries.
    public static boolean[][] generateRandomEntries(int rows, int cols) {
        boolean[][] grid = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (Math.random() > 0.7) {
                    grid[r][c] = true;// init board with 30% random trues
                } else {
                    grid[r][c] = false;
                }
            }
        }
        return grid;
    }

    private static void setClientCoords() {
        int clients = clientList.size();
        int gridRows = (int) Math.sqrt(clients);
        int gridCols = clients / gridRows;
        int rOffset = (rows / gridRows);
        int cOffset = (cols / gridCols);
        int index = 0;
        int sri;
        int eri;
        int sci;
        int eci;

        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                sri = i * rOffset;
                eri = sri + (rOffset - 1);
                sci = j * cOffset;
                eci = sci + (cOffset - 1);
                if (gridRows - i == 1) {
                    eri = rows - 1;
                }
                if (gridCols - j == 1) {
                    eci = cols - 1;
                }
                clientList.get(index).setSri(sri);
                clientList.get(index).setEri(eri);
                clientList.get(index).setSci(sci);
                clientList.get(index).setEci(eci);
                index++;
            }
        }

    }

    public static void printGenSpeed() {
        currentTime = System.nanoTime();
        genTime = currentTime - startTime;
        elapsedTime = genTime / 1000000;
        System.out.println(elapsedTime + "ms/gen " + generationCounter);
        startTime = currentTime;
    }

    private static void runSimulation() {
        // Runnning Loop
        startTime = System.nanoTime();  // Start the timer for the start time.
        while (IO) {
            for (int i = 0; i < clientList.size(); i++) {
                pool.execute(clientList.get(i));
            }

            try {
                barrier.await();
            } catch (InterruptedException ex) {
                System.out.println("Interrupted Exception");
            } catch (BrokenBarrierException ex) {
                System.out.println("Broken Barrier Exception");
            }

            if(isVisible) W.update();  // Update the GUI window

            generationCounter++;  // Increment the generation counter.
            // Check speed
            if (generationCounter % 100 == 0) {
                printGenSpeed();
                System.gc();
            }
        }
    }

    private static void initializeVariables() {
        // Generate the board
        
        switch(preset) {
            case(1): currentStates = generateGalaxy(rows,cols); break;
            case(2): currentStates = generateRow10(rows,cols); break;
            case(3): currentStates = generateLongLongCanoe(rows,cols); break;
            default: currentStates = generateRandomEntries(rows, cols); break;
        }
        if (isVisible) W = new Display();
        pool = Executors.newFixedThreadPool(connections);
        barrier = new CyclicBarrier(connections + 1);
        setClientCoords();//setting up miniserver threads
    }

    private static void getStartUpData() {
        System.out.println("-I am the Game Of Life Server Class-\n");
        System.out.println("Enter the Number of Rows:");
        rows = input.nextInt();
        System.out.println("Enter the Number of Columns:");
        cols = input.nextInt();
        System.out.println("Enter the number of threads each client will run:");
        numOfThreadsPerClient = input.nextInt();
        System.out.println("Preset? (0-3) (0 for random, 1 for Galaxy, 2 for Row10, 3 for LongLongCanoe)");
        preset = input.nextInt();
        System.out.println("Show server display? (y/n):");
        input.nextLine();
        String ans = input.nextLine();
        if (!ans.isEmpty() && ans.toLowerCase().charAt(0) == 'y')  isVisible = true; else isVisible = false;
        input.reset();
        try {
            Server.serverSocket = new ServerSocket(1222);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1222");
        }

    }

    private static void getConnections() throws InterruptedException, BrokenBarrierException {
        // Start thread for accepting clients
        barrier2 = new CyclicBarrier(1);
        Thread getClients = new ClientConnect();
        getClients.start();


        System.out.println("\nEnter any character to stop accepting clients: \n");
        input.hasNext();
        connecting = false;
        barrier2.await();
    }
    
    
    private static boolean[][] generateLongLongCanoe(int rows, int cols) {
        boolean[][] canoeBoard = new boolean[rows][cols];
        //Build the canoe
        canoeBoard[150][150] = true;
        canoeBoard[150][151] = true;
        canoeBoard[151][151] = true;
        canoeBoard[152][150] = true;
        canoeBoard[153][149] = true;
        canoeBoard[154][148] = true;
        canoeBoard[155][147] = true;
        canoeBoard[156][146] = true;
        canoeBoard[156][145] = true;
        canoeBoard[155][145] = true;
        return canoeBoard;
    }

    private static boolean[][] generateRow10(int rows, int cols) {
        boolean[][] osc = new boolean[rows][cols];

        for (int i = 155; i < 166; i++) {
            osc[155][i] = true;
        }
        return osc;
    }

    private static boolean[][] generateGalaxy(int rows, int cols) {
        boolean[][] osc = new boolean[rows][cols];

        for (int i = 151; i <= 156; i++) {
            for (int j = 151; j <= 152; j++) {
                osc[i][j] = true;
            }
        }
        for (int i = 151; i <= 152; i++) {
            for (int j = 154; j <= 159; j++) {
                osc[i][j] = true;
            }
        }
        for (int i = 158; i <= 159; i++) {
            for (int j = 151; j <= 156; j++) {
                osc[i][j] = true;
            }
        }

        for (int i = 154; i <= 159; i++) {
            for (int j = 158; j <= 159; j++) {
                osc[i][j] = true;
            }
        }

        return osc;
    }
}
