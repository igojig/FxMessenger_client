package ru.igojig.fxmessenger.model;


import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -1584943797365343078L;
    String username;
    String login;
    String password;
}
