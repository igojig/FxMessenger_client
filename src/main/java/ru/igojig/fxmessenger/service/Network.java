package ru.igojig.fxmessenger.service;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;


import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class Network {


    // на сколько засыпаем при ожидании подключения к серверу
    private static final int SLEEP_TIMEOUT = 1000;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8186;

    private final String host;
    private final int port;

    volatile private Socket socket;
//    private DataOutputStream out;
//    private DataInputStream in;

    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    private Thread inMsgThread;
    private Thread outMsgThread;

    // читаем из этой очереди
//    ArrayBlockingQueue<String> receivedMsg=new ArrayBlockingQueue<>(100);

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
    }


    private void waitConnectionWithServer() {
//         ждем пока запуститься сервер
        Thread thread = new Thread(() -> {
            System.out.println("Клиент соединяется с сервером....");
            try {
                while (socket == null) {
                    try {
                        socket = new Socket(host, port);
                    } catch (ConnectException e) {
                        try {
                            Thread.sleep(SLEEP_TIMEOUT);
                            socket=null;
                        } catch (InterruptedException ex) {
                            System.out.println("Ошибка при создании сокета");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                System.out.println("Соединение с сервером установлено");
//                in=new DataInputStream(socket.getInputStream());
//                out=new DataOutputStream(socket.getOutputStream());

                objectOutputStream =new ObjectOutputStream(socket.getOutputStream());
                objectInputStream =new ObjectInputStream(socket.getInputStream());
                isConnected = true;



            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Соединение с сервером не установлено");
            }
        });
        thread.setDaemon(true);
        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            socket = new Socket(host, port);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        try {
////            out = new DataOutputStream(socket.getOutputStream());
////            in = new DataInputStream(socket.getInputStream());
//
//                ous = new ObjectOutputStream(socket.getOutputStream());
//                ois = new ObjectInputStream(socket.getInputStream());
//
//        } catch (Throwable e) {
//            System.out.println("Ошибка");
//            throw new RuntimeException(e);
//        }



    }



    // Сообщение отклиента -> клиенту
    public void sendMessage(String message) {
        try {
            if (isConnected) {
                Exchanger exchanger;
//                if (message.startsWith("/")) {
//                    exchanger=new Exchanger(message, null, null);
//                    objectOutputStream.writeObject(exchanger);
////                    out.writeUTF(String.format("%s", message));
//                } else {
                exchanger = new Exchanger(CLIENT_MSG, message, null);
                objectOutputStream.reset();
                objectOutputStream.writeObject(exchanger);
//                    out.writeUTF(String.format("%s %s", CLIENT_MSG_CMD_PREFIX, message));
//                    out.flush();
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Сообшение от клиента не отправлено");
        }
    }

    public void sendPrivateMessage(String message, User sendToUser) {
        try {
            if (isConnected) {

                Exchanger exchanger = new Exchanger(PRIVATE_MSG, message, new UserExchanger(sendToUser));
                objectOutputStream.reset();
                objectOutputStream.writeObject(exchanger);

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Приватное сообщение от клиента не отправлено");
        }
    }

    // сервисное сообщение
    public void sendServiceMessage(Exchanger exchanger) {
        try {
            if (isConnected) {
                objectOutputStream.reset();
                objectOutputStream.writeObject(exchanger);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Сервисное сообшение от клиента не отправлено");
        }
    }


    public void exitClient(User user) throws IOException {

//        readThread.interrupt();
        Exchanger exchanger = new Exchanger(END_CLIENT, "выходим из чата", new UserExchanger(user));


        objectOutputStream.reset();
        objectOutputStream.writeObject(exchanger);

        objectOutputStream.close();
        objectInputStream.close();

//        in.close();
//        out.close();
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

//
//    public DataInputStream getInputStream() {
//        return in;
//    }

//    public DataOutputStream getOutputStream() {
//        return out;
//    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public <T> T read(){
        try {
            return (T)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readObject() throws IOException, ClassNotFoundException {
            return (T)objectInputStream.readObject();
    }

    public <T>void writeObject(T t) throws IOException {
            objectOutputStream.writeObject(t);
    }

}
