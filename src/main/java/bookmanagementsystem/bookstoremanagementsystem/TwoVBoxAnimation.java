package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TwoVBoxAnimation extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create VBox 1
        VBox vbox1 = new VBox();
        vbox1.setStyle("-fx-background-color: lightblue;");
        vbox1.getChildren().add(new Label("VBox 1"));

        // Create VBox 2
        VBox vbox2 = new VBox();
        vbox2.setStyle("-fx-background-color: lightgreen;");
        vbox2.getChildren().add(new Label("VBox 2"));

        // StackPane to hold VBox 1 and VBox 2
        StackPane root = new StackPane();
        root.getChildren().addAll(vbox1, vbox2);

        // Set initial translation for VBox 2
        vbox2.setTranslateX(-200);

        // Set event handlers for VBox 1
        vbox1.setOnMouseEntered(event -> {
            // Translate VBox 2 when mouse enters VBox 1
            vbox2.setTranslateX(0);
        });

        vbox1.setOnMouseExited(event -> {
            // Translate VBox 2 back when mouse exits VBox 1
            vbox2.setTranslateX(-200);
        });

        // Set event handler for VBox 2
        vbox2.setOnMouseEntered(event -> {
            // Prevent translating VBox 2 back if mouse moves from VBox 1 to VBox 2
            vbox2.setOnMouseExited(null);
        });

        vbox2.setOnMouseExited(event -> {
            // Translate VBox 2 back when mouse exits VBox 2
            vbox2.setTranslateX(-200);
            // Reset event handler for VBox 2 to handle future mouse events
            vbox2.setOnMouseExited(e -> {
                vbox2.setTranslateX(-200);
            });
        });

        // Set scene and show stage
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
