package sample.model.serialization;

import sample.model.Entities.Entity;
import sample.view.FieldInfo;

import java.io.IOException;
import java.util.ArrayList;

public interface Serialization {
    void serializeToFile(String fileName, ArrayList<Entity> objects) throws IOException;
    void deserializeFromFile(String fileName, ArrayList<Entity> objects) throws IOException, ClassNotFoundException;
    SerializationType getSerializationType();
    String getFileExt();
    default String getFileExtDescription() {
        return "";
    }
}
