package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class customerAddController implements Initializable {
    @FXML
    private ToggleGroup customerGenderGroup;
    @FXML
    private TextField customerIDField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerPhoneNumberField;
    @FXML
    private TextField customerPointField;
    @FXML
    private RadioButton maleRbutton, femaleRbutton;
    @FXML
    private Button customerAddButton;
    @FXML
    private Button cleanButton;
//    @FXML
//    private ToggleGroup customerGender;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String customerID;
    boolean customerGender = true;
    int customerPoint = 0;
    String searchText = null;

    void warning(String warning){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(warning);
        alert.showAndWait();
    }
    // this get the search text from the management UI then send it to here. Then to later use it to refresh page.
    void setSearchText(String searchText){
        this.searchText = searchText;
    }
    private customerManagementController customerManagementController ;
    public void setController(customerManagementController customerManagementController){
        this.customerManagementController = customerManagementController;
    }

    //if male radio button is selected, return true, else, false
    @FXML
    void setGender(){
        customerGender = maleRbutton.isSelected();
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `customer`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "C000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `customer` WHERE `customerID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    customerIDField.setText(id);
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
                customerIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void addCustomer() {
        customerID = customerIDField.getText();
        String customerName = customerNameField.getText();
        String customerPhoneNumber = customerPhoneNumberField.getText();
        try{
            customerPoint = Integer.parseInt(customerPointField.getText());}
        catch(Exception e){
            warning("Khung điểm chỉ được nhập số.");
            return;
        }

        con = dbConnect.getConnect();
        if (customerID.isEmpty() || customerName.isEmpty() || customerPhoneNumber.isEmpty() || customerPointField.getText().isEmpty() ) {
            warning("Có thể có khung còn trống");
        } else {
            getQuery();
            insert();
            clean();
        }
        customerManagementController.refresh(searchText); //to refresh the customerManagement every time a customer is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, customerIDField.getText());
            preparedStatement.setString(2, customerNameField.getText());
            preparedStatement.setString(3, customerPhoneNumberField.getText());
            preparedStatement.setInt(4, customerPoint);
            preparedStatement.setBoolean(5, customerGender);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(customerAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        customerIDField.setText(null);
        customerNameField.setText(null);
        customerPhoneNumberField.setText(null);
        customerPointField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `customer`( `customerID`, `customerName`,`customerPhoneNumber`,`customerPoint`,`customerGender`) VALUES (?,?,?,?,?)";
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maleRbutton.setSelected(true);
        customerPointField.setText("0");
    }
}
