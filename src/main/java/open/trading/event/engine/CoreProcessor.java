package open.trading.event.engine;

import lombok.extern.slf4j.Slf4j;
import net.openhft.affinity.AffinityLock;
import open.trading.event.engine.model.Event;
import open.trading.event.engine.util.Clock;
import org.agrona.concurrent.ManyToOneConcurrentArrayQueue;
import org.agrona.concurrent.QueuedPipe;
import org.agrona.concurrent.SigInt;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CoreProcessor {
    private volatile boolean isRunning;
    private volatile long startTimeNs;
    private final QueuedPipe<Event> queue = new ManyToOneConcurrentArrayQueue<>(1024);

    public void eventLoop() {
        try (AffinityLock affinityLock = pinCpu()) {
            Thread mainThread = Thread.currentThread();
            startTimeNs = Clock.nanoTime();
            log.info("Started Event Loop at: {}", startTimeNs);
            final AtomicBoolean running = new AtomicBoolean(true);
            SigInt.register(() -> running.set(false));
            do {
                Event nextEvent = queue.poll();
            } while (running.get());
        }
    }

    private AffinityLock pinCpu() {
        String cpu = System.getProperty("cpu");
        return AffinityLock.acquireLock(Integer.parseInt(cpu));
    }
}
