package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestFile extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextField textField = new TextField();

        Image image = new Image("file:icon.png"); // replace with your image file path
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(15); // adjust the height and width as per your needs
        imageView.setFitWidth(15);

//        textField.setGraphic(imageView);

        VBox vbox = new VBox(textField);
        Scene scene = new Scene(vbox, 200, 100);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
