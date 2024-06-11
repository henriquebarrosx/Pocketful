package com.pocketful;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentTestHelper {
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    public static void doSyncAndConcurrently(int totalCalls, ThrowingRunnable action) {
        ExecutorService executorService = Executors.newFixedThreadPool(totalCalls);
        CompletableFuture<?>[] futures = new CompletableFuture<?>[totalCalls];

        for (int i = 0; i < totalCalls; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    action.run();
                } catch (Exception ignore) {
                }
            }, executorService);
        }

        CompletableFuture.allOf(futures).join();
        executorService.shutdown();
    }
}
