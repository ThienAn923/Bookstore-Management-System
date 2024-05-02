package bookmanagementsystem.bookstoremanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class areaModifyController implements Initializable {
    @FXML
    private TextField areaIDField;
    @FXML
    private TextField areaNameField;
    @FXML
    private Button areaModifyButton;
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
    String FloorName;

    Map<String, String> floorsMap = new HashMap<>();
    List<String> floorsNameAndIDList = new ArrayList<>();

    areaManagementController areaManagementController;
    public void setController(areaManagementController areaManagementController) {
        this.areaManagementController = areaManagementController;
    }

    void setUpChoicebox(){
        //extract item from floor name and id list to put it to choice box
        IDBox.getItems().addAll(floorsNameAndIDList);
    }

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

    public void getFloorName(){
        try{
            con = dbConnect.getConnect();
            query = "SELECT FloorName FROM `floor` WHERE FloorID = ?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,FloorID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            FloorName = resultSet.getString(1);
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void ModifyArea() {
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
        areaManagementController.refresh();
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
//        areaIDField.setText(null);
        areaNameField.setText(null);
    }

    private void getQuery() {
        query = "UPDATE `area` SET "
                + "`AreaID`=?,"
                + "`AreaName`= ?,"
                + "`FloorID`=?" +" WHERE AreaID = '"+areaID+"'";
    }

    void setValue(String id, String name, String floorID){
        areaID = id;
        this.FloorID = floorID;
        getFloorName();

        areaIDField.setText(id);
        areaNameField.setText(name);
        IDBox.setValue(FloorID + " - " + FloorName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        areaIDField.setEditable(false); //set the ID field for it to not be able to modify

        getAllID(); // get all floors ID
        //pass all data from getAllFloorID to list box
        setUpChoicebox();
        IDBox.setOnAction(this::getID);
    }
}
