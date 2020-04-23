package sample.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class FieldInfo {
    private Field field;
    private Class fieldType;
    private String fieldName;
    private ArrayList<String> fieldValues;
    private ArrayList<Object> fieldObjects;

    public FieldInfo(Field field, Class fieldType, String fieldName, ArrayList<String> fieldValues, ArrayList<Object> fieldObjects) {
        this.field = field;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.fieldValues = fieldValues;
        this.fieldObjects = fieldObjects;

//        System.out.println(fieldType.getName());
//        System.out.println(fieldName);
//        if (fieldValues != null)
//            System.out.println(Arrays.toString(fieldValues.toArray()));
//        System.out.println();
    }

    public Field getField() {
        return field;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public ArrayList<String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(ArrayList<String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public ArrayList<Object> getFieldObjects() {
        return fieldObjects;
    }

    public void setFieldObjects(ArrayList<Object> fieldObjects) {
        this.fieldObjects = fieldObjects;
    }
}
