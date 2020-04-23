package sample.model.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import netscape.javascript.JSObject;
import sample.model.ClassHandler;
import sample.model.Entities.Entity;
import sample.view.FieldInfo;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JsonSerialization implements Serialization {
    private static final String FILE_EXT = "*.json";
    private static final String EXT_DESCRIPTION = "JSON serialization";
    private static final String CLASS_NAME_PROPERTY = "className";

    @Override
    public void serializeToFile(String fileName, ArrayList<Entity> objects) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();// builder.create();
        JsonArray jsonArray = gson.toJsonTree(objects).getAsJsonArray();
        try {
            for (int i = 0; i < objects.size(); i++) {
                jsonArray.get(i).getAsJsonObject().addProperty(CLASS_NAME_PROPERTY,
                        ClassHandler.getAnnotatedName(objects.get(i).getClass()));
                ArrayList<FieldInfo> fieldsInfo = ClassHandler.getFieldsInfoForClass(objects.get(i).getClass());
                for (FieldInfo fieldInfo : fieldsInfo) {
                    if (Entity.class.isAssignableFrom(fieldInfo.getFieldType())) {
                        Entity entity;
                        if ((entity = (Entity) fieldInfo.getField().get(objects.get(i))) != null) {
                            jsonArray.get(i).getAsJsonObject().addProperty(fieldInfo.getField().getName(), entity.getHashCode());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException();
        }

        String jsonString = gson.toJson(jsonArray);

        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(fileName))) {
            printWriter.print(jsonString);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public void deserializeFromFile(String fileName, ArrayList<Entity> objects) throws IOException, ClassNotFoundException {
        String content = Files.readString(Paths.get(fileName), StandardCharsets.US_ASCII);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray jsonArray = gson.fromJson(content, JsonArray.class);

        ArrayList<Integer> hashes = new ArrayList<>();
        HashMap<Integer, Entity> hashToEntity = new HashMap<>();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String className = jsonObject.get(CLASS_NAME_PROPERTY).getAsString();
            Class entityClass = ClassHandler.getClassByName(className);
            jsonObject.remove(CLASS_NAME_PROPERTY);
            ArrayList<FieldInfo> fieldsInfo = ClassHandler.getFieldsInfoForClass(entityClass);
            boolean isFound = false;
            for (FieldInfo fieldInfo : fieldsInfo) {
                if (Entity.class.isAssignableFrom(fieldInfo.getFieldType())) {
                    String fieldName = fieldInfo.getField().getName();
                    if (jsonObject.has(fieldName)) {
                        String hashCode = jsonObject.get(fieldName).getAsString();
                        jsonObject.remove(fieldName);
                        hashes.add(Integer.parseInt(hashCode));
                        isFound = true;
                        break;
                    }
                }
            }
            if (!isFound)
                hashes.add(0);
            Entity entity = (Entity)gson.fromJson(jsonObject, entityClass);
            hashToEntity.put(entity.getHashCode(), entity);
            objects.add(entity);
        }

        try {
            for (int i = 0; i < hashes.size(); i++) {
                if (hashes.get(i) != 0) {
                    Entity entity = objects.get(i);
                    ArrayList<FieldInfo> fieldsInfo = ClassHandler.getFieldsInfoForClass(entity.getClass());
                    for (FieldInfo fieldInfo : fieldsInfo) {
                        if (Entity.class.isAssignableFrom(fieldInfo.getFieldType())) {
                            fieldInfo.getField().set(entity, hashToEntity.get(hashes.get(i)));
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public SerializationType getSerializationType() {
        return SerializationType.JSON_SERIALIZATION;
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
