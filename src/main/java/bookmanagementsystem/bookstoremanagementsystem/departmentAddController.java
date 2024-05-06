package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class departmentAddController implements Initializable {
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

    String departmentID;

    String searchText = "";
    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private departmentManagementController departmentManagementController ;
    public void setController(departmentManagementController departmentManagementController){
        this.departmentManagementController = departmentManagementController;
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `department`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "D000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `department` WHERE `departmentID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    departmentIDField.setText(id);
                    break; // Exit loop if unused ID is found
                }

            }

            //this part is only use as the final option after 100 try and still no usable ID found
            //it will create true random of character (only until the world burn, this will result in a duplicate ID in the database)
            if(!foundUnusedID){
                final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                StringBuilder randomID = new StringBuilder();
                final SecureRandom secureRandom = new SecureRandom();
                // Generate random characters one by one until the desired length is reached
                for (int i = 0; i < 10; i++) {
                    // Generate a random index to select a character from CHARACTERS
                    int randomIndex = secureRandom.nextInt(CHARACTERS.length());
                    // Append the randomly selected character to the randomID string
                    randomID.append(CHARACTERS.charAt(randomIndex));
                }
                departmentIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void addDepartment() {
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
        departmentManagementController.refresh(searchText); //to refresh the departmentManagement every time a department is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, departmentIDField.getText());
            preparedStatement.setString(2, departmentNameField.getText());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(departmentAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        departmentIDField.setText(null);
        departmentNameField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `department`( `departmentID`, `departmentName`) VALUES (?,?)";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
