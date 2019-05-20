package it.polimi.ingsw.network.common.message;

public enum MessageType {
    //registration to the server
    REGISTER_REQUEST,
    PLAYER_ALREADY_REGISTERED_ERROR,
    REGISTER_SUCCESS,

    //unregistering from the server
    UNREGISTER_REQUEST,
    UNREGISTER_SUCCESS,
    PLAYER_NOT_REGISTERED_ERROR,

    //login to lobby
    LOBBY_LOGIN_REQUEST,
    //todo: generate appropriate login errors

    //logout from lobby
    LOBBY_LOGOUT_REQUEST
    //todo: generate appropriate logout errors
}
