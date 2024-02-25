package ro.ubbcluj.cs.map.service;


import ro.ubbcluj.cs.map.utils.events.MessageChangeEvent;
import ro.ubbcluj.cs.map.utils.observer.Observable;

public interface MessageServiceInterface< ID > extends Observable<MessageChangeEvent> {

}
