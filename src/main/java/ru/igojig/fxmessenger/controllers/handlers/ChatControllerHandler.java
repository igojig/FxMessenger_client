package ru.igojig.fxmessenger.controllers.handlers;

import javafx.application.Platform;
import ru.igojig.fxmessenger.controllers.ChatController;
import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.service.Network;

import java.io.IOException;
import java.net.SocketException;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class ChatControllerHandler extends ControllerHandler {

    Thread readThread;

    volatile boolean isStop = false;

    public ChatControllerHandler(Controller controller, Network network) {
        super(controller, network);
    }


    public void sendMessage(String message) {
        network.sendMessage(message);
    }

    public void sendPrivateMessage(String message, String userName) {
        network.sendPrivateMessage(message, userName);
    }

    public void sendServiceMessage(String msgType, String message) {
        network.sendServiceMessage(msgType, message);
    }

    public void startReadCycle() {
        System.out.println("Запускаем поток чтения сообщений");
        readThread = new Thread(() -> {
            try {
                while (!isStop || !readThread.isInterrupted()) {
                    if (network.isConnected()) {
                        String message = in.readUTF();
                        String[] messageParts = message.split("\\s+", 3);
                        switch (messageParts[0]) {

                            // пришел список пользователей
                            case SERVER_MSG_CMD_PREFIX_LOGGED_USERS -> {
//                                Platform.runLater(() -> ((ChatController)controller).appendMessage("Список пользователей изменился!"));
                                Platform.runLater(() -> ((ChatController) controller).updateUserList(message));
                            }
                            case CHANGE_USERNAME_NEW_LIST -> {
                                Platform.runLater(() -> ((ChatController) controller).updateUserListFromUpdateUsers(message));
                            }
                            case SERVER_MSG_CMD_PREFIX -> {
                                Platform.runLater(() -> ((ChatController) controller).appendMessage( messageParts[1] + " " + messageParts[2]));
                            }
                            case CLIENT_MSG_CMD_PREFIX -> {
                                if(!Controller.username.equals(messageParts[1])) {
                                    Platform.runLater(() -> ((ChatController) controller).appendMessage(messageParts[1] + ": " + messageParts[2]));
                                }
                                else{
                                    Platform.runLater(() -> ((ChatController) controller).appendMessage( messageParts[2]));
                                }
                            }
                            case PRIVATE_MSG_CMD_PREFIX -> {
                                Platform.runLater(() -> ((ChatController) controller).appendMessage("Приватное сообшение от: " + messageParts[1] + ": " + messageParts[2]));
                            }
                            case CHANGE_USERNAME_OK -> {
                                Platform.runLater(() -> ((ChatController) controller).appendMessage("Новое имя пользователя: " + messageParts[1]));
                                Platform.runLater(() -> ((ChatController) controller).updateClientName(messageParts[1]));
                            }
                            case CHANGE_USERNAME_ERR -> {
                                Platform.runLater(() -> ((ChatController) controller).appendMessage("Ошибка смены имени пользователя:"));
                                Platform.runLater(() -> ((ChatController) controller).appendMessage(message));
                                System.out.println("Ошибка смены имени пользователя: " + message);
                            }
                            default -> {
                                Platform.runLater(() -> ((ChatController) controller).appendMessage(message));
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
