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

public class departmentModifyController implements Initializable {
    @FXML
    private TextField departmentIDField;
    @FXML
    private TextField departmentNameField;
    @FXML
    private Button departmentAddButton;
    @FXML
    private Button cleanButton;
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    private boolean update = true;
    String departmentID;

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
    private departmentManagementController departmentManagementController;
    public void setController(departmentManagementController departmentManagementController){
        this.departmentManagementController = departmentManagementController;
    }
    @FXML
    private void modifyDepartment() {
        departmentID = departmentIDField.getText();
        String departmentName = departmentNameField.getText();
        con = dbConnect.getConnect();
        if (departmentID.isEmpty() || departmentName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        departmentManagementController.refresh(searchText); //refresh the departmentManagement scene after modify
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, departmentIDField.getText());
            preparedStatement.setString(2, departmentNameField.getText());
            if (update) { // Set the third parameter only if updating
                preparedStatement.setString(3, departmentID);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(departmentAddController.class.getName()).log(Level.SEVERE, null, ex);
            warning("");
        }

    }
    @FXML
    private void clean() {
//        departmentIDField.setText(null);
        departmentNameField.setText(null);
    }

    private void getQuery() {

        query = "UPDATE `department` SET "
                + "`departmentID` = ?,"
                + "`departmentName` = ? WHERE `departmentID` = ?";

    }
    void setValue(String id, String name){
        departmentID = id;
        departmentIDField.setText(id);
        departmentNameField.setText(name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        departmentIDField.setEditable(false);
    }
}
