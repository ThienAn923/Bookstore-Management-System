package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class promotionAddController implements Initializable {
    @FXML
    private TextField promotionIDField;
    @FXML
    private TextField promotionNameField;
    @FXML
    private TextArea promotionDescriptionField;
    @FXML
    private DatePicker promotionStartDayField;
    @FXML
    private DatePicker promotionEndDayField;
    @FXML
    private TextField promotionPercentageField;
    @FXML
    private Button promotionAddButton;
    @FXML
    private Button cleanButton;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String promotionID;
    String searchText = "";
    boolean runningFromBookAddPromotion = false;

    private promotionManagementController promotionManagementController ;

    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    public void setController(promotionManagementController promotionManagementController){
        this.promotionManagementController = promotionManagementController;
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `promotion`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "PR000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `promotion` WHERE `promotionID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    promotionIDField.setText(id);
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
                promotionIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void addPromotion() {
        promotionID = promotionIDField.getText();
        String promotionName = promotionNameField.getText();
        String promotionDescription = promotionDescriptionField.getText();
        con = dbConnect.getConnect();

        if (promotionID.isEmpty() || promotionName.isEmpty() ) {
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
            preparedStatement.setString(1, promotionIDField.getText());
            preparedStatement.setString(2, promotionNameField.getText());
            preparedStatement.setString(3, promotionDescriptionField.getText());
            preparedStatement.setDate(4, Date.valueOf(promotionStartDayField.getValue()));
            preparedStatement.setDate(5, Date.valueOf(promotionEndDayField.getValue()));
            preparedStatement.setFloat(6, Float.parseFloat(promotionPercentageField.getText()));
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(promotionAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        promotionIDField.setText(null);
        promotionNameField.setText(null);
        promotionDescriptionField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `promotion`( `promotionID`, `promotionName`,`promotionDescription`, `promotionStartDay`, `promotionEndDay`, `promotionPercentage`) VALUES (?,?,?,?,?,?)";
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
