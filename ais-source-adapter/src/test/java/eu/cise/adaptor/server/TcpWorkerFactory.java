package eu.cise.adaptor.server;

import java.net.Socket;

public class TcpWorkerFactory {

    private String authString = "AUTH=admin:secret";

    TcpWorker getWorker(Socket socket) {
        return new AuthTcpWorker(authString, socket);
    }

    public void setAuthString(String authString) {
        this.authString = authString;
    }
}
