package it.polimi.ingsw.view.remote.cli;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.deliverable.*;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.util.Dispatcher;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColorPrinter;
import it.polimi.ingsw.view.remote.GraphicalInterface;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class implements {@link GraphicalInterface} to allow a {@code Client} to be executed via command line interface.
 */
@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
public class CLI implements GraphicalInterface {
    /**
     * The {@code List} of {@code Lobby} available for the {@code Client} to join.
     */
    private final List<String> lobbies = Collections.synchronizedList(new ArrayList<>());

    /**
     * The {@code List} of {@code Client}s connected in the same {@code Lobby} of the current one. This collection can be
     * empty if no {@code Client}a joined the actual {@code Lobby} yet.
     */
    private List<String> opponents;

    /**
     * The standard input method used by this CLI application.
     */
    private final Scanner in = new Scanner(System.in);

    /**
     * The {@link ScheduledExecutorService} responsible for all the periodical interrogations to the {@code Server}.
     */
    private ScheduledExecutorService executor;

    /**
     * The {@link ScheduledFuture} used to control the {@code executor} tasks.
     */
    private ScheduledFuture<?> futureUpdate;

    /**
     * The amount in seconds between successive {@link #executor} executions.
     */
    private final int UPDATE_REQUEST_PERIOD = 5;

    /**
     * The {@link CommunicationHandler} responsible for the interaction with the remote {@code Server}.
     */
    private CommunicationHandler communicationHandler;

    /**
     * A flag indicating whether it's necessary to wait for an incoming {@link Deliverable}. This happens if and only if
     * this flag is set to {@code true}.
     */
    private boolean keepAlive = true;

    /**
     * Sets the {@link #communicationHandler} interface for this console, allowing the {@code Client} to communicate with
     * the remote {@code Server}.
     *
     * @param communicationHandler the new {@link CommunicationHandler} of this graphical interface.
     */
    @Override
    public void setCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    /**
     * Starts the CLI command line application.
     */
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

        while (keepAlive) {
            try {
                Deliverable deliverable = communicationHandler.nextDeliverable();
                if (deliverable != null)
                    manageArrivals(deliverable);
            } catch (ConnectionException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        logoutFromLobby();
        unregister();
    }

    /**
     * Processes the newly arrived {@link Deliverable} and acts consequently, either by notifying the player,
     * printing the {@code CLI} interface, or opening a request routine via the {@link Dispatcher}.
     *
     * @param deliverable the newly arrived {@link Deliverable}.
     * @throws ConnectionException when calling a {@link CommunicationHandler#deliver(Deliverable)} throws
     *                             a {@link ConnectionException}.
     */
    private void manageArrivals(Deliverable deliverable) throws ConnectionException {
        switch (deliverable.getType()) {
            case INFO:
                if (Arrays.asList(
                        DeliverableEvent.UPDATE_TURN,
                        DeliverableEvent.UPDATE_DISCONNECT,
                        DeliverableEvent.UPDATE_WINNER
                ).contains(deliverable.getEvent()))
                    ColorPrinter.println(deliverable.getMessage());
                if (DeliverableEvent.UPDATE_WINNER.equals(deliverable.getEvent()))
                    keepAlive = false;
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

    /**
     * Request an input value to the user and return the equivalent {@code String} to the caller.
     *
     * @return the {@code String} containing the user input.
     */
    private String in() {
        try {
            return in.nextLine();
        } catch (NoSuchElementException ignored) {
            System.exit(-1);
            return null;
        }
    }

    /**
     * Registers the current {@code Client} to the {@code Server} global list
     */
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

    /**
     * Unregisters the {@code Client} from the {@code Server} global list.
     */
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

    /**
     * Updates the local {@code Lobby} list: {@link #lobbies} with the most recent one, provided by the remote {@code Server}
     * and prints it into the standard output.
     */
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

    /**
     * Stops the {@code Lobby} list update and print process.
     */
    private void stopLobbyUpdateAndPrint() {
        if (!futureUpdate.isDone())
            futureUpdate.cancel(true);
    }

    /**
     * Creates a new {@code Lobby} into the remote {@code Server} and logs the registered author in.
     */
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

    /**
     * Logs a registered {@code Client} into the given {@code Lobby}, specified by {@code lobbyName}.
     *
     * @param lobbyName the name of the {@code Lobby} the user wants to join into.
     */
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

    /**
     * Logs the current user out from the {@code Lobby} he previously joined.
     */
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

    /**
     * Requests the user to provide a valid username. This method does not returns until a valid username has been inserted.
     * A valid username must not be {@code null} or being made out of blank space characters only.
     * @return the requested username.
     */
    private String requestUsername() {
        String name;
        do {
            ColorPrinter.print("Username: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    /**
     * Requests the user to provide a valid name for the {@code Lobby} needed by the caller. This method does not returns
     * until a valid {@code Lobby} name has been inserted.
     * A valid {@code Lobby} name must not be {@code null} or being made out of blank space characters only.
     * @return the requested {@code Lobby} name.
     */
    private String requestLobbyName() {
        String name;
        do {
            ColorPrinter.print("Lobby name: ");
            name = in();
        } while (name == null || name.isBlank());
        return name;
    }

    /**
     * Requests the user to provide a valid password for the {@code Lobby} the caller chooses to create. Since passwords
     * have no restrictions and does not servers for security purposes, any given input to this method is considered as valid.
     * @return the requested password.
     */
    private String requestLobbyPassword() {
        ColorPrinter.print("Password: ");
        return in();
    }

    /**
     * Requests the user to provide a valid choice between the possible ones: 'n' representing the user decision to create
     * a new {@code Lobby} or a number referenced in the {@link #lobbies} list representing the user decision to join a
     * specified {@code Lobby}.
     * @return the user input choice.
     */
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

    /**
     * This is the first interface the user comes in contact with. It displays a user friendly console header.
     */
    private void printWelcomeScreen() {
        ColorPrinter.clear();
        ColorPrinter.println("Welcome to Adrenaline !");
    }

    /**
     * Prints the given {@code Lobby} list to the console. After that prints the possibility for the user to create a new
     * {@code Lobby}.
     * @param lobbies the {@code Lobby} list to be printed.
     */
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

    /**
     * Prints the last information before the {@code Game} starts. These includes the {@link #opponents} list and the
     * time left before the game starts.
     */
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

    /**
     * Prints to the console the time left starting from the given {@code startingSeconds} down to zero. This procedure
     * can be interrupted before the countdown reaches zero.
     * @param startingSeconds the amount of seconds to start the countdown from.
     */
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

    /**
     * Stops the count down and print procedure.
     */
    private void stopCountDownPrint() {
        futureUpdate.cancel(true);
        executor.shutdown();
    }

    /**
     * Prints a console message to indicate that the timer has been paused.
     */
    private void printPauseMessage() {
        printOpponents();
        ColorPrinter.println("Too many players left the lobby, countdown suspended.");
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Prints the {@link #opponents} list to the console after clearing the console output for better text formatting.
     */
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
