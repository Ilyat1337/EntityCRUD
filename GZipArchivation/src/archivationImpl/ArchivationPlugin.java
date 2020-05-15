package archivationImpl;

import archivation.Archivation;
import archivation.ArchivationType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ArchivationPlugin implements Archivation {
    private static final String FILE_EXT = "gz";
    private static final String EXT_DESCRIPTION = "GZip archivation";

    @Override
    public byte[] archiveData(byte[] bytes, String entryName) throws IOException {
        byte[] data;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream zipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            zipOutputStream.write(bytes);
            zipOutputStream.close();
            data = byteArrayOutputStream.toByteArray();
        }
        return data;
    }

    @Override
    public byte[] unzipData(byte[] bytes) throws IOException{
        byte[] data;
        try (GZIPInputStream zipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            data = zipInputStream.readAllBytes();
        }
        return data;
    }

    @Override
    public ArchivationType getArchivationType() {
        return ArchivationType.GZIP_ARCHIVATION;
    }

    @Override
    public String getFileExt() {
        return FILE_EXT;
    }

    @Override
    public String getFileExtDescription() {
        return EXT_DESCRIPTION;
    }
}
