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

public class publisherAddController implements Initializable {
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

    String publisherID;

    String searchText = "";
    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private publisherManagementController publisherManagementController ;
    public void setController(publisherManagementController publisherManagementController){
        this.publisherManagementController = publisherManagementController;
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `publisher`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "P000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `publisher` WHERE `publisherID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    publisherIDField.setText(id);
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
                publisherIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void addPublisher() {
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
        publisherManagementController.refresh(searchText); //to refresh the publisherManagement every time a publisher is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, publisherIDField.getText());
            preparedStatement.setString(2, publisherNameField.getText());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(publisherAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        publisherIDField.setText(null);
        publisherNameField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `publisher`( `publisherID`, `publisherName`) VALUES (?,?)";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
