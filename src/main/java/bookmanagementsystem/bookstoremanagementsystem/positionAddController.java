package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class positionAddController implements Initializable {
    @FXML
    private TextField positionIDField;
    @FXML
    private TextField positionNameField;
    @FXML
    private TextField positionSalaryField;
    @FXML
    private Button positionAddButton;
    @FXML
    private Button cleanButton;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String positionID;
    String searchText = "";
    int salary = 0;

    private positionManagementController positionManagementController ;

    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setController(positionManagementController positionManagementController){
        this.positionManagementController = positionManagementController;
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `position`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "PO000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `position` WHERE `positionID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    positionIDField.setText(id);
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
                positionIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void addPosition() {
        positionID = positionIDField.getText();
        String positionName = positionNameField.getText();
        String positionSalary = positionSalaryField.getText();
        try{
            Float.parseFloat(positionSalary);
        }catch(Exception e){
            warning("Hãy chắn khung hệ số lương chỉ được nhập định dạng số như X.X hoặc X");
        }
        con = dbConnect.getConnect();

        if (positionID.isEmpty() || positionName.isEmpty() || positionSalary.isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        positionManagementController.refresh(searchText); //to refresh the positionManagement every time a position is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, positionIDField.getText());
            preparedStatement.setString(2, positionNameField.getText());
            preparedStatement.setFloat(3, Float.parseFloat(positionSalaryField.getText()));
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(positionAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        positionIDField.setText(null);
        positionNameField.setText(null);
        positionSalaryField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `position`( `positionID`, `positionName`,`positionSalary`) VALUES (?,?,?)";
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
