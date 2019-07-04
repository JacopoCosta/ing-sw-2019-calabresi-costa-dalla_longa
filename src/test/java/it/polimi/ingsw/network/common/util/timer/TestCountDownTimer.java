package it.polimi.ingsw.network.common.util.timer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCountDownTimer {
    @Test
    public void start() throws InterruptedException {
        int startingSeconds = 2;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();
        Thread.sleep(1000 + 25);
        assertEquals(1, timer.getTime());
    }

    @Test
    public void startAndDelay() throws InterruptedException {
        int startingSeconds = 1;
        int newSeconds = 2;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();
        Thread.sleep(200);
        timer.setTime(newSeconds);

        Thread.sleep(1000 + 25);
        assertEquals(1, timer.getTime());
    }
}