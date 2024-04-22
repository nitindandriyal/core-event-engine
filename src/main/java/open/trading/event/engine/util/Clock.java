package open.trading.event.engine.util;

import org.agrona.concurrent.EpochNanoClock;
import org.agrona.concurrent.SystemEpochNanoClock;

public class Clock {
    private static final EpochNanoClock clock = new SystemEpochNanoClock();
    public static long nanoTime() {
        return clock.nanoTime();
    }
}
