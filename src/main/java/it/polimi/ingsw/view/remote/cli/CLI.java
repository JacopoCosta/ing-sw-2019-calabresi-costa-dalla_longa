package it.polimi.ingsw.view.remote.cli;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.view.remote.GraphicalInterface;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CLI implements GraphicalInterface {
    private final List<String> lobbies = Collections.synchronizedList(new ArrayList<>());

    private final Scanner in = new Scanner(System.in);

    private Runnable updateTask;
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> futureUpdate;
    private final int UPDATE_REQUEST_PERIOD = 5;

    private CommunicationHandler communicationHandler;

    private final Console console;

    public CLI() {
        console = Console.getInstance();
    }

    @Override
    public void setCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    @Override
    public void display() {
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

    private String in() {
        try {
            return in.nextLine();
        } catch (NoSuchElementException ignored) {
            System.exit(-1);
            return null;
        }
    }

    //register a Client into the Server
    private void register() {
        boolean valid = false;

        do {
            String username = requestUsername();

            try {
                communicationHandler.register(username);
                valid = true;
            } catch (ConnectionException e) {
                console.err("connection to the server is lost, cause: " + e.getMessage());
                System.exit(-1);
            } catch (ClientAlreadyRegisteredException e) {
                console.err(e.getMessage());
            }
        } while (!valid);
    }

    //unregister the Client from the Server global list
    private void unregister() {
        try {
            communicationHandler.unregister();
        } catch (ConnectionException | ClientNotRegisteredException e) {
            console.err("connection to the server is lost, cause: " + e.getMessage());
            System.exit(-1);
        }
        System.exit(0);
    }

    //update the Lobby list and print them
    private void startUpdateAndPrint() {
        updateTask = () -> {

            Map<String, String> lobbyInfo;

            try {
                lobbyInfo = communicationHandler.requestUpdate();
            } catch (ConnectionException e) {
                console.err("connection to the server is lost, cause: " + e.getMessage());
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
    private void stopUpdateAndPrint() {
        if (!futureUpdate.isDone()) {
            futureUpdate.cancel(true);
            executor.shutdown();
        }
    }

    //create a new Lobby and login the Lobby author
    private void initLobby() {
        boolean valid = false;
        do {
            String lobbyName = requestLobbyName();
            String lobbyPassword = requestLobbyPassword();

            try {
                communicationHandler.initLobby(lobbyName, lobbyPassword);
                valid = true;
                console.tinyPrintln("Lobby creation success!\n");
            } catch (ConnectionException e) {
                console.err("connection to the server is lost, cause: " + e.getMessage());
                System.exit(-1);
            } catch (LobbyAlreadyExistsException e) {
                console.err(e.getMessage());
            }
        } while (!valid);
    }

    //log a registered Client into a chosen Lobby
    private void loginToLobby(String lobbyName) {
        String lobbyPassword = requestLobbyPassword();

        try {
            communicationHandler.login(lobbyName, lobbyPassword);
            console.tinyPrintln("Lobby login success!");
        } catch (ConnectionException e) {
            console.err("connection to the server is lost, cause: " + e.getMessage());
            System.exit(-1);
        } catch (LobbyNotFoundException | LobbyFullException | InvalidPasswordException e) {
            console.err(e.getMessage());
            System.exit(-1);
        }
    }

    private void logoutFromLobby() {
        try {
            communicationHandler.logout();
            console.tinyPrintln("Lobby logout success!");
        } catch (ConnectionException e) {
            console.err("connection to the server is lost, cause: " + e.getMessage());
            System.exit(-1);
        }
    }

    //ask the user to perform an action (for now 0 = logout is the only one)
    private int requestAction() {
        int action = 0;
        boolean valid = false;
        console.tinyPrintln("Choose an action:\n");
        console.tinyPrintln("[0] Logout\n");
        do {
            console.tinyPrint("Action: ");
            try {
                action = Integer.parseInt(in());
                if (action == 0)
                    valid = true;
            } catch (NumberFormatException ignored) {
            }
        } while (!valid);
        return action;
    }

    private String requestUsername() {
        String name;
        do {
            console.tinyPrint("Username: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request the new Lobby name
    private String requestLobbyName() {
        String name;
        do {
            console.tinyPrint("Lobby name: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request a Lobby password
    private String requestLobbyPassword() {
        console.tinyPrint("Password: ");
        return in();
    }

    //return the client choice: 'n' or a valid Lobby name
    private String requestChoice() {
        String choice; //the Client choice
        boolean valid = false;
        do {
            console.tinyPrint("Choice: ");
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
    private void printWelcomeScreen() {
        console.clear();
        console.tinyPrintln("Welcome to Adrenaline !");
    }

    //print the given Lobbies and some other commands
    private void printAll(Map<String, String> lobbies) {
        console.clear();

        console.tinyPrintln("Welcome to Adrenaline, " + communicationHandler.getUsername() + " !");
        console.tinyPrintln("List of all Lobbies:\n");

        int i = 0;
        for (Map.Entry<String, String> lobby : lobbies.entrySet())
            console.tinyPrintln("[" + i++ + "] " + lobby.getValue() + " " + lobby.getKey());

        console.tinyPrintln("[n] to create a new lobby\n");
        console.tinyPrint("Choice: ");
    }
}
