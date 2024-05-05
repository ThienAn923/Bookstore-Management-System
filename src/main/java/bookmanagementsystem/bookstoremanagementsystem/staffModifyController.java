package bookmanagementsystem.bookstoremanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class staffModifyController implements Initializable {
    @FXML
    private ToggleGroup staffGenderGroup;
    @FXML
    private TextField staffIDField;
    @FXML
    private TextField staffNameField;
    @FXML
    private TextField staffPhoneNumberField;
    @FXML
    private TextField staffEmailField;
    @FXML
    private DatePicker staffBirthdayField;
    @FXML
    private TextArea staffDescriptionField;
    @FXML
    private TextField staffImageField;
    @FXML
    private RadioButton maleRbutton, femaleRbutton;
    @FXML
    private Button staffmodifyButton;
    @FXML
    private Button cleanButton;
    @FXML
    private Button pickImageButton;
    @FXML
    private CheckBox jobTransferCheckBox;


    @FXML
    private ChoiceBox<String> positionIDBox;
    @FXML
    private ChoiceBox<String> departmentIDBox;

    Map<String, String> positionsMap = new HashMap<>();
    List<String> positionsNameAndIDList = new ArrayList<>();
    Map<String, String> departmentsMap = new HashMap<>();
    List<String> departmentsNameAndIDList = new ArrayList<>();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    //i am tired, it's midnight, my deadline is coming... So i choose the easiest option
    //that is create deleteStuff rather than create a better solution
    //if i have time, i'll clean this code.
    String staffID, deleteStaffID = "";
    String positionID, deletePositionID = ""; //waiting for user to select position and department from choiceBox
    String departmentID, deleteDepartmentID = "";
    Date deleteDateOfAssignment;
    boolean staffGender = true;
    String staffName;
    String staffPhoneNumber;
    String staffEmail;
    Date staffBirthday;
    String staffDescription;
    String imagePath = "";
    String searchText = "";
    Date dateOfAssignment;
    Date today = Date.valueOf(LocalDate.now());



    public void pickImage() {
        // Create a file chooser
        FileChooser fileChooser = new FileChooser();

        // Set title for file chooser dialog
        fileChooser.setTitle("Select Staff Image");

        // Set initial directory (optional)
        // fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Filter to show only JPEG and PNG files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        Stage stage = new Stage();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        // Check if files were selected
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            // Return the path of the first selected file
            imagePath =  selectedFiles.get(0).getAbsolutePath();
            staffImageField.setText(imagePath);
        } else {
            // No file selected or dialog closed
            imagePath = null;
            warning("Có lỗi xảy ra. Chưa có hình ảnh được chọn");
        }
    }
    void warning(String warning){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(warning);
        alert.showAndWait();
    }

    void setUpChoicebox(){
        //extract item from position and department name and id list to put it to choice box
        positionIDBox.getItems().addAll(positionsNameAndIDList);
        departmentIDBox.getItems().addAll(departmentsNameAndIDList);

    }
    private void getAllID() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT positionID, positionName FROM `position`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String positionID = resultSet.getString("positionID");
                String positionNameAndID = positionID + " - " + resultSet.getString("positionName");

                //add to map table and position list
                positionsMap.put(positionNameAndID, positionID);
                positionsNameAndIDList.add(positionNameAndID);
            }

            query = "SELECT departmentID, departmentName FROM `department`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String departmentID = resultSet.getString("departmentID");
                String departmentNameAndID = departmentID + " - " + resultSet.getString("departmentName");

                //add to map table and department list
                departmentsMap.put(departmentNameAndID, departmentID);
                departmentsNameAndIDList.add(departmentNameAndID);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getIDPosition(ActionEvent event){
        positionID = positionsMap.get(positionIDBox.getValue());
    }
    public void getIDdepartment(ActionEvent event){
        departmentID = departmentsMap.get(departmentIDBox.getValue());
    }

    // this get the search text from the management UI then send it to here. Then to later use it to refresh page.
    void setSearchText(String searchText){
        this.searchText = searchText;
    }
    private staffManagementController staffManagementController ;
    public void setController(staffManagementController staffManagementController){
        this.staffManagementController = staffManagementController;
    }

    //if male radio button is selected, return true, else, false
    @FXML
    void setGender(){
        staffGender = maleRbutton.isSelected();
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `staff`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "C000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `staff` WHERE `staffID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    staffIDField.setText(id);
                    break; // Exit loop if unused ID is found
                }

            }

            //this part is only use as the final option after 100 try and still no usable ID found
            //it will create true random of character (only until the world burn, this will result in a duplicate ID in the database)
            if(!foundUnusedID){
                final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                StringBuilder randomID = new StringBuilder();
                final SecureRandom secureRandom = new SecureRandom();
                // Generate random characters one by one until the desired length is reached
                for (int i = 0; i < 10; i++) {
                    // Generate a random index to select a character from CHARACTERS
                    int randomIndex = secureRandom.nextInt(CHARACTERS.length());
                    // Append the randomly selected character to the randomID string
                    randomID.append(CHARACTERS.charAt(randomIndex));
                }
                staffIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void modifyStaff() {
        staffID = staffIDField.getText();
        String staffName = staffNameField.getText();
        String staffPhoneNumber = staffPhoneNumberField.getText();
        String staffEmail = staffEmailField.getText();

        //change positionID and departmentID to create new assign to the database
        //if and only if dateOfAssignment is not today because... be for real
        // who on earth would get job transfer in day 1.
        if(jobTransferCheckBox.isSelected() && dateOfAssignment != today){
            positionID = positionsMap.get(positionIDBox.getValue());
            departmentID = departmentsMap.get(departmentIDBox.getValue());
        }
        else{
            //4 attribute is all primary key so in other to modify, i have to create a new one and delete the old one
            //If i want to "jobsTransfer", i keep the old line
            deleteAssignment();
        }

        con = dbConnect.getConnect();
        if (staffID.isEmpty() || staffName.isEmpty() || staffPhoneNumber.isEmpty() || staffEmail.isEmpty() || staffBirthdayField.getValue() == null) {
            warning("Có thể có khung còn trống");
            return;
        } else {
            getQuery();
            insert();
            getQueryAssign();
            insertAssign();
            clean();

        }
        staffManagementController.refresh(searchText); //to refresh the staffManagement every time a staff is created
        Stage stage = (Stage) staffIDField.getScene().getWindow(); //any FxID would work
        stage.close();
    }
    private void insert() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, staffIDField.getText());
            preparedStatement.setString(2, staffNameField.getText());
            preparedStatement.setString(3, staffPhoneNumberField.getText());
            preparedStatement.setString(4, staffEmailField.getText());
            preparedStatement.setDate(5, java.sql.Date.valueOf(staffBirthdayField.getValue()));
            preparedStatement.setBoolean(6, staffGender);
            preparedStatement.setString(7, staffDescriptionField.getText());
            if(!imagePath.isEmpty())
                preparedStatement.setString(8, staffImageField.getText());

            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void clean() {
        staffIDField.setText(null);
        staffNameField.setText(null);
        staffPhoneNumberField.setText(null);
        staffEmailField.setText(null);
        staffBirthdayField.setValue(LocalDate.of(2000,1,1));
        staffDescriptionField.setText(null);
        maleRbutton.setSelected(true);
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

            deleteStaffID = staffID;

            staffIDField.setText(staffID);
            staffNameField.setText(staffName);
            staffPhoneNumberField.setText(staffPhoneNumber);
            staffEmailField.setText(staffEmail);
            staffBirthdayField.setValue(staffBirthday.toLocalDate());
            staffDescriptionField.setText(staffDescription);
            if ((staffGender)) {
                maleRbutton.setSelected(true);
            } else {
                femaleRbutton.setSelected(true);
            }
            //get the latest assignment
            query = "SELECT * from assign WHERE staffID = '" + staffID + "' ORDER BY dateOfAssignment DESC LIMIT 1";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();

            positionID = resultSet.getString("positionID");
            departmentID = resultSet.getString("departmentID");
            dateOfAssignment = resultSet.getDate("dateOfAssignment");

            deletePositionID = positionID;
            deleteDepartmentID = departmentID;
            deleteDateOfAssignment = dateOfAssignment;

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

            //Set value for ID box
            positionIDBox.setValue(positionID + " - " + positionName);
            departmentIDBox.setValue(departmentID + " - " + departmentName);


        }catch(SQLException e){
            Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    private void getQuery() {
        query = "UPDATE `staff` SET "
                + "`staffID`=?,"
                + "`staffName`= ?,"
                + "`staffPhoneNumber`=?,"
                + "`staffEmail`= ?,"
                + "`staffBirthday`=?,"
                + "`staffGender`= ?,"
                + "`staffDescription`=?";
        //if imagePath != null or empty then send it to database
        if(!imagePath.isEmpty()) query += "`staffImage` = ?";
        query += " WHERE staffID = '"+staffID+"'";

    }

    void deleteAssignment(){
        try {
            query = "DELETE FROM `assign`" + " WHERE "
                    + "(staffID = '" + deleteStaffID + "' AND positionID = '"
                    + deletePositionID + "' AND departmentID = '"
                    + deleteDepartmentID + "' AND dateOfAssignment = '" + deleteDateOfAssignment + "')";
            staffDescriptionField.setText(query);
            preparedStatement = con.prepareStatement(query);
            preparedStatement.execute();
        }catch (SQLException e){
            Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    private void getQueryAssign() {
        if (!jobTransferCheckBox.isSelected()) {
            query = "INSERT INTO `assign`( `staffID`, `departmentID`,`positionID`,`dateOfAssignment`) VALUES (?,?,?,?)";

        }
        else
            query = "INSERT INTO `assign`( `staffID`, `departmentID`,`positionID`,`dateOfAssignment`) VALUES (?,?,?,?)";
    }

    private void insertAssign() {
        try {

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, staffID);
            preparedStatement.setString(2, departmentID);
            preparedStatement.setString(3, positionID);
            if(!jobTransferCheckBox.isSelected())
                preparedStatement.setDate(4, dateOfAssignment);
            else preparedStatement.setDate(4,today);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maleRbutton.setSelected(true);
        getAllID(); // get all position/department ID
        //pass all data from getAllID to list box
        setUpChoicebox();
        //choose the fist option as default
        positionIDBox.setOnAction(this::getIDPosition);
        departmentIDBox.setOnAction(this::getIDdepartment);
        staffIDField.setEditable(false);
    }
}
