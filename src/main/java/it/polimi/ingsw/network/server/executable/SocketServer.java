package it.polimi.ingsw.network.server.executable;

import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.network.server.communication.socket.SocketClientHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is responsible for listening for incoming connections from clients using the {@code Socket} protocol.
 * Every time a socket client connects to the {@code Server}, a dedicated {@code Thread} is associated to him and the
 * corresponding socket is sent to a {@link SocketClientHandler} to handle all the input/output operations.
 */
public class SocketServer implements Runnable {
    /**
     * The {@code SocketServer} ip address.
     */
    private final String ipAddress;

    /**
     * The {@code SocketServer} port.
     */
    private final int port;

    /**
     * This is the only constructor. It creates a new {@code SocketServer} with the given {@code ipAddress} and {@code port}.
     *
     * @param ipAddress the {@code SocketServer} new ip address.
     * @param port      the {@code SocketServer} new port.
     */
    public SocketServer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Listens for every possible client that may join the {code Server} and create a separate thread for each of them by
     * instantiating a different {@link SocketClientHandler} with the client own {@code Socket} reference.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        Console console = Console.getInstance();

        try (ServerSocket socket = new ServerSocket(this.port, 0, InetAddress.getByName(this.ipAddress))) {
            console.log("socket server configured on " + this.ipAddress + ":" + this.port);
            console.log("socket server bounded on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
            console.stat("socket server running...");
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                SocketClientHandler clientHandler = new SocketClientHandler(socket.accept()); //create a User thread to represent the Client
                executor.execute(clientHandler); //execute the user thread
            }
        } catch (IOException e) {
            e.printStackTrace();
            //console.err(e.getClass() + ": " + e.getMessage());
        }
    }
}
