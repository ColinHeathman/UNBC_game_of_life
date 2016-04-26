package gol;

import java.io.*;
import java.net.*;
import java.util.concurrent.BrokenBarrierException;

/**
 *
 * @author calado
 */
class MiniServer extends Thread {

    private Socket socket = null;
    private ObjectOutputStream outOS;
    private ObjectInputStream inOS;
    private boolean[][] nextStates = null;
    private int sri; //start row index
    private int eri; //end row index
    private int sci; //start col index
    private int eci; //end col index

    public MiniServer(Socket socket) {
        super("MiniServer");
        this.socket = socket;

        try {
            outOS = new ObjectOutputStream(this.socket.getOutputStream());
            inOS = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("MiniServer failed to create object streams");
        }

    }

    @Override
    public void run() {
        try {
            if (Server.generationCounter == 0) {
                outOS.writeInt(Server.numOfThreadsPerClient);
                outOS.writeInt(this.sri);
                outOS.writeInt(this.eri);
                outOS.writeInt(this.sci);
                outOS.writeInt(this.eci);
                outOS.flush();
                outOS.reset();
            }

           boolean[][] copy = copyArray(Server.currentStates);
           outOS.writeObject(copy);
           outOS.flush();
           outOS.reset();
           copy = null;
          

        } catch (IOException ex) {
            Server.IO = false;
            System.out.println("MiniServer failed to write to Object Stream");
        }


        try {
            this.nextStates = (boolean[][]) inOS.readObject();
            this.updateCurrentStates();
            nextStates = null;
            
        } catch (IOException ex) {
            Server.IO = false;
            System.out.println("MiniServer Failed to read Object Stream");
        } catch (ClassNotFoundException ex) {
            Server.IO = false;
            System.out.println("MiniServer Failed to recognize serializable class");
        }

        try {
            Server.barrier.await();
        } catch (InterruptedException ex) {
            Server.IO = false;
        } catch (BrokenBarrierException ex) {
            Server.IO = false;
        }
    }

    public void updateCurrentStates() {
        for (int j = this.sri; j <= this.eri; j++) {
            for (int k = this.sci; k <= this.eci; k++) {
                if (Server.currentStates[j][k] != this.nextStates[j][k]) {
                    Server.currentStates[j][k] = this.nextStates[j][k];
                }
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

    public void setEci(int eci) {
        this.eci = eci;
    }

    public void setEri(int eri) {
        this.eri = eri;
    }

    public void setSci(int sci) {
        this.sci = sci;
    }

    public void setSri(int sri) {
        this.sri = sri;
    }
    
}
