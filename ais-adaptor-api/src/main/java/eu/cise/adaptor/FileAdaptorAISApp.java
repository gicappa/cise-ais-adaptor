package eu.cise.adaptor;

import dk.tbsalling.aismessages.AISInputStreamReader;

import java.io.IOException;
import java.io.InputStream;

public class FileAdaptorAISApp {
    public static void main(String[] args) throws IOException {
        new FileAdaptorAISApp().runDemo();
    }

    public void runDemo() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/aistest.stream.txt");

        System.out.println("AISMessages Demo App");
        System.out.println("--------------------");

        AISInputStreamReader streamReader = new AISInputStreamReader(inputStream, new AISMessageHandler<>());

        streamReader.run();
    }

}
