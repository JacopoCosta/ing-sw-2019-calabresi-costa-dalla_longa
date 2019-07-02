package it.polimi.ingsw.network.common.message;

import it.polimi.ingsw.network.server.lobby.Lobby;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.Game;

import java.io.Serializable;

/**
 * This enumeration contains all the possible type of {@link NetworkMessage} that can be sent and/or received from
 * all possible entities.
 *
 * @see NetworkMessage
 */
public enum MessageType implements Serializable {
    /**
     * A {@code PING_MESSAGE} represents a type of message sent from the server application to all the clients connected.
     * It is used to keep track of a client disconnections through its continuous sent (heart-beat protocol).
     * If a {@code PING_MESSAGE} fails to be delivered, a connection issue is plausible and further actions may be taken to
     * respond to this unwanted situation.
     */
    PING_MESSAGE,

    /**
     * A {@code REGISTER_REQUEST} represents a type of message sent from all the clients to server.
     * It is used to ask the server to perform a registration routine, at the end of which the client is
     * stably connected and can take exclusive actions that wouldn't have been possible without the authentication step.
     */
    REGISTER_REQUEST,

    /**
     * A {@code REGISTER_SUCCESS} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the authentication routine has been successful and he is now stably registered
     * onto the server.
     */
    REGISTER_SUCCESS,

    /**
     * A {@code CLIENT_ALREADY_REGISTERED_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that another client has been previously registered with the same credentials and
     * therefore his request has been rejected.
     */
    CLIENT_ALREADY_REGISTERED_ERROR,


    /**
     * A {@code UNREGISTER_REQUEST} represents a type of message sent from all the clients to server.
     * It is used to ask the server to perform an unregistering routine, at the end of which the client is
     * safely disconnected. After this step no further information about the client has been kept on the server.
     */
    UNREGISTER_REQUEST,

    /**
     * An {@code UNREGISTER_SUCCESS} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the unregistering routine has been successful and he is no longer connected
     * to the sever; as a result there will be no client information left on the server.
     */
    UNREGISTER_SUCCESS,

    /**
     * A {@code CLIENT_NOT_REGISTERED_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that there are no clients previously connected to the server that matches the
     * credentials of the caller and, for that reason, no unregistering routine has been executed.
     */
    CLIENT_NOT_REGISTERED_ERROR,

    /**
     * A {@code LOBBY_LIST_UPDATE_REQUEST} represents a type of message sent from the clients to server.
     * It is used to ask the server for the list of all available {@link Lobby}.
     */
    LOBBY_LIST_UPDATE_REQUEST,

    /**
     * A {@code LOBBY_LIST_UPDATE_RESPONSE} represents a type of message sent from the server to all the clients connected.
     * It is used to indicate that the current {@link NetworkMessage} contains the requested {@link Lobby} list update,
     * usually asked via a {@link #LOBBY_LIST_UPDATE_REQUEST} type of message.
     */
    LOBBY_LIST_UPDATE_RESPONSE,

    /**
     * A {@code LOBBY_CREATE_REQUEST} represents a type of message sent from all the clients to server.
     * It is used to ask the server to create a new {@link Lobby} with the information given in the trailer of the attached
     * {@link NetworkMessage}.
     */
    LOBBY_CREATE_REQUEST,

    /**
     * a {@code LOBBY_CREATE_SUCCESS} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the creation of a new {@link Lobby} has been successful and now it's available
     * for connecting clients.
     */
    LOBBY_CREATE_SUCCESS,

    /**
     * A {@code LOBBY_ALREADY_EXISTS_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that another {@link Lobby} with the same identifier has already been created and,
     * as a result, no new lobbies have been registered on the server.
     */
    LOBBY_ALREADY_EXISTS_ERROR,

    /**
     * A {@code LOBBY_NOT_FOUND_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the requested {@link Lobby} was not found on the server and therefore no
     * further actions have ben taken.
     */
    LOBBY_NOT_FOUND_ERROR,

