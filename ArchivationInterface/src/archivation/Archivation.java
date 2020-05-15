package archivation;

import java.io.IOException;

public interface Archivation {
    byte[] archiveData(byte[] data, String entryName) throws IOException;
    byte[] unzipData(byte[] data) throws IOException;
    ArchivationType getArchivationType();
    String getFileExt();
    default String getFileExtDescription() {
        return "";
    }
}
