package it.polimi.ingsw.network.common.timer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCountDownTimer {
    @Test
    public void start() throws InterruptedException {
        int startingSeconds = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        assertEquals(CountDownTimer.TimerState.NOT_STARTED, timer.getState());
        timer.start();

        Thread.sleep(500);
        assertEquals(CountDownTimer.TimerState.STARTED, timer.getState());

        Thread.sleep(500 + 20);
        assertEquals(CountDownTimer.TimerState.EXPIRED, timer.getState());
    }

    @Test
    public void stopBeforeZero() throws InterruptedException {
        int startingSeconds = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();

        Thread.sleep(500);
        assertEquals(CountDownTimer.TimerState.STARTED, timer.getState());

        timer.stop();
        assertEquals(CountDownTimer.TimerState.STOPPED, timer.getState());
    }

    @Test
    public void stopAfterZero() throws InterruptedException {
        int startingSeconds = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();

        Thread.sleep(1000 + 20);
        timer.stop();

        assertEquals(CountDownTimer.TimerState.EXPIRED, timer.getState());
    }

    @Test
    public void expire() throws InterruptedException {
        int startingSeconds = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();

        Thread.sleep(1000 + 20);
        assertEquals(CountDownTimer.TimerState.EXPIRED, timer.getState());
    }

    @Test
    public void delay() throws InterruptedException {
        int startingSeconds = 1;
        int secondsDelay = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();
        Thread.sleep(500);
        assertEquals(CountDownTimer.TimerState.STARTED, timer.getState());

        timer.delay(secondsDelay);
        assertEquals(CountDownTimer.TimerState.STARTED, timer.getState());

        Thread.sleep(500);
        assertEquals(CountDownTimer.TimerState.STARTED, timer.getState());

        Thread.sleep(1000 + 20);
        assertEquals(CountDownTimer.TimerState.EXPIRED, timer.getState());
    }

}