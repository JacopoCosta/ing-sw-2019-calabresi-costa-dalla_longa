package it.polimi.ingsw.network.server;

import it.polimi.ingsw.util.console.Console;
import it.polimi.ingsw.network.server.executable.RMIServer;
import it.polimi.ingsw.network.server.executable.SocketServer;

import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class responsible for the server side infrastructure. This class is the first one invoked if an Adrenaline
 * server is desired, by specifying the correct {@code args[]} to the {@link ServerLauncher} class.
 * This class instantiate two different {@code threads} to handle the client connection using different protocols:
 * an {@link RMIServer} to listen for incoming connections by {@code RMI} clients;
 * a {@link SocketServer} to listen for incoming connections by {@code Socket} clients.
 *
 * @see RMIServer
 * @see SocketServer
 */

@SuppressWarnings("UnnecessaryLocalVariable")
public class Server {
    /**
     * The Adrenaline server ip address.
     */
    private final String ipAddress;

    /**
     * The Adrenaline server port.
     */
    private final int port;

    /**
     * The output method to print any possible error that can occur during the life of this {@code Server}.
     */
    protected final Console console;

    /**
     * The {@link ExecutorService} responsible for the execution of the two server instances.
     */
    private ExecutorService executor;

    /**
     * This is the only constructor. It creates a new {@code Server} instance running on the specified ip address, listening
     * on the specified port.
     *
     * @param ipAddress the new {@code Server} ip address.
     * @param port      the new {@code Server} listening port.
     */
    public Server(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;

        this.console = Console.getInstance();
        this.executor = Executors.newFixedThreadPool(2);
    }

    /**
     * Starts the {@code Server} core logic and creates the two server implementations for {@code RMI} and {@code Socket}.
     */
    public void start() {
        int socketPort = this.port;
        int rmiPort = Registry.REGISTRY_PORT;

        SocketServer socketServer = new SocketServer(this.ipAddress, socketPort);
        RMIServer rmiServer = new RMIServer(this.ipAddress, rmiPort);

        this.console.clear();
        this.console.stat("Running on " + this.console.getOsName() + "...");

        this.executor.execute(socketServer);

        try {
            Thread.sleep(100); //to allow a correct print of server CLI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.executor.execute(rmiServer);
    }
}
