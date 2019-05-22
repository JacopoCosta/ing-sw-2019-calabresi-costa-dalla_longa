package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.controller.RmiController;
import it.polimi.ingsw.network.common.util.ConsoleController;
import it.polimi.ingsw.network.server.communication.rmi.ServerController;
import it.polimi.ingsw.network.server.communication.socket.ClientHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static String serverAddress;
    private static int serverPort;

    private static final ConsoleController CONSOLE_CONTROLLER = new ConsoleController();

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    private static final Runnable socketServer = () -> {
        try (ServerSocket socket = new ServerSocket(serverPort, 0, InetAddress.getByName(serverAddress))) {
            System.out.println("LOG: socket server configured on " + serverAddress + ":" + serverPort);
            System.out.println("LOG: socket server bounded on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
            System.out.println("STATUS: socket server running...");
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                ClientHandler clientHandler = new ClientHandler(socket.accept()); //create a User thread to represent the Client
                executor.execute(clientHandler); //execute the user thread
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getClass().toString() + " error in socketServer: " + e.getMessage());
        }
    };

    private static final Runnable rmiServer = () -> {
        System.out.println("LOG: Starting RMI registry...");
        CONSOLE_CONTROLLER.startRmiRegistry();
        System.out.println("LOG: RMI service started");

        System.out.println("LOG: creating RMI protocol implementation...");
        RmiController serverController;
        try {
            serverController = new ServerController();
        } catch (RemoteException e) {
            System.err.println("ERROR: " + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
            CONSOLE_CONTROLLER.stopRmiRegistry();
            System.exit(-1);
            return;
        }
        System.out.println("LOG: RMI done creating");

        System.out.println("LOG: binding RMI protocol implementation to registry...");
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry();
        } catch (RemoteException e) {
            System.err.println("ERROR: " + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
            CONSOLE_CONTROLLER.stopRmiRegistry();
            System.exit(-1);
            return;
        }

        String bindingName = "rmi://" + serverAddress + ":" + serverPort + "/ServerController";
        try {
            registry.bind(bindingName, serverController);
        } catch (RemoteException e) {
            System.err.println("ERROR: " + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
            CONSOLE_CONTROLLER.stopRmiRegistry();
            System.exit(-1);
        } catch (AlreadyBoundException e) {
            System.out.println("LOG: RMI registry service already bound, rebinding...");
            try {
                registry.rebind(bindingName, serverController);
            } catch (RemoteException ex) {
                System.err.println("ERROR: " + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
                CONSOLE_CONTROLLER.stopRmiRegistry();
                System.exit(-1);
            }
            System.out.println("LOG: RMI service rebounded");
        }
        System.out.println("LOG: RMI done binding");
        System.out.println("LOG: RMI started on " + serverAddress + ":" + serverPort);
        System.out.println("STATUS: RMI server is running...");
    };

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("ERROR: correct syntax is: Server [serverAddress] [serverPort]");
            System.exit(-1);
        }
        serverAddress = args[0];

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: server port not in range [1025 - 65535]");
            System.exit(-1);
        }
        CONSOLE_CONTROLLER.clearConsole();

        executor.execute(socketServer);
        executor.execute(rmiServer);
    }
}
