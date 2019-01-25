package eu.cise.adaptor.server;

import org.junit.rules.ExternalResource;

@SuppressWarnings("unused")
public class TcpServerRule extends ExternalResource {

    private TcpWorkerFactory workerFactory;
    private TcpServer testRestServer;

    @Override
    protected void before() {
        workerFactory = new TcpWorkerFactory();
        testRestServer = new TcpServer(workerFactory, 64738, 1);
        new Thread(testRestServer).start();
    }

    @Override
    protected void after() {
        testRestServer.shutdown();
    }

    public TcpWorkerFactory getWorkerFactory() {
        return workerFactory;
    }

    public TcpServer getServer() {
        return testRestServer;
    }
}
