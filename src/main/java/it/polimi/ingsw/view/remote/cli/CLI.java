package it.polimi.ingsw.view.remote.cli;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.deliverable.*;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColorPrinter;
import it.polimi.ingsw.util.Dispatcher;
import it.polimi.ingsw.view.remote.GraphicalInterface;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.jansi.AnsiConsole;

@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
public class CLI implements GraphicalInterface {
    private final List<String> lobbies = Collections.synchronizedList(new ArrayList<>());
    private List<String> opponents;

    private final Scanner in = new Scanner(System.in);

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> futureUpdate;

    private final int UPDATE_REQUEST_PERIOD = 5;

    private CommunicationHandler communicationHandler;

    public CLI() {
        AnsiConsole.systemInstall();
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
        startLobbyUpdateAndPrint();

        //get the client response: create a new lobby ('n') or select a valid lobby to login (number)
        String choice = requestChoice();

        //stops the update and printOpponents requests once the Client selected a valid choice
        stopLobbyUpdateAndPrint();

        if (choice.equals("n")) //Client wants to create a new Lobby
            initLobby();
        else //Client wants to join an existing Lobby (choice is the lobby name)
            loginToLobby(choice);

        //display the pre-game information: timer countdown and opponents list
        printPreGameInfo();

        //here the game starts //TODO determine exit conditions
        while (true) {
            try {
                Deliverable deliverable = communicationHandler.nextDeliverable();
                if (deliverable != null)
                    manageArrivals(deliverable);
            } catch (ConnectionException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        /*logoutFromLobby();
        unregister();*/
    }

    private void manageArrivals(Deliverable deliverable) throws ConnectionException {
        switch (deliverable.getType()) {
            case INFO:
                ColorPrinter.println(deliverable.getMessage());
                break;
            case DUAL:
                try {
                    boolean res = Dispatcher.requestBoolean(deliverable.getMessage());
                    communicationHandler.deliver(new Response(res ? 1 : 0));
                } catch (ClientTimeOutException e) {
                    communicationHandler.deliver(Response.taint());
                }
                break;
            case MAPPED:
                try {
                    int res = Dispatcher.requestMappedOption(deliverable.getMessage(), ((Mapped) deliverable).getOptions(), ((Mapped) deliverable).getKeys());
                    communicationHandler.deliver(new Response(res));
                } catch (ClientTimeOutException e) {
                    communicationHandler.deliver(Response.taint());
                }
                break;
            case ASSETS:
                ColorPrinter.print(((Assets) deliverable).unpack());
                ColorPrinter.println(Color.RESET);
            default:
                break;
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
                //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            } catch (ClientAlreadyRegisteredException e) {
                ColorPrinter.err(e.getMessage());
            }
        } while (!valid);
    }

    //unregister the Client from the Server global list
    private void unregister() {
        try {
            communicationHandler.unregister();
        } catch (ConnectionException | ClientNotRegisteredException e) {
            //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }

    //update the Lobby list and printOpponents them
    private void startLobbyUpdateAndPrint() {
        //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
        Runnable updateTask = () -> {

            Map<String, String> lobbyInfo;

            try {
                lobbyInfo = communicationHandler.requestLobbyUpdate();
            } catch (ConnectionException e) {
                //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
                return;
            }
            synchronized (lobbies) {
                lobbies.clear();
                lobbies.addAll(lobbyInfo.keySet());
            }
            printAll(lobbyInfo);
        };
        executor = Executors.newScheduledThreadPool(2);
        futureUpdate = executor.scheduleAtFixedRate(updateTask, 0, UPDATE_REQUEST_PERIOD, TimeUnit.SECONDS);
    }

    //stops the update and printOpponents process
    private void stopLobbyUpdateAndPrint() {
        if (!futureUpdate.isDone())
            futureUpdate.cancel(true);
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
                ColorPrinter.println("Lobby creation success!\n");
            } catch (ConnectionException e) {
                //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            } catch (LobbyAlreadyExistsException e) {
                ColorPrinter.err(e.getMessage());
            }
        } while (!valid);
    }

    //log a registered Client into a chosen Lobby
    private void loginToLobby(String lobbyName) {
        String lobbyPassword = requestLobbyPassword();

        try {
            communicationHandler.login(lobbyName, lobbyPassword);
            ColorPrinter.println("Lobby login success!");
        } catch (ConnectionException e) {
            //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (LobbyNotFoundException | LobbyFullException | InvalidPasswordException | PlayerAlreadyAddedException e) {
            ColorPrinter.err(e.getMessage());
            System.exit(-1);
        } catch (GameAlreadyStartedException e) {
            ColorPrinter.err("can't login, game already started");
            System.exit(-1);
        }
    }

    private void logoutFromLobby() {
        try {
            communicationHandler.logout();
            ColorPrinter.println("Lobby logout success!");
        } catch (ConnectionException e) {
            //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private String requestUsername() {
        String name;
        do {
            ColorPrinter.print("Username: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request the new Lobby name
    private String requestLobbyName() {
        String name;
        do {
            ColorPrinter.print("Lobby name: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    //request a Lobby password
    private String requestLobbyPassword() {
        ColorPrinter.print("Password: ");
        return in();
    }

    //return the client choice: 'n' or a valid Lobby name
    private String requestChoice() {
        String choice; //the Client choice
        boolean valid = false;
        do {
            ColorPrinter.print("Choice: ");
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
        ColorPrinter.clear();
        ColorPrinter.println("Welcome to Adrenaline !");
    }

    //printOpponents the given Lobbies and some other commands
    private void printAll(Map<String, String> lobbies) {
        ColorPrinter.clear();

        ColorPrinter.println("Welcome to Adrenaline, " + communicationHandler.getUsername() + " !");
        ColorPrinter.println("List of all Lobbies:\n");

        int i = 0;
        for (Map.Entry<String, String> lobby : lobbies.entrySet())
            ColorPrinter.println("[" + i++ + "] " + lobby.getValue() + " " + lobby.getKey());

        ColorPrinter.println("[n] to create a new lobby\n");
        ColorPrinter.print("Choice: ");
    }

    private void printPreGameInfo() {
        NetworkMessage message;
        do {
            try {
                message = communicationHandler.getPreGameInfoUpdate();
            } catch (ConnectionException e) {
                //ColorPrinter.err(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
                return;
            }

            switch (message.getType()) {
                case COUNTDOWN_UPDATE:
                    printCountDown((int) message.getContent());
                    break;
                case COUNTDOWN_STOPPED:
                    stopCountDownPrint();
                    printPauseMessage();
                    break;
                case OPPONENTS_LIST_UPDATE:
                    opponents = (List<String>) message.getContent();
                    printOpponents();
                    break;
                default:
                    break;
            }
        } while (!message.getType().equals(MessageType.COUNTDOWN_EXPIRED)); //if timer expires the game is about to start
        stopCountDownPrint();
    }

    private void printCountDown(int startingSeconds) {
        futureUpdate.cancel(true);

        AtomicInteger timeLeft = new AtomicInteger(startingSeconds);
        Runnable printCountDownTask = () -> {
            printOpponents();

            if (timeLeft.decrementAndGet() >= 0) {
                ColorPrinter.println("Game starts in " + timeLeft.get());
            } else {
                stopCountDownPrint();
            }
        };
        futureUpdate = executor.scheduleAtFixedRate(printCountDownTask, 0, 1, TimeUnit.SECONDS);
    }

    private void stopCountDownPrint() {
        futureUpdate.cancel(true);
        executor.shutdown();
    }

    private void printPauseMessage() {
        printOpponents();
        ColorPrinter.println("Too many players left the lobby, countdown suspended.");
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    private void printOpponents() {
        ColorPrinter.clear();

        if (opponents.size() == 0)
            ColorPrinter.println("Waiting for opponents to join...");
        else {
            ColorPrinter.println("Your opponents for this game:\n");
            opponents.forEach(ColorPrinter::println);
        }

        ColorPrinter.println("");
    }
}
