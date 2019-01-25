package eu.cise.adaptor.server;

public interface TcpWorker extends Runnable {
    void handleRequest() throws Exception;
}
