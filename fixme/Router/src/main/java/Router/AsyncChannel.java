package Router;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.Channel;
import java.util.Objects;
//import java.util.concurrent.ConcurrentMap;

public interface AsyncChannel {

    default ByteBuffer create(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    default String extract(final ByteBuffer buffer) {
        if (Objects.isNull(buffer)) {
            throw new IllegalArgumentException("Buffer required");
        }

        buffer.flip();
        return new String(buffer.array()).trim();
    }

//    default void updateMessageCache(String id, String value, ConcurrentMap<Integer, StringBuilder> messageCache) {
//        if (messageCache.containsKey(id)) {
//            messageCache.get(id).append(value);
//        } else {
//            messageCache.put(id, new StringBuilder(value));
//        }
//    }

    default void closeChannel(final Channel channel) {
        if (Objects.isNull(channel)) {
            throw new IllegalArgumentException("Required channel");
        }

        try {
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close channel", e);
        }
    }

    default void stopChannelGroup(AsynchronousChannelGroup group) {
        if (Objects.isNull(group)) {
            throw new IllegalArgumentException("Required group");
        }

        try {
            group.shutdownNow();
        } catch (IOException e) {
            throw new RuntimeException("Unable to shutdown channel group");
        }
    }

    void start();

    void stop();

}
