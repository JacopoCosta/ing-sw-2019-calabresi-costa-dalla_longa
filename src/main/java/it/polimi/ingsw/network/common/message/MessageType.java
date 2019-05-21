package it.polimi.ingsw.network.common.message;

public enum MessageType {
    //ping
    PING_MESSAGE,

    //registration to the server
    REGISTER_REQUEST,
    REGISTER_SUCCESS,
    CLIENT_ALREADY_REGISTERED_ERROR,,
    CLIENT_NOT_REGISTERED_ERROR,

    //unregistering from the server
    UNREGISTER_REQUEST,
    UNREGISTER_SUCCESS,
    PLAYER_NOT_REGISTERED_ERROR,

    //lobby live update
    LOBBY_LIST_UPDATE_REQUEST,

    //create new lobby
    LOBBY_INIT_REQUEST,
    LOBBY_INIT_SUCCESS,
    LOBBY_INIT_FAILED, //TODO generate appropriate creation errors
    LOBBY_NOT_FOUND_ERROR,
    LOBBY_FULL_ERROR,

    //login to lobby
    LOBBY_LOGIN_REQUEST,
    LOBBY_LOGIN_SUCCESS,
    LOBBY_LOGIN_FAILED, //TODO: generate appropriate login errors

    //logout from lobby
    LOBBY_LOGOUT_REQUEST,
    LOBBY_LOGOUT_SUCCESS,
    LOBBY_LOGOUT_FAILED //TODO: generate appropriate logout errors
}
