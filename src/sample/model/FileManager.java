package sample.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {
    public static byte[] readBytesFormFile(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    public static void writeBytesToFile(byte[] data, String fileName) throws IOException {
        Files.write(Paths.get(fileName), data);
    }
}
