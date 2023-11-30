package com.nixsolutions.ppp.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class LineCountDemo {

    public static final Path PATH = Path.of("line-count.ser");
    public static final String POEM = "Fodor Ákos – 3 negatív szó\n\nNINCS\nSEMMI\nBAJ.\n";
    public static final Path POEM_PATH = Path.of("poem.txt");

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Files.writeString(POEM_PATH, POEM);

        serialize();

        byte[] bytesOfSerialized = Files.readAllBytes(PATH);
        System.out.printf("Serialized: %s%n", Arrays.toString(bytesOfSerialized));

        deserialize();
    }

    private static void serialize() throws IOException {
        try (var oos = new ObjectOutputStream(new FileOutputStream(PATH.toFile()))) {
            LineCount lineCount = new LineCount(POEM_PATH);
            System.out.printf("Original: %s%n", lineCount);
            oos.writeObject(lineCount);
        }
    }

    private static void deserialize() throws IOException, ClassNotFoundException {
        try (var ois = new ObjectInputStream(new FileInputStream(PATH.toFile()))) {
            LineCount lineCount = (LineCount) ois.readObject();
            System.out.printf("Deserialized: %s%n", lineCount);
        }
    }
}