    /**
     * A {@code LOBBY_NOT_FOUND_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the {@link Lobby} he is trying to connecting to has reached tme maximum amount
     * of participants and therefore no join operation have been performed.
     */
    LOBBY_FULL_ERROR,

    /**
     * A {@code LOBBY_LOGIN_REQUEST} represents a type of message sent from all the clients to server.
     * It is used to ask the server to login to a specified {@link Lobby} whose information are specified in the trailer of
     * the actual {@link NetworkMessage}.
     */
    LOBBY_LOGIN_REQUEST,

    /**
     * a {@code LOBBY_LOGIN_SUCCESS} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the login request performed to join a {@link Lobby} has been successful.
     */
    LOBBY_LOGIN_SUCCESS,

    /**
     * A {@code PLAYER_ALREADY_ADDED_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that another {@link Player} with the same identifier has already joined the specified
     * {@link Lobby} and as a result, no login routine has been performed.
     */
    PLAYER_ALREADY_ADDED_ERROR,

    /**
     * A {@code PASSWORD_NOT_VALID_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the given password for the chosen {@link Lobby} does not match and therefore
     * the login procedure has failed.
     */
    PASSWORD_NOT_VALID_ERROR,

    /**
     * A {@code GAME_ALREADY_STARTED_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that he no longer can access the selected {@link Lobby} because the corresponding {@link Game}
     * has started and therefore, the login procedure has failed.
     */
    GAME_ALREADY_STARTED_ERROR,

    /**
     * A {@code LOBBY_LOGOUT_REQUEST} represents a type of message sent from all the clients to server.
     * It is used to ask the server to log out a client from a specified {@link Lobby} whose information are specified in the trailer of
     * the actual {@link NetworkMessage}.
     */
    LOBBY_LOGOUT_REQUEST,

    /**
     * a {@code LOBBY_LOGOUT_SUCCESS} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the logout request performed to exit a {@link Lobby} has been successful.
     */
    LOBBY_LOGOUT_SUCCESS,

    /**
     * A {@code LOBBY_EMPTY_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the {@link Lobby} he is trying to log out from empty and
     * and therefore no logout operation have been performed.
     */
    LOBBY_EMPTY_ERROR,

    /**
     * A {@code PLAYER_NOT_FOUND_ERROR} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that no {@link Player} that corresponds to the given identifier has been found in
     * the {@link Player}s list.
     */
    PLAYER_NOT_FOUND_ERROR,

    /**
     * A {@code CLIENT_MESSAGE} represents a type of message sent bidirectionally from both the clients and the server.
     * It is used to send non network related content through the communication infrastructure. These type of messages
     * are not handled directly from the communication layer, but they need to be processed from an appropriate interpreter
     * at an higher level. These messages always include a content that can't be checked or parsed from the network level.
     */
    CLIENT_MESSAGE,

    /**
     * A {@code COUNTDOWN_UPDATE} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client every time a relevant timer-related update is triggered. This may occur, as an example,
     * the if the countdown jumps from a value to another not adjacent as a result of some time logic manipulation.
     */
    COUNTDOWN_UPDATE,

    /**
     * A {@code COUNTDOWN_EXPIRED} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client about the timer expiration. A typical scenario of usage is when the local
     * countdown (autonomous from the server) reaches zero while (because of delay) a COUNTDOWN_UPDATE is missing or
     * late.In this situation the {@link Game} wont start until this last message is sent.
     */
    COUNTDOWN_EXPIRED,

    /**
     * A {@code COUNTDOWN_STOPPED} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client that the countdown has been stopped or paused. This happens, as an example, if
     * too many {@link Player}s disconnects from the {@link Lobby} and therefore, there are not enough {@link Player}s
     * left to allow the {@link Game} to start.
     */
    COUNTDOWN_STOPPED,

    /**
     * A {@code OPPONENTS_LIST_UPDATE} represents a type of message sent from the server to all the clients connected.
     * It is used to notify a client with the collection of all the {@link Player}s logged into his same {@link Lobby}.
     */
    OPPONENTS_LIST_UPDATE
}
