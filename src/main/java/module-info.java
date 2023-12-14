module ru.igojig.fxmessenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens ru.igojig.fxmessenger to javafx.fxml;
    exports ru.igojig.fxmessenger;
    exports ru.igojig.fxmessenger.controllers;
    opens ru.igojig.fxmessenger.controllers to javafx.fxml;
    exports ru.igojig.fxmessenger.controllers.handlers;
    opens ru.igojig.fxmessenger.controllers.handlers to javafx.fxml;
    exports ru.igojig.fxmessenger.service;
    exports ru.igojig.fxmessenger.model;
    exports ru.igojig.fxmessenger.exchanger.impl;
    exports ru.igojig.fxmessenger.exchanger;
}