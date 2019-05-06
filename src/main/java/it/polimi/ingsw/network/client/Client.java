package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.client.networkInterface.NetworkHandler;
import it.polimi.ingsw.network.common.exceptions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private static String username; //the Client unique username
    private static String lobbyName; //the unique Lobby name this Client is currently logged in
    private static List<String> lobbies;

    private static NetworkHandler networkHandler; //responsible for the communication to the server

    private static Future<?> futureUpdate; //the Future related to the update procedure
    private static ScheduledExecutorService executor; //the Executor responsible for the update procedure

    private static Scanner sin;
    private static PrintWriter sout;
    private static PrintWriter serr;

    //register a Client into the Server
    private static void register() {
        boolean success = false;
        do {
            out("Username: ", false);
            username = sin.nextLine();

            if (username != null && !username.isBlank()) {
                try {
                    networkHandler.register(username);
                    success = true;
                } catch (ServerRegistrationFailedException | ConnectionLostException e) {
                    e.printStackTrace();
                    try {
                        networkHandler.unregister(username);
                    } catch (ServerUnregisteringFailedException | UserNotFoundException | ConnectionLostException ignored) {
                    }
                    System.exit(-1);
                } catch (UserAlreadyAddedException e) {
                    //err(e.getMessage());
                    e.printStackTrace();
                }
            }
        } while (!success);
    }

    //create a new Lobby and login the Lobby author
    private static void initLobby() {
        boolean success = false;
        String lobbyPassword;
        do {
            lobbyName = requestLobbyName();
            lobbyPassword = requestLobbyPassword();

            try {
                networkHandler.initLobby(lobbyName, lobbyPassword, username);
                System.out.println("Lobby successfully created!");
                success = true;
            } catch (ConnectionLostException | LobbyCreationFailedException | LobbyLoginFailedException
                    | LobbyNotFoundException | InvalidPasswordException | UserAlreadyAddedException
                    | LobbyFullException | UserNotFoundException e) {
                e.printStackTrace();
                try {
                    networkHandler.unregister(username);
                } catch (ServerUnregisteringFailedException | UserNotFoundException | ConnectionLostException ignored) {
                }
                System.exit(-1);
            } catch (LobbyAlreadyExistsException e) {
                //error(e.getMessage());
                e.printStackTrace();
            }
        } while (!success);
    }

    //log a registered Client into a chosen Lobby
    private static void loginToLobby() {
        String lobbyPassword = requestLobbyPassword();
        try {
            networkHandler.login(lobbyName, username, lobbyPassword);
            out("Login success!", true);
        } catch (ConnectionLostException | LobbyLoginFailedException | LobbyNotFoundException | UserNotFoundException
                | LobbyFullException | UserAlreadyAddedException | InvalidPasswordException e) {
            e.printStackTrace();
            try {
                networkHandler.unregister(username);
            } catch (ServerUnregisteringFailedException | UserNotFoundException | ConnectionLostException ignored) {
            }
            System.exit(-1);
        }
    }

    //logout this Client from the Lobby he is currently logged in
    private static void logoutFromLobby() {
        try {
            networkHandler.logout(lobbyName, username);
            out("Logout success!", true);
        } catch (LobbyLogoutFailedException | ConnectionLostException | LobbyNotFoundException
                | UserNotFoundException | EmptyLobbyException e) {
            e.printStackTrace();
            try {
                networkHandler.unregister(username);
            } catch (ServerUnregisteringFailedException | UserNotFoundException | ConnectionLostException ignored) {
            }
            System.exit(-1);
        }
    }

    //unregister the Client from the Server global list
    private static void unregister() {
        try {
            networkHandler.unregister(username);
            out("Unregistering success!", true);
        } catch (ServerUnregisteringFailedException | UserNotFoundException | ConnectionLostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //update the Lobby list and print them
    private static void startUpdateAndPrint() {
        Runnable updateTask = () -> {
            Map<String, String> lobbyInfo;
            try {
                lobbyInfo = networkHandler.getLobbies();
                lobbies = Collections.synchronizedList(new ArrayList<>(lobbyInfo.keySet()));
                printAll(lobbyInfo);
            } catch (ConnectionLostException e) {
                e.printStackTrace();
                try {
                    networkHandler.unregister(username);
                } catch (ServerUnregisteringFailedException | UserNotFoundException | ConnectionLostException ignored) {
                }
                System.exit(-1);
            }
        };
        executor = Executors.newSingleThreadScheduledExecutor();
        futureUpdate = executor.scheduleAtFixedRate(updateTask, 0, 5, TimeUnit.SECONDS);
    }

    //stops the update and print process
    private static void stopUpdateAndPrint() {
        if (!futureUpdate.isDone()) {
            futureUpdate.cancel(true);
            executor.shutdown();
        }
    }

    //return the client choice: 'n' or a valid Lobby name
    private static String requestChoice() {
        String choice; //the Client choice
        boolean valid = false;
        do {
            out("Choice: ", false);
            choice = sin.nextLine();

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

    //request the new Lobby name
    private static String requestLobbyName() {
        String name;
        do {
            out("Lobby name: ", false);
            name = sin.nextLine();
        } while (name == null || name.isBlank());
        return name;
    }

    //request a Lobby password
    private static String requestLobbyPassword() {
        out("Password: ", false);
        return sin.nextLine();
    }

    //ask the user to perform an action (for now 0 = logout is the only one)
    private static int requestAction() {
        int action = 0;
        boolean valid = false;
        out("Choose an action:\n", true);
        out("[0] Logout\n", true);
        do {
            out("Action: ", false);
            try {
                action = Integer.parseInt(sin.nextLine());
                if (action == 0)
                    valid = true;
            } catch (NumberFormatException ignored) {
            }
        } while (!valid);
        return action;
    }

    // [see NOTE 5] clear the console output (works on Windows only)
    private static void clearCMD() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //prints a welcome screen
    private static void printWelcomeScreen() {
        clearCMD();
        out("Welcome to Adrenaline !", true);
    }

    //print the given Lobbies and some other commands
    private static void printAll(Map<String, String> lobbies) {
        clearCMD();

        out("Welcome to Adrenaline, " + username + " !", true);
        out("List of all Lobbies:\n", true);

        int i = 0;
        for (Map.Entry<String, String> lobby : lobbies.entrySet())
            out("[" + i++ + "] " + lobby.getValue() + " " + lobby.getKey(), true);

        out("[n] to create a new lobby\n", true);
        out("Choice: ", false);
    }

    private static void out(String message, boolean newLine) {
        if (newLine)
            sout.println(message);
        else
            sout.print(message);
        sout.flush();
    }

    private static void err(String message) {
        serr.println("ERROR: " + message);
        serr.flush();
    }

    private static void openStreams() {
        sin = new Scanner(System.in);
        sout = new PrintWriter(System.out, true);
        serr = new PrintWriter(System.err, true);
    }

    private static void closeStreams() {
        try {
            sin.close();
        } catch (Exception ignored) {
        }
        try {
            sout.close();
        } catch (Exception ignored) {
        }
        try {
            serr.close();
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        //open the streams
        openStreams();

        if (args.length != 2) {
            System.err.println("ERROR: insert SERVER_ADDRESS as first argument and SERVER_PORT as second");
            return;
        }
        String serverAddress = args[0];
        int serverPort;

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            err("ERROR: server port not in range [1025 - 65535]");
            closeStreams();
            return;
        }

        try {
            networkHandler = new NetworkHandler(serverAddress, serverPort, NetworkHandler.RMI_NETWORK_INTERFACE);
        } catch (NetworkInterfaceConfigurationException e) {
            //err(e.getMessage());
            e.printStackTrace();
            closeStreams();
            return;
        }

        //print a welcome screen
        printWelcomeScreen();

        //register the User
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

        //close the streams
        closeStreams();
    }
}
