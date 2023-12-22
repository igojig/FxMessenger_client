package ru.igojig.fxmessenger;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.controllers.ChatController;
import ru.igojig.fxmessenger.controllers.Controller;
import ru.igojig.fxmessenger.controllers.LogInController;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;


import java.io.IOException;

public class FxMessengerClient extends Application {

    private static final Logger logger= LogManager.getLogger(FxMessengerClient.class);
    static final String SERVER_NAME = "localhost";
    static final int SERVER_PORT = 8186;

    private Network network;

    private Stage chatStage;

    private Scene sceneChat;
    private Scene sceneLogIn;
    private ChatController chatController;
    private LogInController logInController;

    @Override
    public void start(Stage stage) throws IOException {
        network = new Network();
        network.connect();

        chatStage = stage;
        FXMLLoader fxmlLoaderChat = new FXMLLoader(FxMessengerClient.class.getResource("main_view_client.fxml"));
        sceneChat = new Scene(fxmlLoaderChat.load(), 800, 200);
        chatController = fxmlLoaderChat.getController();
        chatController.setNetwork(network);

        stage.setTitle("Fx Messenger-Client");

        FXMLLoader fxmlLoaderLogIn = new FXMLLoader(FxMessengerClient.class.getResource("login.fxml"));
        sceneLogIn = new Scene(fxmlLoaderLogIn.load(), 600, 200);
        logInController = fxmlLoaderLogIn.getController();
        logInController.setNetwork(network);

        stage.setTitle("Вход");
        stage.setScene(sceneLogIn);
        logInController.subscribe();
        stage.show();

        logInController.setFxMessengerClient(this);
        chatController.setFxMessengerClient(this);
    }

    public void showChat() {
        chatStage.setScene(sceneChat);
        chatStage.setTitle("Наш чат");
        chatController.updateClientName(network.getUser());
        chatController.subscribe();
        logInController.unsubscribe();
        chatController.requestLoggedUsers();
        chatController.requestHistory(network.getUser());
        chatStage.show();
    }

    @Override
    public void stop() {
        if (network.getUser() != null) {
            chatController.saveHistory();
        }
        network.exitClient();
        logger.info("Exit JavaFX");
        Platform.exit();
    }

    public void shutDown() {
        chatStage.fireEvent(new WindowEvent(chatStage.getOwner(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public static void main(String[] args) {
        launch();
    }
}