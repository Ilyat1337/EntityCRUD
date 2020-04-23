package sample.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import sample.model.Entities.Entity;
import sample.model.Entities.EntityPlayer;
import sample.model.Entities.EntitySword;
import sample.model.Entities.EntityTool;
import sample.model.serialization.BinarySerialization;
import sample.model.serialization.Serialization;
import sample.model.serialization.SerializationHandler;
import sample.model.serialization.SerializationType;
import sample.view.ClassInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ClassTableModel {

    private ArrayList<Entity> entities;
    private SerializationHandler<Entity> serializationHandler;

    public ClassTableModel() {
        entities = new ArrayList<>();
        serializationHandler = new SerializationHandler<>();
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

    public void handleLoadFromFile(String fileName, ObservableList<ClassInfo> classInfoList) {
        SerializationType serializationType = serializationHandler.getSerializationTypeByFileName(fileName);
        if (serializationType == null) {
            showWrongSerializationAlert();
            return;
        }
        ArrayList<Entity> newEntities = new ArrayList<>();
        try {
            Serialization serialization = serializationHandler.getSerializationByType(serializationType);
            serialization.deserializeFromFile(fileName, newEntities);
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
        SerializationType serializationType = serializationHandler.getSerializationTypeByFileName(fileName);
        if (serializationType == null) {
            showWrongSerializationAlert();
            return;
        }
        try {
            Serialization serialization = serializationHandler.getSerializationByType(serializationType);
            serialization.serializeToFile(fileName, entities);
        } catch (Exception e) {
            showErrorWorkingWithFileAlert();
            return;
        }
        showSavedSuccessAlert();
    }

    private void showWrongSerializationAlert() {
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
        return serializationHandler.getExtensionFilters();
    }
}
