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
     * {@link Observable#notifyObservers(int, int)} method to have all the object's
     * observers notified of the change.
     *
     * @param eventStatus the status in which the {@link Observable} class is found when invoking the notify procedure.
     * @param value the value of the {@link Observable} status.
     * @see Observable#notifyObservers(int, int)
     */
    void onEvent(int eventStatus, int value);
}
