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
import javafx.scene.input.MouseButton;
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

public class staffManagementController implements Initializable {
    @FXML
    private Button staffAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label testLabel;
    @FXML
    private TextField findBox;
    @FXML
    private VBox staffContainer;
    @FXML
    private ScrollPane staffsScrollPane;

    ObservableList<staff> staffs = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    staff staff = null ;
    String searchText = "";

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findStaff(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getStaff();
    }

    @FXML
    private void addStaff(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("staffAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for staffAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            staffAddController staffAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            staffAddController.setController(this);
            staffAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm tác giả");
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
            query = "SELECT staffID, staffName, staffPhoneNumber, staffEmail, staffBirthday, staffGender, staffDescription FROM `staff`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                staffs.add(new staff(
                        resultSet.getString("staffID"),
                        resultSet.getString("staffName"),
                        resultSet.getString("staffPhoneNumber"),
                        resultSet.getString("staffEmail"),
                        resultSet.getDate("staffBirthday"),
                        resultSet.getBoolean("staffGender"),
                        resultSet.getString("staffDescription")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            staffs.clear();
            con = dbConnect.getConnect();
            //search by name, id or phoneNumber
            query = "SELECT staffID, staffName, staffPhoneNumber, staffEmail, staffBirthday, staffGender, staffDescription FROM `staff` WHERE `staffID` LIKE ? OR `staffName` LIKE ? or `staffPhoneNumber` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            preparedStatement.setString(3,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                staffs.add(new staff(
                        resultSet.getString("staffID"),
                        resultSet.getString("staffName"),
                        resultSet.getString("staffPhoneNumber"),
                        resultSet.getString("staffEmail"),
                        resultSet.getDate("staffBirthday"),
                        resultSet.getBoolean("staffGender"),
                        resultSet.getString("staffDescription")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = staffContainer.getChildren().size();
        if (numChildren > 1) {
            staffContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        staffs.clear(); //clear the list staffs
        refreshData();
        getStaff();
    }
    public void refresh(String searchText){
        clearTable();
        staffs.clear(); //clear the list staffs
        refreshData(searchText);
        getStaff();
    }
    private void getStaff() {
        con = dbConnect.getConnect();

        for (staff staff : staffs) {
            HBox staffBox = new HBox(); //Create container to hold the staffs
            staffBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            staffBox.setPrefHeight(36);
            staffBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label staffIdLabel = new Label(staff.getStaffID());
            Label staffNameLabel = new Label(staff.getStaffName());
            Label staffPhoneNumberLabel = new Label(staff.getStaffPhoneNumber());
            Label staffEmaillabel = new Label(staff.getStaffEmail());
            Label staffGenderLabel;
            if (staff.isStaffGender()) //If staff gender = true then he's a Male because women always false (no no, im kidding, don't cancel me, i take that back)
                staffGenderLabel = new Label("Nam");
            else staffGenderLabel = new Label("Nữ");

            staffIdLabel.setMinWidth(130);
            staffNameLabel.setMinWidth(185);
            staffPhoneNumberLabel.setMinWidth(185);
            staffGenderLabel.setMinWidth(130);
            staffEmaillabel.setMinWidth(250);

            staffBox.setMargin(staffIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `staff` WHERE staffID = '" + staff.getStaffID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(staffManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa khách hàng, sao lại đi xóa khách hàng?");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("staffModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for staffAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    staffModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(staff.getStaffID());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Chỉnh sửa thông tin nhân viên");
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

            staffBox.getChildren().addAll(staffIdLabel, staffNameLabel, staffPhoneNumberLabel, staffGenderLabel, staffEmaillabel, buttonBox);
            staffBox.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        // Open a new scene or perform any action you want here
                        FXMLLoader Detailsloader = new FXMLLoader(getClass().getResource("staffDetail.fxml"));
                        Parent root = Detailsloader.load();
                        root.getStylesheets().add(getClass().getResource("css/staffDetail.css").toExternalForm());
                        staffDetailController staffDetailController = Detailsloader.getController();
                        // Pass a reference to the Scene A controller to Scene B
                        staffDetailController.setController(this);
                        if (staffDetailController != null) {
                            if (!staffBox.getChildren().isEmpty() && staffBox.getChildren().get(0) instanceof Label)
                            staffDetailController.setValue(((Label) staffBox.getChildren().get(0)).getText());
                            else warning("Something happen... i can't have children");
                        } else {
                            System.err.println("Controller is null.");
                        }
                        Stage stage = new Stage();
                        stage.setTitle("Thông tin chi tiết");
                        stage.setScene(new Scene(root));

                        stage.show();
                    }catch (IOException e){
                        warning("Không thể xem chi tiết nhân viên");
                    }
                }
            });
            // Add the HBox for each staff to your layout
            staffContainer.getChildren().add(staffBox);
        }

    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getStaff();
        //hide the scroll bar of the scroll pane
        staffsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        staffsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
