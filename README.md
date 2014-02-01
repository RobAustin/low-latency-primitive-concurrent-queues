### Welcome to Bounded Buffer.

An example of how to use on of our low latency bounded queues.

```

// writer thread
Executors.newSingleThreadExecutor().execute(new Runnable() {
    @Override
    public void run() {
        queue.add(1);
    }
});

// reader thread
Executors.newSingleThreadExecutor().execute(new Runnable() {
    @Override
    public void run() {
        final int value = queue.take();
    }
});

```

### Maven Central
We are hosted at [Maven Central] (http://search.maven.org), one of the quickest ways to get up and running is to add this [Maven](http://maven.apache.org/what-is-maven.html) dependency to your pom file :

```
<dependency>
    <groupId>uk.co.boundedbuffer</groupId>
    <artifactId>low-latency-primitive-concurrent-queues</artifactId>
    <version>1.0-SNAPSHOT</version>
<dependency>
```

### JavaDoc
Having trouble ? Check out our documentation at [JavaDoc] (http://boundedbuffer.github.io/low-latency-primitive-concurrent-queues/apidocs/)

### Is this Queue Thread Safe ?

Yes we are thread safe, but you are limited to using just two threads per queue instance, One producer thread and a consumer thread.

### Why am I limited to only using just two threads ?

They queues take advantage of the Unsafe.putOrdered<X>, which provides of non-blocking code with guaranteed writes.
These writes will not be re-ordered by instruction reordering, they use a faster store-store barrier, rather than the the slower store-load barrie ( which is used when doing a volatile write ). One of the trade offs with this improved performance is the visibility of the reads and writes between cores.

### Licence
[Apache v2](http://www.apache.org/licenses/LICENSE-2.0.html)

### Contributors
Contributors are extremely welcome, just fork this project, make your changes, and we'd be happy to review your pull-request.

### Support or Contact
Having Problems ? Contact support@boundedbuffer.com and we’ll help you sort it out.


