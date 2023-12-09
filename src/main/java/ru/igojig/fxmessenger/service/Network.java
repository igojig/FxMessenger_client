package ru.igojig.fxmessenger.service;

import lombok.Getter;
import lombok.Setter;
import ru.igojig.fxmessenger.controllers.handlers.ControllerHandler;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;


import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class Network {

    // на сколько засыпаем при ожидании подключения к серверу
    private static final int WAIT_SERVER_CONNECTION_TIMEOUT = 1000;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8186;

    private final String host;
    private final int port;

    volatile private Socket socket;

    private final List<ControllerHandler<?>> handlerList;

    @Getter
    @Setter
    private User user;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private Thread readThread;

    // флаг установки соединения
    volatile private boolean isConnected = false;

    public Network(String host, int port) {
        this.host = host;
        this.port = port;

        handlerList = new ArrayList<>();
    }

    public Network() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public void connect() {
        waitConnectionWithServer();
    }

    private void waitConnectionWithServer() {
//         ждем пока запуститься сервер
        Thread waitServerThread = new Thread(() -> {
            System.out.println("Клиент соединяется с сервером....");
            try {
                while (socket == null) {
                    try {
                        socket = new Socket(host, port);
                    } catch (ConnectException e) {
                        try {
                            Thread.sleep(WAIT_SERVER_CONNECTION_TIMEOUT);
                            socket = null;
                        } catch (InterruptedException ex) {
                            System.out.println("Ошибка при создании сокета");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                System.out.println("Соединение с сервером установлено");

                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                isConnected = true;

                startReadThread();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Соединение с сервером не установлено");
            }
        });
        waitServerThread.setDaemon(true);
        waitServerThread.start();
    }

    private void startReadThread() {
        readThread = new Thread(() -> {
            while (true) {
                try {
                    Exchanger exchanger = readObject();
                    for (int i = 0; i < handlerList.size(); i++) {
                        handlerList.get(i).consumeMsg(exchanger);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error in read thread: " + e);
                    break;
                }
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    public void sendMessage(String message) {
        Exchanger exchanger = new Exchanger(CLIENT_MSG, message, null);
        sendMessage(exchanger);
    }

    public void sendMessage(String message, User sendToUser) {
        Exchanger exchanger = new Exchanger(PRIVATE_MSG, message, new UserExchanger(sendToUser));
        sendMessage(exchanger);
    }

    public void sendMessage(Exchanger exchanger) {
        try {
            if (isConnected) {
                objectOutputStream.reset();
                objectOutputStream.writeObject(exchanger);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Сообщение от клиента не отправлено");
        }
    }

    public <T> void writeObject(T t) throws IOException {
        objectOutputStream.reset();
        objectOutputStream.writeObject(t);
    }

    public void exitClient() {
        if (isConnected) {
            if (user != null) {
                Exchanger exchanger = new Exchanger(END_CLIENT, "выходим из чата", new UserExchanger(user));
                try {
                    objectOutputStream.reset();
                    objectOutputStream.writeObject(exchanger);
                    objectOutputStream.close();
                    objectInputStream.close();
                } catch (IOException e) {
                    System.out.println("ошибка в ExitClient: " + e);
                }
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject() throws IOException, ClassNotFoundException {
        return (T) objectInputStream.readObject();
    }

    public void subscribe(ControllerHandler<?> handler) {
        handlerList.add(handler);
    }

    public void unsubscribe(ControllerHandler<?> handler) {
        handlerList.remove(handler);
    }
}
