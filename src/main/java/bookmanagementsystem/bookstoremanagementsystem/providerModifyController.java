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

public class providerModifyController implements Initializable {
    @FXML
    private TextField providerIDField;
    @FXML
    private TextField providerNameField;
    @FXML
    private Button providerAddButton;
    @FXML
    private Button cleanButton;
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    private boolean update = true;
    String providerID;

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
    private providerManagementController providerManagementController;
    public void setController(providerManagementController providerManagementController){
        this.providerManagementController = providerManagementController;
    }
    @FXML
    private void modifyProvider() {
        providerID = providerIDField.getText();
        String providerName = providerNameField.getText();
        con = dbConnect.getConnect();
        if (providerID.isEmpty() || providerName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        providerManagementController.refresh(searchText); //refresh the providerManagement scene after modify
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, providerIDField.getText());
            preparedStatement.setString(2, providerNameField.getText());
            if (update) { // Set the third parameter only if updating
                preparedStatement.setString(3, providerID);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(providerAddController.class.getName()).log(Level.SEVERE, null, ex);
            warning("");
        }

    }
    @FXML
    private void clean() {
//        providerIDField.setText(null);
        providerNameField.setText(null);
    }

    private void getQuery() {

        query = "UPDATE `provider` SET "
                + "`providerID` = ?,"
                + "`providerName` = ? WHERE `providerID` = ?";

    }
    void setValue(String id, String name){
        providerID = id;
        providerIDField.setText(id);
        providerNameField.setText(name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        providerIDField.setEditable(false);
    }
}
