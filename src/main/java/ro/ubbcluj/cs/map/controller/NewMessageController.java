package ro.ubbcluj.cs.map.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ubbcluj.cs.map.domain.Message;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.ValidationException;
import ro.ubbcluj.cs.map.service.MessageService;
import ro.ubbcluj.cs.map.service.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NewMessageController {

    private MessageService<Long> service;

    Stage dialogStage;

    Utilizator utilizator;
    List<Utilizator> listUtilizator;

    @FXML
    TextField fromField;

    @FXML
    private TextArea textArea;

    public  void setService(MessageService<Long> service, Stage stage, Utilizator utilizator,List<Utilizator> listUtilizator){
        this.service = service;
        this.dialogStage=stage;
        this.utilizator=utilizator;
        this.listUtilizator=listUtilizator;
        fromField.setEditable(false);
        //fromField.setText(utilizator.getId().toString());
        setFields(listUtilizator);
    }
    private void setFields(List<Utilizator> myList) {
        String concatenatedNames = myList.stream()
                .map(Utilizator::getFirstName)
                .collect(Collectors.joining(", "));
        fromField.setText(concatenatedNames);
    }

    @FXML
    public void handleSaveMessage(){

        String mesaj=textArea.getText();
        if (mesaj.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Nu ati scris nici un mesaj!");
            return;
        }
        Message message=new Message(mesaj,utilizator,listUtilizator, LocalDateTime.now());

        try {
            boolean r= service.addMessage(message);
            if (!r)
                dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare mesaj","Mesajul a fost trimis");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }

        dialogStage.close();
    }
}
