package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.network.server.executable.RMIServer;
import it.polimi.ingsw.network.server.executable.SocketServer;

import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    protected final String ipAddress;
    protected final int port;

    protected final Console console;
    private ExecutorService executor;

    public Server(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;

        this.console = Console.getInstance();
        this.executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        int socketPort = this.port;
        int rmiPort = Registry.REGISTRY_PORT;

        SocketServer socketServer = new SocketServer(this.ipAddress, socketPort);
        RMIServer rmiServer = new RMIServer(this.ipAddress, rmiPort);

        this.console.clear();
        this.console.stat("Running on " + this.console.getOsName() + "...");

        this.executor.execute(socketServer);
        this.executor.execute(rmiServer);
    }
}
