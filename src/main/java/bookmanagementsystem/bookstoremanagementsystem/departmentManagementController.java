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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class departmentManagementController implements Initializable {
    @FXML
    private Button departmentAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField findBox;
    @FXML
    private VBox departmentContainer;
    @FXML
    private ScrollPane departmentsScrollPane;

    ObservableList<department> departments = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    department department = null ;
    String searchText = "";

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findDepartment(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getDepartment();
    }

    @FXML
    private void addDepartment(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("departmentAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for departmentAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            departmentAddController departmentAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            departmentAddController.setController(this);
            departmentAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm Bộ Phận");
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
            query = "SELECT * FROM `department`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                departments.add(new department(
                        resultSet.getString("departmentID"),
                        resultSet.getString("departmentName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            departments.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `department` WHERE `departmentID` LIKE ? OR `departmentName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                departments.add(new department(
                        resultSet.getString("departmentID"),
                        resultSet.getString("departmentName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = departmentContainer.getChildren().size();
        if (numChildren > 1) {
            departmentContainer.getChildren().remove(1, numChildren);
        }
    }

    public void refresh(){
        clearTable();
        departments.clear(); //clear the list areas
        refreshData();
        getDepartment();
    }
    public void refresh(String searchText){
        clearTable();
        departments.clear(); //clear the list areas
        refreshData(searchText);
        getDepartment();
    }

    private void getDepartment() {
        con = dbConnect.getConnect();

        for (department department : departments) {
            HBox departmentBox = new HBox(); //Create container to hold the departments
            departmentBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            departmentBox.setPrefHeight(36);
            departmentBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label departmentIdLabel = new Label(department.getDepartmentID());
            Label departmentNameLabel = new Label(department.getDepartmentName());

            departmentIdLabel.setMinWidth(370);
            departmentNameLabel.setMinWidth(510);
            departmentBox.setMargin(departmentIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `department` WHERE departmentID = '" + department.getDepartmentID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(departmentManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa bộ phận");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("departmentModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for departmentAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    departmentModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(department.getDepartmentID(), department.getDepartmentName());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Modify Department");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    warning("Không thể mở edit");
                }
            });

            HBox buttonBox = new HBox(editIcon, deleteIcon);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setSpacing(10);

            departmentBox.getChildren().addAll(departmentIdLabel, departmentNameLabel, buttonBox);
            // Add the HBox for each floor to your layout
            departmentContainer.getChildren().add(departmentBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getDepartment();
        //hide the scroll bar of the scroll pane
        departmentsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        departmentsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
