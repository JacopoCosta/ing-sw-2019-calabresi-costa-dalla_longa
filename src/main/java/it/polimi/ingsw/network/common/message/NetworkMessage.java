package it.polimi.ingsw.network.common.message;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
    private final String author;
    private final MessageType type;
    private final Object content;

    private NetworkMessage(String author, MessageType type, Object content) {
        this.author = author;
        this.type = type;
        this.content = content;
    }

    public static NetworkMessage simpleMessage(String author, MessageType type) {
        return new NetworkMessage(author, type, null);
    }

    public static NetworkMessage simpleClientMessage(String author, MessageType type) {
        return new NetworkMessage(author, type, null);
    }

    public static NetworkMessage simpleServerMessage(MessageType type) {
        return new NetworkMessage(null, type, null);
    }

    public static NetworkMessage completeMessage(String author, MessageType type, Object content) {
        return new NetworkMessage(author, type, content);
    }

    public static NetworkMessage completeClientMessage(String author, MessageType type, Object content) {
        return new NetworkMessage(author, type, content);
    }

    public static NetworkMessage completeServerMessage(MessageType type, Object content) {
        return new NetworkMessage(null, type, content);
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
