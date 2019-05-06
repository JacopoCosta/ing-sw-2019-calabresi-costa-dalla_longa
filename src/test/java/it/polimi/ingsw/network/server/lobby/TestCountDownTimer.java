package it.polimi.ingsw.network.server.lobby;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCountDownTimer {

    @Test
    public void start() throws InterruptedException {
        int startingSeconds = 2;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        assertEquals(timer.getState(), CountDownTimer.NOT_STARTED);
        timer.start();

        Thread.sleep(1000);
        assertEquals(timer.getState(), CountDownTimer.STARTED);

        Thread.sleep(1000 + 5); //in the 1000th millisecond the timer may not have been stopped yet
        assertEquals(timer.getState(), CountDownTimer.STOPPED);
    }

    @Test
    public void stop() throws InterruptedException {
        int startingSeconds = 2;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();

        Thread.sleep(1000);
        assertEquals(timer.getState(), CountDownTimer.STARTED);

        timer.stop();
        assertEquals(timer.getState(), CountDownTimer.STOPPED);
    }

    @Test
    public void delay() throws InterruptedException {
        int startingSeconds = 2;
        int secondsDelay = 2;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();
        Thread.sleep(1000);
        assertEquals(timer.getState(), CountDownTimer.STARTED);

        timer.delay(secondsDelay);
        assertEquals(timer.getState(), CountDownTimer.STARTED);

        Thread.sleep(2000);
        assertEquals(timer.getState(), CountDownTimer.STARTED);

        Thread.sleep(1000 + 5); //in the 1000th millisecond the timer may not have been stopped yet
        assertEquals(timer.getState(), CountDownTimer.STOPPED);
    }
}