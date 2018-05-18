package eu.cise.adaptor.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * New server created to simulate a TCP/IP stream of AIS information
 * Some constants are defining the
 * - maximum number of threads the server will handle concurrently;
 * - the opened port;
 * - the name of the file used to fetch the AIS data from.
 */
public class TestRestServer implements Runnable {

    private final int port;
    private final int workerThreadNum;
    private final AtomicBoolean shouldRun;
    private final AtomicInteger invocationNum;
    private Consumer<String> requestChecker;

    public TestRestServer(int port, int workerThreadNum) {
        this.port = port;
        this.workerThreadNum = workerThreadNum;
        this.shouldRun = new AtomicBoolean(true);
        this.invocationNum = new AtomicInteger(0);
        this.requestChecker = System.out::println;
    }

    public void shutdown() {
        shouldRun.set(false);
    }

    public void checkRequest(Consumer<String> requestChecker) {
        this.requestChecker = requestChecker;
    }

    @Override
    public void run() {
        ExecutorService executors = Executors.newFixedThreadPool(workerThreadNum);

        System.out.println("Test HTTP Server Started");

        try (ServerSocket listener = new ServerSocket(port)) {

            System.out.println("Listening for new client on port: " + port);

            while (shouldRun.get()) {
                executors.execute(new TestCiseWorker(listener.accept(), requestChecker));
                invocationNum.getAndIncrement();
            }
        } catch (IOException e) {
            System.out.println("Interrupted thread [" + e.getMessage() + "]");
            executors.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int countInvocations() {
        return invocationNum.get();
    }
}
