package bookmanagementsystem.bookstoremanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.stage.Stage;
import java.io.File;

public class staffAddController implements Initializable {
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
    private Button staffAddButton;
    @FXML
    private Button cleanButton;
    @FXML
    private Button pickImageButton;


    @FXML
    private ChoiceBox<String> positionIDBox;
    @FXML
    private ChoiceBox<String> departmentIDBox;
    String positionID = ""; //waiting for user to select position and department from choiceBox
    String departmentID = "";
    Map<String, String> positionsMap = new HashMap<>();
    List<String> positionsNameAndIDList = new ArrayList<>();
    Map<String, String> departmentsMap = new HashMap<>();
    List<String> departmentsNameAndIDList = new ArrayList<>();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String staffID;
    boolean staffGender = true;
    String searchText = "";
    String imagePath;

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
        //set choice box's default value as the first value in database
        if (!positionsNameAndIDList.isEmpty()) {
            // Set the default value to the first option
            positionIDBox.setValue(positionsNameAndIDList.get(0));
            departmentIDBox.setValue(departmentsNameAndIDList.get(0));
            //set default position and department ID as the first position/department in database
            positionID = positionsMap.get(positionIDBox.getValue());
            departmentID = departmentsMap.get(departmentIDBox.getValue());
        }

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
                id = "STF000" + count;
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
    private void addStaff() {
        staffID = staffIDField.getText();
        String staffName = staffNameField.getText();
        String staffPhoneNumber = staffPhoneNumberField.getText();
        String staffEmail = staffEmailField.getText();


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
    }
    private void insert() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, staffIDField.getText());
            preparedStatement.setString(2, staffNameField.getText());
            preparedStatement.setString(3, staffPhoneNumberField.getText());
            preparedStatement.setString(4, staffEmailField.getText());
            preparedStatement.setDate(5, Date.valueOf(staffBirthdayField.getValue()));
            preparedStatement.setBoolean(6, staffGender);
            preparedStatement.setString(7, staffDescriptionField.getText());
            preparedStatement.setString(8, staffImageField.getText());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void getQuery() {
        query = "INSERT INTO `staff`( `staffID`, `staffName`,`staffPhoneNumber`,`staffEmail`,`staffBirthday`,`staffGender`,`staffDescription`,`staffImage`) VALUES (?,?,?,?,?,?,?,?)";
    }
    private void getQueryAssign() {
        query = "INSERT INTO `assign`( `staffID`, `departmentID`,`positionID`,`dateOfAssignment`) VALUES (?,?,?,?)";
    }
    private void insertAssign() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, staffID);
            preparedStatement.setString(2, departmentID);
            preparedStatement.setString(3, positionID);
            preparedStatement.setDate(4, Date.valueOf(LocalDate.now()));
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
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
    }
}
