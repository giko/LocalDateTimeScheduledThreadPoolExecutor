package org.kluge.concurrency;

import org.junit.jupiter.api.RepeatedTest;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalDateTimeScheduledThreadPoolExecutorTest {

    private LocalDateTimeScheduledThreadPoolExecutor getExecutor() {
        return new LocalDateTimeScheduledThreadPoolExecutor(3);
    }

    @RepeatedTest(100)
    void testRandomDataSameTime() {
        LocalDateTimeScheduledThreadPoolExecutor executor = getExecutor();

        List<Integer> testList = new LinkedList<>();
        List<Callable<Integer>> callables = new LinkedList<>();

        IntStream.range(0, 500).forEach((int i) -> {
            int randomInt = ThreadLocalRandom.current().nextInt(-999, 999);
            testList.add(randomInt);
            callables.add(() -> randomInt);
        });

        LocalDateTime now = LocalDateTime.now();

        List<Future<Integer>> futures = callables.parallelStream()
                .map(integerCallable -> executor.schedule(integerCallable, now.plusNanos(1)))
                .collect(Collectors.toList());

        List<Integer> result = futures.parallelStream().map(integerScheduledFuture -> {
            try {
                return integerScheduledFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        assertTrue(testList.equals(result));
    }

    @RepeatedTest(100)
    void testRandomDataOrdered() {
        LocalDateTimeScheduledThreadPoolExecutor executor = getExecutor();

        List<Integer> testList = new LinkedList<>();
        List<Callable<Integer>> callables = new LinkedList<>();

        IntStream.range(0, 500).forEach((int i) -> {
            int randomInt = ThreadLocalRandom.current().nextInt(-999, 999);
            testList.add(randomInt);
            callables.add(() -> randomInt);
        });

        LocalDateTime now = LocalDateTime.now();

        AtomicInteger i = new AtomicInteger();
        List<Future<Integer>> futures = callables.parallelStream()
                .map(integerCallable -> executor.schedule(integerCallable, now.plusNanos(i.incrementAndGet() * 1000)))
                .collect(Collectors.toList());

        List<Integer> result = futures.parallelStream().map(integerScheduledFuture -> {
            try {
                return integerScheduledFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        assertTrue(testList.equals(result));
    }

    private long nowMillis() {
        return System.currentTimeMillis();
    }


    @RepeatedTest(10)
    public void checkElapsedTime() throws ExecutionException, InterruptedException, TimeoutException {
        LocalDateTimeScheduledThreadPoolExecutor executor = getExecutor();

        executor.schedule(() -> 1, LocalDateTime.now().plusNanos(500)).get(1, TimeUnit.SECONDS);
    }
}
