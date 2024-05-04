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

public class publisherManagementController implements Initializable {
    @FXML
    private Button publisherAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField findBox;
    @FXML
    private VBox publisherContainer;
    @FXML
    private ScrollPane publishersScrollPane;

    ObservableList<publisher> publishers = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    publisher publisher = null ;
    String searchText = "";

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findPublisher(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getPublisher();
    }

    @FXML
    private void addPublisher(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("publisherAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for publisherAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            publisherAddController publisherAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            publisherAddController.setController(this);
            publisherAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm Nhà Xuất Bản");
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
            query = "SELECT * FROM `publisher`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                publishers.add(new publisher(
                        resultSet.getString("publisherID"),
                        resultSet.getString("publisherName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            publishers.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `publisher` WHERE `publisherID` LIKE ? OR `publisherName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                publishers.add(new publisher(
                        resultSet.getString("publisherID"),
                        resultSet.getString("publisherName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = publisherContainer.getChildren().size();
        if (numChildren > 1) {
            publisherContainer.getChildren().remove(1, numChildren);
        }
    }

    public void refresh(){
        clearTable();
        publishers.clear(); //clear the list areas
        refreshData();
        getPublisher();
    }
    public void refresh(String searchText){
        clearTable();
        publishers.clear(); //clear the list areas
        refreshData(searchText);
        getPublisher();
    }

    private void getPublisher() {
        con = dbConnect.getConnect();

        for (publisher publisher : publishers) {
            HBox publisherBox = new HBox(); //Create container to hold the publishers
            publisherBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            publisherBox.setPrefHeight(36);
            publisherBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label publisherIdLabel = new Label(publisher.getPublisherID());
            Label publisherNameLabel = new Label(publisher.getPublisherName());

            publisherIdLabel.setMinWidth(370);
            publisherNameLabel.setMinWidth(510);
            publisherBox.setMargin(publisherIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `publisher` WHERE publisherID = '" + publisher.getPublisherID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(publisherManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa nhà xuất bản, hãy chắc chắn nhà xuất bản bạn đang xóa không xuất bản cuốn sách nào!");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("publisherModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for publisherAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    publisherModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(publisher.getPublisherID(), publisher.getPublisherName());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Modify Publisher");
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

            publisherBox.getChildren().addAll(publisherIdLabel, publisherNameLabel, buttonBox);
            // Add the HBox for each floor to your layout
            publisherContainer.getChildren().add(publisherBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getPublisher();
        //hide the scroll bar of the scroll pane
        publishersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        publishersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
