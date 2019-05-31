package it.polimi.ingsw.network.server.executable;

import it.polimi.ingsw.network.common.util.Console;
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
        Console console = new Console();

        console.log("creating RMI protocol implementation...");
        ServerController serverController;
        try {
            /*
             * WARNING:
             *  this property is not supported, can change at any time, and only exist in certain
             *  implementations of the Java Remote Method Invocation (Java RMI).
             *  These properties are not part of the Java RMI public API.
             *
             * The value of this property represents the period (in milliseconds) for which socket
             * connections may reside in an "unused" state, before the Java RMI runtime will allow
             * those connections to be freed (closed). The default value is 15000 milliseconds (15 seconds).
             * */
            try {
                String responseTimeout = "5000";
                System.setProperty("sun.rmi.transport.connectionTimeout", responseTimeout);
            } catch (Exception e) {
                console.err("failed to set system property \"sun.rmi.transport.connectionTimeout\" for ServerController remote object");
            }
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
            registry = LocateRegistry.createRegistry(port);
            console.log("RMI registry started");
        } catch (RemoteException e) {
            console.err(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
            return;
        }

        console.log("binding RMI protocol implementation to registry...");
        String bindingName = "rmi://" + ipAddress + ":" + port + "/RMIController";
        try {
            registry.rebind(bindingName, serverController);
        } catch (RemoteException e) {
            console.err(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
        }
        console.log("RMI done binding");
        console.log("RMI started on " + ipAddress + ":" + port);
        console.stat("RMI server is running...");
    }
}
