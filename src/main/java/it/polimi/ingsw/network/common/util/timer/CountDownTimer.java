package it.polimi.ingsw.network.common.util.timer;

import it.polimi.ingsw.network.common.observer.Observable;
import it.polimi.ingsw.network.common.observer.Observer;
import it.polimi.ingsw.network.server.lobby.Lobby;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@code Timer} class to perform countdown from {@link #currentSeconds} all way down to {@code 0}.
 * This class extends the {@link Observable} interface, so the caller of {@link CountDownTimer} can be notified when the
 * timer expires, stops/pauses, or is being updated and the countdown performs from a slightly different value of time.
 */
public class CountDownTimer implements Observable {
    /**
     * Delay in seconds before the {@link #tick} task is to be executed the first time.
     */
    private static final int INITIAL_DELAY = 1;

    /**
     * Time in seconds between successive {@link #tick} task executions.
     */
    private static final int PERIOD = 1;

    /**
     * Time left in seconds before the countdown expires.
     */
    private final AtomicInteger currentSeconds;

    /**
     * The {@link ScheduledExecutorService} responsible for the actual countdown operation.
     */
    private final ScheduledExecutorService executor;

    /**
     * The {@link ScheduledFuture} used to control the {@link #tick} task during its execution.
     */
    private ScheduledFuture<?> future;

    /**
     * The task to be executed every {@link #PERIOD} seconds.
     */
    private final Runnable tick;

    /**
     * The {@link Observer}s interested in the timer execution.
     */
    private final List<Observer> observers;

    /**
     * This status is used to indicate that the timer has been expired and therefore a new {@link Game} is about to start
     * from the current {@link Lobby} with the logged {@link Player}s till that moment.
     */
    public static final int STATUS_EXPIRED = 1;

    /**
     * This status is used to indicate that the timer has been stopped or paused due to not enough {@link Player}s
     * left in the {@link Lobby}.
     */
    public static final int STATUS_STOPPED = 2;

    /**
     * This status is used to indicate that a time update is performed. This may happen only when a relevant change in
     * the timer countdown has been detected.
     */
    public static final int STATUS_TIME_UPDATE = 3;

    /**
     * Whether or not the timer must send updates to its {@link Observer}s. This flag is typically {@code true}
     * before a {@link Game} starts and becomes {@code false} after that because, as the {@link Game} begins, there
     * is no need to continue sending lobby-related updates to the remote {@link Player}s.
     */
    private final AtomicBoolean sendTimeUpdate;

    /**
     * This is the only constructor. It creates a new {@code CountDownTimer} that starts the countdown from the given
     * amount of seconds.
     *
     * @param startingSeconds the amount of seconds to start counting from.
     */
    public CountDownTimer(int startingSeconds) {
        this.currentSeconds = new AtomicInteger(startingSeconds);
        this.sendTimeUpdate = new AtomicBoolean(true);

        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.tick = () -> {
            if (this.currentSeconds.decrementAndGet() <= 0) {
                stop();
                notifyObservers(STATUS_EXPIRED, -1); //-1: no valuable information given when the timer expires
            } else if (this.sendTimeUpdate.get()) { //a time update is triggered only when a setTime() call is performed
                notifyObservers(STATUS_TIME_UPDATE, this.currentSeconds.get());
                this.sendTimeUpdate.set(false);
            }
        };

        this.observers = new ArrayList<>();
    }

    /**
     * Returns the current value of {@link #currentSeconds}, with concurrency effects as specified at
     * {@link AtomicInteger#get()}.
     *
     * @return the current value of {@link #currentSeconds}.
     */
    public int getTime() {
        return this.currentSeconds.get();
    }

    /**
     * Sets the value of {@link #currentSeconds} to {@code seconds}, with concurrency effects as specified at
     * {@link AtomicInteger#set(int)}.
     *
     * <p>There is no guarantee, other than best effort, that an invocation of this method performed by the super class
     * at the exact same time the countdown expires, prevents the timer to actually expire and consequently perform
     * the {@link #tick} task again.
     *
     * @param seconds the new value of {@link #currentSeconds}.
     */
    public void setTime(int seconds) {
        this.currentSeconds.set(seconds);
        this.sendTimeUpdate.set(true);
    }

    /**
     * Starts the countdown from {@link #currentSeconds} down to {@code 0}.
     * If called repeatedly before the {@link #tick} task is done, the second and subsequent calls have no effect.
     */
    public void start() {
        //prepare the timer to start the countdown from the last value before interruption
        if (this.future == null || this.future.isDone())
            this.future = this.executor.scheduleAtFixedRate(this.tick, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    /**
     * Stops the current countdown task.
     * If called called repeatedly the second and subsequent calls have no effect.
     *
     * <p>There is no guarantee, other than best effort, that an invocation of this method performed by the super class
     * at the exact same time the countdown expires, prevents the timer to actually expire and consequently notify the
     * {@link Observer}s.
     */
    public void stop() {
        this.future.cancel(true);
        if (currentSeconds.get() > 0) //otherwise a STATUS_EXPIRED has already been sent
            notifyObservers(STATUS_STOPPED, -1); //-1: no valuable information given when the timer stops
    }

    /**
     * Adds an {@link Observer} to the {@link #observers} {@code List}, so that
     * it can be notified when the timer expires.
     *
     * @param observer the {@link Observer} to be added.
     */
    @Override
    public void addObserver(Observer observer) {
        if (!this.observers.contains(observer))
            this.observers.add(observer);
    }

    /**
     * Removes an {@link Observer} from the {@link #observers} {@code List}.
     * This typically happens after the {@link Observer} is no longer interested
     * in the timer expiration, or immediately after expired and don't need to
     * repeat a countdown anymore.
     *
     * @param observer the {@link Observer} to be removed.
     */
    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notify all the {@link Observer}s about the expiration, stop and update of the timer status by calling,
     * for each {@link Observer}, it's {@link Observer#onEvent(int, int)} method.
     *
     * @param eventStatus the status in which the {@code timer} is found when notifying its {@link Observer}s.
     * @param value       the value value of the status event.
     */
    @Override
    public void notifyObservers(int eventStatus, int value) {
        for (Observer observer : this.observers)
            observer.onEvent(eventStatus, value);
    }
}
