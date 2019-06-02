package it.polimi.ingsw.network.server.executable;

import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.network.server.communication.socket.ClientHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer implements Runnable {
    private final String ipAddress;
    private final int port;

    public SocketServer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void run() {
        Console console = Console.getInstance();

        try (ServerSocket socket = new ServerSocket(port, 0, InetAddress.getByName(ipAddress))) {
            console.log("socket server configured on " + ipAddress + ":" + port);
            console.log("socket server bounded on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
            console.stat("socket server running...");
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                ClientHandler clientHandler = new ClientHandler(socket.accept()); //create a User thread to represent the Client
                executor.execute(clientHandler); //execute the user thread
            }
        } catch (IOException e) {
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }
}
