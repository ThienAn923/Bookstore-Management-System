package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginUi extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  throws IOException{

        String loginButtonCss = getClass().getResource("css/Button.css").toExternalForm();
        String loginPage = getClass().getResource("css/loginPage.css").toExternalForm();
        System.out.println(loginButtonCss);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 550);

        scene.getStylesheets().add(loginButtonCss);
        scene.getStylesheets().add(loginPage);

        primaryStage.setTitle("Hello!");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
