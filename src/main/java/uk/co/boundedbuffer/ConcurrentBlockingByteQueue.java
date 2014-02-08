package uk.co.boundedbuffer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A low latency, lock free, primitive bounded blocking queue backed by an byte[].
 * This class mimics the interface of {@linkplain java.util.concurrent.BlockingQueue BlockingQueue},
 * however works with primitive bytes rather than {@link Object}s, so is unable to actually implement the BlockingQueue .
 * <p/>
 * This class takes advantage of the Unsafe.putOrderedInt, which allows us to create non-blocking code with guaranteed writes.
 * These writes will not be re-orderd by instruction reordering. Under the covers it uses the faster store-store barrier, rather than the the slower store-load barrier, which is used when doing a volatile write.
 * One of the trade off with this improved performance is we are limited to a single producer, single consumer.
 * For further information on this see, the blog post <a href="http://robsjava.blogspot.co.uk/2013/06/a-faster-volatile.html">A Faster Volatile</a> by Rob Austin.
 * <p/>
 * <p/>
 * <p/>
 * This queue orders elements FIFO (first-in-first-out).  The
 * <em>head</em> of the queue is that element that has been on the
 * queue the longest time.  The <em>tail</em> of the queue is that
 * element that has been on the queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 * <p/>
 * <p>This is a classic &quot;bounded buffer&quot;, in which a
 * fixed-sized array holds elements inserted by producers and
 * extracted by consumers.  Once created, the capacity cannot be
 * changed.  Attempts to {@link #put put(e)} an element into a full queue
 * will result in the operation blocking; attempts to {@link #take take()} an
 * element from an empty queue will similarly block.
 * <p/>
 * <p>Due to the lock free nature of its  implementation, ordering works on a first come first served basis.<p/>
 * Methods come in four forms, with different ways
 * of handling operations that cannot be satisfied immediately, but may be
 * satisfied at some point in the future:
 * one throws an exception, the second returns a special value (either
 * <tt>null</tt> or <tt>false</tt>, depending on the operation), the third
 * blocks the current thread indefinitely until the operation can succeed,
 * and the fourth blocks for only a given maximum time limit before giving
 * up.  These methods are summarized in the following table:
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <tr>
 * <td></td>
 * <td ALIGN=CENTER><em>Throws exception</em></td>
 * <td ALIGN=CENTER><em>Special value</em></td>
 * <td ALIGN=CENTER><em>Blocks</em></td>
 * <td ALIGN=CENTER><em>Times out</em></td>
 * </tr>
 * <tr>
 * <td><b>Insert</b></td>
 * <td>{@link #add add(e)}</td>
 * <td>{@link #offer offer(e)}</td>
 * <td>{@link #put put(e)}</td>
 * <td>{@link #offer(byte, long, java.util.concurrent.TimeUnit) offer(e, time, unit)}</td>
 * </tr>
 * <tr>
 * <td><b>Remove</b></td>
 * <td>{@link #poll poll()}</td>
 * <td>not applicable</td>
 * <td>{@link #take long()}</td>
 * <td>{@link #poll(long, java.util.concurrent.TimeUnit) poll(time, unit)}</td>
 * </tr>
 * <tr>
 * <td><b>Examine</b></td>
 * <td><em>not applicable</em></td>
 * <td><em>not applicable</em></td>
 * <td><em>not applicable</em></td>
 * <td>{@link #peek(long, java.util.concurrent.TimeUnit) peek(time, unit)}</td>>
 * </tr>
 * </table>
 * <p/>
 * <p/>
 * <p>A <tt>uk.co.boundedbuffer.ConcurrentBlockingByteQueue</tt> is capacity bounded. At any given
 * time it may have a <tt>remainingCapacity</tt> beyond which no
 * additional elements can be <tt>put</tt> without blocking.
 * <p/>
 * <p> It is not possible to remove an arbitrary element from a queue using
 * <tt>remove(x)</tt>. As this operation would not performed very efficiently.
 * <p/>
 * <p>All of <tt>uk.co.boundedbuffer.ConcurrentBlockingByteQueue</tt> methods are thread-safe when used with a single producer and single consumer, internal atomicity
 * is achieved using lock free strategies, such as sping locks.
 * <p/>
 * <p>Like a <tt>BlockingQueue</tt>, the uk.co.boundedbuffer.ConcurrentBlockingByteQueue does <em>not</em> intrinsically support
 * any kind of &quot;close&quot; or &quot;shutdown&quot; operation to
 * indicate that no more items will be added.  The needs and usage of
 * such features tend to be implementation-dependent. For example, a
 * common tactic is for producers to insert special
 * <em>end-of-stream</em> or <em>poison</em> objects, that are
 * interpreted accordingly when taken by consumers.
 * <p/>
 * <p/>
 * Usage example, based on a typical producer-consumer scenario.
 * Note that a <tt>BlockingQueue</tt> can safely be used with multiple
 * producers and multiple consumers.
 * <pre>
 * class Producer implements Runnable {
 *   private final BlockingQueue queue;
 *   Producer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while (true) { queue.put(produce()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   Object produce() { ... }
 * }
 *
 * class Consumer implements Runnable {
 *   private final BlockingQueue queue;
 *   Consumer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while (true) { consume(queue.take()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   void consume(Object x) { ... }
 * }
 *
 * class Setup {
 *   void main() {
 *     BlockingQueue q = new SomeQueueImplementation();
 *     Producer p = new Producer(q);
 *     Consumer c1 = new Consumer(q);
 *     new Thread(p).start();
 *     new Thread(c1).start();
 *   }
 * }
 * </pre>
 * <p/>
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a
 * {@code BlockingQueue}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code BlockingQueue} in another thread.
 * <p/>
 * <p/>
 * <p/>
 * Copyright 2014 Rob Austin
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Rob Austin
 * @since 1.1
 */
public class ConcurrentBlockingByteQueue extends AbstractBlockingQueue {

    // intentionally not volatile, as we are carefully ensuring that the memory barriers are controlled below by other objects
    private final byte[] data = new byte[size];

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions, returning
     * <tt>true</tt> upon success and throwing an
     * <tt>IllegalStateException</tt> if no space is currently available.
     * When using a capacity-restricted queue, it is generally preferable to
     * use {@link #offer(byte) offer}.
     *
     * @param value the element to add
     * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
     * @throws IllegalStateException    if the element cannot be added at this
     *                                  time due to capacity restrictions
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    public boolean add(byte value) {

        // volatile read
        final int writeLocation = this.writeLocation;

        final int nextWriteLocation = blockAndGetNextWriteLocation(writeLocation);

        // purposely not volatile
        data[writeLocation] = value;

        setWriteLocation(nextWriteLocation);
        return true;
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * until an element becomes available.
     * <p/>
     * the reads must always occur on the same thread
     *
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    public byte take() {

        // volatile read
        final int readLocation = this.readLocation;

        // sets the nextReadLocation my moving it on by 1, this may cause it it wrap back to the start.
        final int nextReadLocation = blockForReadSpace(readLocation);

        // purposely not volatile as the read memory barrier occurred above when we read 'writeLocation'
        final byte value = data[readLocation];

        setReadLocation(nextReadLocation);

        return value;

    }

    /**
     * Retrieves, but does not remove, the head of this queue.
     *
     * @param timeout how long to wait before giving up, in units of
     *                <tt>unit</tt>
     * @param unit    a <tt>TimeUnit</tt> determining how to interpret the
     *                <tt>timeout</tt> parameter
     * @return the head of this queue
     * @throws java.util.concurrent.TimeoutException if timeout time is exceeded
     */

    public byte peek(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {

        final int readLocation = this.readLocation;

        blockForReadSpace(timeout, unit, readLocation);

        // purposely not volatile as the read memory barrier occurred above when we read ' this.readLocation'
        return data[readLocation];

    }

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions, returning
     * <tt>true</tt> upon success and <tt>false</tt> if no space is currently
     * available.  When using a capacity-restricted queue, this method is
     * generally preferable to {@link #add}, which can fail to insert an
     * element only by throwing an exception.
     *
     * @param value the element to add
     * @return <tt>true</tt> if the element was added to this queue, else
     * <tt>false</tt>
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    public boolean offer(byte value) {

        // we want to minimize the number of volatile reads, so we read the writeLocation just once.
        final int writeLocation = this.writeLocation;

        // sets the nextWriteLocation my moving it on by 1, this may cause it it wrap back to the start.
        final int nextWriteLocation = (writeLocation + 1 == size) ? 0 : writeLocation + 1;

        if (nextWriteLocation == size - 1) {

            if (readLocation == 0)
                return false;

        } else if (nextWriteLocation + 1 == readLocation)
            return false;

        // purposely not volatile see the comment below
        data[writeLocation] = value;

        setWriteLocation(nextWriteLocation);
        return true;
    }

    /**
     * Inserts the specified element into this queue, waiting if necessary
     * for space to become available.
     *
     * @param value the element to add
     * @throws InterruptedException     if interrupted while waiting
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    public void put(byte value) throws InterruptedException {

        final int writeLocation1 = this.writeLocation;
        final int nextWriteLocation = blockForWriteSpace(writeLocation1);

        // purposely not volatile see the comment below
        data[writeLocation1] = value;

        setWriteLocation(nextWriteLocation);
    }

    /**
     * Inserts the specified element into this queue, waiting up to the
     * specified wait time if necessary for space to become available.
     *
     * @param value   the element to add
     * @param timeout how long to wait before giving up, in units of
     *                <tt>unit</tt>
     * @param unit    a <tt>TimeUnit</tt> determining how to interpret the
     *                <tt>timeout</tt> parameter
     * @return <tt>true</tt> if successful, or <tt>false</tt> if
     * the specified waiting time elapses before space is available
     * @throws InterruptedException     if interrupted while waiting
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    public boolean offer(byte value, long timeout, TimeUnit unit)
            throws InterruptedException {

        // we want to minimize the number of volatile reads, so we read the writeLocation just once.
        final int writeLocation = this.writeLocation;

        // sets the nextWriteLocation my moving it on by 1, this may cause it it wrap back to the start.
        final int nextWriteLocation = (writeLocation + 1 == size) ? 0 : writeLocation + 1;


        if (nextWriteLocation == size - 1) {

            final long timeoutAt = System.nanoTime() + unit.toNanos(timeout);

            while (readLocation == 0)
            // this condition handles the case where writer has caught up with the read,
            // we will wait for a read, ( which will cause a change on the read location )
            {
                if (!blockAtAdd(timeoutAt))
                    return false;
            }

        } else {

            final long timeoutAt = System.nanoTime() + unit.toNanos(timeout);

            while (nextWriteLocation + 1 == readLocation)
            // this condition handles the case general case where the read is at the start of the backing array and we are at the end,
            // blocks as our backing array is full, we will wait for a read, ( which will cause a change on the read location )
            {
                if (!blockAtAdd(timeoutAt))
                    return false;
            }
        }

        // purposely not volatile see the comment below
        data[writeLocation] = value;

        // the line below, is where the write memory barrier occurs,
        // we have just written back the data in the line above ( which is not require to have a memory barrier as we will be doing that in the line below
        setWriteLocation(nextWriteLocation);

        return true;
    }

    /**
     * Retrieves and removes the head of this queue, waiting up to the
     * specified wait time if necessary for an element to become available.
     *
     * @param timeout how long to wait before giving up, in units of
     *                <tt>unit</tt>
     * @param unit    a <tt>TimeUnit</tt> determining how to interpret the
     *                <tt>timeout</tt> parameter
     * @return the head of this queue, or throws a <tt>TimeoutException</tt> if the
     * specified waiting time elapses before an element is available
     * @throws InterruptedException                  if interrupted while waiting
     * @throws java.util.concurrent.TimeoutException if timeout time is exceeded
     */
    public byte poll(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {

        final int readLocation = this.readLocation;
        int nextReadLocation = blockForReadSpace(timeout, unit, readLocation);

        // purposely not volatile as the read memory barrier occurred above when we read 'writeLocation'
        final byte value = data[readLocation];
        setReadLocation(nextReadLocation);

        return value;

    }

    /**
     * Returns <tt>true</tt> if this queue contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this queue contains
     * at least one element <tt>e</tt> such that <tt>o.equals(e)</tt>. The behavior of
     * this operation is undefined if modified while the operation is in progress.
     *
     * @param o object to be checked for containment in this queue
     * @return <tt>true</tt> if this queue contains the specified element
     * @throws ClassCastException   if the class of the specified element
     *                              is incompatible with this queue
     *                              (<a href="../Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null
     *                              (<a href="../Collection.html#optional-restrictions">optional</a>)
     */
    public boolean contains(int o) {

        int readLocation = this.readLocation;
        int writeLocation = this.writeLocation;

        for (; ; ) {

            if (readLocation == writeLocation)
                return false;

            if (o == data[readLocation])
                return true;

            // sets the readLocation my moving it on by 1, this may cause it it wrap back to the start.
            readLocation = (readLocation + 1 == size) ? 0 : readLocation + 1;

        }


    }

    /**
     * Removes all available elements from this queue and adds them
     * to the given array. If the target array is smaller than the number of elements then the number of elements read will equal the size of the array.
     * This operation may be more
     * efficient than repeatedly polling this queue.  A failure
     * encountered while attempting to add elements to
     * collection <tt>c</tt> may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown.  Further, the behavior of
     * this operation is undefined if the following methods are called during progress of this operation
     * {@link #take()}. {@link #offer(byte)}, {@link #put(byte)},  {@link #drainTo(byte[], int)}}
     *
     * @param target the collection to transfer elements into
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     *                                       is not supported by the specified collection
     * @throws ClassCastException            if the class of an element of this queue
     *                                       prevents it from being added to the specified collection
     * @throws NullPointerException          if the specified collection is null
     * @throws IllegalArgumentException      if the specified collection is this
     *                                       queue, or some property of an element of this queue prevents
     *                                       it from being added to the specified collection
     */
    int drainTo(byte[] target) {
        return drainTo(target, target.length);
    }

    /**
     * Removes at most the given number of available elements from
     * this queue and adds them to the given collection.  A failure
     * encountered while attempting to add elements to
     * collection <tt>c</tt> may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown.  Attempts to drain a queue to itself result in
     * <tt>IllegalArgumentException</tt>. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     * @param target      the array to transfer elements into
     * @param maxElements the maximum number of elements to transfer
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     *                                       is not supported by the specified collection
     * @throws ClassCastException            if the class of an element of this queue
     *                                       prevents it from being added to the specified collection
     * @throws NullPointerException          if the specified collection is null
     * @throws IllegalArgumentException      if the specified collection is this
     *                                       queue, or some property of an element of this queue prevents
     *                                       it from being added to the specified collection
     */
    int drainTo(byte[] target, int maxElements) {

        // we want to minimize the number of volatile reads, so we read the readLocation just once.
        int readLocation = this.readLocation;

        int i = 0;

        // to reduce the number of volatile reads we are going to perform a kind of double check reading on the volatile write location
        int writeLocation = this.writeLocation;

        do {

            // in the for loop below, we are blocked reading unit another item is written, this is because we are empty ( aka size()=0)
            // inside the for loop, getting the 'writeLocation', this will serve as our read memory barrier.
            if (writeLocation == readLocation) {

                writeLocation = this.writeLocation;


                if (writeLocation == readLocation) {

                    setReadLocation(readLocation);
                    return i;
                }
            }


            // sets the nextReadLocation my moving it on by 1, this may cause it it wrap back to the start.
            readLocation = (readLocation + 1 == size) ? 0 : readLocation + 1;

            // purposely not volatile as the read memory barrier occurred above when we read 'writeLocation'
            target[i] = data[readLocation];


        } while (i <= maxElements);

        setReadLocation(readLocation);

        return maxElements;
    }
}


