package ro.ubbcluj.cs.map;

import ro.ubbcluj.cs.map.UI.Console;
import ro.ubbcluj.cs.map.domain.Message;
import ro.ubbcluj.cs.map.domain.Prietenie;
import ro.ubbcluj.cs.map.domain.Tuple;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.MessageValidator;
import ro.ubbcluj.cs.map.domain.validators.PrietenieValidator;
import ro.ubbcluj.cs.map.domain.validators.UtilizatorValidator;
import ro.ubbcluj.cs.map.domain.validators.Validator;
import ro.ubbcluj.cs.map.repository.*;
import ro.ubbcluj.cs.map.repository.paging.PagingRepository;
import ro.ubbcluj.cs.map.service.Service;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String url="jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "12345678";

        //Repository<Long, Utilizator> userRepository = new ro.ubbcluj.cs.map.repository.UserDBRepository(url, username, password);
        //userRepository.findAll().forEach(System.out::println);
        //System.out.println(userRepository.findOne(1L));



        //InMemoryRepository<Long, Utilizator> repo=new InMemoryRepository<>(new UtilizatorValidator());
        Repository<Long,Utilizator> repo=new UserDBRepository(url,username,password,new UtilizatorValidator());
        Repository<Tuple<Long,Long>,Prietenie> repo2=new FriendDBRepository(url,username,password,new PrietenieValidator());
        //InMemoryRepository<Tuple<Long,Long>, Prietenie> repo2=new InMemoryRepository<>(new PrietenieValidator());
        //Utilizator u7=new Utilizator("primul", "primulnume"); u7.setId(1L);
        //Utilizator u8=new Utilizator("doi", "doi"); u8.setId(2L);
        //repo.save(u7);
        //repo.save(u8);
        Repository<Long, Message> repo3=new MessageDBRepository(url,username,password,new MessageValidator());

        PagingRepository<Long, Utilizator> userRepo=new UserPageDBRepository(url,username,password,new UtilizatorValidator());

        Service<Long> srv=new Service<>(repo,repo2,userRepo);
        Utilizator u=new Utilizator("dima","vlas",null,null);
        u.setId(3L);

        ArrayList<Utilizator> list=new ArrayList<Utilizator>();
        list.add(u);

        LocalDateTime date=LocalDateTime.now();

        //srv.AddMessage(new Message("Hei ce mai faci",u,list,date));

        //Console ui=new Console(srv);
        //ui.execute();
    }
}
