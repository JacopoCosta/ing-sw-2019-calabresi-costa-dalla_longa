package it.polimi.ingsw.network.common.util.timer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the validity of {@link CountDownTimer} by stressing it's resistance to multi thread
 * synchronization.
 */
public class TestCountDownTimer {

    /**
     * Checks whether the timer can start and calculate the elapsed time after 10000 ms
     *
     * @throws InterruptedException if the tester fails to sleep.
     */
    @Test
    public void start() throws InterruptedException {
        int startingSeconds = 2;
        CountDownTimer timer = new CountDownTimer(startingSeconds);

        timer.start();
        Thread.sleep(1000 + 25);
        assertEquals(1, timer.getTime());
    }

    /**
     * Checks whether the timer delay is correctly respected after a caller sleep of 10000 ms
     *
     * @throws InterruptedException if the tester fails to sleep
     */
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