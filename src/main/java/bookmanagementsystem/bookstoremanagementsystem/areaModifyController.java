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

public class areaModifyController implements Initializable {
    @FXML
    private TextField areaIDField;
    @FXML
    private TextField areaNameField;
    @FXML
    private Button areaAddButton;
    @FXML
    private Button cleanButton;
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    private boolean update;
    String areaID;
    String areaName;
    String FloorIDxx = "F0001"; //delete later

    @FXML
    private void addArea() {
        areaID = areaIDField.getText();
        String areaName = areaNameField.getText();
        con = dbConnect.getConnect();
        if (areaID.isEmpty() || areaName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();

        }
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, areaIDField.getText());
            preparedStatement.setString(2, areaNameField.getText());
            preparedStatement.setString(3, FloorIDxx);

            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(floorAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        areaIDField.setText(null);
        areaNameField.setText(null);
    }

    private void getQuery() {

        if (!update) {

            query = "INSERT INTO `area`( `AreaID`, `AreaName`, `FloorID`) VALUES (?,?,?)";

        }else{
            query = "UPDATE `area` SET "
                    + "`AreaID`=?,"
                    + "`AreaName`= ?,"
                    + "`FloorID`=?" +" WHERE AreaID = '"+areaID+"'";

        }

    }
    void setUpdate(boolean b) {
        this.update = b;
    }
    void setValue(String id, String name, String deleteLater){
        areaID = id;
        areaIDField.setText(id);
        areaNameField.setText(name);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
