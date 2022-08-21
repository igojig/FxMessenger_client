package ru.igojig.fxmessenger.controllers.handlers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import ru.igojig.fxmessenger.controllers.ChatController;
import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.exchanger.ChatExchanger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.HistoryExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserListExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatControllerHandler extends ControllerHandler<ChatController> {

    Thread readThread;

    volatile boolean isStop = false;


    public ChatControllerHandler(ChatController controller, Network network) {
        super(controller, network);
    }


    public void sendMessage(String message) {
        network.sendMessage(message);
    }

    public void sendPrivateMessage(String message, User sendToUser) {
        network.sendPrivateMessage(message, sendToUser);
    }

    public void sendServiceMessage(Prefix msgType, String message, ChatExchanger chatExchanger) {
        Exchanger exchanger = new Exchanger(msgType, message, chatExchanger);
        network.sendServiceMessage(exchanger);
    }

    public void startReadCycle() {
        System.out.println("Запускаем поток чтения сообщений");

        readThread = new Thread(() -> {
            try {
                while (!isStop || !readThread.isInterrupted()) {
                    if (network.isConnected()) {

                        Exchanger exchanger = network.readObject();

                        Prefix prefix = exchanger.getCommand();
//                        ChatObject chatObject = exchanger.getChatObject();
//                        ChatController rootController= (ChatController) controller;


                        switch (prefix) {

                            // пришел список пользователей
                            case LOGGED_USERS, CHANGE_USERNAME_NEW_LIST -> {
//                                Platform.runLater(() -> ((ChatController)controller).appendMessage("Список пользователей изменился!"));
//                                Platform.runLater(() -> controller.updateUserList((ChangeUserList) chatObject));
                                Platform.runLater(() -> controller.updateUserList(exchanger.getChatExchanger(UserListExchanger.class)));
                            }
//                            case CHANGE_USERNAME_NEW_LIST -> {
//                                Platform.runLater(() -> ((ChatController) controller).updateUserListFromUpdateUsers(message));
//                            }
                            case SERVER_MSG -> {
                                Platform.runLater(() -> controller.appendMessage(exchanger.getMessage()));
                            }
                            case CLIENT_MSG -> {
                                if (exchanger.getChatExchanger(UserExchanger.class).getUser().getId().equals(Controller.user.getId())) {
                                    Platform.runLater(() -> controller.appendMessage(exchanger.getMessage()));
                                } else {
                                    Platform.runLater(() -> controller.appendMessage(
                                            (exchanger.getChatExchanger(UserExchanger.class).getUser().getUsername() + ":" + exchanger.getMessage())));
                                }
                            }
                            case PRIVATE_MSG -> {
                                Platform.runLater(() -> controller.appendMessage("Приватное сообшение от: "
                                        + (exchanger.getChatExchanger(UserExchanger.class).getUser().getUsername() +
                                        ": " + exchanger.getMessage())));
                            }
                            case CHANGE_USERNAME_OK -> {
                                Platform.runLater(() -> controller.appendMessage("Новое имя пользователя: " +
                                        (exchanger.getChatExchanger(UserExchanger.class).getUser().getUsername())));
                                Platform.runLater(() -> controller.updateClientName(exchanger.getChatExchanger(UserExchanger.class).getUser()));
                            }
                            case CHANGE_USERNAME_ERR -> {
                                Platform.runLater(() -> controller.appendMessage("Ошибка смены имени пользователя:"));
//                                Platform.runLater(() -> ((ChatController) controller).appendMessage(exchanger.getMessage()));
                                System.out.println("Ошибка смены имени пользователя: " + exchanger);
                            }
                            case PRIVATE_MSG_ERR -> {
                                User user = exchanger.getChatExchanger(UserExchanger.class).getUser();
                                Platform.runLater(() -> controller.appendMessage("Пользователь: " + user.getUsername() + " не найден"));
                                System.out.println("ПОльзователь не найден: " + user);

                            }
                            case HISTORY_LOAD -> {
                                HistoryExchanger historyExchanger = exchanger.getChatExchanger(HistoryExchanger.class);
                                controller.setUserHistory(historyExchanger.getHistoryList());
                            }
                            default -> {
                                Platform.runLater(() -> controller.appendMessage("Неизвестный тип сообщения: " + exchanger.getMessage()));
                                System.out.println("Неизвестный тип сообщения: " + exchanger);
                            }

                        }
                    }
                }
            }
//            catch (SocketException e) {
//                if (e.getMessage().equals("Socket closed")) {
//                    System.out.println("Ошибка чтения сообщения - Socket закрыт");
//                } else {
//                    e.printStackTrace();
//                }
//            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Сообшение от сервера не прочитано");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        readThread.start();
    }

    public void stopReadCycle() {
        System.out.println("Останавливаем поток чтения сообщений...");
        isStop = true;
        System.out.println("Поток остановлен");

    }


    public void saveHistory(ObservableList<CharSequence> userHistory) {
        List<String> list = new ArrayList<>(userHistory.stream().map(CharSequence::toString).toList());

        Exchanger ex = new Exchanger(Prefix.HISTORY_SAVE, "сохраняем историю", new HistoryExchanger(list));
        try {
            network.writeObject(ex);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки истории");
        }
    }
}
