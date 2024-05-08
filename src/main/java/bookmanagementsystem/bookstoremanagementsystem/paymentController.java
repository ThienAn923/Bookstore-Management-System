package bookmanagementsystem.bookstoremanagementsystem;

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

public class paymentController implements Initializable {

    @FXML
    private TextField customerIDField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerPhoneNumberField;
    @FXML
    private TextField customerPointField;
    @FXML
    private TextField customerGenderField;

    @FXML
    private TextField bookIDField;
    @FXML
    private TextField bookNameField;
    @FXML
    private TextField bookRepublishField;
    @FXML
    private TextField bookPublisherField;
    @FXML
    private TextField numberField;

    @FXML
    private TextField invoiceIDField;
    @FXML
    private TextField taxNumberField;
    @FXML
    private TextField totalMoneyField;
    @FXML
    private TextField moneyreceiveField;
    @FXML
    private TextField moneyReturnField;
    @FXML
    private DatePicker dateOfCreateField;

    @FXML
    private TextField promotionIDField;
    @FXML
    private TextField promotionNameField;
    @FXML
    private TextField promotionPercentageField;

    @FXML private Button pickCustomerButton;
    @FXML private Button pickBookButton;

    Map<String, String> positionsMap = new HashMap<>();
    List<String> positionsNameAndIDList = new ArrayList<>();
    Map<String, String> departmentsMap = new HashMap<>();
    List<String> departmentsNameAndIDList = new ArrayList<>();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    String invoiceID = "", customerID = "",customerName ="",customerPhoneNumber = "",bookID = "",bookName = "",bookPublisher = "", promotionID = "", promotionName = "";
    String taxNumber = "123456789";
    int customerPoint, bookRepublish, bookNumber, moneyreceive, moneyReturn, totalMoney, point;
    boolean customerGender;
    Date today = Date.valueOf(LocalDate.now());
    Float promotionPercentage;

    String staffID = "STF0001"; // detele later

    mainController mainController;
    public void setController(mainController mainController) {
        this.mainController = mainController;
    }

    void setStaffID(String staffID){
        this.staffID = staffID;
    }

    void warning(String warning){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(warning);
        alert.showAndWait();
    }



    @FXML
    private void autoIDGen(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT COUNT(*) FROM `invoice`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int count = resultSet.getInt(1);

            String id = null;
            boolean foundUnusedID = false;
            for(int i = 0; i < 100; i++){
                count++;
                id = "IV000" + count;
                // Check if the ID is already in use
                query = "SELECT * FROM `invoice` WHERE `invoiceID` = ?";
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    foundUnusedID = true;
                    invoiceIDField.setText(id);
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
                invoiceIDField.setText(String.valueOf(randomID));
                invoiceID = invoiceIDField.getText();

                con.close();
            }
        }catch(SQLException e){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void executePayment() {

        customerID = customerIDField.getText();
        bookID = bookIDField.getText();
        bookNumber = Integer.parseInt(numberField.getText());
        totalMoney = Integer.parseInt(totalMoneyField.getText());
        moneyreceive = Integer.parseInt(moneyReturnField.getText());

        moneyReturn = moneyreceive - totalMoney;
        point = totalMoney/10000;

        con = dbConnect.getConnect();
        if (customerID.isEmpty() || bookID.isEmpty() ) {
            warning("Có thể có khung còn trống hoặc tiền không đủ, không chính xác");
            return;
        } else {

            getQuery();
            insert();
            getQueryInvoiceDetail();
            insertInvoiceDetail();
            if(!promotionID.isEmpty()) {
                getQueryPromotionToInvoice();
                insertPromotionToInvoice();
            }
            subtractNumberOfBook();
            clean();
        }
    }
    private void insert() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, invoiceIDField.getText());
            preparedStatement.setDate(2, Date.valueOf(dateOfCreateField.getValue()));
            preparedStatement.setInt(3, Integer.parseInt(totalMoneyField.getText()));
            preparedStatement.setString(4, taxNumber);
            preparedStatement.setInt(5, point);
            preparedStatement.setInt(6, Integer.parseInt(moneyreceiveField.getText()));
            preparedStatement.setInt(7, Integer.parseInt(moneyReturnField.getText()));
            preparedStatement.setString(8, staffID);
            preparedStatement.setString(9, customerID);


            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void clean() {
        customerIDField.setText(null);
        customerNameField.setText(null);
        customerGenderField.setText(null);
        customerPointField.setText(null);
        customerPhoneNumberField.setText(null);

        bookIDField.setText(null);
        bookNameField.setText(null);
        bookRepublishField.setText(null);
        bookPublisherField.setText(null);
        numberField.setText(null);

        invoiceIDField.setText(null);
        taxNumberField.setText(taxNumber);
        moneyreceiveField.setText(null);
        moneyReturnField.setText(null);
        totalMoneyField.setText(null);
        dateOfCreateField.setValue(today.toLocalDate());

        if(promotionID.isEmpty()) {
            promotionIDField.setText("Không");
            promotionNameField.setText("Không");
            promotionPercentageField.setText("Không");
        }

    }

