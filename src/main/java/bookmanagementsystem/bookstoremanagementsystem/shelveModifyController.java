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

public class shelveModifyController implements Initializable {
    @FXML
    private TextField shelveIDField;
    @FXML
    private TextField shelveNameField;
    @FXML
    private Button shelveModifyButton;
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
    String AreaName;
    String searchText = "";

    Map<String, String> areasMap = new HashMap<>();
    List<String> areasNameAndIDList = new ArrayList<>();


    void setSearchText(String searchText){
        this.searchText = searchText;
    }
    shelveManagementController shelveManagementController;
    public void setController(shelveManagementController shelveManagementController) {
        this.shelveManagementController = shelveManagementController;
    }

    void setUpChoicebox(){
        //extract item from area name and id list to put it to choice box
        IDBox.getItems().addAll(areasNameAndIDList);
    }

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

    public void getAreaName(){
        try{
            con = dbConnect.getConnect();
            query = "SELECT AreaName FROM `area` WHERE AreaID = ?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,AreaID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            AreaName = resultSet.getString(1);
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void ModifyShelve() {
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
//        shelveIDField.setText(null);
        shelveNameField.setText(null);
    }

    private void getQuery() {
        query = "UPDATE `shelve` SET "
                + "`shelveID`=?,"
                + "`shelveName`= ?,"
                + "`AreaID`=?" +" WHERE shelveID = '"+shelveID+"'";
    }

    void setValue(String id, String name, String areaID){
        shelveID = id;
        this.AreaID = areaID;
        getAreaName();

        shelveIDField.setText(id);
        shelveNameField.setText(name);
        IDBox.setValue(AreaID + " - " + AreaName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        shelveIDField.setEditable(false); //set the ID field for it to not be able to modify

        getAllID(); // get all areas ID
        //pass all data from getAllAreaID to list box
        setUpChoicebox();
        IDBox.setOnAction(this::getID);
    }
}
