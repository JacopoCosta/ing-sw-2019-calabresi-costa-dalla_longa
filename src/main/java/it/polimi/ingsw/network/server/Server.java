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
            ConsoleController.log("socket server configured on " + serverAddress + ":" + serverPort);
            ConsoleController.log("socket server bounded on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
            ConsoleController.stat("socket server running...");
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                ClientHandler clientHandler = new ClientHandler(socket.accept()); //create a User thread to represent the Client
                executor.execute(clientHandler); //execute the user thread
            }
        } catch (IOException e) {
            ConsoleController.err("" + e.getClass().toString() + " error in socketServer: " + e.getMessage());
        }
    };

    private static final Runnable rmiServer = () -> {
        ConsoleController.log("Starting RMI registry...");
        CONSOLE_CONTROLLER.startRmiRegistry();
        ConsoleController.log("RMI service started");

        ConsoleController.log("creating RMI protocol implementation...");
        RmiController serverController;
        try {
            serverController = new ServerController();
        } catch (RemoteException e) {
            ConsoleController.err("" + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
            CONSOLE_CONTROLLER.stopRmiRegistry();
            System.exit(-1);
            return;
        }
        ConsoleController.log("RMI done creating");

        ConsoleController.log("binding RMI protocol implementation to registry...");
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry();
        } catch (RemoteException e) {
            ConsoleController.err("" + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
            CONSOLE_CONTROLLER.stopRmiRegistry();
            System.exit(-1);
            return;
        }

        String bindingName = "rmi://" + serverAddress + ":" + serverPort + "/ServerController";
        try {
            registry.bind(bindingName, serverController);
        } catch (RemoteException e) {
            ConsoleController.err("" + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
            CONSOLE_CONTROLLER.stopRmiRegistry();
            System.exit(-1);
        } catch (AlreadyBoundException e) {
            ConsoleController.log("RMI registry service already bound, rebinding...");
            try {
                registry.rebind(bindingName, serverController);
            } catch (RemoteException ex) {
                ConsoleController.err("" + e.getClass().toString() + " error in rmiServer: " + e.getMessage());
                CONSOLE_CONTROLLER.stopRmiRegistry();
                System.exit(-1);
            }
            ConsoleController.log("RMI service rebounded");
        }
        ConsoleController.log("RMI done binding");
        ConsoleController.log("RMI started on " + serverAddress + ":" + serverPort);
        ConsoleController.stat("RMI server is running...");
    };

    public static void main(String[] args) {
        if (args.length != 2) {
            ConsoleController.err("correct syntax is: Server [serverAddress] [serverPort]");
            System.exit(-1);
        }
        serverAddress = args[0];

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ConsoleController.err("server port not in range [1025 - 65535]");
            System.exit(-1);
        }
        CONSOLE_CONTROLLER.clearConsole();

        executor.execute(socketServer);
        //executor.execute(rmiServer);
    }
}
