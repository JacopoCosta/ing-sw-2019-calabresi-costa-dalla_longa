package it.polimi.ingsw.network;

import it.polimi.ingsw.network.exceptions.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

class Lobby {
    private static final int LOBBY_SIZE = 5; //the maximum number of Users into the Lobby
    private static final int MINIMUM_USERS_THRESHOLD = 3; //minimum number of Users needed for the countdown to start

    private static final int COUNTDOWN_STARTING_SECONDS = 90; //initial time in seconds the timer stats counting down from

    private final String name; //unique Lobby name
    private final CountDownTimer timer; //timer to perform the countdown before the match starts
    private String password; //[OPTIONAL] the Lobby password
    private Queue<User> users; //array of Users connected to this Lobby. users[0] is the Lobby admin

    Lobby(String name, String password) {
        this.name = name;
        this.password = password;
        users = new ConcurrentLinkedQueue<>();

        timer = new CountDownTimer(COUNTDOWN_STARTING_SECONDS);
    }

    String getName() {
        return this.name;
    }

    //add a new User to the Lobby only if not already in and password is correct
    void addUser(User user, String password) throws InvalidPasswordException, LobbyFullException, UserAlreadyAddedException {
        //password not correct
        if ((this.password != null && !this.password.isBlank() && !this.password.equals(password))
                || ((this.password == null || this.password.isBlank()) && (password != null && !password.isBlank())))
            throw new InvalidPasswordException("\"" + password + "\" is no a valid password for Lobby \"" + name + "\"");

        //maximum number of Users reached
        if (users.size() == LOBBY_SIZE)
            throw new LobbyFullException("Lobby is full, maximum number of Users is " + LOBBY_SIZE);

        //User already into this Lobby
        for (User u : users)
            if (u.equals(user))
                throw new UserAlreadyAddedException("User \"" + user.getName() + "\" already added to Lobby \"" + name + "\"");

        //add User to this Lobby
        users.add(user);

        //adjust the timer accordingly to the number of players in the Lobby
        adjustTimer();
    }

    //remove a User from the Lobby
    void removeUser(User user) throws EmptyLobbyException, UserNotFoundException {
        int length = users.size();

        //Lobby is empty
        if (length == 0)
            throw new EmptyLobbyException("can't remove User \"" + user.getName() + "\", Lobby \"" + name + "\" is empty");

        //remove the User if present
        users = users.stream().filter(u -> !u.equals(user)).collect(Collectors.toCollection(ConcurrentLinkedQueue::new));

        //array has the same size before and after removal: no Users have been removed
        if (length == users.size())
            throw new UserNotFoundException("User \"" + user.getName() + "\" not found into Lobby \"" + name + "\"");

        //adjust the timer accordingly to the number of players in the Lobby
        adjustTimer();
    }

    //whether or not this Lobby has Users connected to it
    boolean isEmpty() {
        return users.size() == 0;
    }

    //whether or not this Lobby has reached the maximum amount of Users connected to it
    boolean isFull() {
        return users.size() == LOBBY_SIZE;
    }

    //gets the number of the Users connected to this Lobby
    int getCurrentUsers() {
        return users.size();
    }

    //get the maximum number of Users for this Lobby
    int getMaxUses() {
        return LOBBY_SIZE;
    }

    //regulate the timer to adjust the countdown, according to the number of Users in the Lobby
    private void adjustTimer() {
        /* TODO:
            This function is called every time a User is added or removed from this Lobby.
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