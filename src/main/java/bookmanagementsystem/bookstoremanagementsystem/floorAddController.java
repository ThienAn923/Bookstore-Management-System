package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class floorAddController implements Initializable {
    @FXML
    private TextField floorIDField;
    @FXML
    private TextField floorNameField;
    @FXML
    private Button floorAddButton;
    @FXML
    private Button cleanButton;
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    private boolean update;
    String floorID;
    private floorManagementController floorManagementController ;
    public void setController(floorManagementController floorManagementController){
        this.floorManagementController = floorManagementController;
    }
    @FXML
    private void addFloor() {
        floorID = floorIDField.getText();
        String floorName = floorNameField.getText();
        con = dbConnect.getConnect();
        if (floorID.isEmpty() || floorName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        floorManagementController.refresh(); //to refresh the floorManagement every time a floor is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, floorIDField.getText());
            preparedStatement.setString(2, floorNameField.getText());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(floorAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        floorIDField.setText(null);
        floorNameField.setText(null);
    }

    private void getQuery() {

        if (!update) {

            query = "INSERT INTO `floor`( `FloorID`, `FloorName`) VALUES (?,?)";

        }else{
            query = "UPDATE `floor` SET "
                    + "`FloorID`=?,"
                    + "`FloorName`= ? WHERE id = '"+floorID+"'";
        }

    }
    void setUpdate(boolean b) {
        this.update = b;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
