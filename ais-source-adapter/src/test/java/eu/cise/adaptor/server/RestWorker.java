package eu.cise.adaptor.server;

public interface RestWorker extends Runnable {
    void handleRequest() throws Exception;
}
