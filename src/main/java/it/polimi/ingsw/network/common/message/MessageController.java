package it.polimi.ingsw.network.common.message;

public abstract class MessageController {
    private Message message;
    private boolean received;

    protected MessageController() {
        message = null;
        received = false;
    }

    /*
     * called when a new string has been received
     * */
    public synchronized void onMessageReceived(Message message) {
        this.message = message;
        received = true;
    }

    private synchronized boolean newMessageReceived() {
        return received;
    }

    private synchronized Message getMessage() {
        received = false;
        return message;
    }

    protected Message getNextMessage() {
        while (!newMessageReceived()) ;
        return getMessage();
    }
}
