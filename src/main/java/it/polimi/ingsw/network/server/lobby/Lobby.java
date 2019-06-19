package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.util.Table;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.observer.Observer;
import it.polimi.ingsw.network.common.timer.CountDownTimer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A collection of {@link Player}s logged together, waiting for a {@link Game} to start. {@link Player}s should be
 * identified through a unique value and the {@code Lobby} should contain only one instance of the same {@link Player}.
 */

class Lobby implements Observer {
    /**
     * The maximum number of {@link Player}s a {@code Lobby} can contain.
     */
    private final int MAX_PLAYERS = 5;
    /**
     * The number of {@link Player}s currently logged into the {@code Lobby}.
     * This value must always be less or equal than {@link #MAX_PLAYERS}.
     */
    private int currentPlayers;
    /**
     * The {@link List} containing all of the {@link Player}s currently logged into the {@code Lobby}. Each of them
     * has a different {@link Player#getName()} indicating that each one is a different {@link Player}.
     */
    private final List<Player> players;

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
     * The initial waiting time before the {@code Lobby} launches a new {@link Game}. This is
     * modified through {@link #adjustTimer()}.
     *
     * @see it.polimi.ingsw.model.Game
     * @see #adjustTimer()
     */
    private final int WAITING_TIME = 5; //time in seconds the timer starts from
    /**
     * The minimum number of {@link Player}s to reach before the {@link CountDownTimer} begins the countdown
     */
    private final int PLAYERS_THRESHOLD = 3;
    /**
     * The timer responsible for the countdown before the game starts.
     * Calling {@link CountDownTimer} should be performed only in {@link #adjustTimer()}.
     *
     * @see CountDownTimer
     * @see #adjustTimer()
     */
    private final CountDownTimer timer;

    /**
     * This is the only constructor. It create a {@code Lobby} from a given {@code name} and {@code password}.
     *
     * @param name     the new {@code Lobby} name.
     * @param password the new {@code Lobby} password.
     */
    Lobby(String name, String password) {
        this.name = name;
        this.password = password;

        currentPlayers = 0;
        players = new ArrayList<>(MAX_PLAYERS);

        timer = new CountDownTimer(WAITING_TIME);
        timer.addObserver(this);
    }

    /**
     * Returns the name given to this {@code Lobby}.
     *
     * @return the name of this {@code Lobby}.
     */
    public String getName() {
        return name;
    }

    /**
     * the number of {@code Player}s currently logged into this {@code Lobby}.
     *
     * @return the number of {@code Player}s currently logged into this {@code Lobby}.
     */
    int getCurrentPlayers() {
        return currentPlayers;
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
        return new AbstractMap.SimpleEntry<>(name, "[" + currentPlayers + "/" + MAX_PLAYERS + "]");
    }

    /**
     * Returns {@code true} if, and only if, this {@code Lobby} contains the given {@link Player}.
     *
     * @param player the {@link Player} whose presence in this list is to be tested.
     * @return {@code true} if this {@code Lobby} contains the given {@link Player}, {@code false} otherwise.
     */
    boolean contains(Player player) {
        return players.contains(player);
    }

    /**
     * Insert the given {@code player} into this {@code Lobby} if some criteria are satisfied. Then call {@link #adjustTimer()}
     * to adjust the timer countdown.
     * 1 - there must be a space left in the {@code Lobby}. This is true if {@link #currentPlayers} is less than
     * {@link #MAX_PLAYERS}.
     * 2 - the {@code player} can't be {@code null}.
     * 3 - the {@code Lobby} can't contain another instance of {@link Player} {@code p}, so that {@code player.equals(p)}.
     * 4- ether only one of the following conditions must be satisfied:
     *      a - the given {@code password} must be the same a {@code this.password}, so that
     *          {@code password.equals(this.password)}.
     *      b - both the given {@code password} and {@code this.password} must be null simultaneously.
     *      c - {@code this.password} must be either {@code null} or such that {@code this.password.isEmpty()} and
     *          {@code password } must be either {@code null} or such that {@code password.isBlank()}.
     *
     * @param player   the {@link Player} to insert into this {@code Lobby}.
     * @param password the authentication password to allow {@code player} to be inserted into this {@code Lobby}.
     * @throws LobbyFullException          if this {@code Lobby} has no space left.
     * @throws PlayerAlreadyAddedException if this {@code Lobby} already contains another instance of the same {@code player}.
     * @throws InvalidPasswordException    if {@code password} does not satisfy one of the validity conditions.
     * @see #adjustTimer()
     */
    void add(Player player, String password) throws LobbyFullException, PlayerAlreadyAddedException, InvalidPasswordException {
        if (currentPlayers == MAX_PLAYERS)
            throw new LobbyFullException("Lobby \"" + name + "\" is full");

        if (player == null)
            throw new NullPointerException("Player is null");

        if (players.contains(player))
            throw new PlayerAlreadyAddedException("Player \"" + player.getName() + "\" already found into Lobby \"" + name + "\"");

        if ((this.password != null && !this.password.isBlank() && !this.password.equals(password))
                || ((this.password == null || this.password.isBlank()) && (password != null && !password.isBlank())))
            throw new InvalidPasswordException("password \"" + password + "\" invalid for Lobby \"" + name + "\"");

        players.add(player);
        currentPlayers++;

        adjustTimer();
    }

    /**
     * Removes the given {@link Player} from the {@code Lobby} if all of the following criteria are satisfied:
     * 1 - the {@code Lobby} can't be empty.
     * 2 - the {@code player} argument can't be null.
     * 3 - the {@code Lobby} must contain an instance of the given {@code player} {@code p}, so that {@code p.equals(player)}.
     *
     * @param player the {@link Player} to be removed.
     * @throws LobbyEmptyException     if the {@code Lobby} does not contain any {@link Player}
     * @throws PlayerNotFoundException if the {@link Player} passed as parameter is not contained into the {@code Lobby}.
     */
    void remove(Player player) throws LobbyEmptyException, PlayerNotFoundException {
        if (this.currentPlayers == 0)
            throw new LobbyEmptyException("Lobby \"" + name + "\" is empty");

        if (player == null)
            throw new NullPointerException("Player is null");

        if (!players.contains(player))
            throw new PlayerNotFoundException("Player \"" + player.getName() + "\" not found into Lobby \"" + name + "\"");

        players.remove(player);
        currentPlayers--;

        adjustTimer();
    }

    /**
     * This function is called every time a {@link Player} is added or removed from this {@code Lobby}.
     * This function adjusts the {@link CountDownTimer} countdown based on the number of
     * {@link Player} logged into the {@code Lobby}, represented by {@link #currentPlayers}.
     * Every time the number of {@link Player} changes, a delay value can be evaluate to adjust the countdown.
     *
     * @see CountDownTimer
     */
    private void adjustTimer() {
        /* TODO:
         *  This function is called every time a Player is added or removed from this Lobby.
         *  This function adjusts the timer countdown based on the number of Players connected (currentPlayers).
         *  The timer should start when the minimum number of Players is reached:
         *  players.size() == PLAYERS_THRESHOLD
         *  using
         *  timer.start()
         *  Every time the number of Players changes, a delay value can be evaluate to adjust the countdown.
         *  Once the value has been determined, use
         *  timer.delay(amountInSeconds)
         *  to delay the timer countdown by the chosen amount of time, and
         *  timer.stop()
         *  to stop the timer if needed, then use
         *  timer.start()
         *  to restart the countdown from the beginning.
         *  To check the timer status at any time, call
         *  timer.getState()
         *  which returns one of the four possible states (an explanation of the states meaning can be found
         *  in the CountDownTimer class):
         *  CountdownTimer.TimerState.NOT_STARTED
         *  CountdownTimer.TimerState.STARTED
         *  CountdownTimer.TimerState.STOPPED
         *  CountdownTimer.TimerState.EXPIRED
         *  -
         *  Add a counter for previous currentPlayers value
         *  quando un player viene aggiunto controlla se non sia lo stesso nel metodo add
         * */

        if (currentPlayers == 3) {
            timer.start();
        }
    }

    /**
     * This function is called when the {@link #timer} expires and notifies that a new {@link Game}
     * should start between the {@link Player}s currently logged into this {@code Lobby}.
     *
     * @see Observer
     */
    @Override
    public void onEvent() {
        timer.removeObserver(this);

        //TODO: read these from outside
        boolean finalFrenzy = true;
        int roundToPlay = 8;
        int boardType = 2;
        Game game = Game.create(finalFrenzy, roundToPlay, boardType, players);

        new Thread(game::play).start();
        System.out.println("Started a new game with " + Table.list(players));
    }

    /*TODO:
        sincronizza metodo setCommunicationInterface con lock interno;

     */
}