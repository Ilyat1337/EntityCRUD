package sample.model.serialization;

import sample.model.Entities.Entity;
import sample.view.FieldInfo;

import java.io.*;
import java.util.ArrayList;

public class BinarySerialization implements Serialization {
    private static final String FILE_EXT = "binary";
    private static final String EXT_DESCRIPTION = "Binary serialization";

    @Override
    public byte[] serializeToBytes(ArrayList<Entity> objects) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeInt(objects.size());
            for (Entity object : objects) {
                outputStream.writeObject(object);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public void deserializeFromBytes(byte[] data, ArrayList<Entity> objects) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            int size = inputStream.readInt();
            for (int i = 0; i < size; i++) {
                objects.add((Entity)inputStream.readObject());
            }
        }
    }

    @Override
    public SerializationType getSerializationType() {
        return SerializationType.BINARY_SERIALIZATION;
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
