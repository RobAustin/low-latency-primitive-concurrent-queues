package uk.co.boundedbuffer;

import com.sun.japex.TestCase;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Rob Austin
 */
public class ArrayBlockingQueueJapexDriver extends com.sun.japex.JapexDriverBase {

    Queue queue = new ArrayBlockingQueue(10);

    public void warmup(TestCase testCase) {

        queue.add((int) 123);
        final Object removedItem = queue.remove();
    }

    public void run(TestCase testCase) {

        queue.add((int) 123);
        final Object removedItem = queue.remove();
    }
}
