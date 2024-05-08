package bookmanagementsystem.bookstoremanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable{
    @FXML
    private Label TestTextUp;
    @FXML
    private Circle headerLogoPicture;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;



    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    ObservableList<account> accounts = FXCollections.observableArrayList();

    void warning(String warning){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(warning);
        alert.showAndWait();
    }
    @FXML
    void login(){
        try {
            accounts.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `account`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                accounts.add(new account(
                        resultSet.getString("accountID"),
                        resultSet.getString("accountUsername"),
                        resultSet.getString("accountPassword"),
                        resultSet.getBoolean("accountAuthorized"),
                        resultSet.getBoolean("accountHidden"),
                        resultSet.getString("staffID")));

            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean checkAccountInfo = false;
        for (account account : accounts) {
            String username = account.getAccountUsername();
            String password = account.getAccountPassword();

            if (username.equals(usernameField.getText()) && password.equals(passwordField.getText())){
                try {
                    checkAccountInfo = true;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
                    Parent root = loader.load();
                    mainController fac = loader.getController();
                    fac.setController(this);

                    if (fac != null) {
                        fac.getStaffID(account.getStaffID(),account.accountAuthorized);
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Nhà sách ABC");
                    stage.setScene(new Scene(root));
                    stage.show();
                    Stage loginStage = (Stage) usernameField.getScene().getWindow(); //any FxID would work
                    loginStage.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        if(!checkAccountInfo) warning("Sai tài khoản hoặc mật khẩu");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerLogoPicture.setStroke(Color.BLACK);
        String imgURL = getClass().getResource("images/loginLogo2.png").toExternalForm();
        Image headerLogoImage = new Image(imgURL,false);
        headerLogoPicture.setFill(new ImagePattern(headerLogoImage));

    }
    @FXML
    protected void onClick() {
        TestTextUp.setText("Welcome to JavaFX Application!");
    }


}
