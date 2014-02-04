package uk.co.boundedbuffer;

import com.sun.japex.TestCase;

/**
 * Created by Rob Austin
 */
public class ConcurrentBlockingIntQueueJapexDriver extends com.sun.japex.JapexDriverBase {

    ConcurrentBlockingIntQueue queue = new ConcurrentBlockingIntQueue();

    public void warmup(TestCase testCase) {

        queue.add((int) 123);
        queue.take();
    }

    public void run(TestCase testCase) {
        queue.add((int) 123);
        queue.take();

    }
}
