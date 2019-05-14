package it.polimi.ingsw.network.server.lobby;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class CountDownTimer {
    public enum TimerState {
        //the timer has been created successfully, but is not started yet
        NOT_STARTED,

        //the timer is started and is currently performing the countdown
        STARTED,

        //the timer has completed the countdown or has been stopped during the execution.
        //In both cases the timer is not performing the countdown anymore
        STOPPED
    }

    private TimerState timerState; //the current state of the timer

    private static final int INITIAL_DELAY = 0; //delay in seconds before task is to be executed the first time
    private static final int PERIOD = 1; //time in seconds between successive task executions

    private final int statingSeconds; //time to start counting from in seconds
    private AtomicInteger currentSeconds; //the time left in seconds before countdown expires

    private final ScheduledExecutorService executor; //the timer responsible for the actual countdown
    private ScheduledFuture<?> future;

    //the task to execute every PERIOD
    private Runnable tick = () -> {
        if (currentSeconds.decrementAndGet() < 0)
            stop();
    };

    CountDownTimer(int statingSeconds) {
        this.statingSeconds = statingSeconds;
        currentSeconds = new AtomicInteger();
        timerState = TimerState.NOT_STARTED;

        executor = Executors.newSingleThreadScheduledExecutor();
    }

    /*
     * starts the countdown from this.startingSeconds to 0
     * throws IllegalStateException if timer was already started
     * */
    void start() {
        //prepare the timer to start the countdown from the beginning
        currentSeconds.set(statingSeconds);

        future = executor.scheduleAtFixedRate(tick, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
        timerState = TimerState.STARTED;
    }

    /*
     * stops the current timer
     * If called called repeatedly the second and subsequent calls have no effect
     * */
    void stop() {
        timerState = TimerState.STOPPED;
        future.cancel(true);
        executor.shutdown();
    }

    /*
     * delays the timer expiration by a certain amount of time in seconds
     * */
    void delay(int secondsAmount) {
        currentSeconds.addAndGet(secondsAmount);
    }

    /*
     * get the current state of the timer
     * */
    TimerState getState() {
        return timerState;
    }
}
