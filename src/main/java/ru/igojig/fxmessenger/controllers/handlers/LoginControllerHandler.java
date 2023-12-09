package ru.igojig.fxmessenger.controllers.handlers;

import javafx.application.Platform;
import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.controllers.LogInController;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class LoginControllerHandler<T extends LogInController> extends ControllerHandler<T> {

    public LoginControllerHandler(T controller, Network network) {
        super(controller, network);
    }

    @Override
    public void consumeMsg(Exchanger exchanger) {
        if (exchanger.getCommand() == AUTH_OK) {
            User user = exchanger.getChatExchanger(UserExchanger.class).getUser();
            network.setUser(user);
            System.out.println("Пользователь вошел под именем: " + user);

            Platform.runLater(() -> controller.showChat());
        }

        if (exchanger.getCommand() == AUTH_ERR) {
            System.out.println("Ошибка авторизации: " + exchanger.getMessage());
        }

        if (exchanger.getCommand() == REGISTER_OK) {
            User user = exchanger.getChatExchanger(UserExchanger.class).getUser();
            network.setUser(user);
            System.out.println("Новый пользователь зарегистрировался под именем: " + user);

            Platform.runLater(() -> controller.showChat());
        }

        if (exchanger.getCommand() == REGISTER_ERR) {
            System.out.println("Ошибка регистрации:  " + exchanger.getMessage());
        }

        if (exchanger.getCommand() == CMD_SHUT_DOWN_CLIENT) {
            System.out.println("Отключаем клиента");
            network.unsubscribe(this);
            network.exitClient();
            Platform.exit();
        }
    }

    public void logIn(String login, String password) {

        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return;
        }

        Exchanger exchanger = null;
        try {
            exchanger = new Exchanger(AUTH_REQUEST, null, new UserExchanger(new User(null, null, login, password)));
            network.writeObject(exchanger);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды начала авторизации: " + exchanger);
        }
    }

    public void register(String login, String password, String username) {
        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return;
        }

        Exchanger exchanger = null;

        try {
            exchanger = new Exchanger(REGISTER_REQUEST, null, new UserExchanger(new User(null, username, login, password)));
            network.writeObject(exchanger);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды регистрации нового пользователя: " + exchanger);
        }
    }

    public void unsubscribe() {
        network.unsubscribe(this);
    }

    public void subscribe() {
        network.subscribe(this);
    }
}

