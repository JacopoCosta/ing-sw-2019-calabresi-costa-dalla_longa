package it.polimi.ingsw.network.common.timer;

import it.polimi.ingsw.network.common.observer.Observable;
import it.polimi.ingsw.network.common.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownTimer implements Observable {
    public enum TimerState {
        //the timer has been created successfully, but is not started yet
        NOT_STARTED,

        //the timer is started and is currently performing the countdown
        STARTED,

        //the timer has been stopped during the execution before expiring,
        //the timer is not performing the countdown anymore,
        //if stop() is called at the same time the timer expires, the timer state will be set to STOPPED.
        STOPPED,

        //the timer has finished his execution without being interrupted,
        //the timer is not performing the countdown anymore
        EXPIRED
    }

    private TimerState timerState; //the current state of the timer

    private static final int INITIAL_DELAY = 0; //delay in seconds before task is to be executed the first time
    private static final int PERIOD = 1; //time in seconds between successive task executions

    private final int statingSeconds; //time to start counting from in seconds
    private AtomicInteger currentSeconds; //the time left in seconds before countdown expires

    private ScheduledExecutorService executor; //the timer responsible for the actual countdown
    private ScheduledFuture<?> future;

    private Runnable tick = () -> { //the task to execute every PERIOD seconds
        if (currentSeconds.decrementAndGet() < 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
            synchronized (this) {
                timerState = TimerState.EXPIRED;
                stop();
                notifyObservers();
            }
        }
    };

    private final List<Observer> observers;

    public CountDownTimer(int statingSeconds) {
        this.statingSeconds = statingSeconds;
        currentSeconds = new AtomicInteger();
        timerState = TimerState.NOT_STARTED;

        observers = new ArrayList<>();
    }

    /*
     * starts the countdown from this.startingSeconds to 0
     * throws IllegalStateException if timer was already started
     * */
    public void start() {
        //prepare the timer to start the countdown from the beginning
        currentSeconds.set(statingSeconds);

        executor = Executors.newSingleThreadScheduledExecutor();
        future = executor.scheduleAtFixedRate(tick, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
        timerState = TimerState.STARTED;
    }

    /*
     * stops the current timer
     * If called called repeatedly the second and subsequent calls have no effect
     * */
    public synchronized void stop() {
        future.cancel(true);
        executor.shutdown();

        if (!timerState.equals(TimerState.EXPIRED))
            timerState = TimerState.STOPPED;
    }

    /*
     * delays the timer expiration by a certain amount of time in seconds
     * */
    public void delay(int secondsAmount) {
        currentSeconds.addAndGet(secondsAmount);
    }

    /*
     * get the current state of the timer
     * */
    public TimerState getState() {
        return timerState;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers)
            observer.onEvent();
    }
}
