package eu.cise.adaptor.server;

import java.net.Socket;

public class RestWorkerFactory {

    public RestWorker getWorker(Socket socket) {
        return new AuthRestWorker(socket);
    }
}
