package bookmanagementsystem.bookstoremanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class promotionModifyController implements Initializable {
    @FXML
    private TextField promotionIDField;
    @FXML
    private TextField promotionNameField;
    @FXML
    private TextArea promotionDescriptionField;
    @FXML
    private Button promotionModifyButton;
    @FXML
    private Button cleanButton;
    @FXML
    private DatePicker promotionStartDayField;
    @FXML
    private DatePicker promotionEndDayField;
    @FXML
    private TextField promotionPercentageField;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String promotionID;


    String searchText = "";
    void setSearchText(String searchText){
        this.searchText = searchText;
    }

    private promotionManagementController promotionManagementController ;
    public void setController(promotionManagementController promotionManagementController){
        this.promotionManagementController = promotionManagementController;
    }


    @FXML
    private void modifyPromotion() {
        promotionID = promotionIDField.getText();
        String promotionName = promotionNameField.getText();
        String promotionDescription = promotionDescriptionField.getText();
        con = dbConnect.getConnect();
        //if the id and name of the promotion is empty, pop up alert ( description is allowed to be empty)
        if (promotionID.isEmpty() || promotionName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Có thể có khung còn trống");
            alert.showAndWait();
        } else {
            getQuery();
            insert();
            clean();
        }
        promotionManagementController.refresh(searchText); //to refresh the promotionManagement every time a promotion is created
    }
    private void insert() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, promotionIDField.getText());
            preparedStatement.setString(2, promotionNameField.getText());
            preparedStatement.setString(3, promotionDescriptionField.getText());
            preparedStatement.setDate(1, Date.valueOf(promotionStartDayField.getValue()));
            preparedStatement.setDate(2, Date.valueOf(promotionEndDayField.getValue()));
            preparedStatement.setFloat(3, Float.parseFloat(promotionPercentageField.getText()));
            preparedStatement.setString(4, promotionID);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(promotionAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private void clean() {
        promotionNameField.setText(null);
        promotionDescriptionField.setText(null);
    }

    private void getQuery() {
        query = "UPDATE `promotion` SET "
                + "`promotionID` = ?,"
                + "`promotionName` = ?,"
                + "`promotionDescription` = ?,"
                + "`promotionStartDay` = ?,"
                + "`promotionEndDay` = ?,"
                + "`promotionPercentage` = ?WHERE `promotionID` = ?";
    }
    void setValue(String id, String name, String description, Date startDay, Date endDay, float percentage){
        promotionID = id;
        promotionIDField.setText(id);
        promotionNameField.setText(name);
        promotionDescriptionField.setText(description);
        promotionStartDayField.setValue(startDay.toLocalDate());
        promotionEndDayField.setValue(endDay.toLocalDate());
        promotionPercentageField.setText(String.valueOf(percentage));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        promotionIDField.setEditable(false);
    }
}
