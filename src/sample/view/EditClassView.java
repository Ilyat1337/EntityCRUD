package sample.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.model.Entities.Entity;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class EditClassView {

    public void fillChoiceBox(ArrayList<String> classNames, ChoiceBox<String> cbClasses) {
        for (String className : classNames)
            cbClasses.getItems().add(className);
        cbClasses.getSelectionModel().selectFirst();
    }

    public ArrayList<Control> createInputsForFields(ArrayList<FieldInfo> fieldsInfo, GridPane gpInputs) {
        ArrayList<Control> inputs = new ArrayList<>();
        for (FieldInfo fieldInfo : fieldsInfo) {
            if (fieldInfo.getFieldType().equals(int.class) || fieldInfo.getFieldType().equals(double.class)
                || fieldInfo.getFieldType().equals(String.class))
                inputs.add(addTextField(fieldInfo, gpInputs));
            else
                if (fieldInfo.getFieldType().isEnum() || Entity.class.isAssignableFrom(fieldInfo.getFieldType()))
                    inputs.add(addChoiceBox(fieldInfo, gpInputs));
                else
                    if (fieldInfo.getFieldType().equals(boolean.class))
                        inputs.add(addCheckBox(fieldInfo, gpInputs));
        }
        return inputs;
    }

    private TextField addTextField(FieldInfo fieldInfo, GridPane gridPane) {
        TextField textField = new TextField();
        textField.setPromptText(fieldInfo.getFieldType().getSimpleName());
        gridPane.addRow(gridPane.getRowCount(), new Label(fieldInfo.getFieldName()), textField);
        return textField;
    }

    private ChoiceBox addChoiceBox(FieldInfo fieldInfo, GridPane gridPane) {
        ChoiceBox choiceBox = new ChoiceBox();
        for (String enumElement : fieldInfo.getFieldValues())
            choiceBox.getItems().add(enumElement);
        gridPane.addRow(gridPane.getRowCount(), new Label(fieldInfo.getFieldName()), choiceBox);
        choiceBox.getSelectionModel().selectFirst();
        return choiceBox;
    }

    private CheckBox addCheckBox(FieldInfo fieldInfo, GridPane gridPane) {
        CheckBox checkBox = new CheckBox();
        gridPane.addRow(gridPane.getRowCount(), new Label(fieldInfo.getFieldName()), checkBox);
        return checkBox;
    }

    public void fillInputsForObject(Entity entity, ArrayList<FieldInfo> fieldsInfo, ArrayList<Control> inputs,
                                    ArrayList<Entity> entities) {
        int fieldIndex = 0;
        for (FieldInfo fieldInfo : fieldsInfo) {
            try {
                if (fieldInfo.getFieldType().equals(int.class) || fieldInfo.getFieldType().equals(double.class))
                    ((TextField) inputs.get(fieldIndex)).setText(String.valueOf(fieldInfo.getField().get(entity)));
                else if (fieldInfo.getFieldType().equals(String.class))
                    ((TextField) inputs.get(fieldIndex)).setText((String)fieldInfo.getField().get(entity));
                else if (fieldInfo.getFieldType().equals(boolean.class))
                    ((CheckBox) inputs.get(fieldIndex)).setSelected((boolean)fieldInfo.getField().get(entity));
                else if (fieldInfo.getFieldType().isEnum())
                    ((ChoiceBox) inputs.get(fieldIndex)).getSelectionModel().select(
                            getEnumIndex(fieldInfo.getFieldType(),
                            (Enum)fieldInfo.getField().get(entity))
                    );
                else if (Entity.class.isAssignableFrom(fieldInfo.getFieldType()))
                    ((ChoiceBox) inputs.get(fieldIndex)).getSelectionModel().select(
                            getSelectedClassIndex(
                            fieldInfo.getFieldType(),
                            (Entity)fieldInfo.getField().get(entity),
                            entities
                            )
                    );

            } catch (Exception e) {
            }
            fieldIndex++;
        }
    }

    private int getEnumIndex(Class fieldClass, Enum enumField) {
        Object[] objects = fieldClass.getEnumConstants();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].equals(enumField))
                return i;
        }
        return 0;
    }

    private int getSelectedClassIndex(Class fieldClass, Entity entity, ArrayList<Entity> entities) {
        if (entity == null)
            return 0;
        int suitableClassCount = 0;
        for (Entity entityIter : entities) {
            if(fieldClass.isAssignableFrom(entityIter.getClass()))
            {
                suitableClassCount++;
                if (entity == entityIter)
                    return suitableClassCount;
            }
        }
        return 0;
    }
}
