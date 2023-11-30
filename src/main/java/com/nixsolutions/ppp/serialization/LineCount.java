package com.nixsolutions.ppp.serialization;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class LineCount implements Serializable {
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