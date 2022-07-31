package ru.igojig.fxmessenger.controllers.handlers;

import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.service.Network;


import java.io.*;

abstract public class ControllerHandler  {

    public static String username;
    public static int id;

    protected Controller controller;
    protected Network network;
    protected DataInputStream in;
    protected DataOutputStream out;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public ControllerHandler(Controller controller, Network network) {
        this.controller = controller;
        this.network=network;

        in=network.getInputStream();
        out=network.getOutputStream();
//
//        try {
//            objectInputStream=new ObjectInputStream(in);
//            objectOutputStream=new ObjectOutputStream(out);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

}
