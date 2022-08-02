module ru.igojig.fxmesseger {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;


    opens ru.igojig.fxmessenger to javafx.fxml;
    exports ru.igojig.fxmessenger;
    exports ru.igojig.fxmessenger.controllers;
    opens ru.igojig.fxmessenger.controllers to javafx.fxml;
    exports ru.igojig.fxmessenger.controllers.handlers;
    opens ru.igojig.fxmessenger.controllers.handlers to javafx.fxml;
    exports  ru.igojig.fxmessenger.service;

}