package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.rmi.RMIController;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.network.server.communication.rmi.ServerController;
import it.polimi.ingsw.network.server.communication.socket.ClientHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static String serverAddress;
    private static int serverPort;

    private static final Console console = new Console();

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    private static final Runnable socketServer = () -> {
        try (ServerSocket socket = new ServerSocket(serverPort, 0, InetAddress.getByName(serverAddress))) {
            console.log("socket server configured on " + serverAddress + ":" + serverPort);
            console.log("socket server bounded on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
            console.stat("socket server running...");
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                ClientHandler clientHandler = new ClientHandler(socket.accept()); //create a User thread to represent the Client
                executor.execute(clientHandler); //execute the user thread
            }
        } catch (IOException e) {
            console.err("" + e.getClass().toString() + " in socket Server: " + e.getMessage());
        }
    };

    private static final Runnable rmiServer = () -> {
        console.log("creating RMI protocol implementation...");
        ServerController serverController;
        try {
            serverController = new ServerController();
        } catch (RemoteException e) {
            console.err("" + e.getClass().toString() + " in RMI Server: " + e.getMessage());
            System.exit(-1);
            return;
        }
        console.log("RMI implementation created");

        console.log("starting RMI registry...");
        final Registry registry;
        try {
            registry = LocateRegistry.createRegistry(RMIController.DEFAULT_PORT);
            console.log("RMI registry started");
        } catch (RemoteException e) {
            console.err("" + e.getClass().toString() + " in RMI Server: " + e.getMessage());
            System.exit(-1);
            return;
        }

        console.log("binding RMI protocol implementation to registry...");
        String bindingName = "rmi://" + serverAddress + ":" + RMIController.DEFAULT_PORT + "/RMIController";
        try {
            registry.rebind(bindingName, serverController);
        } catch (RemoteException e) {
            console.err("" + e.getClass().toString() + " error in RMI Server: " + e.getMessage());
            //e.printStackTrace();
            System.exit(-1);
        }
        console.log("RMI done binding");
        console.log("RMI started on " + serverAddress + ":" + serverPort);
        console.stat("RMI server is running...");
    };

    public static void main(String[] args) {
        if (args.length != 2) {
            console.err("correct syntax is: Server [serverAddress] [serverPort]");
            System.exit(-1);
        }
        serverAddress = args[0];

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            console.err("server port not in range [1025 - 65535]");
            System.exit(-1);
        }
        console.clear();
        console.stat("Running on " + console.getOsName() + "...");

        executor.execute(socketServer);
        executor.execute(rmiServer);
    }
}
