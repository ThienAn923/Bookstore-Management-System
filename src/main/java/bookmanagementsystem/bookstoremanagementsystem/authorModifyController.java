package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class authorModifyController implements Initializable {
    @FXML
    private TextField authorIDField;
    @FXML
    private TextField authorNameField;
    @FXML
    private TextArea authorDescriptionField;
    @FXML
    private Button authorModifyButton;
    @FXML
    private Button cleanButton;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String authorID;


    String searchText = "";
    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private authorManagementController authorManagementController ;
    public void setController(authorManagementController authorManagementController){
        this.authorManagementController = authorManagementController;
    }


    @FXML
    private void modifyAuthor() {
        authorID = authorIDField.getText();
        String authorName = authorNameField.getText();
        String authorDescription = authorDescriptionField.getText();
        con = dbConnect.getConnect();
        //if the id and name of the author is empty, pop up alert ( description is allowed to be empty)
        if (authorID.isEmpty() || authorName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        authorManagementController.refresh(searchText); //to refresh the authorManagement every time a author is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, authorIDField.getText());
            preparedStatement.setString(2, authorNameField.getText());
            preparedStatement.setString(3, authorDescriptionField.getText());
            preparedStatement.setString(4, authorID);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(authorAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        authorNameField.setText(null);
        authorDescriptionField.setText(null);
    }

    private void getQuery() {
        query = "UPDATE `author` SET "
                + "`authorID` = ?,"
                + "`authorName` = ?,"
                + "`authorDescription` = ? WHERE `authorID` = ?";
    }
    void setValue(String id, String name, String description){
        authorID = id;
        authorIDField.setText(id);
        authorNameField.setText(name);
        authorDescriptionField.setText(description);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authorIDField.setEditable(false);
    }
}
