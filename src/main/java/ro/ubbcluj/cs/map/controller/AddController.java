package ro.ubbcluj.cs.map.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.ValidationException;
import ro.ubbcluj.cs.map.labgui.StartController;
import ro.ubbcluj.cs.map.service.Service;

import java.time.LocalDateTime;

public class AddController {

    @FXML
    private TextField idField;

    @FXML
    private TextField numeField;
    @FXML
    private TextField prenumeField;

    private Service<Long> service;
    Stage dialogStage;
    Utilizator utilizator;

    @FXML
    private void initialize() {
    }

    public  void setService(Service<Long> service, Stage stage, Utilizator u){
        this.service = service;
        this.dialogStage=stage;
        this.utilizator=u;
        idField.setEditable(false);
        if (this.utilizator!=null)
        {
            setFields(u);
            idField.setEditable(false);
        }
    }

    @FXML
    public void handleSave(){
        Long id=5L;//Long.parseLong(idField.getText());
        String nume=numeField.getText();
        String prenume=prenumeField.getText();
        Utilizator utilizator1=new Utilizator(nume,prenume, null,null);
        utilizator1.setId(id);
        saveUtilizator(utilizator1);//service.ServAdaugaUt(utilizator1);
    }

    private void saveUtilizator(Utilizator u)
    {
        try {
            boolean r= this.service.ServAdaugaUt(u);
            if (!r)
                dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare mesaj","Utilizatorul a fost adaugat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }
    private void setFields(Utilizator u)
    {
        idField.setText(u.getId().toString());
        numeField.setText(u.getFirstName());
        prenumeField.setText(u.getLastName());
    }


    @FXML
    public void handleUpdate(){
        Long id=Long.parseLong(idField.getText());
        String nume=numeField.getText();
        String prenume=prenumeField.getText();
        Utilizator utilizator1=new Utilizator(nume,prenume,null,null);
        utilizator1.setId(id);
        handleUpdate(utilizator1);//service.ServAdaugaUt(utilizator1);
    }
    public void handleUpdate(Utilizator u)
    {
        try {
            boolean r= this.service.updateUser(u);
            if (r)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare utilizator","Utilizatorul a fost modificat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }
}
