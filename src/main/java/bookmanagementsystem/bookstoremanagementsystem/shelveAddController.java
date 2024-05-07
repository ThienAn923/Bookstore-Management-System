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

public class shelveAddController implements Initializable {
    @FXML
    private TextField shelveIDField;
    @FXML
    private TextField shelveNameField;
    @FXML
    private Button shelveAddButton;
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

    String shelveID;
    String AreaID = ""; //waiting for user to select areaID from choiceBox
    Map<String, String> areasMap = new HashMap<>();
    List<String> areasNameAndIDList = new ArrayList<>();
    String searchText = "";

    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private shelveManagementController shelveManagementController ;
    public void setController(shelveManagementController shelveManagementController){
        this.shelveManagementController = shelveManagementController;
    }

    void setUpChoicebox(){
        //extract item from area name and id list to put it to choice box
        IDBox.getItems().addAll(areasNameAndIDList);

        //set choice box's default value as the first value in database
        if (!areasNameAndIDList.isEmpty()) {
            IDBox.setValue(areasNameAndIDList.get(0)); // Set the default value to the first option
            //set default area ID as the first area in database
            AreaID = areasMap.get(IDBox.getValue());
        }

    }

    //This function is to get list of areaID
    private void getAllID() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT AreaID, AreaName FROM `area`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String areaID = resultSet.getString("AreaID");
                String areaNameAndID = areaID + " - " + resultSet.getString("AreaName");

                //add to map table and area list
                areasMap.put(areaNameAndID, areaID);
                areasNameAndIDList.add(areaNameAndID);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getID(ActionEvent event){
        AreaID = areasMap.get(IDBox.getValue());
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `shelve`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "SLV000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `shelve` WHERE `shelveID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    shelveIDField.setText(id);
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
                shelveIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    @FXML
    private void addShelve() {
        shelveID = shelveIDField.getText();
        String shelveName = shelveNameField.getText();

        con = dbConnect.getConnect();

        if (shelveID.isEmpty() || shelveName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();

        }
        shelveManagementController.refresh(searchText);
    }
    private void insert() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, shelveIDField.getText());
            preparedStatement.setString(2, shelveNameField.getText());
            preparedStatement.setString(3, AreaID);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(areaAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        shelveIDField.setText(null);
        shelveNameField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `shelve`( `shelveID`, `shelveName`, `AreaID`) VALUES (?,?,?)";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        getAllID(); // get all areas ID
        //pass all data from getAllID to list box
        setUpChoicebox();
        //choose the fist option as default
        IDBox.setOnAction(this::getID);
    }
}
