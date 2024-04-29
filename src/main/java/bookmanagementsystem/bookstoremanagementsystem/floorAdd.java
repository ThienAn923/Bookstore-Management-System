package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class floorAdd extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage floorAddStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("floorManagement.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 550);
        floorAddStage.setTitle("Hello!");
        floorAddStage.setScene(scene);
        floorAddStage.show();
    }
}
