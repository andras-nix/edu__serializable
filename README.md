# [java.io.Serializable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Serializable.html)


## Simple class
```java
import java.io.Serializable;

public class IntBox implements Serializable {
    private final int content;

    public IntBox(int content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("IntBox{content=%d}", content);
    }
}
```


## Let's play with it
```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class IntBoxDemo {

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

class IntBox implements Serializable {
    private final int content;

    public IntBox(int content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("IntBox{content=%d}", content);
    }
}
```


## Improve our subject
```java
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

class LineCount implements Serializable {
    private final Path path;
    private final long lines;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d}", path, lines);
    }
}
```


## Piece of cake
```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class LineCountDemo {

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

class LineCount implements Serializable {
    private final Path path;
    private final long lines;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d}", path, lines);
    }
}
```


## `transient` to the rescue!
```java
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

class LineCount implements Serializable {
    private final transient Path path;
    private final long lines;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d}", path, lines);
    }
}
```

## Let's customize
```java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

class LineCount implements Serializable {
    private Path path;
    private long lines;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d}", path, lines);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(path.toUri());
        out.writeLong(lines);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        path = Path.of((URI) in.readObject());
        lines = in.readLong();
    }
}
```


## Version control
```java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

class LineCount implements Serializable {

    private static int version = 1;

    private Path path;
    private long lines;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d}", path, lines);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(path.toUri());
        out.writeLong(lines);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        path = Path.of((URI) in.readObject());
        lines = in.readLong();
    }
}
```


## The real version control
```java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

class LineCount implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int version = 1;

    private Path path;
    private long lines;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d}", path, lines);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(path.toUri());
        out.writeLong(lines);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        path = Path.of((URI) in.readObject());
        lines = in.readLong();
    }
}
```


## A nice timestamp
```java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

class LineCount implements Serializable {

    private static final long serialVersionUID = 1L;

    private Path path;
    private long lines;
    private final LocalDateTime created;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
        this.created = LocalDateTime.now();
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d, created=%s}", path, lines, created);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(path.toUri());
        out.writeLong(lines);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        path = Path.of((URI) in.readObject());
        lines = in.readLong();
    }
}
```

## Fix it
```java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

class LineCount implements Serializable {

    private static final long serialVersionUID = 1L;

    private Path path;
    private long lines;
    private LocalDateTime created;

    public LineCount(Path path) throws IOException {
        this.path = path;
        this.lines = getLineCount(path);
        this.created = LocalDateTime.now();
    }

    private static long getLineCount(Path path) throws IOException {
        try (var lines = Files.lines(path)) {
            return lines.count();
        }
    }

    @Override
    public String toString() {
        return String.format("LineCount{path=%s, lines=%d, created=%s}", path, lines, created);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(path.toUri());
        out.writeLong(lines);
        out.writeObject(created);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        path = Path.of((URI) in.readObject());
        lines = in.readLong();
        created = (LocalDateTime) in.readObject();
    }
}
```
