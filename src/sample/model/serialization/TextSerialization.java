package sample.model.serialization;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import sample.model.ClassHandler;
import sample.model.Entities.Entity;
import sample.view.FieldInfo;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TextSerialization implements Serialization {
    private static final String FILE_EXT = "txt";
    private static final String EXT_DESCRIPTION = "Custom text serialization";

    @Override
    public byte[] serializeToBytes(ArrayList<Entity> objects) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream), true)) {
            HashSet<String> writtenHashes = new HashSet<>();
            for (Entity entity : objects) {
                writeObjectData(entity, printWriter, writtenHashes);
            }
            printWriter.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private void writeObjectData(Entity entity, PrintWriter printWriter, HashSet<String> writtenHashes) throws IOException {
        ArrayList<FieldInfo> fieldsInfo = ClassHandler.getFieldsInfoForClass(entity.getClass());
        printWriter.println(ClassHandler.getAnnotatedName(entity.getClass()));
        int hashCode = entity.getHashCode();
        printWriter.println(hashCode);
        if (writtenHashes.contains(String.valueOf(hashCode)))
            return;
        writtenHashes.add(String.valueOf(hashCode));
        printWriter.println(entity.getAggregationsCount());
        try {
            for (FieldInfo fieldInfo : fieldsInfo) {
                if (!Entity.class.isAssignableFrom(fieldInfo.getFieldType())) {
                    printWriter.println(fieldInfo.getField().get(entity));
                } else {
                    Entity entityField;
                    if ((entityField = (Entity)fieldInfo.getField().get(entity)) != null)
                        writeObjectData(entityField, printWriter, writtenHashes);
                    else
                        printWriter.println(ClassHandler.NULL_NAME);
                }
            }
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public void deserializeFromBytes(byte[] data, ArrayList<Entity> objects) throws IOException, ClassNotFoundException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)))) {
            HashMap<String, Entity> storedEntities = new HashMap<>();
            String className;
            while ((className = bufferedReader.readLine()) != null) {
                objects.add(readObjectData(className, storedEntities, bufferedReader));
            }
        }
    }

    private Entity readObjectData(String className, HashMap<String, Entity> storedEntities, BufferedReader bufferedReader) throws IOException {
        Class entityClass = ClassHandler.getClassByName(className);
        Constructor constructor = entityClass.getConstructors()[0];
        try {
            Entity entity = (Entity) constructor.newInstance();
            String hasCode = bufferedReader.readLine();
            if (storedEntities.containsKey(hasCode))
                return storedEntities.get(hasCode);

            storedEntities.put(hasCode, entity);
            entity.setHashCode(Integer.parseInt(hasCode));
            String aggregationsCount = bufferedReader.readLine();
            entity.setAggregationsCount(Integer.parseInt(aggregationsCount));

            ArrayList<FieldInfo> fieldsInfo = ClassHandler.getFieldsInfoForClass(entity.getClass());
            for (FieldInfo fieldInfo : fieldsInfo) {
                String fieldValue = bufferedReader.readLine();
                if (fieldInfo.getFieldType().equals(int.class))
                    fieldInfo.getField().set(entity, Integer.parseInt(fieldValue));
                else if (fieldInfo.getFieldType().equals(double.class))
                    fieldInfo.getField().set(entity, Double.parseDouble(fieldValue));
                else if (fieldInfo.getFieldType().equals(String.class))
                    fieldInfo.getField().set(entity, fieldValue);
                else if (fieldInfo.getFieldType().equals(boolean.class))
                    fieldInfo.getField().set(entity, Boolean.parseBoolean(fieldValue));
                else if (fieldInfo.getFieldType().isEnum())
                    fieldInfo.getField().set(entity, Enum.valueOf((Class<Enum>) fieldInfo.getFieldType(), fieldValue));
                else if (Entity.class.isAssignableFrom(fieldInfo.getFieldType())) {
                    if (fieldValue.equals(ClassHandler.NULL_NAME))
                        fieldInfo.getField().set(entity, null);
                    else
                        fieldInfo.getField().set(entity, readObjectData(fieldValue, storedEntities, bufferedReader));
                }
            }

            return entity;
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public SerializationType getSerializationType() {
        return SerializationType.USER_SERIALIZATION;
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
