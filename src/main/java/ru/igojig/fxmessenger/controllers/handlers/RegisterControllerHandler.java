package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class RegisterControllerHandler extends ControllerHandler{

//    String username;

    public RegisterControllerHandler(Controller controller, Network network) {
        super(controller, network);
    }

    public Optional<String> register(String login, String password, String user){
        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return Optional.empty();
        }


//        sendMessage(REGISTER_NEW_USER + " " + login + " " + password + " " + username);
        try {
            out.writeUTF(String.format("%s %s %s %s", REGISTER_NEW_USER, login, password, user));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды регистрации нового пользователя: " + String.format("%s %s %s %s", REGISTER_NEW_USER, login, password, user));
            return Optional.empty();
        }


        try {
            while (true) {
                String response = in.readUTF();
                String[] responseParts = response.split("\\s+", 3);
                if (responseParts[0].equals(REGISTER_OK)) {
                    username = responseParts[1];
                    id=Integer.parseInt(responseParts[2]);
                    System.out.println("Новый пользователь зарегистрировался под именем: " + username + " Id=" + id);
                    return Optional.of(username);
                }
                if (responseParts[0].equals(REGISTER_ERR)) {
                    System.out.println("Ошибка регистрации:  " + response);
                    return Optional.empty();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при регистрации");
            return Optional.empty();
        }
    }
}
