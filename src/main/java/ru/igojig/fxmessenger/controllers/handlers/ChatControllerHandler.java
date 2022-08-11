package ru.igojig.fxmessenger.controllers.handlers;

import javafx.application.Platform;
import ru.igojig.fxmessenger.controllers.ChatController;
import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.exchanger.ChatObject;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.ChangeUserList;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;

public class ChatControllerHandler extends ControllerHandler {

    Thread readThread;

    volatile boolean isStop = false;


    public ChatControllerHandler(Controller controller, Network network) {
        super(controller, network);
    }


    public void sendMessage(String message) {
        network.sendMessage(message);
    }

    public void sendPrivateMessage(String message, User sendToUser) {
        network.sendPrivateMessage(message, sendToUser);
    }

    public void sendServiceMessage(Prefix msgType, String message, ChatObject chatObject) {
        Exchanger exchanger=new Exchanger(msgType, message, chatObject);
        network.sendServiceMessage(exchanger);
    }

    public void startReadCycle() {
        System.out.println("Запускаем поток чтения сообщений");

        readThread = new Thread(() -> {
            try {
                while (!isStop || !readThread.isInterrupted()) {
                    if (network.isConnected()) {

                        Exchanger exchanger= (Exchanger) objectInputStream.readObject();

                        Prefix prefix=exchanger.getCommand();
                        ChatObject chatObject=exchanger.getChatObject();
                        ChatController rootController= (ChatController) controller;

                        switch (prefix) {

                            // пришел список пользователей
                            case LOGGED_USERS, CHANGE_USERNAME_NEW_LIST -> {
//                                Platform.runLater(() -> ((ChatController)controller).appendMessage("Список пользователей изменился!"));
                                Platform.runLater(() -> rootController.updateUserList((ChangeUserList) chatObject));
                            }
//                            case CHANGE_USERNAME_NEW_LIST -> {
//                                Platform.runLater(() -> ((ChatController) controller).updateUserListFromUpdateUsers(message));
//                            }
                            case SERVER_MSG -> {
                                Platform.runLater(() -> rootController.appendMessage(exchanger.getMessage()));
                            }
                            case CLIENT_MSG -> {
                                if(((UserExchanger)(chatObject)).getUser().getId().equals(Controller.user.getId())) {
                                    Platform.runLater(() -> rootController.appendMessage( ": " + exchanger.getMessage()));
                                }
                                else{
                                    Platform.runLater(() -> rootController.appendMessage(
                                            ((UserExchanger)(chatObject)).getUser().getUsername() + ":" + exchanger.getMessage()));
                                }
                            }
                            case PRIVATE_MSG -> {
                                Platform.runLater(() -> rootController.appendMessage("Приватное сообшение от: "
                                        + ((UserExchanger)(chatObject)).getUser().getUsername() +
                                        ": " + exchanger.getMessage()));
                            }
                            case CHANGE_USERNAME_OK -> {
                                Platform.runLater(() -> rootController.appendMessage("Новое имя пользователя: " +
                                        ((UserExchanger)chatObject).getUser().getUsername()));
                                Platform.runLater(() -> rootController.updateClientName(((UserExchanger)chatObject).getUser()));
                            }
                            case CHANGE_USERNAME_ERR -> {
                                Platform.runLater(() -> rootController.appendMessage("Ошибка смены имени пользователя:"));
//                                Platform.runLater(() -> ((ChatController) controller).appendMessage(exchanger.getMessage()));
                                System.out.println("Ошибка смены имени пользователя: " + exchanger);
                            }
                            case PRIVATE_MSG_ERR -> {
                                User user =((UserExchanger)chatObject).getUser();
                                Platform.runLater(() -> rootController.appendMessage("Пользователь: " + user.getUsername() + " не найден"));
                                System.out.println("ПОльзователь не найден: " + user);

                            }
                            default -> {
                                Platform.runLater(() -> rootController.appendMessage("Неизвестный тип сообщения: " + exchanger.getMessage()));
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


}
