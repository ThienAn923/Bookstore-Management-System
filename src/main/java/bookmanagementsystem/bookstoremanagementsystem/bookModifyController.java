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
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class bookModifyController implements Initializable {
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
    private Button bookModifyButton;
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

    String bookID, deleteBookID = "";
    int bookPrice, deletebookPrice;
    int bookRepublish;
    String bookName;
    String bookDescription;
    int bookQuantity;
    Date dateOfChange;
    Date today = Date.valueOf(LocalDate.now());

    String searchText = "";
    String imagePath = "";
    byte[] imageData;


    String mainAuthorID, mainCategoryID;
    ObservableList<author> authors = FXCollections.observableArrayList();
    ObservableList<category> categories = FXCollections.observableArrayList();
    ObservableList<indite> indites = FXCollections.observableArrayList();
    ObservableList<haveCategory> haveCategories = FXCollections.observableArrayList();

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
                shelveID = resultSet.getString("shelveID");
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
    private void modifyBook() {
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
            DeleteIndites();
            DeleteHaveCategory();
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
            if(!imagePath.isEmpty()) preparedStatement.setBytes(8, imageData);

            preparedStatement.setString(5, shelveID );
            preparedStatement.setString(6, publisherID);
            preparedStatement.setInt(7, Integer.parseInt(bookQuantityField.getText()));
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(bookModifyController.class.getName()).log(Level.SEVERE, null, ex);
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

//    private void getQuery() {
//        query = "INSERT INTO `book`( `bookID`, `bookName`,`bookRepublish`,`bookDescription`,`bookImage`,`shelveID`,`publisherID`,`bookQuantity`) VALUES (?,?,?,?,?,?,?,?)";
//    }
    private void getQuery() {
        query = "UPDATE `book` SET "
                + "`bookID`=?,"
                + "`bookName`= ?,"
                + "`bookRepublish`=?,"
                + "`bookDescription`= ?,"
                + "`shelveID`=?,"
                + "`publisherID`= ?,"
                + "`bookQuantity`=? ";
        //if imagePath != null or empty then send it to database
        if(!imagePath.isEmpty()) query += "`bookImage` = ?";
        query += " WHERE bookID = '"+bookID+"'";

    }
    private void getQueryPriceHistory() {
        if(String.valueOf(today).equals(String.valueOf(dateOfChange)))
            query = "INSERT INTO `pricehistory`( `bookID`, `dateOfChange`,`bookPrice`) VALUES (?,?,?)";
        else
            query = "UPDATE INTO `pricehistory` SET ( `bookPrice`) VALUES (?) WHERE bookID = `" + bookID + "` AND dateOfChange = `" + today + "`";

    }

    private void getQueryIndite() {
        query = "INSERT INTO `indite`( `bookID`, `authorID`,`isMainAuthor`) VALUES (?,?,?)";
    }
    private void getQueryHaveCategory() {
        query = "INSERT INTO `havecategory`( `bookID`, `categoryID`,`isMainCategory`) VALUES (?,?,?)";
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

    private void DeleteHaveCategory() {
        try {
            query = "DELETE FROM `havecategory` WHERE bookID = '" + bookID +"'";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.executeUpdate(query);
        }catch (SQLException e){
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    private void DeleteIndites() {
        try {
            query = "DELETE FROM `indite` WHERE bookID = '" + bookID +"'";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.execute(query);
        }catch (SQLException e){
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, e);
        }
    }



    private void insertPriceHistory() {
        try {
            preparedStatement = con.prepareStatement(query);
            if(String.valueOf(today).equals(String.valueOf(dateOfChange))) {
                warning(String.valueOf(dateOfChange));
                preparedStatement.setString(1, bookID);
                preparedStatement.setDate(2, today);
                preparedStatement.setInt(3, Integer.parseInt(bookPriceField.getText()));
            }
            else {
                preparedStatement.setInt(1, Integer.parseInt(bookPriceField.getText()));
            }
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void setValue(String id){
        bookID = id;
        try {
            con = dbConnect.getConnect();
            query = "SELECT * FROM book WHERE bookID = '" + bookID + "'";

            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            resultSet.next();
            bookID = resultSet.getString("bookID");
            bookName = resultSet.getString("bookName");
            bookRepublish = resultSet.getInt("bookRepublish");
            bookDescription = resultSet.getString("bookDescription");
            shelveID  = resultSet.getString("shelveID");
            publisherID  = resultSet.getString("publisherID");
            bookQuantity = resultSet.getInt("bookQuantity");

            deleteBookID = bookID;

            bookIDField.setText(bookID);
            bookNameField.setText(bookName);
            bookRepublishField.setText(String.valueOf(bookRepublish));
            bookDescriptionField.setText(bookDescription);
            bookQuantityField.setText(String.valueOf(bookQuantity));

            shelveID = resultSet.getString("shelveID");
            publisherID = resultSet.getString("publisherID");

            //get id and name from position and department class
            query = "SELECT  * from shelve WHERE shelveID = '" + shelveID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();
            String shelveName = resultSet.getString("shelveName");

            query = "SELECT  * from publisher WHERE publisherID = '" + publisherID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();
            String publisherName = resultSet.getString("publisherName");

            //Set value for ID box
            shelveIDBox.setValue(shelveID + " - " + shelveName);
            publisherIDBox.setValue(publisherID + " - " + publisherName);

            query = "SELECT  * from haveCategory WHERE bookID = '" + bookID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            while(resultSet.next()){
                haveCategories.add(new haveCategory(
                        resultSet.getString("bookID"),
                        resultSet.getString("categoryID"),
                        resultSet.getBoolean("isMainCategory")));

            }


            query = "SELECT  * from indite WHERE bookID = '" + bookID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            while(resultSet.next()){
                indites.add(new indite(
                        resultSet.getString("authorID"),
                        resultSet.getString("bookID"),
                        resultSet.getBoolean("isMainAuthor")));
            }

            getAllAuthorRelated();
            getAllCategoryRelated();

            String indiesString = String.valueOf(inditesStringBuilder());
            bookAuthorField.setText(indiesString);
            String categoriesString = String.valueOf(categoriesStringBuilder());
            bookCategoryField.setText(categoriesString);

            query = "SELECT * from pricehistory WHERE bookID = '" + bookID + "' ORDER BY dateOfChange DESC LIMIT 1";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();
            bookPriceField.setText(resultSet.getString("bookPrice"));
            dateOfChange = resultSet.getDate("dateOfChange");
        }catch(SQLException e){
            Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
        }

    }
    void getAllAuthorRelated(){
        for(indite indite :indites){
            try {
                query = "SELECT  * from author WHERE authorID = '" + indite.getAuthorID() + "'";
                preparedStatement = con.prepareStatement(query);
                resultSet = preparedStatement.executeQuery(query);
                resultSet.next();
                authors.add(new author(
                        resultSet.getString("authorID"),
                        resultSet.getString("authorName"),
                        resultSet.getString("authorDescription")));
            }catch (SQLException e){
                Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    void getAllCategoryRelated(){
        for(haveCategory haveCategory : haveCategories){
            try {
                query = "SELECT  * from category WHERE categoryID = '" + haveCategory.getCategoryID() + "'";
                preparedStatement = con.prepareStatement(query);
                resultSet = preparedStatement.executeQuery(query);
                resultSet.next();
                categories.add(new category(
                        resultSet.getString("categoryID"),
                        resultSet.getString("categoryName")));
            }catch (SQLException e){
                Logger.getLogger(staffModifyController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    StringBuilder inditesStringBuilder(){

        con = dbConnect.getConnect();
        StringBuilder builder = new StringBuilder();
        for(indite indite : indites){
            try {
                query = "SELECT  * from author WHERE authorID = '" + indite.getAuthorID() + "'";
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
        con = dbConnect.getConnect();
        StringBuilder builder = new StringBuilder();
        for(haveCategory haveCategory : haveCategories){
            try {
                query = "SELECT  * from category WHERE categoryID = '" + haveCategory.getCategoryID() + "'";
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

    @FXML
    void setAuthors(){
        try {
            // Open a new scene or perform any action you want here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bookModifyAuthor.fxml"));
            Parent root = loader.load();
            bookModifyAuthorController bookModifyAuthorController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            bookModifyAuthorController.setController(this);
            bookModifyAuthorController.setValue(bookID);

            Stage stage = new Stage();
            stage.setTitle("Chọn Tác Giả");
            stage.setScene(new Scene(root));

            stage.show();
        }catch (IOException e){
            warning("Không thể xem chi tiết nhân viên");
        }
    }
    @FXML
    void setCategories(){
        try {
            // Open a new scene or perform any action you want here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bookModifyCategory.fxml"));
            Parent root = loader.load();
            bookModifyCategoryController bookModifyCategoryController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            bookModifyCategoryController.setController(this);
            bookModifyCategoryController.setValue(bookID);
            Stage stage = new Stage();
            stage.setTitle("Chọn Thể Loại");
            stage.setScene(new Scene(root));

            stage.show();
        }catch (IOException e){
            warning("Không thể xem thể thể loại");
        }
    }

    void getAuthors(String mainAuthorID,ObservableList<String> selectedAuthors ){

        this.authors.clear();
        this.mainAuthorID = mainAuthorID;
        con = dbConnect.getConnect();
        for(String authorIDstring : selectedAuthors){
            try {
                query = "SELECT * FROM `category` WHERE categoryID = `" + authorIDstring + "`";
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
        warning(authorsString);
        bookAuthorField.setText(authorsString);

    }
    void getCategories(String mainCategoryID,ObservableList<String> selectedCategories ){

        this.categories.clear();
        this.mainCategoryID = mainCategoryID;

        con = dbConnect.getConnect();
        for(String categoryIDstring : selectedCategories){
            try {
                query = "SELECT * FROM `category` WHERE categoryID = `" + categoryIDstring + "`";
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
