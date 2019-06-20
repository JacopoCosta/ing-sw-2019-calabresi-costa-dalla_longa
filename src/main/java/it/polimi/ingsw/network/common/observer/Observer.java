package it.polimi.ingsw.network.common.observer;

/**
 * A class can implement the {@code Observer} interface when it
 * wants to be informed of changes in {@link Observable} objects.
 *
 * @see Observable
 */
public interface Observer {
    /**
     * This method is called whenever the observed object is changed. An
     * application calls an {@link Observable} object's
     * {@link Observable#notifyObservers()} method to have all the object's
     * observers notified of the change.
     *
     * @see Observable#notifyObservers()
     */
    void onEvent();
}
