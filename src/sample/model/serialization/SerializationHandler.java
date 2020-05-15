package sample.model.serialization;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import sample.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SerializationHandler<T> {
    private static final String PACKAGE_NAME = "sample.model.serialization";
    private Map<SerializationType, Serialization> serializations;

    public SerializationHandler() {
        serializations = new HashMap<>();
        fillSerializationsMap(PACKAGE_NAME, Serialization.class);
    }

    private void fillSerializationsMap(String packageName, Class superClass) {
        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            URL uPackage = classLoader.getResource(packageName.replaceAll("[.]", "/"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream) uPackage.getContent()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.endsWith(".class")) {
                    String className = packageName + "." + line.substring(0, line.lastIndexOf('.'));
                    Class currClass = Class.forName(className);
                    if (superClass.isAssignableFrom(currClass) && !currClass.isInterface()) {
                        //classes.add(currClass);
                        try {
                            Serialization serialization = (Serialization) currClass.getConstructors()[0].newInstance();
                            serializations.put(serialization.getSerializationType(), serialization);
                        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Ошибка во время загрузки классов из файла!");
        }
    }

    public Serialization getSerializationByType(SerializationType serializationType) {
        return serializations.get(serializationType);
    }

    public ArrayList<Pair> getExtensionPairs() {
        ArrayList<Pair> serializationExtensions = new ArrayList<>();
        for (Map.Entry<SerializationType, Serialization> entry: serializations.entrySet()) {
            serializationExtensions.add(new Pair<>(entry.getValue().getFileExt(),
                    entry.getValue().getFileExtDescription()));
        }
        return serializationExtensions;
    }

    public SerializationType getSerializationTypeByExt(String ext) {
        for (Map.Entry<SerializationType, Serialization> entry: serializations.entrySet()) {
            if (ext.equals(entry.getValue().getFileExt()))
                return entry.getValue().getSerializationType();
        }
        return null;
    }
}
