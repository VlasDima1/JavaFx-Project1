package ro.ubbcluj.cs.map.service;

import ro.ubbcluj.cs.map.domain.*;
import ro.ubbcluj.cs.map.domain.validators.ValidationException;
import ro.ubbcluj.cs.map.repository.Repository;
import ro.ubbcluj.cs.map.repository.paging.Page;
import ro.ubbcluj.cs.map.repository.paging.Pageable;
import ro.ubbcluj.cs.map.repository.paging.PagingRepository;
import ro.ubbcluj.cs.map.utils.events.ChangeEventType;
import ro.ubbcluj.cs.map.utils.events.UtilizatorChangeEvent;
import ro.ubbcluj.cs.map.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
//import org.mindrot.jbcrypt.BCrypt;
import org.apache.commons.codec.digest.DigestUtils;


public class Service <ID>  implements ServiceInterface <ID>{
    private Repository repoUt;

    private Repository repoPr;

    private PagingRepository<Long, Utilizator> userRepo;

    //private Repository repoMs;

   // private Long iduri=1000L;

    public Service(Repository repo, Repository repo2,PagingRepository<Long, Utilizator> userRepo){
            this.repoUt=repo;
            this.repoPr=repo2;
            this.userRepo=userRepo;
            //this.repoMs=repo3;
    }

//    public boolean AddMessage(Message message){
//        message.setDate(LocalDateTime.now());
//        repoMs.save(message);
//        return true;
//    }
//
//    public ArrayList<Message> findMessages(Long id1,Long id2)
//    {
//        Iterable<Message> messages=repoMs.findAll();
//        ArrayList<Message> returnMessages=new ArrayList<>();
//        for (var a:messages)
//        {
//            if (a.getFrom().getId().equals(id1))
//            {
//                for(Utilizator utilizator:a.getTo())
//                {
//                    if (utilizator.getId().equals(id2))
//                    {
//                        returnMessages.add(a);
//                    }
//                }
//            }
//            if (a.getFrom().getId().equals(id2))
//            {
//                for(Utilizator utilizator:a.getTo())
//                {
//                    if (utilizator.getId().equals(id1))
//                    {
//                        returnMessages.add(a);
//                    }
//                }
//            }
//        }
//        returnMessages.sort(Comparator.comparing(Message::getDate));
//        return returnMessages;
//    }

    @Override
    public boolean ServAdaugaUt(Utilizator ut) {
        String hashedPassword = encodePassword(ut.getPassword());
        ut.setPassword(hashedPassword);


        Optional<Utilizator>u= repoUt.save(ut);
        if (u.isEmpty())
        {
            notifyObservers(new UtilizatorChangeEvent(ChangeEventType.ADD,null));
        }
        return u.isEmpty();
    }

    private String encodePassword(String password) {
        // Hash cu SHA-256
        return DigestUtils.sha256Hex(password);
    }
    Utilizator global;
    public Utilizator connect(String username,String password){

        Iterable<Utilizator> all=repoUt.findAll();
        password=encodePassword(password);

        for (Utilizator u:all){
            if (Objects.equals(u.getUsername(), username) && Objects.equals(u.getPassword(), password))
            {
                global=u;
                return u;
            }
        }
        return null;
    }

//    private String hashPassword(String plainTextPassword) {
//        // Generează un salt aleatoriu (log 2^n)
//        String salt = BCrypt.gensalt(12); // 2 la puterea 12
//
//        // Criptează parola cu saltul generat
//        return BCrypt.hashpw(plainTextPassword, salt);
//    }

    public boolean updateUser(Utilizator User) {
        ///idNumber++;
        ///User.setId(idNumber);
        Optional<Utilizator> userOld=repoUt.findOne(User.getId());
        if(userOld.isPresent()) {
            Optional<Utilizator> r=repoUt.update(User);
            notifyObservers(new UtilizatorChangeEvent(ChangeEventType.UPDATE, User, userOld.get()));
            return r.isEmpty();
        }
        return false;
    }

