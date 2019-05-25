package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.common.exceptions.*;
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

    private static final List<String> lobbies = Collections.synchronizedList(new ArrayList<>());

    private static Runnable updateTask;
    private static ScheduledExecutorService executor;
    private static Future<?> futureUpdate;
    private static final int UPDATE_REQUEST_PERIOD = 5;

    private static String in() {
        try {
            return in.nextLine();
        } catch (NoSuchElementException e) {
            ConsoleController.err(e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    //register a Client into the Server
    private static void register() {
        boolean valid = false;

        do {
            String username = requestUsername();

            try {
                communicationHandler.register(username);
                valid = true;
            } catch (ConnectionException e) {
                ConsoleController.err(e.getMessage() + "\n");
                System.exit(-1);
            } catch (ClientAlreadyRegisteredException e) {
                ConsoleController.err(e.getMessage() + "\n");
            }
        } while (!valid);
    }

    //unregister the Client from the Server global list
    private static void unregister() {
        try {
            communicationHandler.unregister();
        } catch (ConnectionException | ClientNotRegisteredException e) {
            ConsoleController.err(e.getMessage() + "\n");
            System.exit(-1);
        }
        System.exit(0);
    }

    //update the Lobby list and print them
    private static void startUpdateAndPrint() {
        updateTask = () -> {

            Map<String, String> lobbyInfo;

            try {
                lobbyInfo = communicationHandler.requestUpdate();
            } catch (ConnectionException e) {
                ConsoleController.err(e.getMessage() + "\n");
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
        boolean valid = false;
        do {
            String lobbyName = requestLobbyName();
            String lobbyPassword = requestLobbyPassword();

            try {
                communicationHandler.initLobby(lobbyName, lobbyPassword);
                valid = true;
                ConsoleController.out("Lobby creation success!\n\n");
            } catch (ConnectionException e) {
                ConsoleController.err(e.getMessage() + "\n");
                System.exit(-1);
            } catch (LobbyAlreadyExistsException e) {
                ConsoleController.err(e.getMessage() + "\n");
            }
        } while (!valid);
    }

    //log a registered Client into a chosen Lobby
    private static void loginToLobby(String lobbyName) {
        String lobbyPassword = requestLobbyPassword();

        try {
            communicationHandler.login(lobbyName, lobbyPassword);
            ConsoleController.out("Lobby login success!\n");
        } catch (ConnectionException | LobbyNotFoundException | LobbyFullException | InvalidPasswordException e) {
            ConsoleController.err(e.getMessage() + "\n");
            System.exit(-1);
        }
    }

    private static void logoutFromLobby() {
        try {
            communicationHandler.logout();
            ConsoleController.out("Lobby logout success!\n");
        } catch (ConnectionException e) {
            ConsoleController.err(e.getMessage() + "\n");
            System.exit(-1);
        }
    }

    //ask the user to perform an action (for now 0 = logout is the only one)
    private static int requestAction() {
        int action = 0;
        boolean valid = false;
        ConsoleController.out("Choose an action:\n\n");
        ConsoleController.out("[0] Logout\n\n");
        do {
            ConsoleController.out("Action: ");
            try {
                action = Integer.parseInt(in());
                if (action == 0)
                    valid = true;
            } catch (NumberFormatException ignored) {
            }
        } while (!valid);
        return action;
    }

    private static String requestUsername() {
        String name;
        do {
            ConsoleController.out("Username: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request the new Lobby name
    private static String requestLobbyName() {
        String name;
        do {
            ConsoleController.out("Lobby name: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request a Lobby password
    private static String requestLobbyPassword() {
        ConsoleController.out("Password: ");
        return in();
    }

    //return the client choice: 'n' or a valid Lobby name
    private static String requestChoice() {
        String choice; //the Client choice
        boolean valid = false;
        do {
            ConsoleController.out("Choice: ");
            choice = in();

            if (choice.equals("n")) {
                valid = true;
            } else
                try {
                    synchronized (lobbies) {
                        int pos = Integer.parseInt(choice);
                        choice = lobbies.get(pos);
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
        ConsoleController.out("Welcome to Adrenaline !\n");
    }

    //print the given Lobbies and some other commands
    private static void printAll(Map<String, String> lobbies) {
        new ConsoleController().clearConsole();

        ConsoleController.out("Welcome to Adrenaline, " + communicationHandler.getUsername() + " !\n");
        ConsoleController.out("List of all Lobbies:\n\n");

        int i = 0;
        for (Map.Entry<String, String> lobby : lobbies.entrySet())
            ConsoleController.out("[" + i++ + "] " + lobby.getValue() + " " + lobby.getKey() + "\n");

        ConsoleController.out("[n] to create a new lobby\n\n");
        ConsoleController.out("Choice: ");
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            ConsoleController.err("correct syntax is: Client [ip address] [port] -conn [s/r]\n");
            System.exit(-1);
            return;
        }
        hostAddress = args[0];

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ConsoleController.err("server port not in range [1025 - 65535]\n");
            System.exit(-1);
            return;
        }

        if (!args[2].equals("-conn")) {
            ConsoleController.err("correct syntax is: Client [ip address] [port] -conn [s/r]\n");
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
            ConsoleController.err("options for param \"-conn\" must be [s] or [r]\n");
            System.exit(-1);
            return;
        }

        try {
            communicationHandler = new CommunicationHandler(hostAddress, port, communicationInterface);
        } catch (ConnectionException e) {
            ConsoleController.err(e.getMessage() + "\n");
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
            loginToLobby(choice);
        }
        //TODO: note: from here client is registered and need to do something :)

        //request the next action to do
        int action = requestAction();

        if (action == 0) {
            logoutFromLobby();
            unregister();
        }
    }
}
