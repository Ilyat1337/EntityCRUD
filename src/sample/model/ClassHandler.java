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

    private static final String PACKAGE_NAME = "sample.model.Entities";
    public static final String NULL_NAME = "null";
    private static ArrayList<Class> classList;
    private static ArrayList<String> classNames;
    private static ArrayList<ArrayList<FieldInfo>> fieldsInfoList;

//    public ClassHandler(ArrayList<Entity> entities, Entity entityForEdit) {
//        classList = new ArrayList<>();
//        classNames = new ArrayList<>();
//        fieldsInfoList = new ArrayList<>();
//        getSubClassesForPackage(PACKAGE_NAME, Entity.class);
//        createFieldsInfo(entities, entityForEdit);
//    }

    public static void initialize() {
        classList = new ArrayList<>();
        classNames = new ArrayList<>();
        fieldsInfoList = new ArrayList<>();
        getSubClassesForPackage(PACKAGE_NAME, Entity.class);
        createFieldsInfo();
    }

    public static ArrayList<Class> getClassList() {
        return classList;
    }

    public static ArrayList<String> getClassNames() {
        return classNames;
    }

    public static ArrayList<FieldInfo> getFieldsInfoForClass(int classIndex) {
        return fieldsInfoList.get(classIndex);
    }

    public static Class getClassByName(String className) {
        for (int i = 0; i < classNames.size(); i++) {
            if (classNames.get(i).equals(className)) {
                return classList.get(i);
            }
        }
        return null;
    }

    public static ArrayList<FieldInfo> getFieldsInfoForClass(Class givenClass) {
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).equals(givenClass)) {
                return getFieldsInfoForClass(i);
            }
        }
        return null;
    }

    private static void getSubClassesForPackage(String packageName, Class superClass) {
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
            alert.showAndWait();
        }
    }

    private static void createFieldsInfo() {
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
                    fieldsInfo.add(getFieldInfoForSimpleClass(field));
            }
            fieldsInfoList.add(fieldsInfo);
        }
    }

    private static FieldInfo getFieldInfoForSimpleClass(Field simpleField) {
        return new FieldInfo(simpleField, simpleField.getType(), getAnnotatedName(simpleField), null, null);
    }

    private static FieldInfo getFieldsInfoForEnum(Field enumField) {
        Field[] enumValues = enumField.getType().getFields();
        ArrayList<String> fieldNames = new ArrayList<>();
        for (Field enumValue : enumValues) {
            fieldNames.add(getAnnotatedName(enumValue));
        }
        return new FieldInfo(enumField, enumField.getType(), getAnnotatedName(enumField), fieldNames, null);
    }

    public static void fillFieldInfoForEntities(ArrayList<Entity> entities, Entity entityForEdit) {
        for (ArrayList<FieldInfo> fieldsInfo : fieldsInfoList) {
            for (FieldInfo fieldInfo : fieldsInfo) {
                if (Entity.class.isAssignableFrom(fieldInfo.getFieldType())) {
                    updateFieldsInfoForEntity(fieldInfo, entities, entityForEdit);
                }
            }
        }
    }

    private static void updateFieldsInfoForEntity(FieldInfo fieldInfo, ArrayList<Entity> entities, Entity entityForEdit) {
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<Object> fieldObjects = new ArrayList<>();
        fieldNames.add(NULL_NAME);
        for (Entity entity : entities) {
            if (fieldInfo.getFieldType().isAssignableFrom(entity.getClass()) && entity != entityForEdit) {
                fieldNames.add(entity.getEntityName() + " [" + entity.toString() + "]");
                fieldObjects.add(entity);
            }
        }
        fieldInfo.setFieldValues(fieldNames);
        fieldInfo.setFieldObjects(fieldObjects);
    }

    public static String getAnnotatedName(Field field) {
        EntityAnnotation annotation = field.getAnnotation(EntityAnnotation.class);
        if (annotation != null)
            return annotation.name();
        else
            return field.getName();
    }

    public static String getAnnotatedName(Class givenClass) {
        EntityAnnotation annotation = (EntityAnnotation) givenClass.getAnnotation(EntityAnnotation.class);
        if (annotation != null)
            return annotation.name();
        else
            return givenClass.getSimpleName();
    }
}
