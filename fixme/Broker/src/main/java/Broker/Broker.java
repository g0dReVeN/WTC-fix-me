package Broker;//import fixme.Controller.Core.Broker.AsyncChannel;
//import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Broker implements AsyncChannel {
//    private final ConcurrentMap<Integer, StringBuilder> messageCache;
    private final AsynchronousChannelGroup group;
    private final int numConnections;
    private final CountDownLatch latch;
    private final boolean stopCalled = false;
    private final Lock lock = new ReentrantLock();
    private final InetSocketAddress remoteAddress;

    public Broker(final int numConnections, final int port) {
        this.numConnections = numConnections;
//        this.messageCache = new ConcurrentHashMap<>(numConnections);
        this.remoteAddress = new InetSocketAddress(port);
        this.latch = new CountDownLatch(numConnections);

        try {
            this.group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool(Executors.defaultThreadFactory()));
        } catch (IOException e) {
            throw new IllegalStateException("[Broker]: Unable to initialize thread group pool for client");
        }
    }

    @Override
    public void start() {
        for (int i = 1; i <= this.numConnections; i++) {
            AsynchronousSocketChannel client;
            try {
                client = AsynchronousSocketChannel.open(this.group);
                connect(client, i);
            } catch (IOException e) {
                throw new RuntimeException("[Broker]: Unable to start clients", e);
            }
        }

        try {
            this.latch.await();
            stop();
        } catch (InterruptedException e) {
            throw new RuntimeException("[Broker]: Unable to wait for requests to finish");
        }
    }

    @Override
    public void stop() {
        if (!this.stopCalled) {
            try {
                this.lock.lock();
                if (!this.stopCalled) {
                    stopChannelGroup(this.group);
                }
            } finally {
                this.lock.unlock();
            }
        }
    }

//    public Map<String, StringBuilder> getMessageCache() {
//        return Collections.unmodifiableMap(this.messageCache);
//    }

    private void read(final AsynchronousSocketChannel channel, int requestId) {
//        assert !Objects.isNull(channel);

        final ByteBuffer buffer = create(2048);
        channel.read(buffer, requestId, new CompletionHandler<Integer, Integer>() {

            @Override
            public void completed(Integer result, Integer attachment) {
//                System.out.println(String.format("[Broker.Broker]: Read Completed in thread %s for request %s", Thread.currentThread().getName(), attachment));

                // Extract what was read from the buffer.
                final String message = Broker.this.extract(buffer);
                System.out.println(message);
                // Update the cache with the message.
//                Broker.Broker.this.updateMessageCache(attachment, message, Broker.Broker.this.messageCache);

                if (message.contains("|10=") || result == -1) {
//                    extractEndMessageMarker(attachment);
                    System.out.println(String.format("[Broker]: Read Completed in thread %s for request %s", Thread.currentThread().getName(), attachment));
                    closeChannel(channel);
                    Broker.this.latch.countDown();
                } else {
                    read(channel, attachment);
                }
            }

            @Override
            public void failed(Throwable exc, Integer attachment) {
                System.out.println(String.format("[Broker]: Read Failed in thread %s", Thread.currentThread().getName()));
                exc.printStackTrace();

                Broker.this.latch.countDown();
                closeChannel(channel);
            }

//            private void extractEndMessageMarker(final int requestId) {
////                assert !Objects.isNull(requestId);
//
//                final String message = Broker.Broker.this.messageCache.get(requestId).toString();
//                Broker.Broker.this.messageCache.put(requestId, new StringBuilder(message.replace("|10=", StringUtils.EMPTY)));
//
//            }
        });
    }

    private void write(final AsynchronousSocketChannel channel, final int requestId) {
//        assert !Objects.isNull(channel);

        final ByteBuffer contents = create(2048);
        contents.put("Tumi is a bitch!".getBytes());
        contents.put("|10=".getBytes());
        contents.flip();

        channel.write(contents, requestId, new CompletionHandler<Integer, Integer>() {

            @Override
            public void completed(Integer result, Integer attachment) {
                System.out.println(String.format("[Broker]: Write Completed in thread %s", Thread.currentThread().getName()));
                read(channel, requestId);
            }

            @Override
            public void failed(Throwable exc, Integer attachment) {
                System.out.println(String.format("[Broker]: Write Failed in thread %s", Thread.currentThread().getName()));
                exc.printStackTrace();

                Broker.this.latch.countDown();
                closeChannel(channel);
            }
        });
    }

    private void connect(final AsynchronousSocketChannel channel, final int requestId) {
        channel.connect(this.remoteAddress, requestId, new CompletionHandler<Void, Integer>() {

            @Override
            public void completed(Void result, Integer attachment) {
                System.out.println(String.format("[Broker]: Connect Completed in thread %s", Thread.currentThread().getName()));
//                updateMessageCache(attachment, StringUtils.EMPTY, Broker.Broker.this.messageCache);

                write(channel, attachment);
            }

            @Override
            public void failed(Throwable exc, Integer attachment) {
                System.out.println(String.format("[Broker]: Connect Failed in thread %s", Thread.currentThread().getName()));
                exc.printStackTrace();

                Broker.this.latch.countDown();
                closeChannel(channel);
            }

        });
    }
}
