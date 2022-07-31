package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class LoginControllerHandler extends ControllerHandler {

    public LoginControllerHandler(Controller controller, Network network) {
        super(controller, network);

    }


    public Optional<String> logIn(String login, String password) {


        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return Optional.empty();
        }


        //команда аутентификации
//        sendMessage(AUTH_CMD_PREFIX + " " + login + " " + password);
        try {
            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды начала авторизации: " + String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            return Optional.empty();
        }

        try {
            while (true) {


                String response = in.readUTF();

                String[] responseParts = response.split("\\s+", 3);

                if (responseParts[0].equals(AUTH_OK_CMD_PREFIX)) {
                    username = responseParts[1];
                    id=Integer.parseInt(responseParts[2]);
                    System.out.println("Пользователь вошел под именем: " + username + " Id=" + id);

                    return Optional.of(username);
                }
                if (responseParts[0].equals(AUTH_ERR_CMD_PREFIX)) {
                    System.out.println("Ошибка авторизации: " + response);
                    return Optional.empty();
                }
//                if(responseParts[0].equals(CMD_SHUT_DOWN_CLIENT)){
//                    System.out.println("Пришла команда закрытия окна");
//                    Platform.exit();
//                }

                System.out.println(response);
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при аутентификации");
            return Optional.empty();
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

