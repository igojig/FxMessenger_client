package ru.igojig.fxmessenger.exchanger;

import ru.igojig.fxmessenger.model.User;

public class Exchanger {
    User user;
    String message;

    public Exchanger(User user, String message) {
        this.user = user;
        this.message = message;
    }
}
