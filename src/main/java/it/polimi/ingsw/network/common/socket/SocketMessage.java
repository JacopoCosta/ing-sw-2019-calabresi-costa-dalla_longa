package it.polimi.ingsw.network.common.socket;

/* This class contains all the messages used in the Socket protocol to allow the communication between Client and Server.
 *
 * Messages with the prefix "CLIENT_" are sent from the Client to the Server.
 * Messages with the prefix "SERVER_" are sent from the Server to the Client.
 * Messages with the suffix "_ERROR" represents an error in some operation and should be notified to the User via CLI/GUI.
 *
 * */

public enum SocketMessage {
    // indicates that the next line sent to the Server will be the username the Client wants to connect to the server with.
    CLIENT_REGISTER_REQUEST,

    //the Client asks the Server to remove him from the global Clients list
    CLIENT_UNREGISTER_REQUEST,

    // the Client requests the updated Lobbies list.
    CLIENT_LOBBY_LIST_REQUEST,

    // the Client asks the Server to create a new Lobby.
    CLIENT_NEW_LOBBY_REQUEST,

    //the Client asks the server to perform the login procedure
    CLIENT_LOGIN_REQUEST,

    // the Client asks the Server to log him out
    CLIENT_LOGOUT_REQUEST,

    // the Server notifies the Client about the successful creation of the Lobby.
    SERVER_LOBBY_CREATION_SUCCESS,

    // the Server notifies the Client about the successful login in the previously selected Lobby.
    SERVER_LOBBY_LOGIN_SUCCESS,

    //the Server notifies the Client that the logout performed successfully.
    SERVER_LOBBY_LOGOUT_SUCCESS,

    // the Server notifies the Client that the Lobbies list is ended.
    SERVER_LOBBY_LIST_END,

    // the Server notifies the Client that is unable to find the specified Lobby the Client wants to join.
    SERVER_LOBBY_NOT_FOUND_ERROR,

    // the Server notifies the Client that the Lobby he is currently trying to create, already exists.
    SERVER_LOBBY_ALREADY_EXISTS_ERROR,

    // the Server notifies the Client that the Lobby already contains him.
    SERVER_USER_ALREADY_ADDED_ERROR,

    // the Server notifies the Client that cannot find him into a specific Lobby.
    SERVER_USER_NOT_FOUND_ERROR,

    // the Server notifies the Client that the chosen Lobby has reached maximum capacity.
    SERVER_FULL_LOBBY_ERROR,

    // the Server notifies the Client that the given Lobby is empty.
    SERVER_EMPTY_LOBBY_ERROR,

    // the Server notifies the Client that the given password is incorrect.
    SERVER_INVALID_LOBBY_PASSWORD_ERROR,

    // indicates that the registration process is successful
    SERVER_REGISTRATION_SUCCESS,

    // indicates that the Client has successfully been removed from the global Clients list
    SERVER_UNREGISTERING_SUCCESS
}
