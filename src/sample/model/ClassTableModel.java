package sample.model;

import archivation.Archivation;
import archivation.ArchivationType;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import sample.model.Entities.Entity;
import sample.model.Entities.EntityPlayer;
import sample.model.Entities.EntitySword;
import sample.model.Entities.EntityTool;
import sample.model.archivation.ArchivationHandler;
import sample.model.serialization.BinarySerialization;
import sample.model.serialization.Serialization;
import sample.model.serialization.SerializationHandler;
import sample.model.serialization.SerializationType;
import sample.view.ClassInfo;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class ClassTableModel {

    private ArrayList<Entity> entities;
    private SerializationHandler<Entity> serializationHandler;
    private ArchivationHandler archivationHandler;

    public ClassTableModel() {
        entities = new ArrayList<>();
        serializationHandler = new SerializationHandler<>();
        archivationHandler = new ArchivationHandler();
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(int entityIndex) {
        entities.remove(entityIndex);
    }

    public void handleDeleteClass(int selectedIndex) {
        Entity entityToDelete = entities.get(selectedIndex);
        if (entityToDelete.isAggregated())
            nullParentLinks(entityToDelete);
        decrementAggregationsCount(entityToDelete);
        entities.remove(selectedIndex);
    }

    private void nullParentLinks(Entity entityToDelete) {
        for (Entity entity : entities) {
            Field[] fields = entity.getClass().getFields();
            for (Field field : fields) {
                try {
                    if (field.getType().isAssignableFrom(entityToDelete.getClass()))
                        field.set(entity, null);
                } catch (Exception e) {
                }
            }
        }
    }

    private void decrementAggregationsCount(Entity entityToDelete) {
        Field[] fields = entityToDelete.getClass().getFields();
        for (Field field : fields) {
            try {
                if (Entity.class.isAssignableFrom(field.getType()))
                    ((Entity) field.get(entityToDelete)).decrementAggregations();
            } catch (Exception e) {
            }
        }
    }

    private class DataModifiers {
        Serialization serialization;
        Archivation archivation;
    }

    public void handleLoadFromFile(String fileName, ObservableList<ClassInfo> classInfoList) {
        DataModifiers dataModifiers = parseFileExt(fileName);
        if (dataModifiers.serialization == null) {
            showWrongExtensionAlert();
            return;
        }
        ArrayList<Entity> newEntities = new ArrayList<>();
        try {
            byte[] data = FileManager.readBytesFormFile(fileName);
            if (dataModifiers.archivation != null)
                data = dataModifiers.archivation.unzipData(data);
            dataModifiers.serialization.deserializeFromBytes(data, newEntities);
        } catch (Exception e) {
            showErrorWorkingWithFileAlert();
            return;
        }
        entities = newEntities;
        classInfoList.clear();
        for (Entity entity : entities) {
            classInfoList.add(new ClassInfo(entity.toString(), entity.getEntityName()));
        }
    }

    public void handleSaveToFile(String fileName) {
        DataModifiers dataModifiers = parseFileExt(fileName);
        if (dataModifiers.serialization == null) {
            showWrongExtensionAlert();
            return;
        }
        try {
            byte[] data = dataModifiers.serialization.serializeToBytes(entities);
            if (dataModifiers.archivation != null)
                data = dataModifiers.archivation.archiveData(data, removeArchivationExt(fileName));
            FileManager.writeBytesToFile(data, fileName);
        } catch (Exception e) {
            showErrorWorkingWithFileAlert();
            return;
        }
        showSavedSuccessAlert();
    }

    private DataModifiers parseFileExt(String fileName) {
        DataModifiers dataModifiers = new DataModifiers();

        String[] fileParts = fileName.split("\\.");
        if (fileParts.length == 1)
            return dataModifiers;
        String archivationExt = null;
        String serializationExt = null;
        if (fileParts.length == 2)
            serializationExt = fileParts[1];
        else {
            serializationExt = fileParts[fileParts.length - 2];
            archivationExt = fileParts[fileParts.length - 1];
        }

        SerializationType serializationType = serializationHandler.getSerializationTypeByExt(serializationExt);
        if (serializationType == null)
            return dataModifiers;
        dataModifiers.serialization = serializationHandler.getSerializationByType(serializationType);

        if (archivationExt == null)
            return dataModifiers;

        ArchivationType archivationType = archivationHandler.getSerializationTypeByExt(archivationExt);
        if (archivationType == null)
            return dataModifiers;
        dataModifiers.archivation = archivationHandler.getArchivationByType(archivationType);

        return dataModifiers;
    }

    private String removeArchivationExt(String fileName) {
        fileName = new File(fileName).getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private void showWrongExtensionAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Неверное расширение");
        alert.setHeaderText("Выбрано неверное расширение.");
        alert.setContentText("Выберите одно из предложенных расширений.");
        alert.showAndWait();
    }

    private void showErrorWorkingWithFileAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка при работе с файлом.");
        alert.setContentText("Выберите другой файл.");
        alert.showAndWait();
    }

    private void showSavedSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText("Операция завершена успешно.");
        alert.setContentText("Файл успешно сохранён.");
        alert.showAndWait();
    }

    public ArrayList<FileChooser.ExtensionFilter> getExtensionFilters() {
        ArrayList<Pair> serializationExtensions = serializationHandler.getExtensionPairs();
        ArrayList<Pair> archivationExtensions = archivationHandler.getExtensionPairs();

        ArrayList<FileChooser.ExtensionFilter> extensionFilters = new ArrayList<>();
        for (Pair serializationExtension : serializationExtensions) {
            extensionFilters.add(new FileChooser.ExtensionFilter((String)serializationExtension.getValue(),
                                                    "*." + serializationExtension.getKey()));
        }

        if (archivationExtensions.isEmpty())
            return extensionFilters;
        for (Pair archivationExtension : archivationExtensions) {
            for (Pair serializationExtension : serializationExtensions) {
                extensionFilters.add(new FileChooser.ExtensionFilter(
                        serializationExtension.getValue() + "+" + archivationExtension.getValue(),
                        "*." + serializationExtension.getKey() + "." + archivationExtension.getKey()));
            }
        }
        return extensionFilters;
    }

    public void loadPlugins() {
        archivationHandler.loadPlugins();
    }

    public void unloadPlugins() {
        archivationHandler.unloadPlugins();
    }
}
