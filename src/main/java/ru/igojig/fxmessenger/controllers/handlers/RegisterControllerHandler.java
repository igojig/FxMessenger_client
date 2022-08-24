package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.LogInController;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class RegisterControllerHandler<T extends LogInController> extends ControllerHandler<T> {

    public RegisterControllerHandler(T controller, Network network) {
        super(controller, network);
    }

    public Optional<User> register(String login, String password, String username) {
        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return Optional.empty();
        }
        // если клиент запущен раньше чем сервер, нодо получить, иначе null
//        objectOutputStream = network.getObjectOutputStream();
//        objectInputStream = network.getObjectInputStream();

        Exchanger exchanger = null;

        try {
            exchanger = new Exchanger(REGISTER_REQUEST, null, new UserExchanger(new User(null, username, login, password)));
            network.writeObject(exchanger);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды регистрации нового пользователя: " + exchanger);
            return Optional.empty();
        }

        try {
            while (true) {
                exchanger =  network.readObject();
                if (exchanger.getCommand() == REGISTER_OK) {
//                    user=((UserExchanger)(exchanger.getChatObject())).getUser();
                    user = exchanger.getChatExchanger(UserExchanger.class).getUser();
                    System.out.println("Новый пользователь зарегистрировался под именем: " + user);
                    return Optional.of(user);
                }
                if (exchanger.getCommand() == REGISTER_ERR) {
                    System.out.println("Ошибка регистрации:  " + exchanger.getMessage());
                    return Optional.empty();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при регистрации");
            return Optional.empty();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
