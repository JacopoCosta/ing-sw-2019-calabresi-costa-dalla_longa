package it.polimi.ingsw.network.common.message;

public abstract class MessageController {
    private NetworkMessage message;
    private boolean received;

    protected MessageController() {
        message = null;
        received = false;
    }

    /*
     * called when a new Message has been received
     * */
    public synchronized void onMessageReceived(NetworkMessage message) {
        this.message = message;
        received = true;
    }

    private synchronized boolean newMessageReceived() {
        return received;
    }

    private synchronized NetworkMessage readMessage() {
        received = false;
        return message;
    }

    protected NetworkMessage getMessage(){
        return message;
    }

    protected NetworkMessage getNextMessage() {
        while (!newMessageReceived()) ;
        return readMessage();
    }
}
