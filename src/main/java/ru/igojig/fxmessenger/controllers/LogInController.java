package ru.igojig.fxmessenger.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;
import ru.igojig.fxmessenger.FxMessengerClient;
import ru.igojig.fxmessenger.controllers.handlers.LoginControllerHandler;
import ru.igojig.fxmessenger.service.Network;


public class LogInController extends Controller {


    @FXML
    public TextField txtLogin;
//
//    @FXML
//    public Label lblErrorText;

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

    public void exitClient() {
        fxMessengerClient.shutDown();
    }

    public void showChat() {
        System.out.println("Открываем чат");
        fxMessengerClient.showChat();
    }


    @FXML
    public void onBtnRegister(ActionEvent actionEvent) {
        String login = txtRegisterLogin.getText().strip();
        String password = txtRegisterPassword.getText().strip();
        String username = txtRegisterUsername.getText().strip();

        if (login.isEmpty() || password.isEmpty() || username.isEmpty()) {
            System.out.println("Поля не должны быть пустыми");
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
}
