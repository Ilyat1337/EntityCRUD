package sample.model;

import javafx.scene.control.*;
import sample.model.Entities.Entity;
import sample.model.Entities.EntityPlayer;
import sample.view.FieldInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class EditClassModel {

    private final String ERROR_MESSAGE = "Неверное значение типа %s в поле %s.";

    private ClassHandler classHandler;
    private Entity createdEntity;
    private int selectedClassIndex;

    public EditClassModel(ArrayList<Entity> entities, Entity entityForEdit) {
        this.entities = entities;
        classHandler = new ClassHandler(entities, entityForEdit);
    }

    public Entity getCreatedEntity() {
        return createdEntity;
    }

    private ArrayList<Entity> entities;

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void setCreatedEntity(Entity entity) {
        createdEntity = entity;
        setSelectedClassIndex(getIndexForClass(entity.getClass()));
    }

    public void setSelectedClassIndex(int classIndex) {
        selectedClassIndex = classIndex;
    }

    public int getSelectedClassIndex() {
        return selectedClassIndex;
    }

    public ArrayList<Class> getClassList() {
        return classHandler.getClassList();
    }

    public ArrayList<String> getClassNames() {
        return classHandler.getClassNames();
    }

    public ArrayList<FieldInfo> getFieldsInfoForClass() {
        return classHandler.getFieldsInfoForClass(selectedClassIndex);
    }

    private int getIndexForClass(Class givenClass) {
        return classHandler.getClassList().indexOf(givenClass);
    }

    public boolean isValidInput(ArrayList<Control> inputControls) {
        ArrayList<FieldInfo> fieldInfos = getFieldsInfoForClass();
        String errorString = "";
        int currFiledIndex = 0;
        boolean isValid = true;
        for (FieldInfo fieldInfo : fieldInfos) {
            try {
                if (fieldInfo.getFieldType().equals(int.class))
                    Integer.parseInt(((TextField) inputControls.get(currFiledIndex)).getText());
                else if (fieldInfo.getFieldType().equals(double.class))
                    Double.parseDouble(((TextField) inputControls.get(currFiledIndex)).getText());
                else if (fieldInfo.getFieldType().equals(String.class))
                    ((TextField) inputControls.get(currFiledIndex)).getText().charAt(0);
            } catch (Exception e) {
                isValid = false;
                errorString += (String.format(ERROR_MESSAGE, fieldInfo.getFieldType().getSimpleName(), fieldInfo.getFieldName()) + "\n");
            }
            currFiledIndex++;
        }
        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Неверные значения");
            alert.setHeaderText("Пожалуйства, исправьте значения следующих полей.");
            alert.setContentText(errorString);

            alert.showAndWait();
        }
        return isValid;
    }

    public void createEntity(ArrayList<Control> inputControls) {
        Class selectedClass = getClassList().get(selectedClassIndex);
        if (createdEntity == null)
            try {
                Constructor constructor = selectedClass.getConstructors()[0];
                createdEntity = (Entity)constructor.newInstance();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Невозможно создать выбранный класс.");
                alert.showAndWait();
                return;
            }

        int currFieldIndex = 0;
        ArrayList<FieldInfo> fieldInfos = getFieldsInfoForClass();
        for (FieldInfo fieldInfo : fieldInfos) {
            try {
                if (fieldInfo.getFieldType().equals(int.class))
                    fieldInfo.getField().set(createdEntity, Integer.parseInt(((TextField) inputControls.get(currFieldIndex)).getText()));
                else if (fieldInfo.getFieldType().equals(double.class))
                    fieldInfo.getField().set(createdEntity, Double.parseDouble(((TextField) inputControls.get(currFieldIndex)).getText()));
                else if (fieldInfo.getFieldType().equals(String.class))
                    fieldInfo.getField().set(createdEntity, ((TextField) inputControls.get(currFieldIndex)).getText());
                else if (fieldInfo.getFieldType().equals(boolean.class))
                    fieldInfo.getField().set(createdEntity, ((CheckBox) inputControls.get(currFieldIndex)).isSelected());
                else if (fieldInfo.getFieldType().isEnum())
                    fieldInfo.getField().set(createdEntity,
                            fieldInfo.getField().getType().getEnumConstants()[
                            ((ChoiceBox) inputControls.get(currFieldIndex)).getSelectionModel().getSelectedIndex()
                    ]);
                else if (Entity.class.isAssignableFrom(fieldInfo.getFieldType())) {
                    Entity selectedEntity = getSelectedClass(
                            fieldInfo.getFieldObjects(),
                            ((ChoiceBox) inputControls.get(currFieldIndex)).getSelectionModel().getSelectedIndex()
                    );
                    Entity setEntity = (Entity)fieldInfo.getField().get(createdEntity);
                    if (setEntity != selectedEntity) {
                        if (setEntity != null)
                            setEntity.decrementAggregations();
                        if (selectedEntity != null)
                            selectedEntity.incrementAggregations();
                    }
                    fieldInfo.getField().set(createdEntity, selectedEntity);
                }
            } catch (Exception e) {
            }
            currFieldIndex++;
        }
    }

    Entity getSelectedClass(ArrayList<Object> fieldObjects, int selectedIndex) {
        if (selectedIndex == 0)
            return null;
        return (Entity)fieldObjects.get(selectedIndex - 1);
    }

}
