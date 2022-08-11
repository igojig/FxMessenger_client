package ru.igojig.fxmessenger.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.igojig.fxmessenger.FxMessengerClient;
import ru.igojig.fxmessenger.controllers.handlers.ControllerHandler;
import ru.igojig.fxmessenger.controllers.handlers.LoginControllerHandler;
import ru.igojig.fxmessenger.controllers.handlers.RegisterControllerHandler;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.util.Optional;


public class LogInController extends Controller{


    @FXML
    public TextField txtLogin;

    @FXML
    public Label lblErrorText;

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

    private Network network;

    private FxMessengerClient fxMessengerClient;

    LoginControllerHandler loginControllerHandler;
    RegisterControllerHandler registerControllerHandler;




    @FXML
    public void initialize(){
//
//        loginControllerHandler= new LoginControllerHandler(this, network);
//        registerControllerHandler=new RegisterControllerHandler(this, network);
    }



    @FXML
    public void logIn(ActionEvent actionEvent) {


        String login=txtLogin.getText().strip();
        String password=txtPassword.getText().strip();

//        boolean response = network.authorize(this, login, password);
        Optional<User> user =loginControllerHandler.logIn(login, password);
        if(user.isEmpty()){
            System.out.println("Ошибка авторизации ");
        }
        else {
            //открыввем чат
//            Controller.username =username.get();
//            Controller.id=ControllerHandler.id;

            Controller.user=ControllerHandler.user;

            System.out.println("Открываем чат");
            fxMessengerClient.showChat();
        }
    }

//    public void setNetwork(Network network) {
//
//        this.network = network;
//    }

    public void setFxMessengerClient(FxMessengerClient fxMessengerClient) {
        this.fxMessengerClient = fxMessengerClient;
    }

    public void exitClient(){
        fxMessengerClient.shutDown();
    }


    @FXML
    public void onBtnRegister(ActionEvent actionEvent) {
        String login=txtRegisterLogin.getText().strip();
        String password=txtRegisterPassword.getText().strip();
        String username=txtRegisterUsername.getText().strip();

        if(login.isEmpty() || password.isEmpty() || username.isEmpty()){
            System.out.println("Поля не должны быть пустыми");
            return;
        }

//        boolean result= network.registerNewUser(login, password, username);
        Optional<User> user=registerControllerHandler.register(login, password, username);

        if(user.isEmpty()){
            System.out.println("Ошибка авторизации ");
        }
        else {
//            network.setUserName(user.get());
//            Controller.username =user.get();
//            Controller.id= ControllerHandler.id;

            Controller.user=ControllerHandler.user;

            //открыввем чат
            System.out.println("Открываем чат");
            fxMessengerClient.showChat();
        }


    }

    public void setNetwork(Network network) {
        this.network = network;

        loginControllerHandler= new LoginControllerHandler(this, network);
        registerControllerHandler=new RegisterControllerHandler(this, network);
    }

//    public String getUserName(){
//        return username;
//    }

}
