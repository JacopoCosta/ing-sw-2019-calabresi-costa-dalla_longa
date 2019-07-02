package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.InvalidSaveStateException;
import it.polimi.ingsw.model.exceptions.UnmatchedSavedParticipantsException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.util.Table;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.observer.Observer;
import it.polimi.ingsw.network.common.observer.Observable;
import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.network.common.util.timer.CountDownTimer;
import it.polimi.ingsw.network.common.util.property.GameProperty;
import it.polimi.ingsw.network.client.communication.ServerCommunicationInterface;
import it.polimi.ingsw.network.server.VirtualClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A collection of {@link Player}s logged together, waiting for a {@link Game} to start. {@link Player}s should be
 * identified through a unique value and the {@code Lobby} should contain only one instance of the same {@link Player}.
 */

@SuppressWarnings("FieldCanBeLocal")
public class Lobby implements Observer {
    /**
     * The maximum number of {@link Player}s a {@code Lobby} can contain.
     */
    private final int MAX_PLAYERS = 5;

    /**
     * The initial waiting time before the {@code Lobby} launches a new {@link Game}. This is
     * modified through {@link #adjustTimer()}.
     *
     * @see it.polimi.ingsw.model.Game
     * @see #adjustTimer()
     */
    private final int WAITING_TIME_FULL = 30; //time in seconds the timer starts from

    /**
     * The cap to the waiting time, in seconds, after the {@code Lobby} reached it maximum capacity at least once.
     *
     * @see #MAX_PLAYERS
     */
    private final int WAITING_TIME_REDUCED = 15;

    /**
     * The initial value of {@link #timeMargin}.
     */
    private final int WAITING_TIME_MARGIN = 6;

    /**
     * The duration, in seconds, of a short interval of time given to a {@code Lobby} before starting the game, and
     * the amount by which the {@link #timer} gets delayed when the amount of {@link #players#size()} drops to the minimum
     * allowed for the lobby, i.e. {@link Game#MINIMUM_PLAYER_COUNT};
     */
    private int timeMargin;

    /**
     * The timer responsible for the countdown before the game starts.
     * Calling {@link CountDownTimer} should be performed only in {@link #adjustTimer()}.
     *
     * @see CountDownTimer
     * @see #adjustTimer()
     */
    private final CountDownTimer timer;

    /**
     * The {@link List} containing all of the {@link Player}s currently logged into the {@code Lobby}.
     * This is a real time up-to-date {@link Player}s list until the {@link Game} starts. After that the list became
     * immutable: no new {@link Player}s can be added and old ones cam be removed.
     */
    private final List<Player> players;

    /**
     * This value is updated together with {@link #players#size()} whenever a {@link Player} is added or removed from the
     * {@code Lobby}. This variable stores the value hold by {@link #players#size()} immediately before the {@link #add(Player, String)}
     * or {@link #remove(Player)} operation is performed.
     * Ideally this value represents the number of {@link Player}s found into the {@code Lobby} at the previous step,
     * when the new one was not yet added or the old one was not yet removed.
     */
    private int previousPlayersAmount;

    /**
     * The {@code Lobby} name. This is chosen when the {@code Lobby} is created for the first time and does not change
     * throughout its entire lifetime.
     */
    private final String name;

    /**
     * The {@code Lobby} password. This is chosen when the {@code Lobby} is created for the first time and does not change
     * throughout its entire lifetime. The {@code password} is stored as plain text and does not serve any security purpose.
     */
    private final String password;

    /**
     * The {@link Game} starting with {@link #players} as participants from this {@code Lobby}.
     *
     * @see Game
     */
    private Game game;

    /**
     * Whether or not the {@link Player}s should be notified about opponents update. This is necessary until the {@link Game}
     * starts. After that no more notifications are desirable.
     */
    private boolean notify;

    /**
     * The properties needed in order to play a new {@link Game}.
     *
     * @see GameProperty
     */
    private GameProperty gameProperty;

    /**
     * The {@link Console} used to print any kind of information needed to the server command prompt.
     */
    private final Console console;

