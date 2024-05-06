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
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class positionManagementController implements Initializable {
    @FXML
    private Button PositionAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label testLabel;
    @FXML
    private TextField findBox;
    @FXML
    private VBox positionContainer;
    @FXML
    private ScrollPane positionsScrollPane;

    ObservableList<position> positions = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    position position = null ;
    String searchText = "";
    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    //turn number to formatted salary
    String toSalary(float salary){
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        return formatter.format(salary * 1000);
    }

    @FXML
    void findPosition(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getPosition();
    }

    @FXML
    private void addPosition(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("positionAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for positionAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            positionAddController positionAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            positionAddController.setController(this);
            positionAddController.setSearchText(searchText);

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
            query = "SELECT * FROM `position`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                positions.add(new position(
                        resultSet.getString("positionID"),
                        resultSet.getString("positionName"),
                        resultSet.getInt("positionSalary")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            positions.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `position` WHERE `positionID` LIKE ? OR `positionName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                positions.add(new position(
                        resultSet.getString("positionID"),
                        resultSet.getString("positionName"),
                        resultSet.getInt("positionSalary")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = positionContainer.getChildren().size();
        if (numChildren > 1) {
            positionContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        positions.clear(); //clear the list positions
        refreshData();
        getPosition();
    }
    public void refresh(String searchText){
        clearTable();
        positions.clear(); //clear the list positions
        refreshData(searchText);
        getPosition();
    }
    private void getPosition() {
        con = dbConnect.getConnect();

        for (position position : positions) {
            HBox positionBox = new HBox(); //Create container to hold the positions
            positionBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            positionBox.setPrefHeight(36);
            positionBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label positionIdLabel = new Label(position.getPositionID());
            Label positionNameLabel = new Label(position.getPositionName());
            Label positionSalaryLabel = new Label(toSalary(position.getPositionSalaryCoefficient()));

            positionIdLabel.setMinWidth(185);
            positionNameLabel.setMinWidth(510);
            positionSalaryLabel.setMinWidth(185);

            positionBox.setMargin(positionIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `position` WHERE positionID = '" + position.getPositionID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(positionManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa tác giả, hãy chắc chắn tác giả bạn đang xóa không còn liên kết với sách nào");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("positionModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for positionAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    positionModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(position.getPositionID(), position.getPositionName(), String.valueOf(position.getPositionSalaryCoefficient()));
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Chỉnh sửa tác giả");
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

            positionBox.getChildren().addAll(positionIdLabel, positionNameLabel, positionSalaryLabel, buttonBox);

            // Add the HBox for each position to your layout
            positionContainer.getChildren().add(positionBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getPosition();
        //hide the scroll bar of the scroll pane
        positionsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        positionsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
