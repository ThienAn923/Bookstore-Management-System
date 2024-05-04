package bookmanagementsystem.bookstoremanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class areaAddController implements Initializable {
    @FXML
    private TextField areaIDField;
    @FXML
    private TextField areaNameField;
    @FXML
    private Button areaAddButton;
    @FXML
    private Button cleanButton;
    @FXML
    private Button autoButton;
    @FXML
    private ChoiceBox<String> IDBox;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String areaID;
    String FloorID = ""; //waiting for user to select floorID from choiceBox
    Map<String, String> floorsMap = new HashMap<>();
    List<String> floorsNameAndIDList = new ArrayList<>();
    String searchText = "";

    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private areaManagementController areaManagementController ;
    public void setController(areaManagementController areaManagementController){
        this.areaManagementController = areaManagementController;
    }

    void setUpChoicebox(){
        //extract item from floor name and id list to put it to choice box
        IDBox.getItems().addAll(floorsNameAndIDList);

        //set choice box's default value as the first value in database
        if (!floorsNameAndIDList.isEmpty()) {
            IDBox.setValue(floorsNameAndIDList.get(0)); // Set the default value to the first option
            //set default floor ID as the first floor in database
            FloorID = floorsMap.get(IDBox.getValue());
        }

    }

    //This function is to get list of floorID
    private void getAllID() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT FloorID, FloorName FROM `floor`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String floorID = resultSet.getString("FloorID");
                String floorNameAndID = floorID + " - " + resultSet.getString("FloorName");

                //add to map table and floor list
                floorsMap.put(floorNameAndID, floorID);
                floorsNameAndIDList.add(floorNameAndID);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getID(ActionEvent event){
        FloorID = floorsMap.get(IDBox.getValue());
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `area`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "A000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `area` WHERE `AreaID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    areaIDField.setText(id);
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
                areaIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    @FXML
    private void addArea() {
        areaID = areaIDField.getText();
        String areaName = areaNameField.getText();

        con = dbConnect.getConnect();

        if (areaID.isEmpty() || areaName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();

        }
        areaManagementController.refresh(searchText);
    }
    private void insert() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, areaIDField.getText());
            preparedStatement.setString(2, areaNameField.getText());
            preparedStatement.setString(3, FloorID);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(floorAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        areaIDField.setText(null);
        areaNameField.setText(null);
    }

    private void getQuery() {
            query = "INSERT INTO `area`( `AreaID`, `AreaName`, `FloorID`) VALUES (?,?,?)";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        getAllID(); // get all floors ID
        //pass all data from getAllFloorID to list box
        setUpChoicebox();
        //choose the fist option as default
        IDBox.setOnAction(this::getID);
    }
}
