package ru.igojig.fxmessenger.controllers.handlers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import ru.igojig.fxmessenger.controllers.ChatController;
import ru.igojig.fxmessenger.exchanger.ChatExchanger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.HistoryExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserListExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.service.Network;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatControllerHandler<T extends ChatController> extends ControllerHandler<T> {

    public ChatControllerHandler(T controller, Network network) {
        super(controller, network);
    }

    @Override
    public void consumeMsg(Exchanger exchanger) {
        Prefix prefix = exchanger.getCommand();
        switch (prefix) {

            // пришел список пользователей
            case LOGGED_USERS, CHANGE_USERNAME_NEW_LIST -> {
                Platform.runLater(() -> controller.updateUserList(exchanger.getChatExchanger(UserListExchanger.class)));
            }
            case SERVER_MSG -> {
                Platform.runLater(() -> controller.appendMessage(exchanger.getMessage()));
            }
            case CLIENT_MSG -> {
                if (exchanger.getChatExchanger(UserExchanger.class).getUser().getId().equals(network.getUser().getId())) {
                    Platform.runLater(() -> controller.appendMessage(exchanger.getMessage()));
                } else {
                    Platform.runLater(() -> controller.appendMessage(
                            (exchanger.getChatExchanger(UserExchanger.class).getUser().getUsername() + ":" + exchanger.getMessage())));
                }
//                Platform.runLater(() -> controller.appendMessage(exchanger.getMessage()));
            }
            case PRIVATE_MSG -> {
                Platform.runLater(() -> controller.appendMessage("Приватное сообщение от ["
                        + (exchanger.getChatExchanger(UserExchanger.class).getUser().getUsername() + "]" +
                        ":" + exchanger.getMessage())));
            }
            case CHANGE_USERNAME_OK -> {
                Platform.runLater(() -> controller.appendMessage("Новое имя пользователя: " +
                        (exchanger.getChatExchanger(UserExchanger.class).getUser().getUsername())));
                network.setUser(exchanger.getChatExchanger(UserExchanger.class).getUser());
                Platform.runLater(() -> controller.updateClientName(exchanger.getChatExchanger(UserExchanger.class).getUser()));
            }
            case CHANGE_USERNAME_ERR -> {
                Platform.runLater(() -> controller.appendMessage("Ошибка смены имени пользователя:"));
                System.out.println("Ошибка смены имени пользователя: " + exchanger);
            }
            case PRIVATE_MSG_ERR -> {
                User sendToUser = exchanger.getChatExchanger(UserExchanger.class).getUser();
                Platform.runLater(() -> controller.appendMessage("Пользователь: " + sendToUser.getUsername() + " не найден"));
                System.out.println("Пользователь не найден: " + sendToUser);
            }
            case CMD_HISTORY_LOAD -> {
                HistoryExchanger historyExchanger = exchanger.getChatExchanger(HistoryExchanger.class);
//                Platform.runLater(()->{controller.appendMessage("Загружаем историю сообщений");});
                Platform.runLater(()->controller.setUserHistory(historyExchanger.getHistoryList()));
            }
            default -> {
                Platform.runLater(() -> controller.appendMessage("Неизвестный тип сообщения: " + exchanger.getMessage()));
                System.out.println("Неизвестный тип сообщения: " + exchanger);
            }
        }
    }

    public void sendMessage(String message) {
        network.sendMessage(message);
    }

    public void sendMessage(String message, User sendToUser) {
        network.sendMessage(message, sendToUser);
    }

    public void sendMessage(Prefix prefix, String message, ChatExchanger chatExchanger) {
        Exchanger exchanger = new Exchanger(prefix, message, chatExchanger);
        network.sendMessage(exchanger);
    }

    public void saveHistory(ObservableList<CharSequence> userHistory) {
        List<String> historyList = new ArrayList<>(userHistory.stream().map(CharSequence::toString).toList());

        Exchanger response = new Exchanger(Prefix.CMD_HISTORY_SAVE, "сохраняем историю", new HistoryExchanger(historyList));
        try {
            network.writeObject(response);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки истории");
        }
    }

    public void subscribe(){
        network.subscribe(this);
    }

    public void requestLoggedUsers() {
        Exchanger exchanger=new Exchanger(Prefix.CMD_REQUEST_USERS_LIST, "запрашиваем список пользователей", null);
        network.sendMessage(exchanger);
    }
}
