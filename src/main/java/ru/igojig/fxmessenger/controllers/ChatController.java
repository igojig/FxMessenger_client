package ru.igojig.fxmessenger.controllers;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.FxMessengerClient;
import ru.igojig.fxmessenger.controllers.handlers.ChatControllerHandler;
import ru.igojig.fxmessenger.exchanger.UserChangeMode;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserListExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.util.List;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.CHANGE_USERNAME_REQUEST;

public class ChatController implements Controller {

    private static final Logger logger = LogManager.getLogger(ChatController.class);

    private final String githubUrl = "www.github.com/igojig";

    @FXML
    public Label lblClientName;
    @FXML
    public Label lblId;

    @FXML
    public Hyperlink menuHyperlink;

    @FXML
    private Button btnSendMessage;

    @FXML
    private ListView<User> lstUsers;

    @FXML
    private TextField txtMessage;

    @FXML
    private TextArea txtAreaMessages;

    @FXML
    private Label lblUserCont;

    @FXML
    private Label lblMessageCount;

    private User selectedRecipient;

    private ChatControllerHandler<ChatController> chatControllerHandler;
    private FxMessengerClient fxMessengerClient;

    @FXML
    public void initialize() {

        updateUserCount();
        updateMessageCount();

        Platform.runLater(() -> txtMessage.requestFocus());

        lstUsers.setCellFactory(lv -> {
            MultipleSelectionModel<User> selectionModel = lstUsers.getSelectionModel();
            ListCell<User> cell = new ListCell<>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(String.format("%s, id=%s", item.getUsername(), item.getId()));
                    }
                }
            };

            ContextMenu contextMenu = new ContextMenu();

            MenuItem menuItemPrivateMsg = new MenuItem();
            menuItemPrivateMsg.textProperty().bind(Bindings.format("Send message to: \"%s\"", cell.textProperty()));
            menuItemPrivateMsg.setOnAction(event -> {
                User sendToUser = cell.getItem();
                // code to edit item...
                sendPrivateMessageFromContextMenu(sendToUser, txtMessage.getText());
            });

            MenuItem menuItemSendToAll = new MenuItem();
            menuItemSendToAll.textProperty().bind(Bindings.format("Send message to all"));
            menuItemSendToAll.setOnAction(event -> {
                User sendToUser = cell.getItem();
                // code to edit item...
                sendMessageToAllFromContextMenu(txtMessage.getText());
            });

            contextMenu.getItems().addAll(menuItemPrivateMsg, menuItemSendToAll);

            cell.textProperty().bindBidirectional(cell.idProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
//                lstUsers.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
    }

    @FXML
    public void onChangeUserName(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");

        dialog.setTitle("Пользователь: ["
                + chatControllerHandler.getNetwork().getUser().getUsername()
                + "]"
                + "; id=["
                + chatControllerHandler.getNetwork().getUser().getId()
                + "]"
        );
        dialog.setHeaderText(String.format("Текущее имя: [%s]", chatControllerHandler.getNetwork().getUser().getUsername()));
        dialog.setContentText("Введите новое имя:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUsername = result.get().strip();
            if (newUsername.isEmpty()) {
                return;
            }
            logger.debug("Запрос на изменение имени: " + newUsername);
            chatControllerHandler.sendMessage(CHANGE_USERNAME_REQUEST, "изменение имени",
                    new UserExchanger(new User(null, newUsername, null, null)));
        }
    }

