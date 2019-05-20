package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    private static String hostAddress;
    private static int port;

    private static final Scanner in = new Scanner(System.in);

    private static CommunicationHandler communicationHandler;

    private static String username;

    private static void out(String string) {
        System.out.print(string);
    }

    private static void err(String string) {
        System.err.print("ERROR: " + string);
    }

    private static String in() {
        return in.nextLine();
    }

    //register a Client into the ServerController
    private static void register() {
        boolean valid = false;
        Message message;

        do {
            out("Username: ");
            username = in();

            message = Message.completeMessage(username, MessageType.REGISTER_REQUEST, username);

            try {
                communicationHandler.sendMessage(message);
                message = communicationHandler.nextMessage();

                switch (message.getType()) {
                    case REGISTER_SUCCESS:
                        valid = true;
                        break;
                    case PLAYER_ALREADY_REGISTERED_ERROR:
                    default:
                        break;
                }
            } catch (ConnectionException e) {
                //err(e.nextMessage() + "\n");
                e.printStackTrace();
                System.exit(-1);
            }
        } while (!valid);
    }

    //unregister the Client from the ServerController global list
    private static void unregister() {
        Message message = Message.completeMessage(username, MessageType.UNREGISTER_REQUEST, username);
        try {
            communicationHandler.sendMessage(message);
            message = communicationHandler.nextMessage();

            switch (message.getType()) {
                case UNREGISTER_SUCCESS:
                    out("Logout success\n");
                    System.exit(0);
                case PLAYER_NOT_REGISTERED_ERROR:
                default:
                    err("client not registered, unregistering failed\n");
            }
        } catch (ConnectionException e) {
            //err(e.nextMessage() + "\n");
            e.printStackTrace();
        }
        System.exit(-1);
    }

    //ask the user to perform an action (for now 0 = logout is the only one)
    private static int requestAction() {
        int action = 0;
        boolean valid = false;
        out("Choose an action:\n\n");
        out("[0] Logout\n\n");
        do {
            out("Action: ");
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
        out("Welcome to Adrenaline !\n");
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
            err("correct syntax is: Client [ip address] [port] -conn [s/r]\n");
            System.exit(-1);
            return;
        }
        hostAddress = args[0];

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            err("server port not in range [1025 - 65535]\n");
            System.exit(-1);
            return;
        }

        if (!args[2].equals("-conn")) {
            err("correct syntax is: Client [ip address] [port] -conn [s/r]\n");
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
            err("options for param \"-conn\" must be [s] or [r]\n");
            System.exit(-1);
            return;
        }

        try {
            communicationHandler = new CommunicationHandler(hostAddress, port, communicationInterface);
        } catch (ConnectionException e) {
            //err("ERROR: " + e.nextMessage() + "\n");
            e.printStackTrace();
            System.exit(-1);
            return;
        }

        //prints a welcome screen
        printWelcomeScreen();

        //register the Client into the Server
        register();

        //request the next action to do
        int action = requestAction();

        if (action == 0) {
            unregister();
        }
    }
}
