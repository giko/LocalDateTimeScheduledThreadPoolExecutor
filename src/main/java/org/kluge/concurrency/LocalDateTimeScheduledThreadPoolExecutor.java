package org.kluge.concurrency;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * A {@link ThreadPoolExecutor} that can additionally
 * schedule tasks to run at specific {@link LocalDateTime}
 */
public class LocalDateTimeScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    /**
     * Creates a new {@code ScheduledThreadPoolExecutor} with the
     * given core pool size.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *                     if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */
    public LocalDateTimeScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * Schedules task to run at specific time
     * Tasks, that are scheduled to run at the same time are processed in FIFO order of submission.
     *
     * @param localDateTime the time at which task
     *                      becomes enabled and available for scheduling
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                           LocalDateTime localDateTime) {
        Duration duration = Duration.between(LocalDateTime.now(), localDateTime);

        long durationNanos = duration.toNanos() < 0 ? 0 : duration.toNanos();
        return schedule(callable, durationNanos, TimeUnit.NANOSECONDS);
    }
}
