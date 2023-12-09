package ru.igojig.fxmessenger.controllers.handlers;

import lombok.Getter;
import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.service.Network;

abstract public class ControllerHandler<T extends Controller> {

    protected T controller;

    @Getter
    protected Network network;

    public ControllerHandler(T controller, Network network) {
        this.controller = controller;
        this.network = network;
    }

    public void requestUserHistory(User user) {
        Exchanger exchanger = new Exchanger(Prefix.CMD_HISTORY_REQUEST, "запрашиваем историю", new UserExchanger(user));
        network.sendMessage(exchanger);
    }

    public abstract void consumeMsg(Exchanger exchanger);

}
