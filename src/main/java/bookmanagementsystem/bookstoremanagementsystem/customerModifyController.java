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

public class customerModifyController implements Initializable {
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
    private Button customerModifyButton;
    @FXML
    private Button cleanButton;

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
    private void modifyCustomer() {
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
            preparedStatement.setString(6, customerIDField.getText());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(customerModifyController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
//        customerIDField.setText(null);
        customerNameField.setText(null);
        customerPhoneNumberField.setText(null);
        customerPointField.setText(null);
    }

    private void getQuery() {
        query = "UPDATE `customer` SET "
                + "`customerID` = ?,"
                + "`customerName` = ?,"
                + "`customerPhoneNumber` = ?,"
                + "`customerPoint` = ?,"
                + "`customerGender` = ? WHERE `customerID` = ?";
    }

    void setValue(String id, String name, String phoneNumber, int customerPoint, boolean gender){
        customerID = id;
        customerIDField.setText(id);
        customerNameField.setText(name);
        customerPhoneNumberField.setText(phoneNumber);

        customerPointField.setText(String.valueOf(customerPoint));
        if(gender) maleRbutton.setSelected(true);
        else femaleRbutton.setSelected(true);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerIDField.setEditable(false);
    }
}
