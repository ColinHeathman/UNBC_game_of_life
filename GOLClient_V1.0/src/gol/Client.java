package gol;

import java.awt.Point;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    //Board Resources (nextStates is shared among threads)

    protected static boolean[][] currentStates;
    public static boolean[][] nextStates;
    protected static int rows;
    protected static int cols;
    // Indices
    protected static int sri;
    protected static int eri;
    protected static int sci;
    protected static int eci;
    //Thread variables
    private static int numOfThreads;
    private static BoardThread[] threadArray;
    private static ExecutorService pool;
    private static Future[] myFutures;
    //Timer Variables
    private static int generationCounter = 0;
    private static long genTime;
    private static long elapsedTime;
    private static long currentTime;
    private static long startTime;
    private static ObjectOutputStream outOS;
    private static ObjectInputStream inOS;
    private static InetAddress ipAddress;
    private static Socket serverSocket;
    private static Display W;
    private static boolean isVisible;

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, ExecutionException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("-Hello, You are running a client program for The Game Of Life\n"
                + "Begin by entering the IP address of the server...\n"
                + "Server IP = ");

        ipAddress = parseIpAddress(scanner.nextLine());// Get the ip address as input...
        System.out.println("Waiting for Server to start...");
        try {
            serverSocket = new Socket(ipAddress, 1222);
            outOS = new ObjectOutputStream(serverSocket.getOutputStream());
            inOS = new ObjectInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Couldn't get I/O for the connection to server");
            System.exit(0);
        }



        // Runnning Loop
        startTime = System.nanoTime();  // Start the timer for the start time.
        while (true) {

            // Initialize the thread pool and rows and cols variables the first time around.
            if (generationCounter == 0) {
                try {
                    numOfThreads = inOS.readInt();
                    // Initialize the thread array
                    threadArray = new BoardThread[numOfThreads];
                    sri = inOS.readInt();
                    eri = inOS.readInt();
                    sci = inOS.readInt();
                    eci = inOS.readInt();
                    currentStates = (boolean[][]) inOS.readObject();
                    initializePoolThreads();
                    rows = currentStates.length;
                    cols = currentStates[0].length;
                    W = new Display();
                    System.out.println("Simulation Started");
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //Get Board from Server
                try {
                    currentStates = (boolean[][]) inOS.readObject();
                } catch (IOException ex) {
                    System.out.println("Server Closed Object Stream");
                    System.exit(0);
                } catch (ClassNotFoundException ex) {
                    System.out.println("Client could not find serializable class");
                    System.exit(0);
                }
            }

            nextStates = copyArray(currentStates);


            BoardThread.setMyCurrentStates(currentStates);   // Next States is current states at this moment
            myFutures = new Future[numOfThreads];
            for (int i = 0; i < threadArray.length; i++) {
                myFutures[i] = pool.submit(threadArray[i]);
            }
            for (int i = 0; i < threadArray.length; i++) {
                boolean join = true;
                if (myFutures[i].isDone() == false) {
                    while (join) {
                        if (myFutures[i].isDone()) {
                            join = false;
                        }
                    }
                }
            }
            
            W.update();

            while(!W.userChanges.isEmpty()) {
                try{
                    Point p = (Point) W.userChanges.take();
                    nextStates[p.y][p.x] = true;}
                catch (InterruptedException e) {/*Do nothing*/}
            }
            
            
            myFutures = null;

            currentStates = null;
            try {
                outOS.writeObject(nextStates);
                outOS.flush();
                outOS.reset();
                nextStates = null;
            } catch (IOException ex) {
                System.out.println("Server Closed Object Stream");
                System.exit(0);
            }
            generationCounter++;  // Increment the generation counter.
            // Check speed
            if (generationCounter % 100 == 0) {
                printGenSpeed();
                System.gc();
            }
        }
    }

    // Copies current to newState, returns newState Cells[][]
    public static boolean[][] copyArray(boolean[][] current) {
        boolean[][] newState = new boolean[current.length][current[0].length];
        for (int r = 0; r < current.length; r++) {
            System.arraycopy(current[r], 0, newState[r], 0, current[0].length);
        }
        current = null;
        return newState;
    }

    private static void initializePoolThreads() {
        pool = Executors.newFixedThreadPool(numOfThreads);
        int gridRows = (int) Math.sqrt(numOfThreads);
        int gridCols = numOfThreads / gridRows;
        int myRows = eri - sri + 1;
        int myCols = eci - sci + 1;
        int rOffset = (myRows / gridRows);
        int cOffset = (myCols / gridCols);
        int index = 0;
        int sri2;
        int eri2;
        int sci2;
        int eci2;

        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                sri2 = (i * rOffset) + sri;
                eri2 = sri2 + (rOffset - 1);
                sci2 = (j * cOffset) + sci;
                eci2 = sci2 + (cOffset - 1);
                if (gridRows - i == 1) {
                    eri2 = myRows - 1 + sri;
                }
                if (gridCols - j == 1) {
                    eci2 = myCols - 1 + sci;
                }
                BoardThread bt = new BoardThread(sri2, eri2, sci2, eci2);
                threadArray[index] = bt;
                index++;
            }
        }
    }

    public static InetAddress parseIpAddress(String ip) throws IllegalArgumentException {
        StringTokenizer tok = new StringTokenizer(ip, ".");

        if (tok.countTokens() != 4) {
            throw new IllegalArgumentException("IP address must be in the format 'xxx.xxx.xxx.xxx'");
        }

        byte[] data = new byte[4];
        int i = 0;
        while (tok.hasMoreTokens()) {
            try {
                int val = Integer.parseInt(tok.nextToken(), 10);

                if (val < 0 || val > 255) {
                    throw new IllegalArgumentException("Illegal value '" + val + "' at byte " + (i + 1) + " in the IP address.");
                }
                data[i++] = (byte) val;
            } catch (NumberFormatException e) {
            }
        }

        try {
            return InetAddress.getByAddress(ip, data);
        } catch (UnknownHostException e) {
            // This actually can't happen since the method InetAddress.getByAddress(String, byte[])
            // doesn't perform any lookups and we have already guaranteed that the length of data is 4
            throw new Error("UnknownHostException somehow thrown when creating an InetAddress", e);
        }
    }

    public static void printGenSpeed() {
        currentTime = System.nanoTime();
        genTime = currentTime - startTime;
        elapsedTime = genTime / 1000000;
        System.out.println(elapsedTime + "ms/gen " + generationCounter);
        startTime = currentTime;
    }
}
