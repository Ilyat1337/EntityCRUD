package sample.view;

import javafx.beans.property.SimpleStringProperty;

public class ClassInfo {
    private SimpleStringProperty classNumber;
    private SimpleStringProperty className;

    public ClassInfo(String classNumber, String className) {
        this.classNumber = new SimpleStringProperty(classNumber);
        this.className = new SimpleStringProperty(className);
    }

    public SimpleStringProperty classNumberProperty() {
        return classNumber;
    }

    public SimpleStringProperty classNameProperty() {
        return className;
    }
}