    @Override
    public boolean DeleteUser(ID id) {
        var u=getById(id);
        //var u=repoUt.delete(id);
        if (u!=null) {
            Iterable<Prietenie> prietenies=ServGetPr();
            if (prietenies!=null) {
                List<Tuple<Long, Long>> list = new ArrayList<>();
                prietenies.forEach(a->{
                    if (a.getId().getLeft().equals( id)) {
                        list.add(a.getId());
                        //DeleteFriend(id,(ID) a.getId().getRight());
                    } else if (a.getId().getRight().equals(id)) {
                        list.add(a.getId());
                        //DeleteFriend((ID)a.getId().getLeft(),id);
                    }
                });
                if (list!=null)
                    for (var a : list) {
                        DeleteFriend((ID) a.getLeft(), (ID) a.getRight());
                    }
            }
            repoUt.delete(id);
            notifyObservers(new UtilizatorChangeEvent(ChangeEventType.DELETE, u));
            return true;
        }
        return false;

    }


    @Override
    public boolean AddFriend(ID u1,ID u2) {
        Utilizator ut1=getById(u1);
        Utilizator ut2=getById(u2);
        if (ut1==null || ut2==null)
            throw new ValidationException("unu dintre iduri nu exista");
        LocalDateTime date=LocalDateTime.now();
        Prietenie prietenie=new Prietenie(ut1,ut2,date);
        Prietenie prietenie2=new Prietenie(ut2,ut1,date);
        var all=ServGetPr();

        if (all.iterator().hasNext())
            for (Prietenie p:all)
            {
                if (p.getId().equals(prietenie.getId()))//|| p.getId().equals(prietenie2.getId())
                    return false;
                if (p.getId().equals(prietenie2.getId()))
                {
                    if (p.getAcceptance().equals(FriendRequest.PENDING))
                    {
                        prietenie2.setAcceptance(FriendRequest.ACCEPTED);
                        repoPr.update(prietenie2);
                    }
                    return false;
                }
            }
        prietenie.setAcceptance(FriendRequest.PENDING);
        if(repoPr.save(prietenie).isEmpty()) {
            ut1.addFriend(ut2);
            ut2.addFriend(ut1);
            return true;
        }
        return false;
    }

    @Override
    public boolean DeleteFriend(ID u1,ID u2) {
        Tuple<ID,ID> n=new Tuple<>(u1,u2);
        if (repoPr.delete(n).isPresent())
        {
//            Utilizator ut1=getById(u1);
//            Utilizator ut2=getById(u2);
//            ut1.removeFriend(ut2);
//            ut2.removeFriend(ut1);
            return true;
        }
        return false;
    }

    public Iterable<Utilizator> ServGetUt(){
         return repoUt.findAll();
    }

    public Iterable<Prietenie> ServGetPr(){
        return repoPr.findAll();
    }

    public Utilizator getById(ID id){
        Iterable<Utilizator> all=ServGetUt();
        Iterable<Prietenie> allFr=ServGetPr();
        if (!all.iterator().hasNext()) return null;
        for (var a: all)
        {
            if (a.getId().equals(id))
            {
                for (var f:allFr)
                {
                    if (f.getId().getLeft().equals(id) && f.getAcceptance().equals(FriendRequest.ACCEPTED))
                    {
                        var utilizator=repoUt.findOne(f.getId().getRight());
                        a.addFriend((Utilizator) utilizator.get());
                    }
                    else if (f.getId().getRight().equals(id) && f.getAcceptance().equals(FriendRequest.ACCEPTED)) {
                        var utilizator=repoUt.findOne(f.getId().getLeft());
                        a.addFriend((Utilizator) utilizator.get());
                    }
                }
                return a;
            }
        }
        return null;
    }
    /*public Utilizator getById(ID id) {
        Iterable<Utilizator> all=ServGetUt();
        Iterable<Prietenie> allFr=ServGetPr();
        if (!all.iterator().hasNext()) return null;
        return StreamSupport.stream(ServGetUt().spliterator(), false)
                .filter(a -> Objects.equals(a.getId(), id))
                .findFirst()
                .orElse(null);
    }*/

