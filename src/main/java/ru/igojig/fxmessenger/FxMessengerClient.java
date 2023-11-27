package ru.igojig.fxmessenger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.igojig.fxmessenger.controllers.ChatController;
import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.controllers.LogInController;
import ru.igojig.fxmessenger.service.Network;


import java.io.IOException;

public class FxMessengerClient extends Application {
    static final String SERVER_NAME = "localhost";
    static final int SERVER_PORT = 8186;

    private Network network;

    private Stage chatStage;

    private Scene sceneChat;
    private Scene sceneLogIn;
    private ChatController chatController;
    private LogInController LogInOrRegisterController;


    @Override
    public void start(Stage stage) throws IOException {
        network = new Network();
        network.connect();

        chatStage = stage;
        FXMLLoader fxmlLoaderChat = new FXMLLoader(FxMessengerClient.class.getResource("main_view_client.fxml"));
        sceneChat = new Scene(fxmlLoaderChat.load(), 800, 600);
        chatController = fxmlLoaderChat.getController();
        chatController.setNetwork(network);

        stage.setTitle("Fx Messenger-Client");
//        stage.setScene(sceneChat);

        FXMLLoader fxmlLoaderLogIn = new FXMLLoader(FxMessengerClient.class.getResource("login.fxml"));
        sceneLogIn = new Scene(fxmlLoaderLogIn.load(), 645, 266);
        LogInOrRegisterController = fxmlLoaderLogIn.getController();
        LogInOrRegisterController.setNetwork(network);

        stage.setTitle("LogIn");
        stage.setScene(sceneLogIn);
        stage.show();


        LogInOrRegisterController.setFxMessengerClient(this);

//        network.waitMessage(chatController);

//        ServiceClient serviceClient=new ServiceClient(null);
//        LogInClient logInClient=new LogInClient(logInController);
//        MessegingClient messegingClient=new MessegingClient(chatController);
//
//        network.getMainCycle().registerObserver(serviceClient);
//        network.getMainCycle().registerObserver(logInClient);
//        network.getMainCycle().registerObserver(messegingClient);


    }

    public void showChat() {
//        myFile = new MyFile(Controller.user.getId());
//        mainStage.hide();
        chatController.startReadCycle();
        chatStage.setScene(sceneChat);
        chatStage.setTitle("Наш чат");
//        chatController.setMsgHistory(myFile.read());
        chatController.updateClientName(Controller.user);
        chatStage.show();
//        network.waitMessage(chatController);
    }

    @Override
    public void stop() throws Exception {
        //  если никто не авторизовался или зарегистрировался
//        if(Controller.user!=null) {
//            myFile.write(chatController.getMsgHistory());
////            myFile.read();
//        }
        if (Controller.user != null) {
            chatController.saveHistory();
        }


        chatController.stop();
        network.exitClient(Controller.user);
        System.out.println("Exit JavaFX");
        Platform.exit();
    }

    public void shutDown() {
        chatStage.fireEvent(new WindowEvent(chatStage.getOwner(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public static void main(String[] args) {
        launch();
    }
}