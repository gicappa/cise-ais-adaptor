package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

public class AISTrackFilter {

    private final InputStream inputStream;
    private final Map<String, Integer> mmsis;
    private final FileOutputStream outputStream;

    List<Integer> selectedMMSI = Arrays.asList(
            257018140, 251244110, 123456789, 257155500, 251495110,
            619015000, 367111320, 251850240, 338169566, 367649230);
    private int n;

    public AISTrackFilter() throws FileNotFoundException {
        n = 0;
        inputStream = getClass().getResourceAsStream("/aistest.stream.txt");
        mmsis = new HashMap<>();
        outputStream = new FileOutputStream("./filtered.ais.stream.txt");
    }

    public static void main(String[] args) throws IOException {
        new AISTrackFilter().run();
    }

    public void run() throws IOException {
//        printMostUsedMMSI();
        filterAISMessages();
    }

    private void filterAISMessages() throws IOException {
        new AISInputStreamReader(inputStream, aisMessage -> filter(aisMessage)).run();
    }

    private void filter(AISMessage aisMessage) {
        print().accept("#");

        if (!selectedMMSI.contains(aisMessage.getSourceMmsi().getMMSI()))
            return;

        Arrays.stream(aisMessage.getNmeaMessages())
                .map(NMEAMessage::getRawMessage)
                .map(ms -> ms + "\n")
                .map(String::getBytes)
                .forEach(this::write);
    }

    private Consumer<String> print() {
        if (n++ == 80) {
            n = 0;
            return System.out::println;
        }

        return System.out::print;
    }

    private void write(byte[] b) {
        try {
            outputStream.write(b);
        } catch (IOException e) {

        }
    }

    private void printMostUsedMMSI() throws IOException {
        new AISInputStreamReader(inputStream, aisMessage -> update(aisMessage)).run();
        System.out.printf("Results:");
        Map<String, Integer> sortedMap = mmsis.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));


        sortedMap.forEach((k, v) -> {
            System.out.println("MMSI: " + k + " / hit number: " + v);
        });
    }

    private void update(AISMessage aisMessage) {
        String key = aisMessage.getSourceMmsi().toString();
        Integer hits = mmsis.getOrDefault(key, 0);
        mmsis.put(key, ++hits);
        System.out.print("#");
    }
}
