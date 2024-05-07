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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class bookManagementController implements Initializable{
    @FXML
    private Button bookAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label testLabel;
    @FXML
    private TextField findBox;
    @FXML
    private VBox bookContainer;
    @FXML
    private ScrollPane booksScrollPane;


    ObservableList<book> books = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    book book = null ;
    String searchText = "";

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findBook(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getBook();
    }

    @FXML
    private void addBook(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("bookAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for bookAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            bookAddController bookAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            bookAddController.setController(this);
            bookAddController.setSearchText(searchText);

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
            query = "SELECT bookID, bookName, bookRepublish, bookDescription, bookQuantity, shelveID , publisherID  FROM `book`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                books.add(new book(
                        resultSet.getString("bookID"),
                        resultSet.getString("bookName"),
                        resultSet.getInt("bookRepublish"),
                        resultSet.getString("bookDescription"),
                        resultSet.getString("shelveID"),
                        resultSet.getString("publisherID"),
                        resultSet.getInt("bookQuantity")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            books.clear();
            con = dbConnect.getConnect();
            //search by name, id or phoneNumber
            query = "SELECT bookID, bookName, bookRepublish, bookDescription, bookQuantity, shelveID , publisherID FROM `book` WHERE `bookID` LIKE ? OR `bookName` LIKE ? ";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                books.add(new book(
                        resultSet.getString("bookID"),
                        resultSet.getString("bookName"),
                        resultSet.getInt("bookRepublish"),
                        resultSet.getString("bookDescription"),
                        resultSet.getString("shelveID"),
                        resultSet.getString("publisherID"),
                        resultSet.getInt("bookQuantity")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = bookContainer.getChildren().size();
        if (numChildren > 1) {
            bookContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        books.clear(); //clear the list books
        refreshData();
        getBook();
    }
    public void refresh(String searchText){
        clearTable();
        books.clear(); //clear the list books
        refreshData(searchText);
        getBook();
    }
    private void getBook() {
        con = dbConnect.getConnect();

        for (book book : books) {
            HBox bookBox = new HBox(); //Create container to hold the books
            bookBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            bookBox.setPrefHeight(36);
            bookBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label bookIdLabel = new Label(book.getBookID());
            Label bookNameLabel = new Label(book.getBookName());
            Label bookRepublishLabel = new Label(String.valueOf(book.getBookRepublish()));
            Label bookDescriptionLabel = new Label(book.getBookDescription());
            Label shelveIDLabel = new Label(book.getShelveID());
            Label publisherIDLabel = new Label(book.getPublicsherID());
            Label bookQuantityLabel = new Label(String.valueOf(book.getBookQuantity()));


            bookIdLabel.setMinWidth(70);
            bookNameLabel.setMinWidth(270);
            shelveIDLabel.setMinWidth(200);
            bookRepublishLabel.setMinWidth(70);
            publisherIDLabel.setMinWidth(200);
            bookQuantityLabel.setMinWidth(70);

            bookBox.setMargin(bookIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `book` WHERE bookID = '" + book.getBookID() + "'";
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("bookModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for bookAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    bookModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);

                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(book.getBookID());
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


            bookBox.getChildren().addAll(bookIdLabel, bookNameLabel, shelveIDLabel, bookRepublishLabel, publisherIDLabel,bookQuantityLabel, buttonBox);
            bookBox.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        // Open a new scene or perform any action you want here
                        FXMLLoader Detailsloader = new FXMLLoader(getClass().getResource("bookDetail.fxml"));
                        Parent root = Detailsloader.load();
                        root.getStylesheets().add(getClass().getResource("css/staffDetail.css").toExternalForm());
                        bookDetailController bookDetailController = Detailsloader.getController();
                        // Pass a reference to the Scene A controller to Scene B
                        bookDetailController.setController(this);
                        if (bookDetailController != null) {
                            if (!bookBox.getChildren().isEmpty() && bookBox.getChildren().get(0) instanceof Label)
                                bookDetailController.setValue(((Label) bookBox.getChildren().get(0)).getText());
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
            // Add the HBox for each book to your layout
            bookContainer.getChildren().add(bookBox);
        }

    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getBook();
        //hide the scroll bar of the scroll pane
        booksScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        booksScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

}
