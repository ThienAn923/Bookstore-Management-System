package bookmanagementsystem.bookstoremanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

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

public class bookManagementController implements Initializable {
    @FXML
    private TableView<book> bookTable;
    @FXML
    private TableColumn<book, String> bookIdCol;
    @FXML
    private TableColumn<book, String> bookAuthorCol;
    @FXML
    private TableColumn<book, String> bookPublisherCol;
    @FXML
    private TableColumn<book, Integer> bookCostCol;
    @FXML
    private TableColumn<book, Integer> bookRepulbishCol;
    @FXML
    private TableColumn<book, String> bookLocationCol;
    @FXML
    private TableColumn<book, String> bookCategoryCol;
    @FXML
    private TableColumn<book, String> bookDescriptionCol;
    @FXML
    ObservableList<book>  books = FXCollections.observableArrayList();

    String query = null;
    Connection connection = null ;
    PreparedStatement preparedStatement = null ;
    ResultSet resultSet = null ;
    book book = null ;
    @FXML
//    private Label lalala;
    private void refreshTable() {
        try {
            books.clear();
            Connection con = dbConnect.getConnect();
            query = "SELECT * FROM `book`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                books.add(new book(
                        resultSet.getString("bookID"),
                        resultSet.getInt("bookRepublish"),
                        resultSet.getInt("bookCost"),
                        resultSet.getString("bookAuthor"),
                        resultSet.getString("bookDescription"),
                        resultSet.getString("bookImage"),
                        resultSet.getString("bookLocation"),
                        resultSet.getString("bookCategory"),
                        resultSet.getString("bookPublisher")));
                bookTable.setItems((ObservableList<bookmanagementsystem.bookstoremanagementsystem.book>) books);

            }

            con.close();


        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    private void getBook() {

        connection = dbConnect.getConnect();
        refreshTable();

        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        bookAuthorCol.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        bookPublisherCol.setCellValueFactory(new PropertyValueFactory<>("bookPublisher"));
        bookCostCol.setCellValueFactory(new PropertyValueFactory<>("bookCost"));
        bookRepulbishCol.setCellValueFactory(new PropertyValueFactory<>("bookRepublish"));
        bookLocationCol.setCellValueFactory(new PropertyValueFactory<>("bookLocation"));
        bookCategoryCol.setCellValueFactory(new PropertyValueFactory<>("bookCategory"));
        bookDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("bookDescription"));


    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getBook();
    }
//    private List<book> book(){
//        List<book> ls = new ArrayList<>();
//        for(int i = 0; i < 10; i++) {
//            book book = new book();
//            book.setBookImage("images/loginLogo2.png");
//            book.setBookAuthor("Thiên Ân");
//            book.setBookCategory("Sex");
//            book.setBookDescription("Hot book.... really really hot...");
//            book.setBookID("B1111");
//            book.setBookCost(50000);
//            book.setBookRepublish(2);
//            book.setBookLocation("Ke 2");
//            book.setBookPublisher("Nha xuat ban kim dong");
//            ls.add(book);
//        }
//        return ls;
//    }

}
