package ro.ubbcluj.cs.map.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.cs.map.controller.MessageAlert;
import ro.ubbcluj.cs.map.domain.Message;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.ValidationException;
import ro.ubbcluj.cs.map.service.MessageService;
import ro.ubbcluj.cs.map.utils.events.MessageChangeEvent;
import ro.ubbcluj.cs.map.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllMessagesController implements Observer<MessageChangeEvent> {

    private MessageService<Long> service;
    private Stage dialogStage;
    private Utilizator myUser, toUser;

    private Message replyToThisMsg, myMessage;

    private final List<Utilizator> sendToList = new ArrayList<>();

    @FXML
    private ScrollPane myScrollPane;

    @FXML
    private TextArea messageArea;

    @FXML
    private TextField replyID;
    ArrayList<Message> messages = new ArrayList<>();

    public void init() {
        messages = service.findMessages(myUser.getId(), toUser.getId());
        VBox messageContainer = new VBox();
        messageContainer.setPadding(new Insets(10));
        messageContainer.setPrefWidth(355);
        messageContainer.setPrefHeight(100);

        for (Message message : messages) {
            Text messageText = createMessageText(message);
            VBox container = new VBox(messageText);
            container.setAlignment(myUser.getId().equals(message.getFrom().getId()) ? Pos.BASELINE_RIGHT : Pos.BASELINE_LEFT);
            messageContainer.getChildren().add(container);
//            Text messageText = createMessageText(message);
//            messageContainer.getChildren().add(messageText);
        }

        messageArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSend();
            }
        });


        myScrollPane.setContent(messageContainer);
        myScrollPane.setVvalue(1.0);
    }

    @FXML
    private void initialize() {
//        u2.setId(4L);
//        List<Utilizator> lst = new ArrayList<>();
//        List<Utilizator> lst2 = new ArrayList<>();
//        lst2.add(u1);
//        lst.add(u2);
//        u1.setId(3L);
//        List<Message> messages = Arrays.asList(
//                new Message("Hello, this is my messageeeeeeeeeeee.", u1,lst, LocalDateTime.now()),
//                new Message("Hi there! This is another user's message.", u2,lst2, LocalDateTime.now()),
//                new Message("Message with avatarrrrrrrrrrrrrrrrrrrrr.", u1,lst, LocalDateTime.now()),
//                new Message("Another message with avatar.", u2,lst2, LocalDateTime.now())
//        );

//        TextFlow messageContainer = new TextFlow();
//        messageContainer.setPadding(new Insets(10));
//        VBox messageContainer = new VBox();
//        messageContainer.setPadding(new Insets(10));
//        messageContainer.setPrefWidth(385);
//        messageContainer.setPrefHeight(300);
//
//        for (Message message : messages) {
//            Text messageText = createMessageText(message);
//            VBox container = new VBox(messageText);
//            container.setAlignment(u1.getId().equals(message.getFrom().getId()) ? Pos.BASELINE_RIGHT : Pos.BASELINE_LEFT);
//            messageContainer.getChildren().add(container);
////            Text messageText = createMessageText(message);
////            messageContainer.getChildren().add(messageText);
//        }
//
//        myScrollPane.setContent(messageContainer);
    }

    private Text createMessageText(Message message) {
        Text messageText;
        if (message.getReplyTo() != 0) {
            messageText = new Text(
                    message.getId() + "--" +
                            message.getFrom().getFirstName() + " replied to " +
                            message.getReplyTo()  + "--" +
                            message.getMessage() + "\n"
            );
        } else {
            messageText = new Text(message.getId() + "--" + message.getFrom().getFirstName() + ": " + message.getMessage() + "\n");
        }
        if (myUser.getId().equals(message.getFrom().getId())) {
            messageText.setStyle("-fx-fill: blue; -fx-font-weight: bold; ");
        } else {
            messageText.setStyle("-fx-fill: green; -fx-font-weight: bold; ");
        }

        return messageText;
    }

    public void setService(MessageService<Long> serv, Stage dialogStage, Utilizator myUser, Utilizator toUser) {
        this.service = serv;
        this.service.addObserver(this);
        this.dialogStage = dialogStage;
        this.myUser = myUser;
        this.toUser = toUser;
        this.sendToList.add(toUser);
        init();
    }

    @FXML
    public void handleSend() {
        String msg = messageArea.getText();
        messageArea.clear();
        Long id;
        myMessage = new Message(msg, myUser, sendToList, LocalDateTime.now());
        if (!replyID.getText().isEmpty()) {
            id = Long.parseLong(replyID.getText());
            replyToThisMsg = service.FindByID(id);
            replyID.clear();
            if (replyToThisMsg == null) {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare!", "Mesajul cu acest ID nu exista!");
                return;
            }
            else {
                myMessage.setReplyTo(id);
            }
        }
        handleSend(myMessage);
    }

    public void handleSend(Message msg) {
        try {
            this.service.addMessage(msg);
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }


    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        init();
    }
}