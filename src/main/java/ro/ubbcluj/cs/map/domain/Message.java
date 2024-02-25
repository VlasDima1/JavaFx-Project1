package ro.ubbcluj.cs.map.domain;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Message extends Entity<Long> {

    private String message;
    private Utilizator from;
    private List<Utilizator> to;
    private LocalDateTime date;

    private Long replyToID;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

    public Long getReplyTo() {
        return replyToID;
    }

    public void setReplyTo(Long replyTo) {
        this.replyToID = replyTo;
    }

    public Message(String message, Utilizator from, List<Utilizator> to, LocalDateTime date) {
        this.message = message;
        this.from = from;
        this.to = to;
        this.date = date;
        this.replyToID=null;
    }


    @Override
    public String toString() {
        return super.toString() + " " + message + " " + from + " " + date.format(formatter);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