    /**
     * This is the only constructor. It create a {@code Lobby} from a given {@code name} and {@code password}.
     *
     * @param name     the new {@code Lobby} name.
     * @param password the new {@code Lobby} password.
     */
    Lobby(String name, String password) {
        this.name = name;
        this.password = password;

        this.players = new ArrayList<>();

        this.previousPlayersAmount = 0;
        this.timeMargin = WAITING_TIME_MARGIN;
        this.timer = new CountDownTimer(this.WAITING_TIME_FULL);
        this.timer.addObserver(this);

        this.console = Console.getInstance();

        this.notify = true;
    }

    /**
     * Sets the given {@link GameProperty} to the {@code Lobby} ones. These will be used by the {@link #game} to start.
     *
     * @param gameProperty the {@link GameProperty} used by the {@link #game} to start.
     * @see GameProperty
     */
    void setGameProperty(GameProperty gameProperty) {
        this.gameProperty = gameProperty;
    }

    /**
     * Returns the name given to this {@code Lobby}.
     *
     * @return the name of this {@code Lobby}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * the number of {@code Player}s currently logged into this {@code Lobby}.
     *
     * @return the number of {@code Player}s currently logged into this {@code Lobby}.
     */
    int getCurrentPlayers() {
        return this.players.size();
    }

    /**
     * Returns a {@link Map.Entry} containing the current status of this {@code Lobby}.
     * The status of a {@code Lobby} is defined by his {@code name} and the number of {@link Player}s currently
     * logged-in, out of the maximum number of {@link Player}s.
     * Example:
     * Suppose a {@code Lobby} is called "foo" and contains 3 {@link Player}s out of 5. The return value will be:
     * ("foo", "[3/5]").
     *
     * @return the {@link Map.Entry} representing the lobby current status.
     */
    Map.Entry<String, String> getStatus() {
        return new AbstractMap.SimpleEntry<>(this.name, "[" + this.players.size() + "/" + this.MAX_PLAYERS + "]");
    }

    /**
     * Returns {@code true} if, and only if, this {@code Lobby} contains the given {@link Player}.
     *
     * @param player the {@link Player} whose presence in this list is to be tested.
     * @return {@code true} if this {@code Lobby} contains the given {@link Player}, {@code false} otherwise.
     */
    boolean contains(Player player) {
        return this.players.contains(player);
    }

    /**
     * Insert the given {@code player} into this {@code Lobby} if some criteria are satisfied. Then call {@link #adjustTimer()}
     * to adjust the timer countdown. This behavior occurs before a new {@link Game} is started: after this event the list
     * become immutable: no other {@link Player}s can be added. The only operation possible is to update a {@link Player}'s
     * {@link ServerCommunicationInterface} with a new one. This happens when a {@link Player} disconnects from the {@link Game}
     * and reconnects after a certain amount of time.
     *
     * <p>The insertion criteria (that applies before the {@link Game} starts) are:
     * 1 - there must be a space left in the {@code Lobby}. This is true if {@code players.size()} is less than
     * {@link #MAX_PLAYERS}.
     * 2 - the {@code player} can't be {@code null}.
     * 3 - the {@code Lobby} can't contain another instance of {@link Player} {@code p}, so that {@code player.equals(p)}.
     * 4- ether only one of the following conditions must be satisfied:
     * a - the given {@code password} must be the same a {@code this.password}, so that
     * {@code password.equals(this.password)}.
     * b - both the given {@code password} and {@code this.password} must be null simultaneously.
     * c - {@code this.password} must be either {@code null} or such that {@code this.password.isEmpty()} and
     * {@code password } must be either {@code null} or such that {@code password.isBlank()}.
     *
     * @param player   the {@link Player} to insert into the {@code Lobby}.
     * @param password the authentication password to allow {@code player} to be inserted into this {@code Lobby}.
     * @throws LobbyFullException          if this {@code Lobby} has no space left.
     * @throws PlayerAlreadyAddedException if this {@code Lobby} already contains another instance of the same {@code player}.
     * @throws InvalidPasswordException    if {@code password} does not satisfy one of the validity conditions.
     * @see #adjustTimer()
     */
    void add(Player player, String password)
            throws LobbyFullException, PlayerAlreadyAddedException, InvalidPasswordException, GameAlreadyStartedException {
        if (this.players.size() == MAX_PLAYERS)
            throw new LobbyFullException("Lobby \"" + this.name + "\" is full");

        if (player == null)
            throw new NullPointerException("Player is null");

        if ((this.password != null && !this.password.isBlank() && !this.password.equals(password))
                || ((this.password == null || this.password.isBlank()) && (password != null && !password.isBlank())))
            throw new InvalidPasswordException("password \"" + password + "\" invalid for Lobby \"" + this.name + "\"");

        //could be a new Player or an old one previously disconnected
        if (game == null || !game.isStarted()) {
            //can add other players
            if (this.players.contains(player))
                throw new PlayerAlreadyAddedException("Player \"" + player.getName() + "\" already found into Lobby \"" + this.name + "\"");
            this.previousPlayersAmount = players.size();
            this.players.add(player);
            adjustTimer();
            return;
        }
        if (!players.contains(player))
            throw new GameAlreadyStartedException(); //this is a new Player: too late to join this Lobby
        //this is an old Player disconnected: he is already inside his Lobby (and Game)
    }

