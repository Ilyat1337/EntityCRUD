package sample.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class FieldInfo {
    private Field field;
    private Class fieldType;
    private String fieldName;
    private ArrayList<String> fieldValues;

    public FieldInfo(Field field, Class fieldType, String fieldName, ArrayList<String> fieldValues) {
        this.field = field;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.fieldValues = fieldValues;

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
}
