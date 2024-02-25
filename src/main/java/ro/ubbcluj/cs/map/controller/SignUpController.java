package ro.ubbcluj.cs.map.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.ValidationException;
import ro.ubbcluj.cs.map.service.Service;

public class SignUpController {

    @FXML
    private TextField numeSignField;
    @FXML
    private TextField prenumeSignField;
    @FXML
    private TextField usernameSignField;
    @FXML
    private TextField passwordSignField;

    private Service<Long> service;
    Stage dialogStage;

    @FXML
    private void initialize() {
    }

    public  void setService(Service<Long> service, Stage stage){
        this.service = service;
        this.dialogStage=stage;
    }
    @FXML
    public void handleSave(){
        Long id=5L;//Long.parseLong(idField.getText());
        String nume=numeSignField.getText();
        String prenume=prenumeSignField.getText();
        String username=usernameSignField.getText();
        String password=passwordSignField.getText();
        Utilizator utilizator1=new Utilizator(nume,prenume,username,password);
        utilizator1.setId(id);
        createUtilizator(utilizator1);//service.ServAdaugaUt(utilizator1);
    }

    private void createUtilizator(Utilizator u)
    {
        try {
            boolean r= this.service.ServAdaugaUt(u);
            if (!r)
                dialogStage.close();
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare mesaj","Ai creat cu succes cont");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }

    }



}
