package it.polimi.ingsw.network.server.lobby;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCountDownTimer {

    @Test
    public void start() throws InterruptedException {
        int startingSeconds = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        assertEquals(timer.getState(), CountDownTimer.TimerState.NOT_STARTED);
        timer.start();

        Thread.sleep(500);
        assertEquals(timer.getState(), CountDownTimer.TimerState.STARTED);

        Thread.sleep(500 + 5); //in the 1000th millisecond the timer may not have been stopped yet
        assertEquals(timer.getState(), CountDownTimer.TimerState.STOPPED);
    }

    @Test
    public void stop() throws InterruptedException {
        int startingSeconds = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();

        Thread.sleep(500);
        assertEquals(timer.getState(), CountDownTimer.TimerState.STARTED);

        timer.stop();
        assertEquals(timer.getState(), CountDownTimer.TimerState.STOPPED);
    }

    @Test
    public void delay() throws InterruptedException {
        int startingSeconds = 1;
        int secondsDelay = 1;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();
        Thread.sleep(500);
        assertEquals(timer.getState(), CountDownTimer.TimerState.STARTED);

        timer.delay(secondsDelay);
        assertEquals(timer.getState(), CountDownTimer.TimerState.STARTED);

        Thread.sleep(500);
        assertEquals(timer.getState(), CountDownTimer.TimerState.STARTED);

        Thread.sleep(1000 + 5); //in the 1000th millisecond the timer may not have been stopped yet
        assertEquals(timer.getState(), CountDownTimer.TimerState.STOPPED);
    }
}
