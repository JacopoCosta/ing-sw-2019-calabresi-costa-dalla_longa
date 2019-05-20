package it.polimi.ingsw.network.common.message;

import java.io.Serializable;

public class Message implements Serializable {
    private final String author;
    private final MessageType type;
    private final Object content;

    private Message(String author, MessageType type, Object content) {
        this.author = author;
        this.type = type;
        this.content = content;
    }

    public static Message simpleMessage(String author, MessageType type) {
        return new Message(author, type, null);
    }

    public static Message completeMessage(String author, MessageType type, Object content) {
        return new Message(author, type, content);
    }

    public String getAuthor() {
        return author;
    }

    public MessageType getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }
}
