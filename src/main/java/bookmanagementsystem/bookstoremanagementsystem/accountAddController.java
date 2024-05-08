package bookmanagementsystem.bookstoremanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class accountAddController implements Initializable {
    @FXML
    private TextField accountIDField;
    @FXML
    private TextField accountUsernameField;
    @FXML
    private Button accountAddButton;
    @FXML
    private Button cleanButton;
    @FXML
    private Button autoButton;
    @FXML
    private PasswordField accountPasswordField;
    @FXML
    private CheckBox isAuthorizedCheckbox;
    @FXML
    private ChoiceBox<String> IDBox;


    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String accountID;
    String staffID = ""; //waiting for user to select staffID from choiceBox
    Map<String, String> staffsMap = new HashMap<>();
    List<String> staffsNameAndIDList = new ArrayList<>();
    String searchText = "";
    boolean isAuthorized = false;
    boolean isHidden = false;

    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private accountManagementController accountManagementController ;
    public void setController(accountManagementController accountManagementController){
        this.accountManagementController = accountManagementController;
    }

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    void setUpChoicebox(){
        //extract item from staff name and id list to put it to choice box
        IDBox.getItems().addAll(staffsNameAndIDList);

        //set choice box's default value as the first value in database
        if (!staffsNameAndIDList.isEmpty()) {
            IDBox.setValue(staffsNameAndIDList.get(0)); // Set the default value to the first option
            //set default staff ID as the first staff in database
            staffID = staffsMap.get(IDBox.getValue());
        }

    }

    //This function is to get list of staffID
    private void getAllID() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT staffID, staffName FROM `staff`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String staffID = resultSet.getString("staffID");
                String staffNameAndID = staffID + " - " + resultSet.getString("staffName");

                //add to map table and staff list
                staffsMap.put(staffNameAndID, staffID);
                staffsNameAndIDList.add(staffNameAndID);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getID(ActionEvent event){
        staffID = staffsMap.get(IDBox.getValue());
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `account`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "AC000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `account` WHERE `accountID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    accountIDField.setText(id);
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
                accountIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    @FXML
    private void addAccount() {
        accountID = accountIDField.getText();

        String accountUsername = accountUsernameField.getText();
        String accountPassword = accountPasswordField.getText();
        con = dbConnect.getConnect();
        if(isAuthorizedCheckbox.isSelected()) isAuthorized = true;
        if (accountID.isEmpty() || accountUsername.isEmpty() || accountPassword.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();

        }
        accountManagementController.refresh(searchText);
    }
    private void insert() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, accountIDField.getText());
            preparedStatement.setString(2, accountUsernameField.getText());
            preparedStatement.setString(3, accountPasswordField.getText());
            preparedStatement.setBoolean(4, isAuthorized);
            preparedStatement.setBoolean(5, isHidden);
            preparedStatement.setString(6, staffID);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        accountIDField.setText(null);
        accountUsernameField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `account`( `accountID`, `accountUsername`, `accountPassword`,`accountAuthorized`,`accountHidden`,`staffID`) VALUES (?,?,?,?,?,?)";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        getAllID(); // get all staffs ID
        //pass all data from getAllID to list box
        setUpChoicebox();
        //choose the fist option as default
        IDBox.setOnAction(this::getID);
    }
}
