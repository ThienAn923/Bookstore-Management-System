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

public class bookModifyAuthorController implements Initializable {
    @FXML
    private Button modifyAuthorButton;
    @FXML
    private Button modifyMoreAuthorButton;
    @FXML
    private TextField findBox;
    @FXML
    private VBox authorContainer;
    @FXML
    private ScrollPane authorsScrollPane;

    ObservableList<author> authors = FXCollections.observableArrayList();
    ObservableList<indite> indites = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    author author = null ;
    String searchText = "";
    private ToggleGroup mainAuthorToggleGroup;
    String bookID;

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void findAuthor(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getAuthor();
    }
    bookModifyController bookModifyController;
    public void setController(bookModifyController bookModifyController) {
        this.bookModifyController = bookModifyController;
    }

    private void refreshData(String searchText) {
        try {
            authors.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `author` WHERE `authorID` LIKE ? OR `authorName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                authors.add(new author(
                        resultSet.getString("authorID"),
                        resultSet.getString("authorName"),
                        resultSet.getString("authorDescription")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = authorContainer.getChildren().size();
        if (numChildren > 1) {
            authorContainer.getChildren().remove(1, numChildren);
        }
    }

    private void getAuthor() {
        con = dbConnect.getConnect();
        mainAuthorToggleGroup = new ToggleGroup();
        getAuthors();
        for (author author : authors) {
            HBox authorBox = new HBox(); //Create container to hold the authors
            authorBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            authorBox.setPrefHeight(36);
            authorBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label authorIdLabel = new Label(author.getAuthorID());
            Label authorNameLabel = new Label(author.getAuthorName());

            RadioButton isMainAuthorRadioButton = new RadioButton();
            isMainAuthorRadioButton.setToggleGroup(mainAuthorToggleGroup);

            CheckBox selected = new CheckBox("");

            //setter

            for(indite indite : indites){
                if(indite.getAuthorID().equals(author.getAuthorID())){
                    if(indite.isMainAuthor()) isMainAuthorRadioButton.setSelected(true);
                    selected.setSelected(true);
                    break;
                }
            }


            authorIdLabel.setMinWidth(100);
            authorNameLabel.setMinWidth(150);
            selected.setMinWidth(70);
            isMainAuthorRadioButton.setMinWidth(50);
            authorBox.setMargin(authorIdLabel, new Insets(0,0,0,10));

            authorBox.getChildren().addAll(authorIdLabel, authorNameLabel,isMainAuthorRadioButton,selected);

            // Modify the HBox for each author to your layout
            authorContainer.getChildren().add(authorBox);
        }
    }
    public void refresh(){
        clearTable();
        authors.clear(); //clear the list authors
        refreshData("");
        getAuthor();
    }
    public void refresh(String searchText){
        clearTable();
        authors.clear(); //clear the list authors
        refreshData(searchText);
        getAuthor();
    }

    @FXML
    private void modifyAuthor(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("authorModify.fxml"));
            Parent root = (Parent) loader.load();

            authorAddController authorAddController = loader.getController();
            authorAddController.setController(this);
            authorAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm tác giả");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void modifyAuthorToBook() {
        ObservableList<String> selectedAuthors = FXCollections.observableArrayList();
        String mainAuthor = "";
        // Loop through each HBox in the authorContainer
        for (int i = 1; i < authorContainer.getChildren().size(); i++) {
            Node node = authorContainer.getChildren().get(i);
            if (node instanceof HBox) {
                HBox authorBox = (HBox) node;

                // Get the author ID and whether it is the main author
                Label authorIdLabel = (Label) authorBox.getChildren().get(0);
                CheckBox selectedCheckBox = (CheckBox) authorBox.getChildren().get(3);
                RadioButton isMainAuthorRadioButton = (RadioButton) authorBox.getChildren().get(2);

                // Check if this author is selected as the main author
                if (selectedCheckBox.isSelected()) {
                    if(isMainAuthorRadioButton.isSelected()){
                        mainAuthor = authorIdLabel.getText();

                    }
                    // Add the author ID to the list of selected authors
                    selectedAuthors.add(authorIdLabel.getText());

                }
            }
        }
        bookModifyController.getAuthors(mainAuthor,selectedAuthors);
    }

    void getAuthors(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT * FROM `indite` WHERE `bookID` = '" + bookID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                indites.add(new indite(
                        resultSet.getString("authorID"),
                        resultSet.getString("bookID"),
                        resultSet.getBoolean("isMainAuthor")));

            }
            warning("2");
        } catch (SQLException e) {
            Logger.getLogger(authorAddController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    void setValue(String id){
        bookID = id;

    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData("");

        getAuthor();
        //hide the scroll bar of the scroll pane
        authorsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        authorsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
