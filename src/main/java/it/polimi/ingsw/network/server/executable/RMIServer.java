package it.polimi.ingsw.network.server.executable;

import it.polimi.ingsw.util.console.Console;
import it.polimi.ingsw.network.server.communication.rmi.RMIServerController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * This class is responsible for creating the {@code RMI} {@link Registry} and binding all the properties needed in order to
 * allow the remote clients to connect to the {@code Server} via the {@code RMI} protocol.
 */
public class RMIServer implements Runnable {
    /**
     * The {@code RMIServer} ip address.
     */
    private final String ipAddress;

    /**
     * The {@code RMIServer} port.
     */
    private final int port;

    /**
     * This is the only constructor. It creates a new {@code RMIServer} with the given {@code ipAddress} and {@code port}.
     *
     * @param ipAddress the {@code RMIServer} new ip address.
     * @param port      the {@code RMIServer} new port.
     */
    public RMIServer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Creates and binds all the properties needed to the default {@code RMI} {@link Registry} in order to allow the remote
     * clients to connect to the {@code Server} by invoking the desired {@link RMIServerController} methods.
     *
     * @see RMIServerController
     */
    @Override
    public void run() {
        Console console = Console.getInstance();

        console.log("creating RMI protocol implementation...");
        RMIServerController serverController;

        try {
            serverController = new RMIServerController();
        } catch (RemoteException e) {
            e.printStackTrace();
            //console.err(e.getClass() + ": " + e.getMessage());
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
            e.printStackTrace();
            //console.err(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
            return;
        }

        console.log("binding RMI protocol implementation to registry...");
        String bindingName = "rmi://" + this.ipAddress + ":" + this.port + "/RMIController";
        try {
            registry.rebind(bindingName, serverController);
        } catch (RemoteException e) {
            e.printStackTrace();
            //console.err(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
        }
        console.log("RMI done binding");
        console.log("RMI started on " + this.ipAddress + ":" + this.port);
        console.stat("RMI server is running...");
    }
}
