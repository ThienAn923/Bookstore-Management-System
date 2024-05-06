package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class positionModifyController implements Initializable {
    @FXML
    private TextField positionIDField;
    @FXML
    private TextField positionNameField;
    @FXML
    private TextField positionSalaryField;
    @FXML
    private Button positionModifyButton;
    @FXML
    private Button cleanButton;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String positionID;


    String searchText = "";
    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private positionManagementController positionManagementController ;
    public void setController(positionManagementController positionManagementController){
        this.positionManagementController = positionManagementController;
    }


    @FXML
    private void modifyPosition() {
        positionID = positionIDField.getText();
        String positionName = positionNameField.getText();
        String positionDescription = positionSalaryField.getText();
        con = dbConnect.getConnect();
        //if the id and name of the position is empty, pop up alert ( description is allowed to be empty)
        if (positionID.isEmpty() || positionName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        positionManagementController.refresh(searchText); //to refresh the positionManagement every time a position is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, positionIDField.getText());
            preparedStatement.setString(2, positionNameField.getText());
            preparedStatement.setString(3, positionSalaryField.getText());
            preparedStatement.setString(4, positionID);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(positionAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        positionNameField.setText(null);
        positionSalaryField.setText(null);
    }

    private void getQuery() {
        query = "UPDATE `position` SET "
                + "`positionID` = ?,"
                + "`positionName` = ?,"
                + "`positionSalary` = ? WHERE `positionID` = ?";
    }
    void setValue(String id, String name, String description){
        positionID = id;
        positionIDField.setText(id);
        positionNameField.setText(name);
        positionSalaryField.setText(description);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        positionIDField.setEditable(false);
    }
}
