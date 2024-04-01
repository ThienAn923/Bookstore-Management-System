package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{
    @FXML
    private Label TestTextUp;
    @FXML
    private Circle headerLogoPicture;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerLogoPicture.setStroke(Color.BLACK);
        String imgURL = getClass().getResource("images/loginLogo2.png").toExternalForm();
        Image headerLogoImage = new Image(imgURL,false);
        headerLogoPicture.setFill(new ImagePattern(headerLogoImage));
        System.out.println("asbfkjasnklfsanklfnaskl");
    }
    @FXML
    protected void onClick() {
        TestTextUp.setText("Welcome to JavaFX Application!");
    }


}
