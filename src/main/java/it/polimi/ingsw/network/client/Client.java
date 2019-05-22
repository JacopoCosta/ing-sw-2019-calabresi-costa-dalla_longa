package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.util.ConsoleController;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {
    private static String hostAddress;
    private static int port;

    private static final Scanner in = new Scanner(System.in);

    private static CommunicationHandler communicationHandler;

    private static String username;
    private static String lobbyName;
    private static final List<String> lobbies = Collections.synchronizedList(new ArrayList<>());

    private static Runnable updateTask;
    private static ScheduledExecutorService executor;
    private static Future<?> futureUpdate;
    private static final int UPDATE_REQUEST_PERIOD = 5;

    private static void out(String string) {
        System.out.print(string);
    }

    private static void err(String string) {
        System.err.print("ERROR: " + string);
    }

    private static String in() {
        return in.nextLine();
    }

    //register a Client into the Server
    private static void register() {
        boolean valid = false;
        Message message;

        do {
            out("Username: ");
            username = in();

            message = Message.simpleMessage(username, MessageType.REGISTER_REQUEST);

            try {
                communicationHandler.sendMessage(message);
                message = communicationHandler.nextMessage();
            } catch (ConnectionException e) {
                err(e.getMessage() + "\n");
                System.exit(-1);
            }

            switch (message.getType()) {
                case REGISTER_SUCCESS:
                    valid = true;
                    break;
                case PLAYER_ALREADY_REGISTERED_ERROR:
                default:
                    break;
            }
        } while (!valid);
    }

    //unregister the Client from the Server global list
    private static void unregister() {
        Message message = Message.simpleMessage(username, MessageType.UNREGISTER_REQUEST);
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
            err(e.getMessage() + "\n");
            unregister();
        }
        System.exit(-1);
    }

    //update the Lobby list and print them
    private static void startUpdateAndPrint() {
        updateTask = () -> {
            Message update = Message.simpleMessage(username, MessageType.LOBBY_LIST_UPDATE_REQUEST);
            Map<String, String> lobbyInfo;

            try {
                communicationHandler.sendMessage(update);
                lobbyInfo = (Map<String, String>) communicationHandler.nextMessage().getContent();
            } catch (ConnectionException e) {
                unregister();
                System.exit(-1);
                return;
            }
            synchronized (lobbies) {
                lobbies.clear();
                lobbies.addAll(lobbyInfo.keySet());
            }
            printAll(lobbyInfo);
        };
        executor = Executors.newSingleThreadScheduledExecutor();
        futureUpdate = executor.scheduleAtFixedRate(updateTask, 0, UPDATE_REQUEST_PERIOD, TimeUnit.SECONDS);
    }

    //stops the update and print process
    private static void stopUpdateAndPrint() {
        if (!futureUpdate.isDone()) {
            futureUpdate.cancel(true);
            executor.shutdown();
        }
    }

    //create a new Lobby and login the Lobby author
    private static void initLobby() {
        boolean success = false;
        String lobbyPassword;
        do {
            lobbyName = requestLobbyName();
            lobbyPassword = requestLobbyPassword();

            String[] lobbyInfo = {lobbyName, lobbyPassword};
            Message message = Message.completeMessage(username, MessageType.LOBBY_INIT_REQUEST, lobbyInfo);

            try {
                communicationHandler.sendMessage(message);
                message = communicationHandler.nextMessage();
            } catch (ConnectionException e) {
                //err(e.getMessage() + "\n");
                e.printStackTrace();
                unregister();
                System.exit(-1);
            }

            switch (message.getType()) {
                case LOBBY_INIT_SUCCESS:
                    out("Lobby successfully created!\n");
                    success = true;
                    break;
                case LOBBY_INIT_FAILED:
                    //TODO handle error messages
                    break;
            }
        } while (!success);
    }

    //log a registered Client into a chosen Lobby
    private static void loginToLobby() {
        String lobbyPassword = requestLobbyPassword();
        String[] lobbyInfo = {lobbyName, lobbyPassword};

        Message message = Message.completeMessage(username, MessageType.LOBBY_INIT_REQUEST, lobbyInfo);

        try {
            communicationHandler.sendMessage(message);
            message = communicationHandler.nextMessage();
        } catch (ConnectionException e) {
            //out(e.getMessage() + "\n");
            e.printStackTrace();
            unregister();
            System.exit(-1);
            return;
        }

        switch (message.getType()) {
            case LOBBY_LOGIN_SUCCESS:
                out("Login success!\n");
                break;
            case LOBBY_LOGIN_FAILED:
                //TODO handle error messages
                break;
        }
    }

    private static void logoutFromLobby() {
        Message message = Message.completeMessage(username, MessageType.LOBBY_LOGOUT_REQUEST, lobbyName);

        try {
            communicationHandler.sendMessage(message);
            message = communicationHandler.nextMessage();
        } catch (ConnectionException e) {
            //out(e.getMessage() + "\n");
            e.printStackTrace();
            unregister();
            System.exit(-1);
            return;
        }

        switch (message.getType()) {
            case LOBBY_LOGOUT_SUCCESS:
                out("Login success!\n");
                break;
            case LOBBY_LOGOUT_FAILED:
                //TODO handle error messages
                break;
        }
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

    //request the new Lobby name
    private static String requestLobbyName() {
        String name;
        do {
            out("Lobby name: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request a Lobby password
    private static String requestLobbyPassword() {
        out("Password: ");
        return in();
    }

    //return the client choice: 'n' or a valid Lobby name
    private static String requestChoice() {
        String choice; //the Client choice
        boolean valid = false;
        do {
            out("Choice: ");
            choice = in();

            if (choice.equals("n")) {
                valid = true;
            } else
                try {
                    synchronized (lobbies) {
                        int val = Integer.parseInt(choice);
                        choice = lobbies.get(val);
                    }
                    valid = true;
                } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                }
        } while (!valid);
        return choice;
    }

    //prints a welcome screen
    private static void printWelcomeScreen() {
        new ConsoleController().clearConsole();
        out("Welcome to Adrenaline !\n");
    }

    //print the given Lobbies and some other commands
    private static void printAll(Map<String, String> lobbies) {
        new ConsoleController().clearConsole();

        out("Welcome to Adrenaline, " + username + " !\n");
        out("List of all Lobbies:\n\n");

        int i = 0;
        for (Map.Entry<String, String> lobby : lobbies.entrySet())
            out("[" + i++ + "] " + lobby.getValue() + " " + lobby.getKey() + "\n");

        out("[n] to create a new lobby\n\n");
        out("Choice: ");
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
            err(e.getMessage() + "\n");
            System.exit(-1);
            return;
        }

        //prints a welcome screen
        printWelcomeScreen();

        //register the Client into the Server
        register();

        //requests the updated Lobby list with a delay interval and prints them
        startUpdateAndPrint();

        //get the client response: create a new lobby ('n') or select a valid lobby to login (number)
        String choice = requestChoice();

        //stops the update and print requests once the Client selected a valid choice
        stopUpdateAndPrint();

        if (choice.equals("n")) //Client wants to create a new Lobby
            initLobby();
        else { //Client wants to join an existing Lobby (choice is the lobby name)
            lobbyName = choice;
            loginToLobby();
        }

        //request the next action to do
        int action = requestAction();

        if (action == 0) {
            logoutFromLobby();
            unregister();
        }
    }
}
