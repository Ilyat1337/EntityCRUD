package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.model.EditClassModel;
import sample.model.Entities.Entity;
import sample.view.EditClassView;

import java.util.ArrayList;

public class EditClassController {
    private EditClassModel model;
    private EditClassView view;

    private boolean saveClicked;

    private Stage stage;

    @FXML
    private ChoiceBox<String> cbClasses;

    @FXML
    private GridPane gpInputs;

    @FXML
    private Button btSave;

    private ArrayList<Control> inputControls;

    public EditClassController(ArrayList<Entity> entities) {
        model  = new EditClassModel(entities);
        view = new EditClassView();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Entity getCreatedEntityInfo() {
        return model.getCreatedEntity();
    }

    @FXML
    private void initialize() {
        view.fillChoiceBox(model.getClassNames(), cbClasses);
        onChoiceBoxIndexChanged(0);

        cbClasses.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> onChoiceBoxIndexChanged((int)newValue));
    }

    @FXML
    private void btSavePressed() {
        if (model.isValidInput(inputControls)) {
            model.createEntity(inputControls);
            saveClicked = true;
            stage.close();
        }
    }

    @FXML
    private void btCancelPressed() {
        stage.close();
    }

    private void onChoiceBoxIndexChanged(int selectedIndex) {
        model.setSelectedClassIndex(selectedIndex);
        drawInputsForClass();
    }

    public void setEntity(Entity entity) {
        model.setCreatedEntity(entity);
        cbClasses.getSelectionModel().select(model.getSelectedClassIndex());
        drawInputsForClass();
        view.fillInputsForObject(entity, model.getFieldsInfoForClass(), inputControls, model.getEntities());

        cbClasses.setDisable(true);
        btSave.setText("Сохранить");
    }

    private void drawInputsForClass() {
        gpInputs.getChildren().clear();
        inputControls = view.createInputsForFields(model.getFieldsInfoForClass(), gpInputs);
    }

}
