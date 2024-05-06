package bookmanagementsystem.bookstoremanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class staffDetailController implements Initializable {

    @FXML
    private Label staffIDLabel;
    @FXML
    private Label staffNameLabel;
    @FXML
    private Label staffPhoneNumberLabel;
    @FXML
    private Label staffEmailLabel;
    @FXML
    private Label staffBirthdayLabel;
    @FXML
    private Label staffDescriptionLabel;
    @FXML
    private Label staffGenderLabel;
    @FXML
    private Label staffPositionLabel;
    @FXML
    private Label staffDepartmentLabel;
    @FXML
    private Label staffJobStartDateLabel;
    @FXML
    private ImageView staffImage;
    @FXML
    private Label profileLabel;




    String positionID = ""; //waiting for user to select position and department from choiceBox
    String departmentID = "";
    Date deleteDateOfAssignment;
    String staffID;
    boolean staffGender = true;
    String staffName;
    String staffPhoneNumber;
    String staffEmail;
    Date staffBirthday;
    String staffDescription;
    String imagePath = "";
    String searchText = "";
    Date dateOfAssignment;
    Image image;


    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    void warning(String warning){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(warning);
        alert.showAndWait();
    }

    private staffManagementController staffManagementController ;
    public void setController(staffManagementController staffManagementController){
        this.staffManagementController = staffManagementController;
    }

    void setValue(String id){
        staffID = id;
        try {
            con = dbConnect.getConnect();
            query = "SELECT * FROM staff WHERE staffID = '" + staffID + "'";

            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            resultSet.next();
            staffID = resultSet.getString("staffID");
            staffName = resultSet.getString("staffName");
            staffPhoneNumber = resultSet.getString("staffPhoneNumber");
            staffEmail = resultSet.getString("staffEmail");
            staffBirthday = resultSet.getDate("staffBirthday");
            staffGender = resultSet.getBoolean("staffGender");
            staffDescription = resultSet.getString("staffDescription");

            Blob imageData = resultSet.getBlob("staffImage"); // Retrieve image data from the database
            if (imageData != null) {
                // Read the binary stream from the Blob

                try (InputStream inputStream = imageData.getBinaryStream()) {
                    // Create a byte array to store the image data
                    byte[] imageBytes = inputStream.readAllBytes();

                    // Convert the byte array to a JavaFX Image
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                    image = new Image(byteArrayInputStream);


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            //get the latest assignment
            query = "SELECT * from assign WHERE staffID = '" + staffID + "' ORDER BY dateOfAssignment DESC LIMIT 1";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();

            positionID = resultSet.getString("positionID");
            departmentID = resultSet.getString("departmentID");
            dateOfAssignment = resultSet.getDate("dateOfAssignment");

            //get id and name from position and department class
            query = "SELECT  * from position WHERE positionID = '" + positionID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();
            String positionName = resultSet.getString("positionName");

            query = "SELECT  * from department WHERE departmentID = '" + departmentID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();
            String departmentName = resultSet.getString("departmentName");

            //Set value for Label
            staffIDLabel.setText(staffID);
            staffNameLabel.setText(staffName);
            staffPhoneNumberLabel.setText(staffPhoneNumber);
            staffEmailLabel.setText(staffEmail);
            staffBirthdayLabel.setText(String.valueOf(staffBirthday.toLocalDate()));
            staffDescriptionLabel.setText(staffDescription);
            staffPositionLabel.setText(positionName);
            staffDepartmentLabel.setText(departmentName);
            staffJobStartDateLabel.setText(String.valueOf(dateOfAssignment));
            if ((staffGender)) {
                staffGenderLabel.setText("Nam");
            } else {
                staffGenderLabel.setText("Nữ");
            }
            // Set the Image to the ImageView
            if (image != null) {
                staffImage.setImage(image);
            } else {
                // Handle the case where the image is null
                profileLabel.setText(profileLabel.getText() + " (Không có hình ảnh)");
            }


            staffIDLabel.setWrapText(true);
            staffNameLabel.setWrapText(true);
            staffPhoneNumberLabel.setWrapText(true);
            staffEmailLabel.setWrapText(true);
            staffBirthdayLabel.setWrapText(true);
            staffDescriptionLabel.setWrapText(true);
            staffPositionLabel.setWrapText(true);
            staffDepartmentLabel.setWrapText(true);


        }catch(SQLException e){
            Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
