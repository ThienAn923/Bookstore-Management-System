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

public class publisherModifyController implements Initializable {

    @FXML
    private TextField publisherIDField;
    @FXML
    private TextField publisherNameField;
    @FXML
    private Button publisherAddButton;
    @FXML
    private Button cleanButton;
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    private boolean update = true;
    String publisherID;

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
    private publisherManagementController publisherManagementController;
    public void setController(publisherManagementController publisherManagementController){
        this.publisherManagementController = publisherManagementController;
    }
    @FXML
    private void modifyPublisher() {
        publisherID = publisherIDField.getText();
        String publisherName = publisherNameField.getText();
        con = dbConnect.getConnect();
        if (publisherID.isEmpty() || publisherName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        publisherManagementController.refresh(searchText); //refresh the publisherManagement scene after modify
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, publisherIDField.getText());
            preparedStatement.setString(2, publisherNameField.getText());
            if (update) { // Set the third parameter only if updating
                preparedStatement.setString(3, publisherID);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(publisherAddController.class.getName()).log(Level.SEVERE, null, ex);
            warning("");
        }

    }
    @FXML
    private void clean() {
//        publisherIDField.setText(null);
        publisherNameField.setText(null);
    }

    private void getQuery() {

        query = "UPDATE `publisher` SET "
                + "`publisherID` = ?,"
                + "`publisherName` = ? WHERE `publisherID` = ?";

    }
    void setValue(String id, String name){
        publisherID = id;
        publisherIDField.setText(id);
        publisherNameField.setText(name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        publisherIDField.setEditable(false);
    }
}
