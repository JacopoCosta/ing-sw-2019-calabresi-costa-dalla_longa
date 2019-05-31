package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.rmi.RMIController;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.network.server.executable.RMIServer;
import it.polimi.ingsw.network.server.executable.SocketServer;

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

        console = new Console();
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        int socketPort = port;
        int rmiPort = RMIController.DEFAULT_PORT;

        SocketServer socketServer = new SocketServer(ipAddress, socketPort);
        RMIServer rmiServer = new RMIServer(ipAddress, rmiPort);

        console.clear();
        console.stat("Running on " + console.getOsName() + "...");

        executor.execute(socketServer);
        executor.execute(rmiServer);
    }
}
