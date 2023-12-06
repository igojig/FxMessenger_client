package ru.igojig.fxmessenger.prefix;


public enum Prefix  {
    AUTH_REQUEST,
    AUTH_OK,
    AUTH_ERR,

    CLIENT_MSG,
    SERVER_MSG,
    PRIVATE_MSG,

    PRIVATE_MSG_ERR,

    //сисок залогиненных пользователей
    LOGGED_USERS,

    STOP_SERVER,
    END_CLIENT,
    CMD_SHUT_DOWN_CLIENT,

    REGISTER_REQUEST,
    REGISTER_OK,
    REGISTER_ERR,

    CHANGE_USERNAME_REQUEST,
    CHANGE_USERNAME_OK,
    CHANGE_USERNAME_ERR,

    CHANGE_USERNAME_NEW_LIST,

    HISTORY_REQUEST,
    HISTORY_SAVE,
    HISTORY_LOAD,

    CMD_REQUEST_USERS

}