    private void getQuery() {
        query = "INSERT INTO `invoice`( `invoiceID`, `dateOfCreate`,`totalMoney`,`taxNumber`,`point`,`moneyReceive`,`moneyPayBack`,`staffID`,`customerID`) VALUES (?,?,?,?,?,?,?,?,?)";
    }
    private void getQueryInvoiceDetail() {
        query = "INSERT INTO `invoicedetail`( `invoiceID`, `bookID`, `number`) VALUES (?,?,?)";
    }
    private void getQueryPromotionToInvoice() {
        query = "INSERT INTO `promotiontoinvoice`( `invoiceID`, `promotionID`) VALUES (?,?)";
    }
    private void insertInvoiceDetail() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, invoiceIDField.getText());
            preparedStatement.setString(2, bookID);
            preparedStatement.setInt(3, Integer.parseInt(numberField.getText()));
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void insertPromotionToInvoice() {
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, invoiceID);
            preparedStatement.setString(2, promotionID);
            preparedStatement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void pickCustomer(){
        try {
//            getCustomerInfo();
            // Open a new scene or perform any action you want here
            FXMLLoader customerManagement = new FXMLLoader(getClass().getResource("customerManagement.fxml"));
            Parent root = customerManagement.load();
            root.getStylesheets().add(getClass().getResource("css/staffDetail.css").toExternalForm());
            // Pass a reference to the Scene A controller to Scene B
            customerManagementController customerManagementController = customerManagement.getController();
            customerManagementController.setController(this);

            Stage stage = new Stage();
            stage.setTitle("Chọn Khách Hàng");
            stage.setScene(new Scene(root));

            stage.show();
        }catch (IOException e){
            warning("Không thể chọn Khách hàng");
        }
//        setCustomer(customerID,customerName,customerPhoneNumber,customerPoint,customerGender);
    }

    void setCustomer(String customerID, String customerName, String customerPhoneNumber, int customerPoint, boolean customerGender){
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerGender = customerGender;
        this.customerPoint = customerPoint;
        this.customerPhoneNumber = customerPhoneNumber;

        customerIDField.setText(customerID);
        customerNameField.setText(customerName);
        customerPhoneNumberField.setText(customerPhoneNumber);
        customerPointField.setText(String.valueOf(customerPoint));
        if(customerGender) customerGenderField.setText("Nam");
        else customerGenderField.setText("Nữ");

    }

    @FXML
    void pickBook(){
        try {
            FXMLLoader bookManagement = new FXMLLoader(getClass().getResource("bookManagement.fxml"));
            Parent root = bookManagement.load();
            root.getStylesheets().add(getClass().getResource("css/staffDetail.css").toExternalForm());
            // Pass a reference to the Scene A controller to Scene B
            bookManagementController bookManagementController = bookManagement.getController();
            bookManagementController.setController(this);

            Stage stage = new Stage();
            stage.setTitle("Chọn Sách");
            stage.setScene(new Scene(root));

            stage.show();
        }catch (IOException e){
            warning("Không thể chọn sách");
        }

    }
    void setBook(String bookID, String bookName, int bookRepublish, String bookPublisher){
        this.bookID = bookID;
        this.bookName = bookName;
        this.bookRepublish = bookRepublish;
        this.bookPublisher = bookPublisher;

        bookIDField.setText(bookID);
        bookNameField.setText(bookName);
        bookRepublishField.setText(String.valueOf(bookRepublish));
        bookPublisherField.setText(bookPublisher);
    }


//    void getPromotion(){
//        try {
//            query = "SELECT * FROM promotion " +
//                    "WHERE ? < promotionEndDay " +
//                    "AND ? > promotionStartDay " +
//                    "ORDER BY promotionPercentage DESC LIMIT 1";
//            preparedStatement = con.prepareStatement(query);
//            preparedStatement.setDate(1, today);
//            preparedStatement.setDate(2, today);
//            resultSet = preparedStatement.executeQuery(query);
//            if(resultSet.next()){
//                this.promotionPercentage = resultSet.getFloat("promotionPercentage");
//                this.promotionName = resultSet.getString("promotionName");
//                this.promotionID = resultSet.getString("promotionID");
//            }
//
//        }catch (SQLException e){
//            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, e);
//        }
//    }
    void getPromotion() {
        try {
            query = "SELECT * FROM promotion " +
                    "WHERE ? < promotionEndDay " +
                    "AND ? > promotionStartDay " +
                    "ORDER BY promotionPercentage DESC LIMIT 1";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setDate(1, today);
            preparedStatement.setDate(2, today);
            resultSet = preparedStatement.executeQuery(); // Remove 'query' from executeQuery

            if (resultSet.next()) {
                this.promotionPercentage = resultSet.getFloat("promotionPercentage");
                this.promotionName = resultSet.getString("promotionName");
                this.promotionID = resultSet.getString("promotionID");
            }

        } catch (SQLException e) {
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    void subtractNumberOfBook(){
        try {
            con = dbConnect.getConnect();
            query = "UPDATE book SET bookQuantity = bookQuantity - ? WHERE bookID = '" + bookID + "'";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(numberField.getText()));
            preparedStatement.executeUpdate();
            con.close();
        }catch (SQLException ex){
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    void calculateTotalMoney(){
        try {
            con = dbConnect.getConnect();
            query = "SELECT * from pricehistory WHERE bookID = '" + bookID + "' ORDER BY dateOfChange DESC LIMIT 1";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            resultSet.next();
            totalMoney = Integer.valueOf(numberField.getText()) * resultSet.getInt("bookPrice");
            totalMoneyField.setText(String.valueOf(totalMoney));
        }catch (SQLException ex){
            Logger.getLogger(staffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    void calculateMoneyReturn(){
        moneyReturn = Integer.valueOf(moneyreceiveField.getText()) - Integer.valueOf(totalMoneyField.getText());
        moneyReturnField.setText(String.valueOf(moneyReturn));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getPromotion();
        if(promotionID.isEmpty()){
            promotionIDField.setText("Không");
            promotionNameField.setText("Không");
            promotionPercentageField.setText("Không");
        }
        taxNumberField.setText(taxNumber);
        autoIDGen();
    }
}