    public void onSendAction(ActionEvent actionEvent) {

        String message = txtMessage.getText().strip();
        if (!message.isEmpty()) {
            if (selectedRecipient != null) {
                // себе приватное сообщение не посылаем
                if (!selectedRecipient.getId().equals(chatControllerHandler.getNetwork().getUser().getId())) {
                    chatControllerHandler.sendMessage(message, selectedRecipient);
                }
            } else {
                chatControllerHandler.sendMessage(message);
            }
        }
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    public void appendMessage(String message) {
        if (!message.isBlank()) {
            txtAreaMessages.appendText(message + '\n');
            updateMessageCount();
        }
    }

    private void updateMessageCount() {
        int size = txtAreaMessages.getParagraphs().size() - 1;
        lblMessageCount.setText("Сообщений: " + size);
    }

    public void updateClientName(User user) {
        lblClientName.setText("User: [" + user.getUsername() + "]");
    }

    public void menuFileCloseAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void menuHelpAboutAction(ActionEvent actionEvent) {
        final String about = """
                Привет!
                Меня зовут Игорь.
                Я начинающий Java-разработчик.
                Это мой первый учебный проект.
                """;

        Font font=new Font(16);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        Label label = new Label(about);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(font);

        Hyperlink hyperlink = new Hyperlink(githubUrl);
        hyperlink.setAlignment(Pos.CENTER);
        hyperlink.setTextAlignment(TextAlignment.CENTER);
        hyperlink.setFont(font);
        hyperlink.setBorder(Border.EMPTY);
        hyperlink.setPadding(new Insets(4, 0, 4, 0));

        Alert alert = new Alert(Alert.AlertType.NONE, about, ButtonType.OK);

        hyperlink.setOnAction(event -> {
            HostServices hostServices = fxMessengerClient.getHostServices();
            hostServices.showDocument(githubUrl);
        });

        vBox.getChildren().add(label);
        vBox.getChildren().add(hyperlink);

        alert.getDialogPane().setContent(vBox);
        alert.setTitle("About");
        alert.showAndWait();
    }

    public void menuEditDeleteHistoryAction(ActionEvent actionEvent) {
        txtAreaMessages.clear();
        updateMessageCount();
    }

    public void updateUserCount() {
        lblUserCont.setText("Участников: " + lstUsers.getItems().size());
    }

    public void updateUserList(UserListExchanger userListExchanger) {
        lstUsers.getItems().clear();

        UserChangeMode userChangeMode = userListExchanger.getUserChangeMode();

        if (userChangeMode == UserChangeMode.ADD) {
            appendMessage("Подключился: " + userListExchanger.getChangedUser().getUsername());
        }
        if (userChangeMode == UserChangeMode.REMOVE) {
            appendMessage("Отключился: " + userListExchanger.getChangedUser().getUsername());
        }
        userListExchanger.getUserList().forEach((u -> lstUsers.getItems().add(u)));

        lstUsers.refresh();
        updateUserCount();
    }

    public void setNetwork(Network network) {
        chatControllerHandler = new ChatControllerHandler<>(this, network);
    }

    public void sendPrivateMessageFromContextMenu(User sendToUser, String message) {
        if (!message.isBlank()) {
            chatControllerHandler.sendMessage(message, sendToUser);
        }
    }

    public void sendMessageToAllFromContextMenu(String message) {
        if (!message.isBlank()) {
            chatControllerHandler.sendMessage(message);
        }
    }

    public void setUserHistory(List<String> historyList) {
        for (String s : historyList) {
            if (!s.isBlank()) {
                txtAreaMessages.appendText(s);
                txtAreaMessages.appendText("\n");
            }
        }
        updateMessageCount();
    }

    public void saveHistory() {
        chatControllerHandler.saveHistory(txtAreaMessages.getParagraphs());
    }

    public void subscribe() {
        chatControllerHandler.subscribe();
    }

    public void requestLoggedUsers() {
        chatControllerHandler.requestLoggedUsers();
    }

    public void requestHistory(User user) {
        chatControllerHandler.requestUserHistory(user);
    }

    @FXML
    public void onMenuHyperlink(ActionEvent actionEvent) {
        HostServices hostServices = fxMessengerClient.getHostServices();
        hostServices.showDocument(githubUrl);
    }

    public void setFxMessengerClient(FxMessengerClient fxMessengerClient) {
        this.fxMessengerClient = fxMessengerClient;
    }
}