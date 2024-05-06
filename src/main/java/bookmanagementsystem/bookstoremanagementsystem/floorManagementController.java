package bookmanagementsystem.bookstoremanagementsystem;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.input.MouseEvent;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.HBox;


public class floorManagementController implements Initializable {
    @FXML
    private Button floorAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField findBox;
    @FXML
    private VBox floorContainer;
    @FXML
    private ScrollPane floorsScrollPane;

    ObservableList<floor> floors = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    floor floor = null ;
    String searchText = "";

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findFloor(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getFloor();
    }

    @FXML
    private void addFloor(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("floorAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for floorAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            floorAddController floorAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            floorAddController.setController(this);
            floorAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm tầng");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshData() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT * FROM `floor`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                floors.add(new floor(
                        resultSet.getString("FloorID"),
                        resultSet.getString("FloorName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            floors.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `floor` WHERE `FloorID` LIKE ? OR `FloorName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                floors.add(new floor(
                        resultSet.getString("FloorID"),
                        resultSet.getString("FloorName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = floorContainer.getChildren().size();
        if (numChildren > 1) {
            floorContainer.getChildren().remove(1, numChildren);
        }
    }

    public void refresh(){
        clearTable();
        floors.clear(); //clear the list areas
        refreshData();
        getFloor();
    }
    public void refresh(String searchText){
        clearTable();
        floors.clear(); //clear the list areas
        refreshData(searchText);
        getFloor();
    }

    private void getFloor() {
        con = dbConnect.getConnect();

        for (floor floor : floors) {
            HBox floorBox = new HBox(); //Create container to hold the floors
            floorBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            floorBox.setPrefHeight(36);
            floorBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label floorIdLabel = new Label(floor.getFloorID());
            Label floorNameLabel = new Label(floor.getFloorName());

            floorIdLabel.setMinWidth(370);
            floorNameLabel.setMinWidth(510);
            floorBox.setMargin(floorIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `floor` WHERE FloorID = '" + floor.getFloorID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(floorManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa bản, hãy chắc chắn tầng bạn đang xóa không chứa bất kỳ khu vực nào!");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("floorModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for floorAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    floorModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(floor.getFloorID(), floor.getFloorName());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Modify Floor");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    warning("Không thể mở edit");
                }
            });

            HBox buttonBox = new HBox(editIcon, deleteIcon);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
//            buttonBox.setStyle("-fx-alignment:center");
            buttonBox.setSpacing(10);

            floorBox.getChildren().addAll(floorIdLabel, floorNameLabel, buttonBox);
//            floorBox.setSpacing(10);

            // Add the HBox for each floor to your layout
            floorContainer.getChildren().add(floorBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getFloor();
        //hide the scroll bar of the scroll pane
        floorsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        floorsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
