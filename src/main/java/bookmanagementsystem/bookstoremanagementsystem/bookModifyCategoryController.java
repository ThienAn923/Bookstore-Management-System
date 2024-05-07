package bookmanagementsystem.bookstoremanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class bookModifyCategoryController implements Initializable {
    @FXML
    private Button modifyCategoryButton;
    @FXML
    private Button modifyMoreCategoryButton;
    @FXML
    private TextField findBox;
    @FXML
    private VBox categoryContainer;
    @FXML
    private ScrollPane categoriesScrollPane;

    ObservableList<category> categories = FXCollections.observableArrayList();
    ObservableList<haveCategory> haveCategories = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    category category = null ;
    String searchText = "";
    private ToggleGroup mainCategoryToggleGroup;
    String bookID;

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void findCategory(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getCategory();
    }
    bookModifyController bookModifyController;
    public void setController(bookModifyController bookModifyController) {
        this.bookModifyController = bookModifyController;
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

    private void getCategory() {
        con = dbConnect.getConnect();
        mainCategoryToggleGroup = new ToggleGroup();

        for (category category : categories) {
            HBox categoryBox = new HBox(); //Create container to hold the categories
            categoryBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            categoryBox.setPrefHeight(36);
            categoryBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label categoryIdLabel = new Label(category.getCategoryID());
            Label categoryNameLabel = new Label(category.getCategoryName());

            RadioButton isMainCategoryRadioButton = new RadioButton();
            isMainCategoryRadioButton.setToggleGroup(mainCategoryToggleGroup);

            CheckBox selected = new CheckBox("");

            //setter
            for(haveCategory haveCategory : haveCategories){
                if(haveCategory.getCategoryID().equals(category.getCategoryID())){
                    if(haveCategory.isIsMainCategory()) isMainCategoryRadioButton.setSelected(true);
                    selected.setSelected(true);
                    break;
                }
            }


            categoryIdLabel.setMinWidth(100);
            categoryNameLabel.setMinWidth(150);
            selected.setMinWidth(70);
            isMainCategoryRadioButton.setMinWidth(50);
            categoryBox.setMargin(categoryIdLabel, new Insets(0,0,0,10));

            categoryBox.getChildren().addAll(categoryIdLabel, categoryNameLabel,isMainCategoryRadioButton,selected);

            // Modify the HBox for each category to your layout
            categoryContainer.getChildren().add(categoryBox);
        }
    }
    public void refresh(){
        clearTable();
        categories.clear(); //clear the list categories
        refreshData("");
        getCategory();
    }
    public void refresh(String searchText){
        clearTable();
        categories.clear(); //clear the list categories
        refreshData(searchText);
        getCategory();
    }

    @FXML
    private void modifyCategory(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("categoryModify.fxml"));
            Parent root = (Parent) loader.load();

            categoryAddController categoryAddController = loader.getController();
            categoryAddController.setController(this);
            categoryAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm tác giả");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void modifyCategoryToBook() {
        ObservableList<String> selectedCategories = FXCollections.observableArrayList();
        String mainCategory = "";
        // Loop through each HBox in the categoryContainer
        for (int i = 1; i < categoryContainer.getChildren().size(); i++) {
            Node node = categoryContainer.getChildren().get(i);
            if (node instanceof HBox) {
                HBox categoryBox = (HBox) node;

                // Get the category ID and whether it is the main category
                Label categoryIdLabel = (Label) categoryBox.getChildren().get(0);
                CheckBox selectedCheckBox = (CheckBox) categoryBox.getChildren().get(3);
                RadioButton isMainCategoryRadioButton = (RadioButton) categoryBox.getChildren().get(2);

                // Check if this category is selected as the main category
                if (selectedCheckBox.isSelected()) {
                    if(isMainCategoryRadioButton.isSelected()){
                        mainCategory = categoryIdLabel.getText();

                    }
                    // Add the category ID to the list of selected categories
                    selectedCategories.add(categoryIdLabel.getText());

                }
            }
        }
        bookModifyController.getCategories(mainCategory,selectedCategories);
    }

    void getCategories(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT * FROM `haveCategory` WHERE bookID = '" + bookID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                haveCategories.add(new haveCategory(
                        resultSet.getString("bookID"),
                        resultSet.getString("categoryID"),
                        resultSet.getBoolean("isMainCategory")));

            }
        } catch (SQLException e) {
            Logger.getLogger(categoryAddController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    void setValue(String id){
        bookID = id;
        getCategories();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData("");

        getCategory();
        //hide the scroll bar of the scroll pane
        categoriesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        categoriesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
