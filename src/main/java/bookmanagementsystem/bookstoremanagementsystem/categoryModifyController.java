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

public class categoryModifyController implements Initializable {
    @FXML
    private TextField categoryIDField;
    @FXML
    private TextField categoryNameField;
    @FXML
    private Button categoryAddButton;
    @FXML
    private Button cleanButton;
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    private boolean update = true;
    String categoryID;

    String searchText;
    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private categoryManagementController categoryManagementController;
    public void setController(categoryManagementController categoryManagementController){
        this.categoryManagementController = categoryManagementController;
    }
    @FXML
    private void modifyCategory() {
        categoryID = categoryIDField.getText();
        String categoryName = categoryNameField.getText();
        con = dbConnect.getConnect();
        if (categoryID.isEmpty() || categoryName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        categoryManagementController.refresh(searchText); //refresh the categoryManagement scene after modify
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, categoryIDField.getText());
            preparedStatement.setString(2, categoryNameField.getText());
            if (update) { // Set the third parameter only if updating
                preparedStatement.setString(3, categoryID);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(categoryAddController.class.getName()).log(Level.SEVERE, null, ex);
            warning("");
        }

    }
    @FXML
    private void clean() {
//        categoryIDField.setText(null);
        categoryNameField.setText(null);
    }

    private void getQuery() {

        query = "UPDATE `category` SET "
                + "`categoryID` = ?,"
                + "`categoryName` = ? WHERE `categoryID` = ?";

    }
    void setValue(String id, String name){
        categoryID = id;
        categoryIDField.setText(id);
        categoryNameField.setText(name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryIDField.setEditable(false);
    }

}
