package ro.ubbcluj.cs.map.service;



import ro.ubbcluj.cs.map.domain.Message;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.repository.Repository;
import ro.ubbcluj.cs.map.utils.events.ChangeEventType;
import ro.ubbcluj.cs.map.utils.events.MessageChangeEvent;
import ro.ubbcluj.cs.map.utils.observer.Observer;

import java.util.*;
import java.util.stream.StreamSupport;

public class MessageService< ID >  implements MessageServiceInterface<ID>{

    private Repository messageRepo;

    public MessageService(Repository messageRepo) {
        this.messageRepo = messageRepo;
    }

    public boolean addMessage(Message msg) {
        Optional<Message> myMessage = messageRepo.save(msg);
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, null));
        return myMessage.isEmpty();
    }

    public ArrayList<Message> findMessages(Long id1, Long id2) {
        Iterable<Message> messages = messageRepo.findAll();
        ArrayList<Message> returnMessages = new ArrayList<>();
        for (var a : messages) {
            if (a.getFrom().getId().equals(id1)) {
                for (Utilizator utilizator : a.getTo()) {
                    if (utilizator.getId().equals(id2)) {
                        returnMessages.add(a);
                    }
                }
            }
            if (a.getFrom().getId().equals(id2)) {
                for (Utilizator utilizator : a.getTo()) {
                    if (utilizator.getId().equals(id1)) {
                        returnMessages.add(a);
                    }
                }
            }
        }
        returnMessages.sort(Comparator.comparing(Message::getDate));
        return returnMessages;
    }
    public Message FindByID(Long id){
        Iterable<Message> allMessages = messageRepo.findAll();
        return StreamSupport.stream(allMessages.spliterator(), false)
                .filter(msg -> Objects.equals(msg.getId(), id))
                .findFirst()
                .orElse(null);
    }

    List<Observer<MessageChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(o -> o.update(t));
    }
}