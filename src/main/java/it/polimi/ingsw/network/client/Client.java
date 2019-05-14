package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.common.exceptions.ConfigurationException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;

import java.io.IOException;
import java.util.Scanner;

/*
 * TODO:
 *  NOTE 4: for now the Client interaction with the program is mixed with the communication logic. It will be split
 *          and managed by different entities.
 *  NOTE 5: output format is based on Windows CMD only. In the futureUpdate it will be extended to support MacOS and Linux.
 *  NOTE 6: all exceptions are thrown as they are. In the final release they will be handled with proper
 *          user friendly messages.
 *
 * */

public class Client {
    private static String hostAddress;
    private static int port;

    private static final Scanner in = new Scanner(System.in);

    private static CommunicationHandler communicationHandler;

    private static String username;

    //register a Client into the ServerController
    private static void register() {
        boolean valid = false;
        do {
            System.out.print("Username: ");
            username = in.nextLine();

            try {
                communicationHandler.register(username);
                valid = true;
            } catch (ConnectionException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (ClientAlreadyRegisteredException e) {
                System.out.println(e.getMessage());
            }
        } while (!valid);
    }

    //unregister the Client from the ServerController global list
    private static void unregister() {
        try {
            communicationHandler.unregister(username);
            System.out.println("Logout success");
            System.exit(0);
        } catch (ConnectionException | ClientNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //ask the user to perform an action (for now 0 = logout is the only one)
    private static int requestAction() {
        int action = 0;
        boolean valid = false;
        System.out.println("Choose an action:\n");
        System.out.println("[0] Logout\n");
        do {
            System.out.print("Action: ");
            try {
                action = Integer.parseInt(in.nextLine());
                if (action == 0)
                    valid = true;
            } catch (NumberFormatException ignored) {
            }
        } while (!valid);
        return action;
    }

    //prints a welcome screen
    private static void printWelcomeScreen() {
        clearCMD();
        System.out.println("Welcome to Adrenaline !");
    }

    // [see NOTE 5] clear the console output (works on Windows only)
    private static void clearCMD() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("ERROR: correct syntax is: Client [ip address] [port] -conn [s/r]");
            System.exit(-1);
            return;
        }
        hostAddress = args[0];

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: server port not in range [1025 - 65535]");
            System.exit(-1);
            return;
        }

        if (!args[2].equals("-conn")) {
            System.err.println("ERROR: correct syntax is: Client [ip address] [port] -conn [s/r]");
            System.exit(-1);
            return;
        }
        String interfaceType = args[3];

        CommunicationHandler.Interface communicationInterface;
        if (interfaceType.equals("s")) {
            communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
        } else if (interfaceType.equals("r")) {
            communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
        } else {
            System.err.println("ERROR: options for param \"-conn\" must be [s] or [r]");
            System.exit(-1);
            return;
        }

        try {
            communicationHandler = new CommunicationHandler(hostAddress, port, communicationInterface);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            System.exit(-1);
            return;
        }

        //prints a welcome screen
        printWelcomeScreen();

        //register the Client into the ServerController
        register();

        //request the next action to do
        int action = requestAction();

        if (action == 0) {
            unregister();
        }
    }
}
