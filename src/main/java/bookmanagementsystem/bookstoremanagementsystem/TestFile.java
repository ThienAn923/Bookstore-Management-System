package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestFile extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create a VBox
        VBox vBox = new VBox();

        // Add some HBox instances to the VBox
        for (int i = 0; i < 5; i++) {
            HBox hBox = new HBox(new Button("Button " + i));
            vBox.getChildren().add(hBox);
        }

        // Create a button to delete all children of the VBox
        Button deleteButton = new Button("Delete All Children");
        deleteButton.setOnAction(event -> {
            vBox.getChildren().clear(); // Clear all children of the VBox
        });

        // Add the delete button to the VBox
        vBox.getChildren().add(deleteButton);

        // Create a scene with the VBox as the root
        Scene scene = new Scene(vBox, 400, 300);

        // Set the scene to the stage and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("VBox with HBox Children");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
