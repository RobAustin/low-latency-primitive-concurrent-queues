package uk.co.boundedbuffer;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

        final java.util.concurrent.ArrayBlockingQueue<Integer> actual = new java.util.concurrent.ArrayBlockingQueue<Integer>(1);

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


    @Test
    public void testWrite() throws Exception {

    }

    @Test
    public void testRead() throws Exception {
        final ConcurrentBlockingIntQueue ConcurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();
        ConcurrentBlockingIntQueue.add(10);
        final int value = ConcurrentBlockingIntQueue.take();
        junit.framework.Assert.assertEquals(10, value);
    }

    @Test
    public void testRead2() throws Exception {
        final ConcurrentBlockingIntQueue ConcurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();
        ConcurrentBlockingIntQueue.add(10);
        ConcurrentBlockingIntQueue.add(11);
        final int value = ConcurrentBlockingIntQueue.take();
        junit.framework.Assert.assertEquals(10, value);
        final int value1 = ConcurrentBlockingIntQueue.take();
        junit.framework.Assert.assertEquals(11, value1);
    }

    @Test
    public void testReadLoop() throws Exception {
        final ConcurrentBlockingIntQueue ConcurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();

        for (int i = 1; i < 50; i++) {
            ConcurrentBlockingIntQueue.add(i);
            final int value = ConcurrentBlockingIntQueue.take();
            junit.framework.Assert.assertEquals(i, value);
        }
    }

    /**
     * reader and add, reader and writers on different threads
     *
     * @throws Exception
     */
    @Test
    public void testWithFasterReader() throws Exception {

        final ConcurrentBlockingIntQueue concurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();
        final int max = 100;
        final CountDownLatch countDown = new CountDownLatch(1);

        final AtomicBoolean success = new AtomicBoolean(true);


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            concurrentBlockingIntQueue.add(i);
                            try {
                                Thread.sleep((int) (java.lang.Math.random() * 100));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        int value = 0;
                        for (int i = 1; i < max; i++) {

                            final int newValue = concurrentBlockingIntQueue.take();
                            try {
                                junit.framework.Assert.assertEquals(i, newValue);
                            } catch (Error e) {
                                System.out.println("value=" + newValue);

                            }


                            if (newValue != value + 1) {
                                success.set(false);
                                return;
                            }

                            value = newValue;

                            try {
                                Thread.sleep((int) (Math.random() * 10));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        countDown.countDown();

                    }
                }).start();

        countDown.await();

        Assert.assertTrue(success.get());
    }


    /**
     * faster writer
     *
     * @throws Exception
     */
    @Test
    public void testWithFasterWriter() throws Exception {

        final ConcurrentBlockingIntQueue concurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();
        final int max = 200;
        final CountDownLatch countDown = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(true);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            concurrentBlockingIntQueue.add(i);
                            try {
                                Thread.sleep((int) (Math.random() * 3));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        int value = 0;
                        for (int i = 1; i < max; i++) {

                            final int newValue = concurrentBlockingIntQueue.take();
                            try {
                                junit.framework.Assert.assertEquals(i, newValue);
                            } catch (Error e) {
                                System.out.println("value=" + newValue);

                            }


                            if (newValue != value + 1) {
                                success.set(false);
                                return;
                            }

                            value = newValue;

                            try {
                                Thread.sleep((int) (Math.random() * 10));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        countDown.countDown();

                    }
                }).start();

        countDown.await();
        Assert.assertTrue(success.get());
    }


    @Test
    public void testFlatOut() throws Exception {

        testConcurrentBlockingIntQueue(Integer.MAX_VALUE);

    }

    private void testConcurrentBlockingIntQueue(final int nTimes) throws InterruptedException {
        final ConcurrentBlockingIntQueue queue = new ConcurrentBlockingIntQueue(1024);
        final CountDownLatch countDown = new CountDownLatch(1);

        final AtomicBoolean success = new AtomicBoolean(true);

        Thread writerThread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (int i = 1; i < nTimes; i++) {
                                queue.add(i);

                            }

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });


        writerThread.setName("ConcurrentBlockingIntQueue-writer");

        Thread readerThread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        int value = 0;
                        for (int i = 1; i < nTimes; i++) {

                            final int newValue = queue.take();


                            if (newValue != value + 1) {
                                success.set(false);
                                return;
                            }

                            value = newValue;

                        }
                        countDown.countDown();

                    }
                });

        readerThread.setName("ConcurrentBlockingIntQueue-reader");

        writerThread.start();
        readerThread.start();

        countDown.await();

        writerThread.stop();
        readerThread.stop();
    }


    private void testArrayBlockingQueue(final int nTimes) throws InterruptedException {

        final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1024);
        final CountDownLatch countDown = new CountDownLatch(1);

        final AtomicBoolean success = new AtomicBoolean(true);

        Thread writerThread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (int i = 1; i < nTimes; i++) {
                                queue.put(i);

                            }

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });


        writerThread.setName("ArrayBlockingQueue-writer");

        Thread readerThread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        int value = 0;
                        for (int i = 1; i < nTimes; i++) {

                            final int newValue;
                            try {
                                newValue = queue.take();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }


                            if (newValue != value + 1) {
                                success.set(false);
                                return;
                            }

                            value = newValue;

                        }
                        countDown.countDown();

                    }
                });

        readerThread.setName("ArrayBlockingQueue-reader");

        writerThread.start();
        readerThread.start();

        countDown.await();

        writerThread.stop();
        readerThread.stop();
    }


    @Test
    public void testLatency() throws NoSuchFieldException, InterruptedException {

        for (int pwr = 2; pwr < 200; pwr++) {
            int i = (int) Math.pow(2, pwr);


            final long arrayBlockingQueueStart = System.nanoTime();
            testArrayBlockingQueue(i);
            final double arrayBlockingDuration = System.nanoTime() - arrayBlockingQueueStart;


            final long concurrentBlockingIntQueueStart = System.nanoTime();
            testConcurrentBlockingIntQueue(i);
            final double concurrentBlockingDuration = System.nanoTime() - concurrentBlockingIntQueueStart;

      /*      System.out.printf("Performing %,d loops, ArrayBlockingQueue() took %.3f ms and calling ConcurrentBlockingIntQueue took %.3f ms on average, ratio=%.1f%n",
                    i, arrayBlockingDuration / 1000000.0, concurrentBlockingDuration / 1000000.0, (double) arrayBlockingDuration / (double) concurrentBlockingDuration);
*/
            System.out.printf("%d\t%.3f\t%.3f\n",
                    i, arrayBlockingDuration / 1000000.0, concurrentBlockingDuration / 1000000.0, (double) arrayBlockingDuration / (double) concurrentBlockingDuration);

        }


    }
}
