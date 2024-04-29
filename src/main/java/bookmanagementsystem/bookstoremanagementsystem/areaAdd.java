package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class areaAdd extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage areaAddStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("areaManagement.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 550);
        areaAddStage.setTitle("Hello!");
        areaAddStage.setScene(scene);
        areaAddStage.show();
    }
}
