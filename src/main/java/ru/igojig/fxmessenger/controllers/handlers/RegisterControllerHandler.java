package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class RegisterControllerHandler extends ControllerHandler{

//    String username;

    public RegisterControllerHandler(Controller controller, Network network) {
        super(controller, network);
    }

    public Optional<User> register(String login, String password, String username){
        if (!network.isConnected()) {
            System.out.println("Клиент не подключен к серверу");
            return Optional.empty();
        }
        objectOutputStream=network.getObjectOutputStream();
        objectInputStream=network.getObjectInputStream();

        Exchanger exchanger=null;

//        sendMessage(REGISTER_NEW_USER + " " + login + " " + password + " " + username);
        try {
            exchanger=new Exchanger(REGISTER_REQUEST, null, new UserExchanger(new User(null, username, login, password)));

//            out.writeUTF(String.format("%s %s %s %s", REGISTER_NEW_USER, login, password, user));

            objectOutputStream.writeObject(exchanger);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки команды регистрации нового пользователя: " + exchanger);
            return Optional.empty();
        }


        try {
            while (true) {
                exchanger=(Exchanger) objectInputStream.readObject();
//                String response = in.readUTF();
//                String[] responseParts = response.split("\\s+", 3);

                if (exchanger.getCommand()==REGISTER_OK) {
//                    username = responseParts[1];
//                    id=Integer.parseInt(responseParts[2]);

                    user=((UserExchanger)(exchanger.getChatObject())).getUser();

                    System.out.println("Новый пользователь зарегистрировался под именем: " + user);
                    return Optional.of(user);
                }
                if (exchanger.getCommand()==REGISTER_ERR) {
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
