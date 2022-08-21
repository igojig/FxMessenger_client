package ru.igojig.fxmessenger.prefix;


import java.io.Serializable;

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



}



//
//public class Prefix {
//    public static final String AUTH_CMD_PREFIX = "/auth";          // + login + password
//    public static final String AUTH_OK_CMD_PREFIX = "/auth_ok";     // + username + id
//    public static final String AUTH_ERR_CMD_PREFIX = "/auth_err";  // + error
//
//
//    public static final String CLIENT_MSG_CMD_PREFIX = "/c_msg";
//    public static final String SERVER_MSG_CMD_PREFIX = "/s_msg";
//    public static final String PRIVATE_MSG_CMD_PREFIX = "/p_msg";   // + username + message
//
//    //сисок залогиненных пользователей
//    public static final String SERVER_MSG_CMD_PREFIX_LOGGED_USERS = "/s_msg_users"; //[(+/-)] username + user1 + user2 +....
//
//
//    public static final String STOP_SERVER_CMD_PREFIX = "/server_stop";
//    public static final String END_CLIENT_CMD_PREFIX = "/client_exit";
//
//    //TODO
//    // сообщение клиенту что соединеие закрыто по истечении тайм аута и что надо закрыть окно
//    public static final String CMD_SHUT_DOWN_CLIENT = "/client_shut_down";
//
//    // регистрация нового пользователя
//    public static final String REGISTER_NEW_USER = "/register";  // + login + password + username
//    //
//    public static final String REGISTER_OK="/register_ok";   // + username
//    public static final String REGISTER_ERR="/register_err";   // + error
//
//
//    public static final String CHANGE_USERNAME_REQUEST="/chg_name_req"; // + New username
//    public static final String CHANGE_USERNAME_OK="/chg_name_ok"; // + New username; + broadcast
//    public static final String CHANGE_USERNAME_ERR="/chg_name_err"; // + error
//    public static final String CHANGE_USERNAME_NEW_LIST="/chg_name_list"; // + user1+user2+.....
//}
