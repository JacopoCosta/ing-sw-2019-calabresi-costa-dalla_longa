package it.polimi.ingsw.network.common.observer;

/**
 * This interface represents an observable object. It can be implemented to represent an
 * object that the application wants to have observed.
 * <p>
 * An {@code Observable} object can have one or more observers. An observer
 * is object that implements interface {@link Observer}. After an
 * observable instance changes, an application calling the
 * {@code Observable}'s {@link #notifyObservers()} method
 * causes all of its observers to be notified of the change by a call
 * to their {@link Observer#onEvent()} method.
 * <p>
 * The order in which notifications will be delivered may vary depending on the
 * data structure in which {@link Observer}s are stored and the notification
 * algorithm implementation.
 * <p>
 * Note that this notification mechanism has nothing to do with threads
 * and is completely separate from the {@link Object#wait()} and {@link Object#notify()}
 * mechanism of class {@link Object}.
 * <p>
 * When an {@code Observable} object is newly created, its set of observers is
 * empty. Two {@link Observer}s are considered the same if and only if the
 * {@code equals()} method returns true for them.
 *
 * @see Observer
 */
public interface Observable {
    /**
     * Adds an {@link Observer} to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     *
     * @param observer the {@link Observer} to be added.
     */
    void addObserver(Observer observer);

    /**
     * Removes an {@link Observer} from the set of observers of this object.
     *
     * @param observer the {@link Observer} to be removed.
     */
    void removeObserver(Observer observer);

    /**
     * Indicates that a change in the extender class happened and notifies all of
     * its {@link Observer}s.
     * As a result, each {@link Observer} has its {@link Observer#onEvent()} method called.
     */
    void notifyObservers();
}
