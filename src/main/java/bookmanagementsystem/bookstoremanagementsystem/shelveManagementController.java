package bookmanagementsystem.bookstoremanagementsystem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class shelveManagementController implements Initializable {
    @FXML
    private Button addShelveButton;
    @FXML
    private Button refreshButton;
    @FXML
    private VBox shelvesContainer;
    @FXML
    private ScrollPane shelvesScrollPane;
    @FXML
    private TextField findBox;

    ObservableList<shelve> shelves = FXCollections.observableArrayList();
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    shelve shelve = null ;
    String searchText = "";
    Map<String, String> areasMap = new HashMap<>();

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findShelve(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getShelve();
    }

    void getAllArea(){
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT AreaID, AreaName FROM `area`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String areaID = resultSet.getString("AreaID");
                String areaName =resultSet.getString("AreaName");

                //add to map table and area list
                areasMap.put(areaID, areaName);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String getAreaName(String AreaID){
        String AreaName = areasMap.get(AreaID);
        return AreaName;
    }

    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain shelves infomation)
        int numChildren = shelvesContainer.getChildren().size();
        if (numChildren > 1) {
            shelvesContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        shelves.clear(); //clear the list shelves
        refreshData();
        getShelve();
    }
    public void refresh(String searchText){
        clearTable();
        shelves.clear(); //clear the list shelves
        refreshData(searchText);
        getShelve();
    }

    @FXML
    private void addShelve(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("shelveAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for shelveAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            shelveAddController shelveAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            shelveAddController.setController(this);
            shelveAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm Khu Vực");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshData() {
        try {
            shelves.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `shelve`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                shelves.add(new shelve(
                        resultSet.getString("shelveID"),
                        resultSet.getString("shelveName"),
                        resultSet.getString("AreaID")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            shelves.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `shelve` WHERE `shelveID` LIKE ? OR `shelveName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                shelves.add(new shelve(
                        resultSet.getString("shelveID"),
                        resultSet.getString("shelveName"),
                        resultSet.getString("AreaID")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private void getShelve() {
        con = dbConnect.getConnect();

        for (shelve shelve : shelves) {
            HBox shelveBox = new HBox(); //Create container to hold the shelves
            shelveBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            shelveBox.setPrefHeight(36);
            shelveBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label shelveIdLabel = new Label(shelve.getShelveID());
            Label shelveNameLabel = new Label(shelve.getShelveName());
            Label AreaName = new Label(getAreaName(shelve.getAreaID()));

            shelveIdLabel.setMinWidth(185);
            AreaName.setMinWidth(185);
            shelveNameLabel.setMinWidth(510);

            shelveBox.setMargin(shelveIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `shelve` WHERE shelveID = '" + shelve.getShelveID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(shelveManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa khu vực. Hãy chắc chắc không có kệ nằm trong khu vực này.");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("shelveModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for Addcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    shelveModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
//                        fac.setUpdate(true);
                        fac.setValue(shelve.getShelveID(), shelve.getShelveName(), shelve.getAreaID());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Modify Shelve");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            HBox buttonBox = new HBox(editIcon, deleteIcon);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
//            buttonBox.setStyle("-fx-alignment:center");
            buttonBox.setSpacing(10);

            shelveBox.getChildren().addAll(shelveIdLabel, AreaName, shelveNameLabel, buttonBox);

            // Add the HBox for each shelve to your layout
            shelvesContainer.getChildren().add(shelveBox);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getAllArea(); //get all the Area and put to HashMap so i don't have to search for AreaName in database every time
        refreshData(); //get shelves data
        getShelve(); //Create shelves hbox

        //Hide the scroll bars
        shelvesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        shelvesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
