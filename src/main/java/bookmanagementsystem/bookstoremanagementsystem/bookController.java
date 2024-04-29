package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;
import bookmanagementsystem.bookstoremanagementsystem.book;
public class bookController implements Initializable {
    @FXML
    ImageView bookImage;
    @FXML
    Label bookID;
    @FXML
    Label bookAuthor;
    @FXML
    Label bookPublisher;
    @FXML
    Label bookCost;
    @FXML
    Label bookRepublish;
    @FXML
    Label bookLocation;
    @FXML
    Label bookDescription;
    @FXML
    Label bookCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setData(book book){
        Image image = new Image(getClass().getResource(book.getBookImage()).toExternalForm());
        bookImage.setImage(image);
        bookID.setText(book.getBookID());
        bookAuthor.setText(book.getBookAuthor());
        bookPublisher.setText(book.getBookPublisher());
        bookCost.setText(String.valueOf(book.getBookCost()));
        bookRepublish.setText(String.valueOf(book.getBookRepublish()));
        bookLocation.setText(book.getBookLocation());
        bookPublisher.setText(book.getBookPublisher());
        bookCategory.setText(book.getBookCategory());
        bookDescription.setText(book.getBookDescription());
    }
}
