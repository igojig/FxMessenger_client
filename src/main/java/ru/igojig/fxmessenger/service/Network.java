package ru.igojig.fxmessenger.service;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.model.User;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class Network {





    // на сколько засыпаем при ожидании подключения к серверу
    private static final int SLEEP_TIMEOUT = 500;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8186;

    private final String host;
    private final int port;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

//    private Thread readThread;
//    private Thread sendThread;

    // читаем из этой очереди
//    ArrayBlockingQueue<String> inMsg=new ArrayBlockingQueue<>(100);

    // пишем в очередь
//    ArrayBlockingQueue<String> outMsg=new ArrayBlockingQueue<>(100);

//    List<Listener> listenerList=new ArrayList<>();
//    List<Sender> senderList=new ArrayList<>();
//    List<ControllerHandler> controllerHandlerList=new ArrayList<>();
//
//    List<Reader> readerList=new ArrayList<>();


//    private String userName;

    // флаг установки соединения
    volatile private boolean isConnected = false;
    volatile private boolean isAuthorized = false;

    public Network(String host, int port) {
        this.host = host;
        this.port = port;


//        messageListenerList = new ArrayList<>();
    }

    public Network() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }




    public void connect() {
        waitConnectionWithServer();

        //TODO
        // ждем подключения
        // сделать по человечески
//        while (!is_connected) ;
//
//        mainCycle = new MainCycle(in, out);
//        System.out.println("Запускаем поток чтения с сервера");
//        mainCycle.startCycle();


    }



    private void waitConnectionWithServer() {
        // ждем пока запуститься сервер
        Thread thread = new Thread(() -> {
            System.out.println("Клиент соединяется с сервером....");
            try {
                while (socket == null) {
                    try {
                        socket = new Socket(host, port);
                    } catch (ConnectException e) {
                        try {
                            Thread.sleep(SLEEP_TIMEOUT);
//                            socket=null;
                        } catch (InterruptedException ex) {
                            System.out.println("Ошибка при создании сокета");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                System.out.println("Соединение с сервером установлено");

                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());

                objectInputStream=new ObjectInputStream(in);
                objectOutputStream=new ObjectOutputStream((out);

                isConnected = true;

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Соединение с сервером не установлено");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


//    public void readMessage(){
//        readThread=new Thread(()->{
//            while (true){
//                if(isConnected) {
//                    try {
//                        if(!blocked){
//                            String message = in.readUTF();
////                            listenerList.forEach(o -> o.listen(message));
//                            inMsg.put(message);
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        readThread.setDaemon(true);
//        readThread.start();
//    }

//    public void sndMessage(){
//        sendThread=new Thread(()->{
//            while (true){
//                if(isConnected) {
//                    try {
//                        if(!blocked){
//                            for (Sender sender : senderList) {
////                                String message=sender.send();
//                                String message= outMsg.take();
//                                out.writeUTF(message);
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        });
//        sendThread.setDaemon(true);
//        sendThread.start();
//    }




    // Сообщение отклиента -> клиенту
    public void sendMessage(String message) {
        try {
            if (isConnected) {
                Exchanger exchanger;
                if (message.startsWith("/")) {
                    exchanger=new Exchanger(message, null, null);
                    objectOutputStream.writeObject(exchanger);
//                    out.writeUTF(String.format("%s", message));
                } else {
                    exchanger=new Exchanger(CLIENT_MSG_CMD_PREFIX, message, user);
                    objectOutputStream.writeObject(exchanger);
//                    out.writeUTF(String.format("%s %s", CLIENT_MSG_CMD_PREFIX, message));
//                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Сообшение от клиента не отправлено");
        }
    }

    public void sendPrivateMessage(String message, String userName) {
        try {
            if (isConnected) {

                Exchanger exchanger=new Exchanger(PRIVATE_MSG_CMD_PREFIX,message, new User(null, userName, null, null));
                objectOutputStream.writeObject(exchanger);

//                out.writeUTF(String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, userName, message));
//                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Приватное сообшение от клиента не отправлено");
        }
    }

    // сервисное сообщение
    public void sendServiceMessage(String msgType, String message){
        try {
            if (isConnected) {
                Exchanger exchanger=new Exchanger(msgType, message, null);
                objectOutputStream.writeObject(exchanger);
//                out.writeUTF(String.format("%s %s", msgType, message));
//                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Сервисное сообшение от клиента не отправлено");
        }
    }

//    public void waitMessage(ChatController chatController) {
//
//        readThread = new Thread(() -> {
//            try {
//                while (true) {
//                    if (isConnected) {
//                        String message = in.readUTF();
//                        String[] messageParts = message.split("\\s+", 3);
//                        switch (messageParts[0]) {
//
//                            // пришел список пользователей
//                            case SERVER_MSG_CMD_PREFIX_LOGGED_USERS -> {
//                                Platform.runLater(() -> chatController.appendMessage("Список пользователей изменился!"));
//                                Platform.runLater(() -> chatController.updateUserList(message));
//                            }
//                            case CLIENT_MSG_CMD_PREFIX -> {
//                                Platform.runLater(() -> chatController.appendMessage(messageParts[1] + ": " + messageParts[2]));
//                            }
//                            case PRIVATE_MSG_CMD_PREFIX -> {
//                                Platform.runLater(() -> chatController.appendMessage("Приватное сообшение от: " + messageParts[1] + ": " + messageParts[2]));
//                            }
//                            default -> {
//                                Platform.runLater(() -> chatController.appendMessage(message));
//                            }
//
//                        }
//                    }
//                }
//            } catch (SocketException e) {
//                if (e.getMessage().equals("Socket closed")) {
//                    System.out.println("Ошибка чтения сообщения - Socket закрыт");
//                } else {
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Сообшение от сервера не прочитано");
//            }
//        });
//        readThread.setDaemon(true);
//        readThread.start();
//    }

//    public boolean authorize(LogInController chatController, String login, String password) {
//        if (!isConnected) {
//            System.out.println("Клиент не подключен к серверу");
//            return false;
//        }
//
//        //команда аутентификации
////        sendMessage(AUTH_CMD_PREFIX + " " + login + " " + password);
//        try {
//            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Ошибка отправки команда начала авторизации: " + String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
//            return false;
//        }
//
//        try {
//            while (true) {
//                String response = in.readUTF();
//
//                String[] responseParts = response.split("\\s+", 2);
//
//                if (responseParts[0].equals(AUTH_OK_CMD_PREFIX)) {
//                    userName = responseParts[1];
//                    System.out.println("Пользователь вошел под именем: " + userName);
//                    return true;
//                }
//                if (responseParts[0].equals(AUTH_ERR_CMD_PREFIX)) {
//                    System.out.println("Ошибка авторизации: " + response);
//                    return false;
//                }
////                if(responseParts[0].equals(CMD_SHUT_DOWN_CLIENT)){
////                    System.out.println("Пришла команда закрытия окна");
////                    Platform.exit();
////                }
//
//                System.out.println(response);
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Ошибка при аутентификации");
//            return false;
//        }
//
//
//    }

//    public boolean registerNewUser(String login, String password, String username) {
//        if (!isConnected) {
//            System.out.println("Клиент не подключен к серверу");
//            return false;
//        }
//
////        sendMessage(REGISTER_NEW_USER + " " + login + " " + password + " " + username);
//        try {
//            out.writeUTF(String.format("%s %s %s %s", REGISTER_NEW_USER, login, password, username));
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Ошибка отправки команды регистрации нового пользователя: " + String.format("%s %s %s %s", REGISTER_NEW_USER, login, password, username));
//            return false;
//        }
//
//
//        try {
//            while (true) {
//                String response = in.readUTF();
//                String[] responseParts = response.split("\\s+", 2);
//                if (responseParts[0].equals(REGISTER_OK)) {
//                    userName = responseParts[1];
//                    System.out.println("Новый пользователь зарегистрировался под именем: " + userName);
//                    return true;
//                }
//                if (responseParts[0].equals(REGISTER_ERR)) {
//                    System.out.println("Ошибка регистрации:  " + response);
//                    return false;
//                }
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Ошибка при регистрации");
//            return false;
//        }
//
//    }


    public void exitClient() throws IOException {

//        readThread.interrupt();

        out.writeUTF(END_CLIENT_CMD_PREFIX);
        out.flush();

        in.close();
        out.close();
        socket.close();
    }

//    public String getUserName() {
//        return userName;
//    }
//    public void setUserName(String userName){
//        this.userName=userName;
//    }


    public boolean isConnected() {
        return isConnected;
    }


    public DataInputStream getInputStream() {
        return in;
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

}
