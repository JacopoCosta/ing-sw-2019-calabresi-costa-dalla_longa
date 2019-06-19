package it.polimi.ingsw.network.server.executable;

import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.network.server.communication.rmi.ServerController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer implements Runnable {
    private final String ipAddress;
    private final int port;

    public RMIServer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void run() {
        Console console = Console.getInstance();

        console.log("creating RMI protocol implementation...");
        ServerController serverController;

        try {
            serverController = new ServerController();
        } catch (RemoteException e) {
            console.err(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
            return;
        }
        console.log("RMI implementation created");

        console.log("starting RMI registry...");
        final Registry registry;
        try {
            registry = LocateRegistry.createRegistry(this.port);
            console.log("RMI registry started");
        } catch (RemoteException e) {
            console.err(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
            return;
        }

        console.log("binding RMI protocol implementation to registry...");
        String bindingName = "rmi://" + this.ipAddress + ":" + this.port + "/RMIController";
        try {
            registry.rebind(bindingName, serverController);
        } catch (RemoteException e) {
            console.err(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
        }
        console.log("RMI done binding");
        console.log("RMI started on " + this.ipAddress + ":" + this.port);
        console.stat("RMI server is running...");
    }
}
