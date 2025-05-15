package ch.brix.gql.client;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Queue;

public class ThrottledClient extends Client{

    private final Queue<Long> queue = new LinkedList<>();
    private final long requests;
    private final long perSeconds;

    public ThrottledClient(String endpoint, TypeRegistry typeRegistry, long requests, long perSeconds) {
        this(endpoint, new OkHttpClient.Builder().build(), typeRegistry, requests, perSeconds);
    }

    public ThrottledClient(String endpoint, OkHttpClient client, TypeRegistry typeRegistry,
                           long requests, long perSeconds) {
        super(endpoint, client, typeRegistry);
        this.requests = requests;
        this.perSeconds = perSeconds;
    }

    @Override
    public synchronized <T> T execute(CallBuilder<T> callBuilder) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        long t = System.currentTimeMillis();
        queue.offer(t + perSeconds * 1000);
        while (!queue.isEmpty() && queue.peek() < t)
            queue.poll();
        if (queue.size() >= requests) {
            try {
                long sleep = queue.poll() - System.currentTimeMillis();
                if (sleep > 0)
                    Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return super.execute(callBuilder);
    }
}
