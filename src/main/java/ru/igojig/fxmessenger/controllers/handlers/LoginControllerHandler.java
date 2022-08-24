package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.controllers.LogInController;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.FutureTask;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class LoginControllerHandler<T extends LogInController> extends ControllerHandler<T> {

    public LoginControllerHandler(T controller, Network network) {
        super(controller, network);
    }

    public Optional<User> logIn(String login, String password) {

        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return Optional.empty();
        }

//        objectOutputStream=network.getObjectOutputStream();
//        objectInputStream=network.getObjectInputStream();

        Exchanger exchanger = null;
        try {
            exchanger = new Exchanger(AUTH_REQUEST, null, new UserExchanger(new User(null, null, login, password)));
//            objectOutputStream.writeObject(exchanger);
            network.writeObject(exchanger);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды начала авторизации: " + exchanger);
            return Optional.empty();
        }

        try {
            while (true) {
//                exchanger = (Exchanger) objectInputStream.readObject();

                exchanger=network.readObject();
//                FutureTask<Exchanger> futureTask=new FutureTask<>(()->{
//                    Exchanger exchan=network.read();
//                });


                if (exchanger.getCommand() == AUTH_OK) {
//                    user = ((UserExchanger) (exchanger.getChatObject())).getUser();
                    user=exchanger.getChatExchanger(UserExchanger.class).getUser();
                    System.out.println("Пользователь вошел под именем: " + user);
                    return Optional.of(user);
                }
                if (exchanger.getCommand() == AUTH_ERR) {
                    System.out.println("Ошибка авторизации: " + exchanger.getMessage());
                    return Optional.empty();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при аутентификации");
            return Optional.empty();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}




//
//    @Override
//    public void listen(String message) {
//        String[] responseParts = message.split("\\s+", 2);
//
//        if (responseParts[0].equals(AUTH_OK_CMD_PREFIX)) {
//            userName = responseParts[1];
//            System.out.println("Пользователь вошел под именем: " + userName);
//
//            isLogIn=true;
//
//        }
//        if (responseParts[0].equals(AUTH_ERR_CMD_PREFIX)) {
//            System.out.println("Ошибка авторизации: " + message);
//            isLogIn=false;
//        }
//    }

