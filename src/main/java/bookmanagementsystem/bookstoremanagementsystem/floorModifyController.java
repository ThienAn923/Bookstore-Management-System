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

public class floorModifyController implements Initializable {
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
    private boolean update = true;
    String floorID;

    String searchText = "";
    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private floorManagementController floorManagementController;
    public void setController(floorManagementController floorManagementController){
        this.floorManagementController = floorManagementController;
    }
    @FXML
    private void modifyFloor() {
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
        floorManagementController.refresh(searchText); //refresh the floorManagement scene after modify
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, floorIDField.getText());
            preparedStatement.setString(2, floorNameField.getText());
            if (update) { // Set the third parameter only if updating
                preparedStatement.setString(3, floorID);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(floorAddController.class.getName()).log(Level.SEVERE, null, ex);
            warning("");
        }

    }
    @FXML
    private void clean() {
//        floorIDField.setText(null);
        floorNameField.setText(null);
    }

    private void getQuery() {

        query = "UPDATE `floor` SET "
                + "`FloorID` = ?,"
                + "`FloorName` = ? WHERE `FloorID` = ?";

    }
    void setValue(String id, String name){
        floorID = id;
        floorIDField.setText(id);
        floorNameField.setText(name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        floorIDField.setEditable(false);
    }
}
