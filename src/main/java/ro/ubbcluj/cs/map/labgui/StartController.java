package ro.ubbcluj.cs.map.labgui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.ubbcluj.cs.map.controller.*;
import ro.ubbcluj.cs.map.domain.*;
import ro.ubbcluj.cs.map.repository.paging.Page;
import ro.ubbcluj.cs.map.repository.paging.Pageable;
import ro.ubbcluj.cs.map.service.MessageService;
import ro.ubbcluj.cs.map.service.Service;
import ro.ubbcluj.cs.map.utils.events.UtilizatorChangeEvent;
import ro.ubbcluj.cs.map.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StartController implements Observer<UtilizatorChangeEvent> {

    //private BorderPane ;
    Stage myStage;
    Service<Long> service;
    MessageService<Long> serviceMessage;




    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<WRU> modelFriend = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelChat = FXCollections.observableArrayList();


    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableView<WRU> tableView1;

    @FXML
    TableView<Utilizator> tableViewChat;

    @FXML
    TableColumn<Utilizator, Long> tableColumnID,tableColumnIDChat;
    @FXML
    TableColumn<Prietenie, Long> tableColumnIDFriend;

    @FXML
    TableColumn<Utilizator, String> tableColumnNume,tableColumnNumeChat;
    @FXML
    TableColumn<Prietenie, String> tableColumnNumeFriend;


    @FXML
    TableColumn<Utilizator, String> tableColumnPrenume,tableColumnPrenumeChat;
    @FXML
    TableColumn<WRU, String> tableColumnStatusFriend;


    @FXML
    private Button btnAdd, btnUpdate, btnDelete, btnUsers, btnFriends, btnUsers1, btnFriends1;

    @FXML
    private Button btnChat,btnChat1,btnUsers11,btnFriends11;

    @FXML
    BorderPane userBorderPane,startApp;
    @FXML
    BorderPane friendshipBorderPane,chatBorderPane;

    @FXML
    Label labelFriendNume,labelFriendPrenume,labelChatNume,labelChatPrenume,labelUserNume,labelUserPrenume;

    @FXML
    Button previousButton;

    @FXML
    Button nextButton;

    public void setController(Service<Long> service, Stage primaryStage,MessageService<Long> serviceMessage) {
        this.service = service;
        service.addObserver(this);
        initModel();
        myStage = primaryStage;
        this.serviceMessage=serviceMessage;
        //initialize();

    }
    @FXML
    public void signUpWindow() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("signup-view.fxml"));//"add-view.fxml"


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sign up");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/Edit.png")));
            dialogStage.getIcons().add(icon);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SignUpController editMessageViewController = loader.getController();
            editMessageViewController.setService(service, dialogStage);

            dialogStage.show();
            initModel();
            initialize();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private TextField usernameField,passwordField;
    @FXML
    public void loginClick(){

        String username=usernameField.getText();
        String password=passwordField.getText();

        Utilizator u= service.connect(username,password);

        if (u!=null)
        {
            startApp.setVisible(false);
            userBorderPane.setVisible(true);
            selected = u;
            labelUserNume.setText(u.getFirstName());
            labelUserPrenume.setText(u.getLastName());
        }
        else
        {
            MessageAlert.showErrorMessage(null, "Username sau parola gresita!");
        }
    }
    @FXML
    public void logoutClick(){
        startApp.setVisible(true);
        userBorderPane.setVisible(false);
        usernameField.setText("");
        passwordField.setText("");
    }



    public void initialize() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
    }

    public void initializeChat(){
        tableViewChat.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        labelChatNume.setText(selected.getFirstName());
        labelChatPrenume.setText(selected.getLastName());
        tableColumnIDChat.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnNumeChat.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnPrenumeChat.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewChat.setItems(modelChat);
    }
    public void initializeFriend() {
        labelFriendNume.setText(selected.getFirstName());
        labelFriendPrenume.setText(selected.getLastName());
        tableColumnIDFriend.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnNumeFriend.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnStatusFriend.setCellValueFactory(new PropertyValueFactory<>("status"));


        tableColumnStatusFriend.setCellFactory(tv -> new TableCell<WRU,String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    // Dacă celula este goală sau obiectul Friend este null, nu face nimic
                    setStyle("");
                } else {
                    // Verifică statusul și setează culoarea de fundal corespunzătoare
                    if ("ACCEPTED".equals(status)) {
                        setStyle("-fx-background-color: lightgreen;");
                    } else if ("REJECTED".equals(status)) {
                        setStyle("-fx-background-color: #f5566e;");
                    } else if ("PENDING".equals(status)){
                        setStyle("-fx-background-color: #ddb78a;");
                    } else if ("You have Request".equals(status))
                    {
                        setStyle("-fx-background-color: #ddb78a;");
                    }
                    setText(status);
                }
            }
        });

        tableView1.setItems(modelFriend);
    }
    private void initModelFriend(Long id) {
        Iterable<WRU> prietenii = service.verifyAll(id);
        List<WRU> messageTaskList = StreamSupport.stream(prietenii.spliterator(), false)
                .collect(Collectors.toList());
        modelFriend.clear();
        modelFriend.setAll(messageTaskList);
    }

    private void initModelChat(Long id) {
        Iterable<Utilizator> prietenii = service.GetFriends(id);
        List<Utilizator> messageTaskList = StreamSupport.stream(prietenii.spliterator(), false)
                .collect(Collectors.toList());
        modelChat.clear();
        modelChat.setAll(messageTaskList);
    }


    private void initModel() {
        Page<Utilizator> usersOnCurrentPage = service.getUsersOnPage(new Pageable(currentPage, numberOfRecordsPerPage));
        totalNumberOfElements = usersOnCurrentPage.getTotalNumberOfElements();
        List<Utilizator> userList = StreamSupport.stream(usersOnCurrentPage.getElementsOnPage().spliterator(), false).toList();
        model.setAll(userList);
        handlePageNavigationChecks();
        /*Iterable<Utilizator> messages = service.ServGetUt();
        List<Utilizator> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.clear();
        model.setAll(messageTaskList);*/
    }


    @FXML
    void handleAddFriend()
    {
        WRU selected2=tableView1.getSelectionModel().getSelectedItem();
        tableView1.getSelectionModel().clearSelection();
        if (selected2!=null)
        {
            if (selected2.getStatus().equals("PENDING")) {
                MessageAlert.showErrorMessage(null, "Deja exista o cerere pentru acesta!");
                return;
            }
            if (selected2.getStatus().equals("ACCEPTED")) {
                MessageAlert.showErrorMessage(null, "Deja esti prieten cu aceasta persoana!");
                return;
            }
            if (selected2.getStatus().equals("You have Request")) {
                MessageAlert.showErrorMessage(null, "Ai o cerere deja de la aceasta persoana!");
                return;
            }
            service.AddFriend(selected.getId(),selected2.getId());
            initModelFriend(selected.getId());
            initializeFriend();/////////
        }
        else MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
    }

    @FXML
    void handleDeleteFriend()
    {
        WRU selected2=tableView1.getSelectionModel().getSelectedItem();
        tableView1.getSelectionModel().clearSelection();
        if(selected2==null) {
            MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
            return;
        }
        if (selected2.getStatus().equals("REJECTED"))
        {
            MessageAlert.showErrorMessage(null, "Nu esti prieten cu aceasta persoana!");
            return;
        }
        if (selected2.getStatus().equals("You have Request"))
        {
            MessageAlert.showErrorMessage(null, "Nu poti sa stergi pentru ca nu esti inca prieten!");
            return;
        }
        if (selected2.getStatus().equals("PENDING"))
        {
            MessageAlert.showErrorMessage(null, "Inca nu ai primit raspuns!");
            return;
        }
        service.DeleteFriend(selected.getId(),selected2.getId());
        initModelFriend(selected.getId());
        initializeFriend();
        tableView1.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAcceptFriend()
    {
        WRU selected2=tableView1.getSelectionModel().getSelectedItem();
        tableView1.getSelectionModel().clearSelection();
        if(selected2==null)
            return;
        if (selected2.getStatus().equals("REJECTED"))
            return;
        if (selected2.getStatus().equals("PENDING"))
            return;
        if(selected2.getStatus().equals("ACCEPTED"))
            return;
        service.AddFriend(selected.getId(),selected2.getId());
        initModelFriend(selected.getId());
        initializeFriend();
        tableView1.getSelectionModel().clearSelection();
    }

    @FXML
    void handleDeclineFriend()
    {
        WRU selected2=tableView1.getSelectionModel().getSelectedItem();
        tableView1.getSelectionModel().clearSelection();
        if(selected2==null)
            return;
        if (selected2.getStatus().equals("REJECTED"))
            return;
        if (selected2.getStatus().equals("ACCEPTED"))
            return;
        service.DeleteFriend(selected.getId(),selected2.getId());
        initModelFriend(selected.getId());
        initializeFriend();
        tableView1.getSelectionModel().clearSelection();
    }


    private Utilizator selected;
    @FXML
    void handleAddUser(ActionEvent event) {
        if (event.getSource() == btnAdd) {
            //showDialog("add-view");
            showUtilizatorAdd(null, "add-view.fxml");
        }
        if (event.getSource() == btnDelete) {
            showDialog("update-view");
        }
        if (event.getSource() == btnUsers1 || event.getSource()==btnUsers11) {//event.getSource() == btnUsers ||
            chatBorderPane.setVisible(false);
            tableView.getSelectionModel().clearSelection();
            userBorderPane.setVisible(true);
            friendshipBorderPane.setVisible(false);
        }
        if (event.getSource() == btnFriends || event.getSource()==btnFriends11) {//|| event.getSource() == btnFriends1
            chatBorderPane.setVisible(false);
            if (userBorderPane.isVisible())
            {
                /*selected = tableView.getSelectionModel().getSelectedItem();
                if (selected==null)
                {
                    MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
                    return;
                }*/

            }
            Long r= selected.getId();
            userBorderPane.setVisible(false);
            friendshipBorderPane.setVisible(true);
            initModelFriend(r);
            initializeFriend();
        }
        if (event.getSource()==btnChat || event.getSource()==btnChat1)
        {
            if (userBorderPane.isVisible()) {
               /* selected = tableView.getSelectionModel().getSelectedItem();
                if (selected==null)
                {
                    MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
                    return;
                }*/
                userBorderPane.setVisible(false);
            }
            if(friendshipBorderPane.isVisible())
                friendshipBorderPane.setVisible(false);

            Long r= selected.getId();
            initModelChat(r);
            initializeChat();
            chatBorderPane.setVisible(true);
        }

    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        Utilizator selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean r = service.DeleteUser(selected.getId());
            if (r)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Utilizatorul a fost sters cu succes!");
        } else MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
    }

    @FXML
    void handleUpdateUser(ActionEvent event) {
        Utilizator utilizator = tableView.getSelectionModel().getSelectedItem();
        if (utilizator != null && event.getSource() == btnUpdate) {
            showUtilizatorAdd(utilizator, "update-view.fxml");
        } else MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
    }

    private void showDialog(String fxml) {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml + ".fxml")));
            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showUtilizatorAdd(Utilizator utilizator, String name) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(name));//"add-view.fxml"


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/Edit.png")));
            dialogStage.getIcons().add(icon);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddController editMessageViewController = loader.getController();
            editMessageViewController.setService(service, dialogStage, utilizator);

            dialogStage.show();
            initModel();
            initialize();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNewMessage()
    {
        try {
            List<Utilizator> selectedItems = tableViewChat.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty())
            {
                MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
                return;
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("newChat-view.fxml"));//"add-view.fxml"


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Message");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/Edit.png")));
            dialogStage.getIcons().add(icon);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            NewMessageController messageController=loader.getController();
            messageController.setService(serviceMessage,dialogStage,selected,selectedItems);
            //AddController editMessageViewController = loader.getController();
            //editMessageViewController.setService(service, dialogStage,);
            dialogStage.show();

            //dialogStage.show();
            //initModel();
            //initialize();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleMessages()
    {
        try {
            Utilizator utilizator = tableViewChat.getSelectionModel().getSelectedItem();
            if (utilizator==null)
            {
                MessageAlert.showErrorMessage(null, "Nu ati selectat nici un utilizator!");
                return;
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("allMessages-view.fxml"));//"add-view.fxml"


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Message");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/Edit.png")));
            dialogStage.getIcons().add(icon);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AllMessagesController messageController=loader.getController();
            messageController.setService(serviceMessage,dialogStage,selected,utilizator);
            //AddController editMessageViewController = loader.getController();
            //editMessageViewController.setService(service, dialogStage,);
            dialogStage.show();

            //dialogStage.show();
            //initModel();
            //initialize();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private TextField numberUsers;
    private int currentPage=0;
    private int numberOfRecordsPerPage = 5;
    private int totalNumberOfElements;
    private void handlePageNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*numberOfRecordsPerPage >= totalNumberOfElements);
    }
    public void goToNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void goToPreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }
    public void handlePageNumber(ActionEvent actionEvent){
            String text=numberUsers.getText();
        if (isNumeric(text) && Integer.parseInt(text) > 0) {
            // Valid number
            currentPage=0;
            numberOfRecordsPerPage=Integer.parseInt(text);
            initModel();
        }
    }
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+");
    }



    @Override
    public void update(UtilizatorChangeEvent utilizatorChangeEvent) {
        initModel();
    }
}