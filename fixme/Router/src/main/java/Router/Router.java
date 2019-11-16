package Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;


public class Router implements AsyncChannel {
    private AsynchronousServerSocketChannel server;
    private AsynchronousChannelGroup group;
    private int requestID;
    private Map<Integer, String> routeTable;

    public Router(final int BrokerPort, final int MarketPort) {

        this.requestID = 0;
        this.routeTable = new HashMap<Integer, String>();
        try {
            this.group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool(Executors.defaultThreadFactory()));
//            this.server = AsynchronousServerSocketChannel.open(this.group).bind(new InetSocketAddress(BrokerPort)).bind(new InetSocketAddress(MarketPort));
            this.server = AsynchronousServerSocketChannel.open(this.group).bind(new InetSocketAddress(BrokerPort));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("[Router]: Unable to initialize thread group pool");
        }
    }

    @Override
    public void start() {
        System.out.println("[Router]: Accepting connections from brokers");
        System.out.println("[Router]: Accepting connections from markets");
        acceptConn(++requestID);
    }

    @Override
    public void stop() {
        stopChannelGroup(this.group);
    }

    private void acceptConn(int requestID) {

        this.server.accept(requestID, new CompletionHandler<AsynchronousSocketChannel, Integer>() {

            @Override
            public void completed(AsynchronousSocketChannel result, Integer attachment) {
                // Delegate off to another thread for the next connection.
                System.out.println(String.format("[Router]: Accepted a connection. ID# %d", requestID));
                acceptConn(++attachment);

                // Delegate off to another thread to handle this connection.
                Router.this.readRequest(result, attachment);
            }

            @Override
            public void failed(Throwable exc, Integer attachment) {
                System.out.println(String.format("[Router]: Failed to accept connection in thread %s", Thread.currentThread().getName()));
                exc.printStackTrace();
            }
        });
    }

    private void readRequest(AsynchronousSocketChannel channel, int id) {

//        assert !Objects.isNull(channel);

        final ByteBuffer buffer = create(2048);
        channel.read(buffer, id, new CompletionHandler<Integer, Integer>() {

            @Override
            public void completed(Integer result, Integer attachment) {
                System.out.println(String.format("[Router]: Read Completed in thread %s", Thread.currentThread().getName()));

                try {
                    System.out.println(String.format("[Router]: Accepted a connection. port %d", ((InetSocketAddress) channel.getLocalAddress()).getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Extract what was read from the buffer.
//                final String message = Router.Router.this.extract(buffer);
                System.out.println(Router.this.extract(buffer));
                String message = "I agree.|10=";
                if (result != -1 && message.contains("|10=")) {

                    // Create echo message.
//                    final String echo = createEcho(attachment);

                    // Send echo message to client.
//                    doEcho(echo, channel);


//                    assert !(Objects.isNull(channel) && !StringUtils.isNotEmpty(message));

                    final ByteBuffer responseBuffer = create(2048);
                    responseBuffer.put(message.trim().getBytes());

                    responseBuffer.flip();

                    channel.write(responseBuffer);

                    // Prepare to read again.
                    readRequest(channel, id);
                } else {
                    closeChannel(channel);
                }
            }

            @Override
            public void failed(Throwable exc, Integer attachment) {
                System.out.println(String.format("[Router]: Read Failed in thread %s", Thread.currentThread().getName()));
                exc.printStackTrace();

                closeChannel(channel);
            }
        });
    }

}
