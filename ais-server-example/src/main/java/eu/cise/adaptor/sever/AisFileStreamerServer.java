package eu.cise.adaptor.sever;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * New server created to simulate a TCP/IP stream of AIS information
 * Some constants are defining the
 * - maximum number of threads the server will handle concurrently;
 * - the opened port;
 * - the name of the file used to fetch the AIS data from.
 */
public class AisFileStreamerServer {

    public static final int PORT = 60000;
    public static final String AIS_FILE_NAME = "/aistest.stream.txt";
    public static final int N_THREADS = 10;

    public static void main(String[] args) {
        new AisFileStreamerServer().run();
    }

    public void run() {
        ExecutorService executors = Executors.newFixedThreadPool(N_THREADS);

        System.out.println("*** AIS File Streamer Server v1.0");
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("* Listening for new client on port: " + PORT);

            while (true) {
                executors.execute(new AisFileStreamerWorker(AIS_FILE_NAME, listener.accept()));
            }
        } catch (IOException /*| InterruptedException*/ e) {
            e.printStackTrace();
        }
    }
}