    /**
     * Removes the given {@link Player} from the {@code Lobby} if all of the following criteria are satisfied:
     * 1 - the {@code Lobby} can't be empty.
     * 2 - the {@code player} argument can't be null.
     * 3 - the {@code Lobby} must contain an instance {@code p} of the given {@code player}, so that {@code p.equals(player)}.
     *
     * <p>This behavior applies until a new {@link Game} is started. Since that, no other {@link Player}s can be removed
     * due to the fact that a disconnected {@link Player} must be recognizable at the moment he decides to join again the
     * {@code Lobby} he was connected before.
     *
     * @param player the {@link Player} to be removed.
     * @throws LobbyEmptyException     if the {@code Lobby} does not contain any {@link Player}.
     * @throws PlayerNotFoundException if the {@link Player} passed as parameter is not contained into the {@code Lobby}.
     * @see #adjustTimer()
     */
    void remove(Player player) throws LobbyEmptyException, PlayerNotFoundException {
        if (this.players.size() == 0)
            throw new LobbyEmptyException("Lobby \"" + this.name + "\" is empty");

        if (player == null)
            throw new NullPointerException("Player is null");

        if (!this.players.contains(player))
            throw new PlayerNotFoundException("Player \"" + player.getName() + "\" not found into Lobby \"" + this.name + "\"");

        //a Player can be removed only while the Game is not started
        if (this.game == null || !this.game.isStarted()) {
            this.previousPlayersAmount = this.players.size();
            this.players.removeIf(p -> p.equals(player));
            adjustTimer();
        }
    }

    /**
     * This function is called every time a new {@link Player} is added to this {@code Lobby} or an existing one is removed,
     * only if the game is not started yet.
     * This function adjusts the {@link CountDownTimer} countdown based on the number of
     * {@link Player}s logged into the {@code Lobby}, represented by {@link #players#size()}.
     * Every time the number of {@link Player}s changes, a delay value can be evaluate to adjust the countdown.
     *
     * @see CountDownTimer
     */
    private void adjustTimer() {
        // When the capacity limit is reached, drop the waiting time by WAITING_TIME_REDUCED, but not below timeMargin
        if (players.size() == MAX_PLAYERS) {
            int time = timer.getTime();
            int newTime = Math.max(time - WAITING_TIME_REDUCED, this.timeMargin);
            timer.setTime(newTime);
        }

        // When there is the bare minimum amount of players
        else if (players.size() == Game.MINIMUM_PLAYER_COUNT) {

            // if there was an additional player who left, increase the time by timeMargin, but not above WAITING_TIME_REDUCED
            // any subsequent take of this branch will decrease the effect of timeMargin, as it decrements each time
            // this branch is entered (lower bound is 1);
            if (previousPlayersAmount > Game.MINIMUM_PLAYER_COUNT) {
                int time = timer.getTime();
                int newTime = Math.min(time + this.timeMargin, WAITING_TIME_REDUCED);
                this.timeMargin = Math.max(1, this.timeMargin - 1);
                timer.setTime(newTime);
            }
            // if, instead, there were only 2 players, there are now enough players for a game, so the timer shall start.
            else {
                timer.start();
            }
        }

        // When the number of players drops just below the threshold, stop the timer and reset it to the maximum value
        // Note that this does not reset timeMargin
        else if (players.size() < Game.MINIMUM_PLAYER_COUNT && previousPlayersAmount >= Game.MINIMUM_PLAYER_COUNT) {
            timer.stop();
            timer.setTime(WAITING_TIME_FULL);
        }
    }

