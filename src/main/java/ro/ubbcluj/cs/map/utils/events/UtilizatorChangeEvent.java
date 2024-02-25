package ro.ubbcluj.cs.map.utils.events;


import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.utils.events.Event;

public class UtilizatorChangeEvent implements Event {
    private ChangeEventType type;
    private Utilizator data, oldData;

    public UtilizatorChangeEvent(ChangeEventType type, Utilizator data) {
        this.type = type;
        this.data = data;
    }
    public UtilizatorChangeEvent(ChangeEventType type, Utilizator data, Utilizator oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Utilizator getData() {
        return data;
    }

    public Utilizator getOldData() {
        return oldData;
    }
}