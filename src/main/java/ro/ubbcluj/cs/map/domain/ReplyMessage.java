package ro.ubbcluj.cs.map.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyMessage extends Message{

    private Message repliedMessage;
    public ReplyMessage(String message, Utilizator from, List<Utilizator> to, LocalDateTime date) {
        super(message, from, to, date);
    }

    public void setRepliedMessage(Message repliedMessage) {
        this.repliedMessage = repliedMessage;
    }

    public Message getRepliedMessage() {
        return repliedMessage;
    }
}
