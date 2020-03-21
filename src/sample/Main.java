package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.ClassTableController;
import sample.controller.EditClassController;

public class Main extends Application {

    public static final String WINDOW_TITLE = "Entity CRUD";
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        this.primaryStage = primaryStage;
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("view/EditClass.fxml"));
//        Parent root = loader.load();
//
//        ((EditClassController) loader.getController()).setStage(primaryStage);
//
//        primaryStage.setResizable(false);
//        primaryStage.setTitle(windowTitle);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();

        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/ClassTable.fxml"));
        Parent root = loader.load();

        ((ClassTableController) loader.getController()).setStage(primaryStage);

        primaryStage.setResizable(false);
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
