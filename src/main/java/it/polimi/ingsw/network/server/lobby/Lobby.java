package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Lobby {
    private final int MAX_PLAYERS = 5;
    private int currentPlayers;

    private final List<Player> players;

    private final String name;
    private final String password;

    private final int WAITING_TIME = 5; //time in seconds the timer starts from
    private final int PLAYERS_THRESHOLD = 3; //minimum number of players to reach before the timer begins the countdown
    private final CountDownTimer timer;

    Lobby(String name, String password) {
        this.name = name;
        this.password = password;

        currentPlayers = 0;
        players = new ArrayList<>(MAX_PLAYERS);

        timer = new CountDownTimer(WAITING_TIME);
    }

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

    public String getName() {
        return name;
    }

    int getCurrentPlayers() {
        return currentPlayers;
    }

    boolean contains(Player player) {
        return players.contains(player);
    }

    Map.Entry<String, String> getStatus() {
        return new AbstractMap.SimpleEntry<>(name, "[" + currentPlayers + "/" + MAX_PLAYERS + "]");
    }

    //regulate the timer to adjust the countdown, according to the number of players in the Lobby
    private void adjustTimer() {
        /* TODO:
         *  This function is called every time a User is added or removed from this Lobby.
         *  This function adjusts the timer countdown based on the number of Users connected (currUsers).
         *  The timer should start when the minimum number of Users is reached:
         *  users.size() == MINIMUM_USERS_THRESHOLD
         *  using
         *  timer.start()
         *  Every time the number of Users changes, a delay value can be evaluate to adjust the countdown.
         *  Once the value has been determined, use
         *  timer.delay(amountInSeconds)
         *  to delay the timer countdown by the chosen amount of time, and
         *  timer.stop()
         *  to stop the timer if needed, then use
         *  timer.start()
         *  to restart the countdown from the beginning.
         *  To check the timer status at any time, call
         *  timer.getState()
         *  which returns one of the three possible states (an explanation of the states meaning can be found
         *  in the CountdownTimer class):
         *  CountdownTimer.NOT_STARTED
         *  CountdownTimer.STARTED
         *  CountdownTimer.STOPPED
         * */
    }
}