    /**
     * Notifies all the {@link #players} that the {@link #timer} has been expired.
     */
    private void notifyExpired() {
        players.stream()
                .filter(VirtualClient::isConnected)
                .forEach(player -> {
                    try {
                        player.sendMessage(NetworkMessage.simpleServerMessage(MessageType.COUNTDOWN_EXPIRED));
                        this.console.mexS("message " + MessageType.COUNTDOWN_EXPIRED + " sent to client \"" + player.getName() + "\"");
                    } catch (ConnectionException ignored) {
                    }
                });
    }

    /**
     * Notifies all the {@link #players} that the {@link #timer} has performed a time update.
     *
     * @param seconds the new amount of time left to count down from.
     */
    private void notifyTimeUpdate(int seconds) {
        players.stream()
                .filter(VirtualClient::isConnected)
                .forEach(player -> {
                    try {
                        player.sendMessage(NetworkMessage.completeServerMessage(MessageType.COUNTDOWN_UPDATE, seconds));
                        this.console.mexS("message " + MessageType.COUNTDOWN_UPDATE + " sent to client \"" + player.getName() + "\"");
                    } catch (ConnectionException ignored) {
                    }
                });
    }

    /**
     * Notifies all the {@link #players} that the {@link #timer} has been stopped or paused due to not enough {@link Player}s
     * left in the {@code Lobby}.
     */
    private void notifyStopped() {
        players.stream()
                .filter(VirtualClient::isConnected)
                .forEach(player -> {
                    try {
                        player.sendMessage(NetworkMessage.simpleServerMessage(MessageType.COUNTDOWN_STOPPED));
                        this.console.mexS("message " + MessageType.COUNTDOWN_STOPPED + " sent to client \"" + player.getName() + "\"");
                    } catch (ConnectionException ignored) {
                    }
                });
    }

    /**
     * Notifies all the {@link #players} with the updated list of all the other {@link Player}s logged in the same {@link Lobby},
     * except himself.
     */
    void notifyOpponentUpdate() {
        if (notify)
            players.stream()
                    .filter(VirtualClient::isConnected)
                    .forEach(player -> {
                        try {
                            player.sendMessage(NetworkMessage.completeServerMessage(MessageType.OPPONENTS_LIST_UPDATE,
                                    players.stream()
                                            .filter(p -> !p.equals(player))
                                            .map(VirtualClient::getName)
                                            .collect(Collectors.toList())));
                            this.console.mexS("message " + MessageType.OPPONENTS_LIST_UPDATE + " sent to client \"" + player.getName() + "\"");
                        } catch (ConnectionException ignored) {
                        }
                    });
    }

    /**
     * This function is called when the {@link #timer} experience a relevant update while performing the countdown.
     * Depending on the {@code eventStatus} that has been triggered and its {@code content}, different actions may be taken
     * to respond accordingly to the occurred event.
     *
     * @param eventStatus the status in which the {@link Observable} is found when performs the call to {@code notifyEvent()}.
     * @param value       the value of the actual event status.
     * @see Observer
     */
    @Override
    public void onEvent(int eventStatus, int value) {
        if (eventStatus == CountDownTimer.STATUS_EXPIRED) {
            this.timer.removeObserver(this);

            //notify Clients the Game is about to start
            notifyExpired();

            //start a new game or load an existing one
            new Thread(() -> {
                try {
                    //FIXME remove NullPointerException and adjust Game.load mechanic
                    this.game = Game.load(null, this.players);
                    this.console.mexG("previous Game loaded from Lobby \"" + this.name + "\" with Players " + Table.list(this.players));
                } catch (InvalidSaveStateException | UnmatchedSavedParticipantsException | NullPointerException ignored) {
                    this.game = Game.create(this.gameProperty.finalFrenzy(), this.gameProperty.roundsToPlay(),
                            this.gameProperty.boardType(), this.players);
                    this.console.stat("new Game started from Lobby \"" + this.name + "\" with Players " + Table.list(this.players));
                }
                game.play();
            }).start();
            notify = false;
        } else if (eventStatus == CountDownTimer.STATUS_TIME_UPDATE)
            notifyTimeUpdate(value);
        else if (eventStatus == CountDownTimer.STATUS_STOPPED) //else should be enough but, just in case...
            notifyStopped();
    }
}