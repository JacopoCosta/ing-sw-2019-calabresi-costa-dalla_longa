package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.rmi.RmiController;
import it.polimi.ingsw.network.server.rmi.RmiControllerImpl;
import it.polimi.ingsw.network.server.socket.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * TODO:
 *  NOTE 3: logging activity is performed by System.out for now. In the future it will eventually be replaced
 *         with a proper logger.
 *  NOTE 4: an auto-save feature is required. This will be introduced in the future updates.
 *  NOTE 6: all exceptions are thrown as they are. In the final release they will be handled with proper
 *          user friendly messages (in both Server and ClientHandler).
 *
 * */

public class Server {

    //An Executor for the two Server protocol: one for the Socket Server, the other for the RMI Server
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    private static int SERVER_PORT;
    private static InetAddress SERVER_ADDRESS;

    private static Runnable socketServer = () -> {
        try (ServerSocket socket = new ServerSocket(SERVER_PORT, 0, SERVER_ADDRESS)) {
            System.out.println("LOG: socket server configured on " + SERVER_ADDRESS + ":" + SERVER_PORT);
            System.out.println("LOG: socket Server bounded on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
            System.out.println("LOG: socket server running...");
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                ClientHandler clientHandler = new ClientHandler(socket.accept()); //create a User thread to represent the Client
                executor.execute(clientHandler); //execute the user thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    private static Runnable rmiServer = () -> {
        System.out.println("LOG: Starting RMI registry...");
        startRmiRegistry();
        System.out.println("LOG: RMI service started");

        System.out.println("LOG: creating RMI protocol implementation...");
        RmiController rmiController;
        try {
            rmiController = new RmiControllerImpl();
        } catch (RemoteException e) {
            e.printStackTrace();
            stopRmiRegistry();
            return;
        }
        System.out.println("LOG: RMI done creating");

        System.out.println("LOG: binding RMI protocol implementation to registry...");
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry();
        } catch (RemoteException e) {
            e.printStackTrace();
            stopRmiRegistry();
            return;
        }

        try {
            registry.bind(RmiController.REMOTE_REFERENCE_NAME, rmiController);
        } catch (RemoteException e) {
            e.printStackTrace();
            stopRmiRegistry();
            return;
        } catch (AlreadyBoundException e) {
            System.out.println("LOG: RMI registry service already bound, unbinding...");
            try {
                registry.unbind(RmiController.REMOTE_REFERENCE_NAME);
            } catch (RemoteException | NotBoundException ex) {
                ex.printStackTrace();
                stopRmiRegistry();
                return;
            }
            System.out.println("LOG: RMI done unbinding");

            System.out.println("LOG: rebinding RMI registry service...");
            try {
                registry = LocateRegistry.getRegistry();
                registry.bind(RmiController.REMOTE_REFERENCE_NAME, rmiController);
            } catch (RemoteException | AlreadyBoundException ex) {
                ex.printStackTrace();
                stopRmiRegistry();
                return;
            }
            System.out.println("LOG: RMI service rebounded");
        }
        System.out.println("LOG: RMI done binding");
        System.out.println("LOG: RMI server running on " + SERVER_ADDRESS + ":" + SERVER_PORT);
    };

    //starts the Socket server on a different Thread using Executor
    private static void startSocketServer() {
        executor.execute(socketServer);
    }

    //starts a RMI Server on a different Thread using Executor
    private static void startRmiServer() {
        executor.execute(rmiServer);
    }

    //starts the RMI registry via command line
    private static void startRmiRegistry() {
        String RMI_REGISTRY_EXECUTION_PATH = RmiController.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toString()
                .substring(6)
                .replaceAll("%20", " ");
        String START_RMI_REGISTRY_COMMAND = "cd /d " + RMI_REGISTRY_EXECUTION_PATH + "&&" + "start rmiregistry";
        executeCMDCommand(START_RMI_REGISTRY_COMMAND);
    }

    //stops the RMI registry via command line
    private static void stopRmiRegistry() {
        String STOP_RMI_REGISTRY_COMMAND = "taskkill /im rmiregistry.exe";
        executeCMDCommand(STOP_RMI_REGISTRY_COMMAND);
    }

    //clear the cmd to better display LOG activity
    private static void clearCMD() {
        executeCMDCommand("cls");
    }

    // [see Client.NOTE 5] execute a cmd command
    private static void executeCMDCommand(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", command);
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.err.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("ERROR: insert SERVER_ADDRESS as first argument and SERVER_PORT as second");
            return;
        }

        try {
            SERVER_ADDRESS = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        SERVER_PORT = Integer.parseInt(args[1]);

        clearCMD();

        startSocketServer();
        startRmiServer();
    }
}