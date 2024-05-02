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

public class categoryManagementController implements Initializable {
    @FXML
    private Button categoryAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField findBox;
    @FXML
    private VBox categoryContainer;
    @FXML
    private ScrollPane categoriesScrollPane;

    ObservableList<category> categories = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    category category = null ;

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findCategory(){
        String searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getCategory();
    }

    @FXML
    private void addCategory(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("categoryAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for categoryAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            categoryAddController categoryAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            categoryAddController.setController(this);

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
            query = "SELECT * FROM `category`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                categories.add(new category(
                        resultSet.getString("categoryID"),
                        resultSet.getString("categoryName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            categories.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `category` WHERE `categoryID` LIKE ? OR `categoryName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                categories.add(new category(
                        resultSet.getString("categoryID"),
                        resultSet.getString("categoryName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = categoryContainer.getChildren().size();
        if (numChildren > 1) {
            categoryContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        categories.clear(); //clear the list areas
        refreshData();
        getCategory();
    }
    private void getCategory() {
        con = dbConnect.getConnect();

        for (category category : categories) {
            HBox categoryBox = new HBox(); //Create container to hold the categorys
            categoryBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            categoryBox.setPrefHeight(36);
            categoryBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label categoryIdLabel = new Label(category.getCategoryID());
            Label categoryNameLabel = new Label(category.getCategoryName());

            categoryIdLabel.setMinWidth(370);
            categoryNameLabel.setMinWidth(510);
            categoryBox.setMargin(categoryIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `category` WHERE categoryID = '" + category.getCategoryID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(categoryManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể thể loại, hãy chắc chắn thể loại bạn đang xóa không thuộc cuốn sách nào!");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("categoryModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for categoryAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    categoryModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);

                    if (fac != null) {
                        fac.setValue(category.getCategoryID(), category.getCategoryName());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Modify Category");
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

            categoryBox.getChildren().addAll(categoryIdLabel, categoryNameLabel, buttonBox);
            // Add the HBox for each floor to your layout
            categoryContainer.getChildren().add(categoryBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getCategory();
        //hide the scroll bar of the scroll pane
        categoriesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        categoriesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
