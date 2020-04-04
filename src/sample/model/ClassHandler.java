package sample.model;

import javafx.scene.control.Alert;
import sample.Main;
import sample.model.Entities.Entity;
import sample.model.Entities.EntityAnnotation;
import sample.view.FieldInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class ClassHandler {

    private final String PACKAGE_NAME = "sample.model.Entities";
    private final String NULL_NAME = "null";
    private ArrayList<Class> classList;
    private ArrayList<String> classNames;
    private ArrayList<ArrayList<FieldInfo>> fieldsInfoList;

    public ClassHandler(ArrayList<Entity> entities, Entity entityForEdit) {
        classList = new ArrayList<>();
        classNames = new ArrayList<>();
        fieldsInfoList = new ArrayList<>();
        getSubClassesForPackage(PACKAGE_NAME, Entity.class);
        createFieldsInfo(entities, entityForEdit);
    }

    public ArrayList<Class> getClassList() {
        return classList;
    }

    public ArrayList<String> getClassNames() {
        return classNames;
    }

    public ArrayList<FieldInfo> getFieldsInfoForClass(int classIndex) {
        return fieldsInfoList.get(classIndex);
    }

    private void getSubClassesForPackage(String packageName, Class superClass) {
        try {
            ArrayList<Class> classes = new ArrayList<>();
            ClassLoader classLoader = Main.class.getClassLoader();
            URL uPackage = classLoader.getResource(packageName.replaceAll("[.]", "/"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream) uPackage.getContent()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.endsWith(".class")) {
                    String className = packageName + "." + line.substring(0, line.lastIndexOf('.'));
                    Class currClass = Class.forName(className);
                    if (superClass.isAssignableFrom(currClass)) {
                        classes.add(currClass);
                        //System.out.println(className);в
                    }
                }
            }

            //System.out.println();

            HashSet<Class> superClasses = new HashSet<>(25);
            for (Class classIter : classes)
                superClasses.add(classIter.getSuperclass());

            for (Class classIter : classes)
                if (!superClasses.contains(classIter)) {
                    classList.add(classIter);
                    classNames.add(getAnnotatedName(classIter));
                    //System.out.println(classIter.getName());
                }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Ошибка во время загрузки классов из файла!");
        }
    }

    private void createFieldsInfo(ArrayList<Entity> entities, Entity entityForEdit) {
        for (Class classIter : classList) {
            ArrayList<FieldInfo> fieldsInfo = new ArrayList<>();
            Field[] fields = classIter.getFields();
            for (Field field : fields) {
                if (field.getType().equals(int.class) || field.getType().equals(double.class)
                        || field.getType().equals(String.class) || field.getType().equals(boolean.class))
                    fieldsInfo.add(getFieldInfoForSimpleClass(field));
                else if (field.getType().isEnum())
                    fieldsInfo.add(getFieldsInfoForEnum(field));
                else if (Entity.class.isAssignableFrom(field.getType()))
                    fieldsInfo.add(getFieldsInfoForEntity(field, entities, entityForEdit));
            }
            fieldsInfoList.add(fieldsInfo);
        }
    }

    private FieldInfo getFieldInfoForSimpleClass(Field simpleField) {
        return new FieldInfo(simpleField, simpleField.getType(), getAnnotatedName(simpleField), null, null);
    }

    private FieldInfo getFieldsInfoForEnum(Field enumField) {
        Field[] enumValues = enumField.getType().getFields();
        ArrayList<String> fieldNames = new ArrayList<>();
        for (Field enumValue : enumValues) {
            fieldNames.add(getAnnotatedName(enumValue));
        }
        return new FieldInfo(enumField, enumField.getType(), getAnnotatedName(enumField), fieldNames, null);
    }

    private FieldInfo getFieldsInfoForEntity(Field classField, ArrayList<Entity> entities, Entity entityForEdit) {
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<Object> fieldObjects = new ArrayList<>();
        fieldNames.add(NULL_NAME);
        for (Entity entity : entities) {
            if (classField.getType().isAssignableFrom(entity.getClass()) && entity != entityForEdit) {
                fieldNames.add(entity.getEntityName() + " [" + entity.toString() + "]");
                fieldObjects.add(entity);
            }
        }
        return new FieldInfo(classField, classField.getType(), getAnnotatedName(classField), fieldNames, fieldObjects);
    }

    private String getAnnotatedName(Field field) {
        EntityAnnotation annotation = field.getAnnotation(EntityAnnotation.class);
        if (annotation != null)
            return annotation.name();
        else
            return field.getName();
    }

    private String getAnnotatedName(Class givenClass) {
        EntityAnnotation annotation = (EntityAnnotation) givenClass.getAnnotation(EntityAnnotation.class);
        if (annotation != null)
            return annotation.name();
        else
            return givenClass.getSimpleName();
    }
}
