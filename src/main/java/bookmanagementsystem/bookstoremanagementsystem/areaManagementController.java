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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

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

public class areaManagementController implements Initializable {

    @FXML
    private Button addAreaButton;
    @FXML
    private Button refreshButton;
    @FXML
    private VBox areasContainer;
    @FXML
    private ScrollPane areasScrollPane;
    @FXML
    private TextField findBox;

    ObservableList<area> areas = FXCollections.observableArrayList();
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    area area = null ;
    String searchText = "";
    Map<String, String> floorsMap = new HashMap<>();

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findArea(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getArea();
    }

    void getAllFloor(){
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT FloorID, FloorName FROM `floor`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String floorID = resultSet.getString("FloorID");
                String floorName =resultSet.getString("FloorName");

                //add to map table and floor list
                floorsMap.put(floorID, floorName);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String getFloorName(String FloorID){
        String FloorName = floorsMap.get(FloorID);
        return FloorName;
    }

    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = areasContainer.getChildren().size();
        if (numChildren > 1) {
            areasContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        areas.clear(); //clear the list areas
        refreshData();
        getArea();
    }
    public void refresh(String searchText){
        clearTable();
        areas.clear(); //clear the list areas
        refreshData(searchText);
        getArea();
    }

    @FXML
    private void addArea(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("areaAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for areaAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            areaAddController areaAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            areaAddController.setController(this);
            areaAddController.setSearchText(searchText);

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
            areas.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `area`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                areas.add(new area(
                        resultSet.getString("AreaID"),
                        resultSet.getString("AreaName"),
                        resultSet.getString("FloorID")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            areas.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `area` WHERE `AreaID` LIKE ? OR `AreaName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                areas.add(new area(
                        resultSet.getString("AreaID"),
                        resultSet.getString("AreaName"),
                        resultSet.getString("FloorID")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private void getArea() {
        con = dbConnect.getConnect();

        for (area area : areas) {
            HBox areaBox = new HBox(); //Create container to hold the areas
            areaBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            areaBox.setPrefHeight(36);
            areaBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label areaIdLabel = new Label(area.getAreaID());
            Label areaNameLabel = new Label(area.getAreaName());
            Label FloorName = new Label(getFloorName(area.getFloorID()));

            areaIdLabel.setMinWidth(185);
            FloorName.setMinWidth(185);
            areaNameLabel.setMinWidth(510);

            areaBox.setMargin(areaIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `area` WHERE AreaID = '" + area.getAreaID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(areaManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa khu vực. Hãy chắc chắc không có kệ nằm trong khu vực này.");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("areaModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for Addcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    areaModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
//                        fac.setUpdate(true);
                        fac.setValue(area.getAreaID(), area.getAreaName(), area.getFloorID());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Modify Area");
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

            areaBox.getChildren().addAll(areaIdLabel, FloorName, areaNameLabel, buttonBox);

            // Add the HBox for each area to your layout
            areasContainer.getChildren().add(areaBox);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getAllFloor(); //get all the Floor and put to HashMap so i don't have to search for FloorName in database every time
        refreshData(); //get areas data
        getArea(); //Create areas hbox

        //Hide the scroll bars
        areasScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        areasScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
