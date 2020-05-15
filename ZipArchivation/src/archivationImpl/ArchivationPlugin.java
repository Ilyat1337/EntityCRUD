package archivationImpl;

import archivation.Archivation;
import archivation.ArchivationType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ArchivationPlugin implements Archivation {
    private static final String FILE_EXT = "zip";
    private static final String EXT_DESCRIPTION = "Zip archivation";

    @Override
    public byte[] archiveData(byte[] bytes, String entryName) throws IOException {
        byte[] data;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            ZipEntry entry = new ZipEntry(entryName);
            entry.setSize(bytes.length);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(bytes);
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            data = byteArrayOutputStream.toByteArray();
        }
        return data;
    }

    @Override
    public byte[] unzipData(byte[] bytes) throws IOException{
        byte[] data;
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            data = zipInputStream.readAllBytes();
            zipInputStream.closeEntry();
        }
        return data;
    }

    @Override
    public ArchivationType getArchivationType() {
        return ArchivationType.ZIP_ARCHIVATION;
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
