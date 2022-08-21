package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.service.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

abstract public class ControllerHandler<T extends Controller> {

    // наш User
    public static User user;
//    public static String username;
//    public static int id;


    protected T controller;
    protected Network network;
//    protected DataInputStream in;
//    protected DataOutputStream out;

//    static ObjectInputStream objectInputStream;
//    static ObjectOutputStream objectOutputStream;

    public ControllerHandler(T controller, Network network) {
        this.controller = controller;
        this.network = network;

//        in=network.getInputStream();
//        out=network.getOutputStream();

//        objectInputStream = network.getObjectInputStream();
//        objectOutputStream = network.getObjectOutputStream();


//
//        try {
//            objectInputStream=new ObjectInputStream(in);
//            objectOutputStream=new ObjectOutputStream(out);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    public void requestUserHistory(User user) {
//        if (objectOutputStream == null || objectInputStream == null) {
//            objectInputStream = network.getObjectInputStream();
//            objectOutputStream = network.getObjectOutputStream();
//        }
        Exchanger exchanger = new Exchanger(Prefix.HISTORY_REQUEST, "запрашиваем историю", new UserExchanger(user));
        network.sendServiceMessage(exchanger);

//        Object o = null;
//        while (true) {
//            try {
//                o = objectInputStream.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                return Collections.emptyList();
//            }
//            Exchanger ex = (Exchanger) o;
//            History his = ex.getChatObject(History.class);
//            return his.getHistoryList();
//        }
    }

}
