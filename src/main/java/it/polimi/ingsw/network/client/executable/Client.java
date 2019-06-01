package it.polimi.ingsw.network.client.executable;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.view.remote.BoardArt;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Client extends Application implements Runnable, EventHandler<ActionEvent> {
    private String hostAddress;
    private int port;
    private CommunicationHandler.Interface communicationInterface;
    private String graphicalInterface;

    private static final Scanner in = new Scanner(System.in);

    private static CommunicationHandler communicationHandler;

    private static final List<String> lobbies = Collections.synchronizedList(new ArrayList<>());

    private static Runnable updateTask;
    private static ScheduledExecutorService executor;
    private static ScheduledFuture<?> futureUpdate;
    private static final int UPDATE_REQUEST_PERIOD = 5;

    private static BoardArt boardArt;

    private String[] args;

    private static final Console console = new Console();

    public Client() {
        //without this the GUI app won't start :(
    }

    public Client(String hostAddress, int port, CommunicationHandler.Interface communicationInterface, String graphicalInterface, String[] args) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.communicationInterface = communicationInterface;
        this.graphicalInterface = graphicalInterface;

        this.args = args;
    }

    private static String in() {
        try {
            return in.nextLine();
        } catch (NoSuchElementException ignored) {
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
                console.err("connection to the server is lost, cause: " + e.getMessage());
                System.exit(-1);
            } catch (ClientAlreadyRegisteredException e) {
                console.err(e.getMessage());
            }
        } while (!valid);
    }

    //unregister the Client from the Server global list
    private static void unregister() {
        try {
            communicationHandler.unregister();
        } catch (ConnectionException | ClientNotRegisteredException e) {
            console.err("connection to the server is lost, cause: " + e.getMessage());
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
    private static void loginToLobby(String lobbyName) {
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

    private static void logoutFromLobby() {
        try {
            communicationHandler.logout();
            console.tinyPrintln("Lobby logout success!");
        } catch (ConnectionException e) {
            console.err("connection to the server is lost, cause: " + e.getMessage());
            System.exit(-1);
        }
    }

    //ask the user to perform an action (for now 0 = logout is the only one)
    private static int requestAction() {
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

    private static String requestUsername() {
        String name;
        do {
            console.tinyPrint("Username: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request the new Lobby name
    private static String requestLobbyName() {
        String name;
        do {
            console.tinyPrint("Lobby name: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request a Lobby password
    private static String requestLobbyPassword() {
        console.tinyPrint("Password: ");
        return in();
    }

    //return the client choice: 'n' or a valid Lobby name
    private static String requestChoice() {
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
    private static void printWelcomeScreen() {
        console.clear();
        console.tinyPrintln("Welcome to Adrenaline !");
    }

    //print the given Lobbies and some other commands
    private static void printAll(Map<String, String> lobbies) {
        console.clear();

        console.tinyPrintln("Welcome to Adrenaline, " + communicationHandler.getUsername() + " !");
        console.tinyPrintln("List of all Lobbies:\n");

        int i = 0;
        for (Map.Entry<String, String> lobby : lobbies.entrySet())
            console.tinyPrintln("[" + i++ + "] " + lobby.getValue() + " " + lobby.getKey());

        console.tinyPrintln("[n] to create a new lobby\n");
        console.tinyPrint("Choice: ");
    }

    @Override
    public void run() {

        try {
            communicationHandler = new CommunicationHandler(hostAddress, port, communicationInterface);
        } catch (ConnectionException e) {
            console.err("connection to the server is lost, cause: " + e.getMessage());
            System.exit(-1);
            return;
        }

        if (graphicalInterface.equals("CLI")) {

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
        } else {
            //GUI
            launch(args);
        }
    }

    @Override   //needed for JavaFX
    public void start(Stage primaryStage) {
        boardArt = new BoardArt(communicationHandler);
        boardArt.displayLogin(primaryStage);
    }

    @Override   //needed for JavaFX
    public void handle(javafx.event.ActionEvent event) {
        //blank, as events are handled by using lambda functions
    }
}
