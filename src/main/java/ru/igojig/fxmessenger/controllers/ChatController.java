package ru.igojig.fxmessenger.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import ru.igojig.fxmessenger.controllers.handlers.ChatControllerHandler;
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

    private Network network;

    private User selectedRecipient;

    ChatControllerHandler chatControllerHandler;


    @FXML
    public void initialize() {
//        lstUsers.getItems().addAll("User1", "User2", "User3", "User4");


        updateUserCount();
        updateMessageCount();

        //перевод фокуса на наше поле
        Platform.runLater(() -> txtMessage.requestFocus());


        lstUsers.setCellFactory(lv -> {
            MultipleSelectionModel<User> selectionModel = lstUsers.getSelectionModel();
            ListCell<User> cell = new ListCell<>(){
                @Override
                protected void updateItem(User item, boolean empty) {
//                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.getUsername());
                    }
                }
            };
            //TODO !!!!!
//            cell.textProperty().bind(cell.itemProperty().);
            cell.textProperty().bind(cell.itemProperty().asString());
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
            return cell;
        });

//        chatControllerHandler = new ChatControllerHandler(this, network);

    }

    @FXML
    public void onChangeUserName(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Пользователь: " + username + " id=" + id);
        dialog.setHeaderText("Введите новое имя пользователя");
        dialog.setContentText("Enter new Username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUsername = result.get().strip();
            if (newUsername.isEmpty()) {
                return;
            }
            System.out.println("Запрос на изменение имени: " + newUsername);
            chatControllerHandler.sendServiceMessage(CHANGE_USERNAME_REQUEST, newUsername);
        }
    }


    public void onSendAction(ActionEvent actionEvent) {

        String message = txtMessage.getText().strip();
        if (!message.isEmpty()) {

//        appendMessage(text);
            if (selectedRecipient != null) {
                chatControllerHandler.sendPrivateMessage(message, selectedRecipient);
//            netWork.sendPrivateMessage(message, selectedRecipient);
            } else {
                chatControllerHandler.sendMessage(message);
//            netWork.sendMessage(message);
            }
        }
        txtMessage.clear();
        txtMessage.requestFocus();

    }


    public void appendMessage(String text) {
        if (!text.isBlank()) {
            txtAreaMessages.appendText(text + '\n');
            updateMessageCount();
        }
    }

    private void updateMessageCount() {
        int size = txtAreaMessages.getParagraphs().size() - 1;
        lblMessageCount.setText("Сообщений: " + size);
    }

    public void updateClientName(String name) {
        lblClientName.setText(name);
        lblId.setText("Id=" + id);
        username = name;
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
//    public void setNetwork(Network network) {
//        this.netWork=network;

//    }

    public void updateUserList(String users) {
        String[] arrUsers = users.split("\\s+");
        lstUsers.getItems().clear();


        if (arrUsers[1].startsWith("+")) {
            // если это мы, то не пишем
            if (!username.equals(arrUsers[1].substring(1)))
                appendMessage("Подключился: " + arrUsers[1].substring(1));
        } else {
            appendMessage("Отключился: " + arrUsers[1].substring(1));
        }

        for (int i = 2; i < arrUsers.length; i++) {
            lstUsers.getItems().add(arrUsers[i]);
        }
        lstUsers.refresh();
//        lblUserCont.setText("Участников: " + lstUsers.getItems().size());
        updateUserCount();
    }


    public void setNetwork(Network network) {
        this.network = network;
        chatControllerHandler = new ChatControllerHandler(this, network);
    }

    public void startReadCycle() {
        chatControllerHandler.startReadCycle();
    }

    public void updateUserListFromUpdateUsers(String message) {
        String[] arrUsers = message.split("\\s+");
        lstUsers.getItems().clear();

        for (int i = 1; i < arrUsers.length; i++) {
            lstUsers.getItems().add(arrUsers[i]);
        }
        lstUsers.refresh();
//        lblUserCont.setText("Участников: " + lstUsers.getItems().size());
        updateUserCount();
    }

    public void stop() {
        chatControllerHandler.stopReadCycle();
    }

    public ObservableList<CharSequence> getMsgHistory() {
        return txtAreaMessages.getParagraphs();
    }

    public void setMsgHistory(List<String> stringList){
        for (String s : stringList) {
            if(!s.isBlank()) {
                txtAreaMessages.appendText(s);
                txtAreaMessages.appendText("\n");
            }
        }
        updateMessageCount();
    }
}