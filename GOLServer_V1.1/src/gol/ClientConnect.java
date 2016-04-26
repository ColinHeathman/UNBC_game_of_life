package gol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author calado
 */
public class ClientConnect extends Thread {

    public boolean connecting;

    ClientConnect() {
        connecting = true;
    }

    @Override
    public void run() {
        System.out.println("Now waiting for Client connections...");
        Server.connections = 0;
        while (connecting) {  //Make connections with clients
            try {
                Socket clientSocket = Server.serverSocket.accept();
                Server.clientList.add(new MiniServer(clientSocket));
                System.out.println(++Server.connections + " Connection(s) established...");
            } catch (IOException ex) {
                System.out.println("Connecting to client has failed...");
            }
        }
        try {
            Server.serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Server.barrier2.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            Logger.getLogger(ClientConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
