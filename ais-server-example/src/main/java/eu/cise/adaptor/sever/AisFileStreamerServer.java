/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
