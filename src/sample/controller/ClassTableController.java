package sample.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Main;
import sample.model.ClassTableModel;
import sample.model.Entities.Entity;
import sample.model.Entities.EntitySword;
import sample.model.Entities.EntityTool;
import sample.view.ClassInfo;

public class ClassTableController {

    private ClassTableModel model = new ClassTableModel();

    private Stage stage;

    @FXML
    private TableView<ClassInfo> tvClasses;

    @FXML
    private TableColumn<ClassInfo, String> tcNumber;

    @FXML
    private TableColumn<ClassInfo, String> tcName;

    @FXML
    private Button btDelete;

    @FXML
    private Button btChange;

    @FXML
    private Button btAdd;

    private ObservableList<ClassInfo> classInfoList = FXCollections.observableArrayList();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        tcNumber.setCellValueFactory(cellData -> cellData.getValue().classNumberProperty());
        tcName.setCellValueFactory(cellData -> cellData.getValue().classNameProperty());
        tvClasses.setItems(classInfoList);
        addContextMenu();
    }


    @FXML
    private void btAddPressed() {
        Entity createdEntity = openEditClassForm(null);
        if (createdEntity != null) {
            model.addEntity(createdEntity);
            classInfoList.add(new ClassInfo(createdEntity.toString(), createdEntity.getEntityName()));
        }
    }

    @FXML
    private void btEditPressed() {
        int selectedIndex;
        if ((selectedIndex = tvClasses.getSelectionModel().getSelectedIndex()) >= 0) {
            openEditClassForm(model.getEntities().get(selectedIndex));
            classInfoList.get(selectedIndex).classNameProperty().set(model.getEntities().get(selectedIndex).getEntityName());
        }
    }

    @FXML
    private void btDeletePressed() {
        int selectedIndex;
        if ((selectedIndex = tvClasses.getSelectionModel().getSelectedIndex()) >= 0) {
            model.handleDeleteClass(selectedIndex);
            classInfoList.remove(selectedIndex);
        }
    }

    private Entity openEditClassForm(Entity entityForEdit) {
        try {
            EditClassController controller = new EditClassController(model.getEntities(), entityForEdit);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/EditClass.fxml"));
            loader.setController(controller);
            AnchorPane page = loader.load();

            Stage editStage = new Stage();
            editStage.setTitle(Main.WINDOW_TITLE);
            editStage.initModality(Modality.WINDOW_MODAL);
            editStage.initOwner(stage);
            editStage.setResizable(false);
            Scene scene = new Scene(page);
            editStage.setScene(scene);
            controller.setStage(editStage);

            if (entityForEdit != null)
                controller.setEntity(entityForEdit);
            editStage.showAndWait();

            if (controller.isSaveClicked())
                return controller.getCreatedEntityInfo();
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void addContextMenu() {
        ContextMenu cmChange = new ContextMenu();
        MenuItem miEdit = new MenuItem("Изменить");
        miEdit.setOnAction(click -> btEditPressed());
        cmChange.getItems().add(miEdit);
        MenuItem miDelete = new MenuItem("Удалить");
        miDelete.setOnAction(click -> btDeletePressed());
        cmChange.getItems().add(miDelete);

        tvClasses.setRowFactory(new Callback<TableView<ClassInfo>, TableRow<ClassInfo>>() {
            @Override
            public TableRow<ClassInfo> call(TableView<ClassInfo> tableView) {
                final TableRow<ClassInfo> row = new TableRow<>();
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu)null)
                                .otherwise(cmChange)
                );
                return row;
            }
        });
    }

}
