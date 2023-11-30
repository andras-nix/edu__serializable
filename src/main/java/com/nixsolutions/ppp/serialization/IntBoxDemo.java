package com.nixsolutions.ppp.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class IntBoxDemo {

    public static final Path PATH = Path.of("box.ser");

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try (var oos = new ObjectOutputStream(new FileOutputStream(PATH.toFile()))) {
            IntBox box = new IntBox(214748364);
            System.out.printf("Original: %s%n", box);
            oos.writeObject(box);
        }

        byte[] bytesOfSerialized = Files.readAllBytes(PATH);
        System.out.printf("Serialized: %s%n", Arrays.toString(bytesOfSerialized));

        try (var ois = new ObjectInputStream(new FileInputStream(PATH.toFile()))) {
            IntBox box = (IntBox) ois.readObject();
            System.out.printf("Deserialized: %s%n", box);
        }
    }
}