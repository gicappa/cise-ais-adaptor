package eu.cise.adaptor.sever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class is the Worker thread that will open the file containing the AIS
 * data from the classpath and that will stream on the TCP/IP socket connection
 * passed from the server the data coming from the file.
 */
public class AisFileStreamerWorker extends Thread {

    private final Socket socket;
    private final BufferedReader inputReader;

    public AisFileStreamerWorker(String filename, Socket socket) {
        this.socket = socket;
        this.inputReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
    }

    @Override
    public void interrupt() {
        super.interrupt();
        System.out.println("AISFileStreamer Interrupted");
    }

    @Override
    public void run() {
        System.out.println("* Streaming the contents to the socket");
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (inputReader.ready()) {
                out.println(inputReader.readLine());
                Thread.sleep(50);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("* Done.");
    }
}