    public Iterable<WRU> verifyAll(Long id)
    {
        Iterable<Utilizator> all=ServGetUt();
        Set<WRU> prietenies = new HashSet<>();
        for (var a: all)
        {
            if (!a.getId().equals(id)) {
                Optional<Prietenie> pr = repoPr.findOne(new Tuple<>(a.getId(),id));
                if (pr.isPresent()) {
                    if (pr.get().getAcceptance().equals(FriendRequest.ACCEPTED))
                    {
                        WRU u = new WRU(a.getFirstName()+" "+a.getLastName(), a.getId(), "ACCEPTED");
                        prietenies.add(u);
                    }
                    else {
                        if (pr.get().getId().getRight().equals(id)) {
                            WRU u = new WRU(a.getFirstName()+" " + a.getLastName(), a.getId(), "You have Request");
                            prietenies.add(u);
                        } else {
                            WRU u = new WRU(a.getFirstName()+" " + a.getLastName(), a.getId(), pr.get().getAcceptance().toString());
                            prietenies.add(u);
                        }
                    }
                } else {
                    WRU u = new WRU(a.getFirstName()+" "+a.getLastName(), a.getId(), "REJECTED");
                    prietenies.add(u);
                }
            }
        }
        return prietenies;
    }

    public Iterable<Utilizator> GetFriends(ID id){
        return Optional.ofNullable(getById(id))
                .map(Utilizator::getFriends)
                .orElse(null);

        //Utilizator u=getById(id);
        //if (u!=null)
            // return u.getFriends();
        //return null;
    }

    public int Comuntati(){
        Iterable<Utilizator> all=ServGetUt();
        Set<Utilizator> set=new HashSet<>();
        AtomicInteger nr= new AtomicInteger(0);
        if (all.iterator().hasNext()) {
            all.forEach(u -> {
                if (!set.contains(u)) {
                    nr.getAndIncrement();
                    DFS(u, set);
                }
            });
        }
        return nr.get();
    }

    public List<Utilizator> DFS(Utilizator u,Set<Utilizator> set)
    {
        List<Utilizator> finallist = new ArrayList<>();
        finallist.add(u);
        set.add(u);
        Utilizator u1=getById((ID) u.getId());
        u1.getFriends().forEach(f->{
            if (!set.contains(f))
            {
                List<Utilizator> l = DFS(f, set);
                finallist.addAll(l);
            }
        });


        return finallist;
    }

    public List<Utilizator> sociabila(){
            AtomicReference<List<Utilizator>> most= new AtomicReference<>(new ArrayList<>());
            Iterable<Utilizator> all = ServGetUt();
            Set<Utilizator> set=new HashSet<>();

            AtomicInteger max= new AtomicInteger(-1);
            if (all!=null) {
                all.forEach(u -> {
                    if (!set.contains(u)) {
                        List<Utilizator> list = DFS(u, set);
                        if (list.size() > max.get()) {
                            most.set(list);
                            max.set(list.size());
                        }
                    }
                });
            }
            return most.get();
    }

    public List<Prietenie> RelatiiFunc(Long Id, String luna) {
        Iterable<Prietenie> all = ServGetPr();
        if (!all.iterator().hasNext()) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(all.spliterator(), false)
                .filter(p -> (p.getUser1().getId().equals(Id) || p.getUser2().getId().equals(Id)) &&
                        p.getDate().getMonth().toString().equals(luna))
                .collect(Collectors.toList());
    }



    private List<Observer<UtilizatorChangeEvent>> observers=new ArrayList<>();
    @Override
    public void addObserver(Observer<UtilizatorChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UtilizatorChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UtilizatorChangeEvent t) {
        observers.forEach(x->x.update(t));//observers.stream().forEach(x->x.update(t));
    }

    public Page<Utilizator> getUsersOnPage(Pageable pageable){
        return userRepo.findAll(pageable);
    }
}
