package ru.igojig.fxmessenger.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.FxMessengerClient;
import ru.igojig.fxmessenger.controllers.handlers.LoginControllerHandler;
import ru.igojig.fxmessenger.service.Network;


public class LogInController extends Controller {

    private static final Logger logger= LogManager.getLogger(LogInController.class);

    @FXML
    public TextField txtLogin;

    @FXML
    public Button btnLogIn;

    @FXML
    public TextField txtPassword;
    @FXML
    public Button btnRegister;
    @FXML
    public TextField txtRegisterLogin;
    @FXML
    public TextField txtRegisterPassword;
    @FXML
    public TextField txtRegisterUsername;
    @FXML
    public Tab tabLogin;

    @FXML
    public Tab tabRegister;

    @Getter
    private FxMessengerClient fxMessengerClient;

    private LoginControllerHandler<LogInController> loginControllerHandler;

    @FXML
    public void initialize() {
    }

    @FXML
    public void logIn(ActionEvent actionEvent) {
        String login = txtLogin.getText().strip();
        String password = txtPassword.getText().strip();
        loginControllerHandler.logIn(login, password);
    }


    public void setFxMessengerClient(FxMessengerClient fxMessengerClient) {
        this.fxMessengerClient = fxMessengerClient;
    }

    public void showChat() {
        logger.info("Открываем чат");
        fxMessengerClient.showChat();
    }

    @FXML
    public void onBtnRegister(ActionEvent actionEvent) {
        String login = txtRegisterLogin.getText().strip();
        String password = txtRegisterPassword.getText().strip();
        String username = txtRegisterUsername.getText().strip();

        if (login.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Alert alert=new Alert(Alert.AlertType.WARNING, "Поля должны быть заполнены", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        loginControllerHandler.register(login, password, username);
    }

    public void setNetwork(Network network) {
        loginControllerHandler = new LoginControllerHandler<>(this, network);
    }

    public void subscribe() {
        loginControllerHandler.subscribe();
    }

    public void unsubscribe() {
        loginControllerHandler.unsubscribe();
    }

    public void onRegister(Event event) {
        TabPane tabPane = tabLogin.getTabPane();
        if(tabPane!=null){
            Stage primStage = (Stage) tabPane.getScene().getWindow();
            primStage.setTitle("Регистрация");
        }
    }

    public void onLogin(Event event) {
        TabPane tabPane = tabLogin.getTabPane();
        if(tabPane!=null){
            Stage primStage = (Stage) tabPane.getScene().getWindow();
            primStage.setTitle("Вход");
        }
    }
}
