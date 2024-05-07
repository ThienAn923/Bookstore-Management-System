package bookmanagementsystem.bookstoremanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class bookAddController implements Initializable {
    @FXML
    private TextField bookIDField;
    @FXML
    private TextField bookNameField;
    @FXML
    private TextField bookRepublishField;
    @FXML
    private TextField bookQuantityField;
    @FXML
    private TextArea bookDescriptionField;
    @FXML
    private TextField bookPriceField;
    @FXML
    private TextField bookCategoryField;
    @FXML
    private TextField bookAuthorField;

    @FXML
    private TextField bookImageField;
    @FXML
    private Button bookAddButton;
    @FXML
    private Button cleanButton;
    @FXML
    private Button pickImageButton;
    @FXML
    private Button chooseAuthor;
    @FXML
    private Button chooseCategory;



    @FXML
    private ChoiceBox<String> shelveIDBox;
    @FXML
    private ChoiceBox<String> publisherIDBox;
    String shelveID = ""; //waiting for user to select shelve and publisher from choiceBox
    String publisherID = "";
    Map<String, String> shelvesMap = new HashMap<>();
    List<String> shelvesNameAndIDList = new ArrayList<>();
    Map<String, String> publishersMap = new HashMap<>();
    List<String> publishersNameAndIDList = new ArrayList<>();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String bookID;
    String searchText = "";
    String imagePath = "";
    byte[] imageData;
    Date today = Date.valueOf(LocalDate.now());
    String mainAuthorID, mainCategoryID;
    ObservableList<author> authors = FXCollections.observableArrayList();
    ObservableList<category> categories = FXCollections.observableArrayList();

    public void pickImage() throws IOException {
        // Create a file chooser
        FileChooser fileChooser = new FileChooser();

        // Set title for file chooser dialog
        fileChooser.setTitle("Select Book Image");

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
            bookImageField.setText(imagePath);
            imageData = Files.readAllBytes(Paths.get(imagePath));
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
        //extract item from shelve and publisher name and id list to put it to choice box
        shelveIDBox.getItems().addAll(shelvesNameAndIDList);
        publisherIDBox.getItems().addAll(publishersNameAndIDList);
        //set choice box's default value as the first value in database
        if (!shelvesNameAndIDList.isEmpty()) {
            // Set the default value to the first option
            shelveIDBox.setValue(shelvesNameAndIDList.get(0));
            publisherIDBox.setValue(publishersNameAndIDList.get(0));
            //set default shelve and publisher ID as the first shelve/publisher in database
            shelveID = shelvesMap.get(shelveIDBox.getValue());
            publisherID = publishersMap.get(publisherIDBox.getValue());
        }

    }
    private void getAllID() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT shelveID, shelveName FROM `shelve`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String shelveID = resultSet.getString("shelveID");
                String shelveNameAndID = shelveID + " - " + resultSet.getString("shelveName");

                //add to map table and shelve list
                shelvesMap.put(shelveNameAndID, shelveID);
                shelvesNameAndIDList.add(shelveNameAndID);
            }

            query = "SELECT publisherID, publisherName FROM `publisher`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String publisherID = resultSet.getString("publisherID");
                String publisherNameAndID = publisherID + " - " + resultSet.getString("publisherName");

                //add to map table and publisher list
                publishersMap.put(publisherNameAndID, publisherID);
                publishersNameAndIDList.add(publisherNameAndID);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getIDShelve(ActionEvent event){
        shelveID = shelvesMap.get(shelveIDBox.getValue());
    }
    public void getIDpublisher(ActionEvent event){
        publisherID = publishersMap.get(publisherIDBox.getValue());
    }

    // this get the search text from the management UI then send it to here. Then to later use it to refresh page.
    void setSearchText(String searchText){
        this.searchText = searchText;
    }
    private bookManagementController bookManagementController ;
    public void setController(bookManagementController bookManagementController){
        this.bookManagementController = bookManagementController;
    }

    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `book`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "BK000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `book` WHERE `bookID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    bookIDField.setText(id);
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
                bookIDField.setText(String.valueOf(randomID));

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void addBook() {
        bookID = bookIDField.getText();
        String bookName = bookNameField.getText();
        String bookRepublish =bookRepublishField.getText();
        String bookQuantity =bookQuantityField.getText();



        con = dbConnect.getConnect();
        if (bookID.isEmpty() || bookName.isEmpty() || bookRepublish.isEmpty() || bookQuantity.isEmpty() ) {
            warning("Có thể có khung còn trống");
            return;
        } else {
            getQuery();
            insert();
            getQueryPriceHistory();
            insertPriceHistory();


            boolean isMainAuthor, isMainCategory = false;
            for(author author : authors){
                isMainAuthor = false;
                getQueryIndite();
                if(author.getAuthorID().equals(mainAuthorID)) isMainAuthor = true;
                insertIndite(author.getAuthorID(), isMainAuthor);
            }

            for(category category : categories){
                isMainCategory = false;
                getQueryHaveCategory();
                if(category.getCategoryID().equals(mainCategoryID)) isMainCategory = true;
                insertHaveCategory(category.getCategoryID(), isMainCategory);
            }
            clean();
        }
        bookManagementController.refresh(searchText); //to refresh the bookManagement every time a book is created
    }
    private void insert() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, bookIDField.getText());
            preparedStatement.setString(2, bookNameField.getText());
            preparedStatement.setInt(3, Integer.parseInt(bookRepublishField.getText()));
            preparedStatement.setString(4, bookDescriptionField.getText());
            preparedStatement.setBytes(5, imageData);
            preparedStatement.setString(6, shelveID );
            preparedStatement.setString(7, publisherID);


            preparedStatement.setInt(8, Integer.parseInt(bookQuantityField.getText()));
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(bookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void clean() {
        bookIDField.setText(null);
        bookNameField.setText(null);
        bookRepublishField.setText("1");
        bookDescriptionField.setText(null);
        bookImageField.setText(null);
        bookQuantityField.setText(null);
        bookPriceField.setText(null);
        bookCategoryField.setText(null);
        bookAuthorField.setText(null);
    }

    private void getQuery() {
        query = "INSERT INTO `book`( `bookID`, `bookName`,`bookRepublish`,`bookDescription`,`bookImage`,`shelveID`,`publisherID`,`bookQuantity`) VALUES (?,?,?,?,?,?,?,?)";
    }
    private void getQueryPriceHistory() {
        query = "INSERT INTO `pricehistory`( `bookID`, `dateOfChange`,`bookPrice`) VALUES (?,?,?)";
    }
    private void getQueryIndite() {
        query = "INSERT INTO `indite`( `bookID`, `authorID`,`isMainAuthor`) VALUES (?,?,?)";
    }
    private void getQueryHaveCategory() {
        query = "INSERT INTO `havecategory`( `bookID`, `categoryID`,`isMainCategory`) VALUES (?,?,?)";
    }
    private void insertPriceHistory() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, bookID);
            preparedStatement.setDate(2, today);
            preparedStatement.setInt(3, Integer.parseInt(bookPriceField.getText()));
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void insertIndite(String authorID, boolean isMainAuthor ) {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, bookID);
            preparedStatement.setString(2, authorID);
            preparedStatement.setBoolean(3, isMainAuthor);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void insertHaveCategory(String categoryID, boolean isMainCategory ) {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, bookID);
            preparedStatement.setString(2, categoryID);
            preparedStatement.setBoolean(3, isMainCategory);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void setAuthors(){
        try {
            // Open a new scene or perform any action you want here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bookAddAuthor.fxml"));
            Parent root = loader.load();
            bookAddAuthorController bookAddAuthorController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            bookAddAuthorController.setController(this);

            Stage stage = new Stage();
            stage.setTitle("Chọn Tác Giả");
            stage.setScene(new Scene(root));

            stage.show();
        }catch (IOException e){
            warning("Không thể xem tác giả");
        }
    }
    @FXML
    void setCategories(){
        try {
            // Open a new scene or perform any action you want here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bookAddCategory.fxml"));
            Parent root = loader.load();
            bookAddCategoryController bookAddCategoryController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            bookAddCategoryController.setController(this);
            Stage stage = new Stage();
            stage.setTitle("Chọn Thể Loại");
            stage.setScene(new Scene(root));

            stage.show();
        }catch (IOException e){
            warning("Không thể xem thể thể loại");
        }
    }
    StringBuilder inditesStringBuilder(){
        con = dbConnect.getConnect();
        StringBuilder builder = new StringBuilder();
        for(author author :authors){
            try {
                query = "SELECT  * from author WHERE authorID = '" + author.getAuthorID() + "'";
                preparedStatement = con.prepareStatement(query);
                resultSet = preparedStatement.executeQuery(query);
                resultSet.next();
                builder.append(resultSet.getString("authorName")).append(". ");
            }catch (SQLException e){
                Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return builder;
    }
    StringBuilder categoriesStringBuilder(){
        StringBuilder builder = new StringBuilder();
        con = dbConnect.getConnect();
        for(category category : categories){
            try {
                query = "SELECT  * from category WHERE categoryID = '" + category.getCategoryID() + "'";
                preparedStatement = con.prepareStatement(query);
                resultSet = preparedStatement.executeQuery(query);
                resultSet.next();
                builder.append(resultSet.getString("categoryName")).append(". ");
            }catch (SQLException e){
                Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return builder;
    }
    void getAuthors(String mainAuthorID,ObservableList<String> selectedAuthors ){
        this.authors.clear();
        this.mainAuthorID = mainAuthorID;
        con = dbConnect.getConnect();
        for(String authorIDstring : selectedAuthors){
            try {
                query = "SELECT * FROM `author` WHERE authorID = '" + authorIDstring + "'";
                preparedStatement = con.prepareStatement(query);
                resultSet = preparedStatement.executeQuery(query);
                resultSet.next();
                this.authors.add(new author(
                        resultSet.getString("authorID"),
                        resultSet.getString("authorName"),
                        resultSet.getString("authorDescription")));
            }catch (SQLException e){
                Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        String authorsString = String.valueOf(inditesStringBuilder());
        bookAuthorField.setText(authorsString);

    }
    void getCategories(String mainCategoryID,ObservableList<String> selectedCategories ){
        this.categories.clear();
        this.mainCategoryID = mainCategoryID;

        con = dbConnect.getConnect();
        for(String categoryIDstring : selectedCategories){
            try {
                query = "SELECT * FROM `category` WHERE `categoryID` = '" + categoryIDstring + "'";
                preparedStatement = con.prepareStatement(query);
                resultSet = preparedStatement.executeQuery(query);
                resultSet.next();
                this.categories.add(new category(
                        resultSet.getString("categoryID"),
                        resultSet.getString("categoryName")));
            }catch (SQLException e){
                Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        String categoriesString = String.valueOf(categoriesStringBuilder());
        bookCategoryField.setText(categoriesString);
        warning(categoriesString);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getAllID(); // get all shelve/publisher ID
        //pass all data from getAllID to list box
        setUpChoicebox();
        //choose the fist option as default
        shelveIDBox.setOnAction(this::getIDShelve);
        publisherIDBox.setOnAction(this::getIDpublisher);
    }
}
