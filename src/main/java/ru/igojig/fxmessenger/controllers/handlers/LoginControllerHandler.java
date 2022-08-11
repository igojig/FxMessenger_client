package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class LoginControllerHandler extends ControllerHandler {

    public LoginControllerHandler(Controller controller, Network network) {
        super(controller, network);

    }


    public Optional<User> logIn(String login, String password) {


        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return Optional.empty();
        }
        objectOutputStream=network.getObjectOutputStream();
        objectInputStream=network.getObjectInputStream();

        Exchanger exchanger = null;
        //команда аутентификации
//        sendMessage(AUTH_CMD_PREFIX + " " + login + " " + password);
        try {
//            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            exchanger = new Exchanger(AUTH_REQUEST, null, new UserExchanger(new User(null, null, login, password)));
            objectOutputStream.writeObject(exchanger);
//            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды начала авторизации: " + exchanger);
            return Optional.empty();
        }

        try {
            while (true) {


//                String response = in.readUTF();
                exchanger = (Exchanger) objectInputStream.readObject();

//                String[] responseParts = response.split("\\s+", 3);

                if (exchanger.getCommand() == AUTH_OK) {
//                    username = responseParts[1];
//                    id=Integer.parseInt(responseParts[2]);
                    user = ((UserExchanger) (exchanger.getChatObject())).getUser();

                    System.out.println("Пользователь вошел под именем: " + user);

                    return Optional.of(user);
                }
                if (exchanger.getCommand() == AUTH_ERR) {
                    System.out.println("Ошибка авторизации: " + exchanger.getMessage());
                    return Optional.empty();
                }
//                if(responseParts[0].equals(CMD_SHUT_DOWN_CLIENT)){
//                    System.out.println("Пришла команда закрытия окна");
//                    Platform.exit();
//                }

//                System.out.println(response);
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

