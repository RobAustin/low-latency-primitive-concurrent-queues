package uk.co.boundedbuffer;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by robaustin on 31/01/2014.
 */
public class ConcurrentBlockingIntQueueTest {

    @Test
    public void testTake() throws Exception {

        final ConcurrentBlockingIntQueue queue = new ConcurrentBlockingIntQueue();

        // writer thread
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                queue.add(1);
            }
        });

        final ArrayBlockingQueue<Integer> actual = new ArrayBlockingQueue<Integer>(1);

        // reader thread
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final int value = queue.take();
                actual.add(value);

            }
        });

        final Integer value = actual.poll(1, TimeUnit.SECONDS);
        Assert.assertEquals((int) value, 1);
        Thread.sleep(100);
    }
}
