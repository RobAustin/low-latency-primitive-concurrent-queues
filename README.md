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
We are hosted at [Maven Central] (http://http://search.maven.org)

```

<dependency>
    <groupId>uk.co.boundedbuffer</groupId>
    <artifactId>low-latency-primitive-concurrent-queues</artifactId>
    <version>1.0-SNAPSHOT</version>
<dependency>

```

### JavaDoc
Having trouble ? Check out our documentation at [JavaDoc] (http://boundedbuffer.github.io/low-latency-primitive-concurrent-queues/apidocs/)


### Contributors
Contributors are extremely welcome, just fork this project, make your changes, and we'd be happy to review your pull-request.

### Support or Contact
Having Problems ? Contact support@boundedbuffer.com and we’ll help you sort it out.

### Licence
[Apache v2](http://www.apache.org/licenses/LICENSE-2.0.html)


