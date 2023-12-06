package ru.igojig.fxmessenger.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ru.igojig.fxmessenger.controllers.handlers.ChatControllerHandler;
import ru.igojig.fxmessenger.exchanger.impl.UserListExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.service.Network;

import java.util.List;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class ChatController extends Controller {

    @FXML
    public Label lblClientName;
    @FXML
    public Label lblId;

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
                System.out.println("Private message send");
            });

            MenuItem menuItemSendToAll = new MenuItem();
            menuItemSendToAll.textProperty().bind(Bindings.format("Send message to all"));
            menuItemSendToAll.setOnAction(event -> {
                User sendToUser = cell.getItem();
                // code to edit item...
                sendMessageToAllFromContextMenu(txtMessage.getText());
                System.out.println("Message to all send");
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

        dialog.setTitle("Пользователь: " + chatControllerHandler.getNetwork().getUser().getUsername() + " id=" + chatControllerHandler.getNetwork().getUser().getId());
        dialog.setHeaderText("Введите новое имя пользователя");
        dialog.setContentText("Enter new Username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUsername = result.get().strip();
            if (newUsername.isEmpty()) {
                return;
            }
            System.out.println("Запрос на изменение имени: " + newUsername);
            chatControllerHandler.sendServiceMessage(CHANGE_USERNAME_REQUEST, "изменение имени",
                    new UserExchanger(new User(null, newUsername, null, null)));
        }
    }

    public void onSendAction(ActionEvent actionEvent) {

        String message = txtMessage.getText().strip();
        if (!message.isEmpty()) {
            if (selectedRecipient != null) {
                // себе приватное сообщение не посылаем
                if (!selectedRecipient.getId().equals(chatControllerHandler.getNetwork().getUser().getId())) {
                    chatControllerHandler.sendPrivateMessage(message, selectedRecipient);
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
        lblClientName.setText("User: ["+user.getUsername() + "]");
        lblId.setText("id=" + user.getId());
    }

    public void menuFileCloseAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void menuHelpAboutAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Здесь в дальнейшем будет нормальное окно...", ButtonType.OK);
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

        UserListExchanger.Mode mode = userListExchanger.getMode();

        if (mode == UserListExchanger.Mode.ADD) {
            appendMessage("Подключился: " + userListExchanger.getChangedUser().getUsername());
        }
        if (mode == UserListExchanger.Mode.REMOVE) {
            appendMessage("Отключился: " + userListExchanger.getChangedUser().getUsername());
        }
        if (mode == UserListExchanger.Mode.CHANGE_NAME) {
            //???
        }

        for (User u : userListExchanger.getUserList()) {
            lstUsers.getItems().add(u);
        }

        lstUsers.refresh();
        updateUserCount();
    }

    public void setNetwork(Network network) {
        chatControllerHandler = new ChatControllerHandler<>(this, network);
    }

    public void sendPrivateMessageFromContextMenu(User sendToUser, String message) {
        if (!message.isBlank()) {
            chatControllerHandler.sendPrivateMessage(message, sendToUser);
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
}