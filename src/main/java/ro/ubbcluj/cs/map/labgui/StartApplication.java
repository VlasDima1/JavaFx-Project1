package ro.ubbcluj.cs.map.labgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubbcluj.cs.map.domain.Message;
import ro.ubbcluj.cs.map.domain.Prietenie;
import ro.ubbcluj.cs.map.domain.Tuple;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.MessageValidator;
import ro.ubbcluj.cs.map.domain.validators.PrietenieValidator;
import ro.ubbcluj.cs.map.domain.validators.UtilizatorValidator;
import ro.ubbcluj.cs.map.repository.*;
import ro.ubbcluj.cs.map.repository.paging.PagingRepository;
import ro.ubbcluj.cs.map.service.MessageService;
import ro.ubbcluj.cs.map.service.Service;

import java.io.IOException;
import java.util.Objects;

public class StartApplication extends Application {

    String url="jdbc:postgresql://localhost:5432/socialnetwork";
    String username = "postgres";
    String password = "12345678";
    Repository<Long, Utilizator> repo;
    Repository<Tuple<Long,Long>, Prietenie> repo2;

    Repository<Long,Message> repoMessage;

    Repository<Long, Message> repoChat;
    Service<Long> srv;
    MessageService<Long> srvMessage;

    private PagingRepository<Long, Utilizator> userRepo;


    public static void main(String[] args) {
        //System.out.println("e bine");
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        repo=new UserDBRepository(url,username,password,new UtilizatorValidator());
        repo2=new FriendDBRepository(url,username,password,new PrietenieValidator());

        repoChat=new MessageDBRepository(url,username,password,new MessageValidator());

        userRepo=new UserPageDBRepository(url,username,password,new UtilizatorValidator());

        srv=new Service<>(repo,repo2,userRepo);

        repoMessage=new MessageDBRepository(url,username,password,new MessageValidator());

        srvMessage=new MessageService<>(repoMessage);




        initView(primaryStage);
        primaryStage.setTitle("SocialNetworkApp");
        Image icon=new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/s.png")));
        primaryStage.getIcons().add(icon);


        primaryStage.setWidth(850);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader messageLoader = new FXMLLoader();
        messageLoader.setLocation(getClass().getResource("utilizator-view.fxml"));
        AnchorPane messageTaskLayout = messageLoader.load();


        Scene scene=new Scene(messageTaskLayout);
        //scene.setFill(javafx.scene.paint.Color.BLUE);



        primaryStage.setScene(scene);
        //primaryStage.setScene(new Scene(messageTaskLayout));

        StartController AppController = messageLoader.getController();
        AppController.setController(srv,primaryStage,srvMessage);
    }
}
