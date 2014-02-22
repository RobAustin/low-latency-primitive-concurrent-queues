package uk.co.boundedbuffer;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
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

        final ConcurrentBlockingIntQueue ConcurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();
        final int max = 100;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            ConcurrentBlockingIntQueue.add(i);
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
                        for (int i = 1; i < max; i++) {

                            final int value = ConcurrentBlockingIntQueue.take();
                            try {
                                junit.framework.Assert.assertEquals(i, value);
                            } catch (Error e) {
                                System.out.println("value=" + value);

                            }
                            System.out.println("value=" + value);
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
    }


    /**
     * faster writer
     *
     * @throws Exception
     */
    @Test
    public void testWithFasterWriter() throws Exception {

        final ConcurrentBlockingIntQueue ConcurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();
        final int max = 200;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            ConcurrentBlockingIntQueue.add(i);
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
                        for (int i = 1; i < max; i++) {

                            final int value = ConcurrentBlockingIntQueue.take();
                            try {
                                junit.framework.Assert.assertEquals(i, value);
                            } catch (Error e) {
                                System.out.println("value=" + value);

                            }
                            System.out.println("value=" + value);
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
    }


    @Test
    public void testFlatOut() throws Exception {

        final ConcurrentBlockingIntQueue concurrentBlockingIntQueue = new ConcurrentBlockingIntQueue();
        final int max = 101024;
        final CountDownLatch countDown = new CountDownLatch(1);

        Thread writerThread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (int i = 1; i < max; i++) {
                                concurrentBlockingIntQueue.add(i);

                            }
                            System.out.println("writer finished");
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });


        writerThread.setName("writer");

        Thread readerThread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (int i = 1; i < max; i++) {

                                final int value = concurrentBlockingIntQueue.take();
                                try {
                                    junit.framework.Assert.assertEquals(i, value);
                                } catch (Error e) {
                                    System.out.println("value=" + value);

                                }
                                System.out.println("value=" + value);

                            }
                            countDown.countDown();
                            System.out.println("reader finished");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        readerThread.setName("reader");

        writerThread.start();
        readerThread.start();

        countDown.await();

    }
}
