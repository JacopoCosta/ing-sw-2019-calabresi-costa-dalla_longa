package it.polimi.ingsw.network.common.message;

public enum MessageType {
    //ping
    PING_MESSAGE,

    //registration to the server
    REGISTER_REQUEST,
    REGISTER_SUCCESS,
    CLIENT_ALREADY_REGISTERED_ERROR,

    //unregistering from the server
    UNREGISTER_REQUEST,
    UNREGISTER_SUCCESS,
    CLIENT_NOT_REGISTERED_ERROR,

    //lobby live update
    LOBBY_LIST_UPDATE_REQUEST,
    LOBBY_LIST_UPDATE_RESPONSE,

    //create new lobby
    LOBBY_CREATE_REQUEST,
    LOBBY_CREATE_SUCCESS,
    LOBBY_ALREADY_EXISTS_ERROR,
    LOBBY_NOT_FOUND_ERROR,
    LOBBY_FULL_ERROR,

    //login to lobby
    LOBBY_LOGIN_REQUEST,
    LOBBY_LOGIN_SUCCESS,
    PLAYER_ALREADY_ADDED_ERROR,
    PASSWORD_NOT_VALID_ERROR,

    //logout from lobby
    LOBBY_LOGOUT_REQUEST,
    LOBBY_LOGOUT_SUCCESS,
    LOBBY_EMPTY_ERROR,
    PLAYER_NOT_FOUND_ERROR,

    //user message
    CLIENT_MESSAGE
}
