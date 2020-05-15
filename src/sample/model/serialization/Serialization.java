package sample.model.serialization;

import sample.model.Entities.Entity;
import sample.view.FieldInfo;

import java.io.IOException;
import java.util.ArrayList;

public interface Serialization {
    byte[] serializeToBytes(ArrayList<Entity> objects) throws IOException;
    void deserializeFromBytes(byte[] data, ArrayList<Entity> objects) throws IOException, ClassNotFoundException;
    SerializationType getSerializationType();
    String getFileExt();
    default String getFileExtDescription() {
        return "";
    }
}
