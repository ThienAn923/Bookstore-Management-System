package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class testManagement extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage main) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("providerManagement.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 550);
        main.setTitle("Hello!");
        main.setScene(scene);
        main.show();
    }
}
