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

public class authorManagementController implements Initializable {
    @FXML
    private Button AuthorAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label testLabel;
    @FXML
    private TextField findBox;
    @FXML
    private VBox authorContainer;
    @FXML
    private ScrollPane authorsScrollPane;

    ObservableList<author> authors = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    author author = null ;
    String searchText;
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

    @FXML
    private void addAuthor(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("authorAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for authorAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            authorAddController authorAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
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

    private void refreshData() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT * FROM `author`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

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
    public void refresh(){
        clearTable();
        authors.clear(); //clear the list authors
        refreshData();
        getAuthor();
    }
    public void refresh(String searchText){
        clearTable();
        authors.clear(); //clear the list authors
        refreshData(searchText);
        getAuthor();
    }
    private void getAuthor() {
        con = dbConnect.getConnect();

        for (author author : authors) {
            HBox authorBox = new HBox(); //Create container to hold the authors
            authorBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            authorBox.setPrefHeight(36);
            authorBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label authorIdLabel = new Label(author.getAuthorID());
            Label authorNameLabel = new Label(author.getAuthorName());
            Label authorDescriptionLabel;
            //if there are no description about this author, show "Không có mô tả" ("No description")
            if (!author.getAuthorDescription().isEmpty())
                authorDescriptionLabel = new Label(author.getAuthorDescription());
            else authorDescriptionLabel = new Label("(Không có mô tả!)");

            authorIdLabel.setMinWidth(185);
            authorNameLabel.setMinWidth(185);
            authorDescriptionLabel.setMinWidth(510);

            authorBox.setMargin(authorIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `author` WHERE authorID = '" + author.getAuthorID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(authorManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa tác giả, hãy chắc chắn tác giả bạn đang xóa không còn liên kết với sách nào");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("authorModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for authorAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    authorModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(author.getAuthorID(), author.getAuthorName(), author.getAuthorDescription());
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

            authorBox.getChildren().addAll(authorIdLabel, authorNameLabel, authorDescriptionLabel, buttonBox);

            // Add the HBox for each author to your layout
            authorContainer.getChildren().add(authorBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getAuthor();
        //hide the scroll bar of the scroll pane
        authorsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        authorsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}

