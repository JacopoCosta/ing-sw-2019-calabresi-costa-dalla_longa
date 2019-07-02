package it.polimi.ingsw.network.common.message;

import java.io.Serializable;

/**
 * A {@code NetworkMessage} represents the simplest object that flows through the network. It is used to carry different
 * kind of information from a sender (or author) to a recipient. It can encapsulate higher level messages or can be simply
 * used to communicate at the network level.
 *
 * @see MessageType
 */
public class NetworkMessage implements Serializable {
    /**
     * The name of the {@code NetworkMessage} author.
     */
    private final String author;

    /**
     * The type this {@code NetworkMessage} is associated with.
     */
    private final MessageType type;

    /**
     * The optional content of a {@code NetworkMessage}. Can be anything from a service information used at a low level, to a more complex
     * high level packet encapsulated to be carried between different top level entities.
     */
    private final Object content;

    /**
     * This is the only constructor. It create a new {@code NetworkMessage} from the given arguments:
     * - an {@code author} always required for a message sent from a client to the server; it can be null if the message
     * is sent from the server, as because a server is a unique entity and does not need to have a unique indicator
     * - a {@code type} represented by the {@link MessageType} enumeration, to be distinguishable depending on his purpose.
     * - an optional {@code content} that can contain anything is desirable tobe carried over the network.
     *
     * @param author  the author of the {@code NetworkMessage}.
     * @param type    the type of {@code NetworkMessage}, expressed through {@link MessageType}.
     * @param content the content of the {@code NetworkMessage}.
     */
    private NetworkMessage(String author, MessageType type, Object content) {
        this.author = author;
        this.type = type;
        this.content = content;
    }

    /**
     * Creates a simple {@link NetworkMessage} without content. This kind of {@code NetworkMessage} should only be
     * used to send information from a client to the server as because an author identifier is needed.
     *
     * @param author the author of the {@link NetworkMessage}.
     * @param type   the type of {@code NetworkMessage}, expressed through {@link MessageType}.
     * @return a new {@code NetworkMessage} with the given {@code author} and {@code type}.
     */
    public static NetworkMessage simpleClientMessage(String author, MessageType type) {
        return new NetworkMessage(author, type, null);
    }

    /**
     * Creates a simple {@link NetworkMessage} without content. This kind of {@code NetworkMessage} should only be
     * used to send information from the server to one or more clients as because the author identifier is omitted.
     *
     * @param type the type of {@code NetworkMessage}, expressed through {@link MessageType}.
     * @return a new {@code NetworkMessage} with the given {@code type}.
     */
    public static NetworkMessage simpleServerMessage(MessageType type) {
        return new NetworkMessage(null, type, null);
    }

    /**
     * Creates a complete {@code NetworkMessage} which also include a content. This kind of {@code NetworkMessage} should only be
     * used to send information from a client to the server as because an author identifier is needed.
     *
     * @param author  the author of the {@code NetworkMessage}.
     * @param type    the type of {@code NetworkMessage}, expressed through {@link MessageType}.
     * @param content the content of the {@code NetworkMessage}.
     * @return a new {@code NetworkMessage} with the given {@code author}, {@code type} and {@code content}.
     */
    public static NetworkMessage completeClientMessage(String author, MessageType type, Object content) {
        return new NetworkMessage(author, type, content);
    }

    /**
     * Creates a complete {@code NetworkMessage} which also include a content. This kind of {@code NetworkMessage} should only be
     * used to send information from the server to one or more clients as because the author identifier is omitted.
     *
     * @param type    the type of {@code NetworkMessage}, expressed through {@link MessageType}.
     * @param content the content of the {@code NetworkMessage}.
     * @return a new {@code NetworkMessage} with the given {@code type} and {@code content}.
     */
    public static NetworkMessage completeServerMessage(MessageType type, Object content) {
        return new NetworkMessage(null, type, content);
    }

    /**
     * Returns the author of the {@code NetworkMessage}.
     *
     * @return the {@code NetworkMessage} author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the {@link MessageType} corresponding to the type of {@code NetworkMessage} used.
     *
     * @return the {@code NetworkMessage} type.
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns the content of the {@code NetworkMessage}.
     *
     * @return the {@code NetworkMessage} content.
     */
    public Object getContent() {
        return content;
    }
}