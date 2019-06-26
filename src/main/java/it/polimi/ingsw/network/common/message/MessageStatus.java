package it.polimi.ingsw.network.common.message;

/**
 * A representation of different states in which a {@link NetworkMessage} can be fount. This enumeration may be used to
 * synchronize two entities on the reception of the same {@link NetworkMessage} based on its state.
 */
public enum MessageStatus {
    /**
     * A {@code WAITING} state indicates that a {@link NetworkMessage} is expected to be received, but this is not happened yet.
     */
    WAITING,
    /**
     * An {@code UNAVAILABLE} state indicates that a {@link NetworkMessage} should have been delivered, but this
     * can't happen anymore, due to adverse circumstances. In this state there is no guarantee that the expected {@link NetworkMessage}
     * will be delivered anytime in the future and other assumptions shouldn't be made based on this hypothesis.
     */
    UNAVAILABLE,
    /**
     * An {@code AVAILABLE} state indicates that a {@link NetworkMessage} has been successfully received and can
     * now be safely processed.
     */
    AVAILABLE
}
