package it.polimi.ingsw.network.common.timer;

import it.polimi.ingsw.network.common.observer.Observer;
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

     /* // <-------------------- UNCOMMENT ME

    @Test
    public void multipleExecutions() throws InterruptedException {
        final int sleepMillis = ((TestClass.nTimesButFinal * TestClass.countingSeconds) * 1000) + 1000;

        new TestClass().start();
        Thread.sleep(sleepMillis);
    }

    static class TestClass implements Observer {
        private final CountDownTimer timer;
        private double start = 0D, end = 0D;
        private double tot = 0D;
        private int i = 1;

        private int nTimes = 10;
        public static final int nTimesButFinal = 10; // <--------------- CHANGE ME to add more executions
        public static final int countingSeconds = 3; // <--------------- CHANGE ME to start the countdown from a different number

        TestClass() {
            timer = new CountDownTimer(countingSeconds);
            timer.addObserver(this);
        }

        private void go() {
            if (nTimes <= 0) {
                System.out.println("average: " + (tot / nTimesButFinal));
                return;
            }

            timer.start();
            start = System.currentTimeMillis();
        }

        void start() {
            go();
        }

        @Override
        public void onEvent() {
            end = System.currentTimeMillis();

            double elapsed = (end - start);
            System.out.println("[" + i++ + "/" + nTimesButFinal + "]" + " elapsed: " + elapsed);
            tot += elapsed;

            if (nTimes-- > 0)
                go();
        }
    }


    */ // <-------------------- UNCOMMENT ME
}