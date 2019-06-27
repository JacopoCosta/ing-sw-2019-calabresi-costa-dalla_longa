package it.polimi.ingsw.network.common.util.timer;

import it.polimi.ingsw.network.common.observer.Observable;
import it.polimi.ingsw.network.common.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@code Timer} class to perform countdown from {@link #currentSeconds} all way down to {@code 0}.
 * This class extends the {@link Observable} interface, so the caller of {@link CountDownTimer} can be notified when the
 * timer expires.
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
     * This is the only constructor. It creates a new {@code CountDownTimer} that starts the countdown from the given
     * amount of seconds.
     *
     * @param startingSeconds the amount of seconds to start counting from.
     */
    public CountDownTimer(int startingSeconds) {
        this.currentSeconds = new AtomicInteger(startingSeconds);

        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.tick = () -> {
            if (this.currentSeconds.decrementAndGet() == 0) {
                stop();
                notifyObservers();
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
    }

    /**
     * Starts the countdown from {@link #currentSeconds} down to  {@code 0}.
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
     * Notify all the {@link Observer}s that the timer has expired by calling,
     * for each {@link Observer}, it's {@link Observer#onEvent()} method.
     */
    @Override
    public void notifyObservers() {
        for (Observer observer : this.observers)
            observer.onEvent();
    }
}
