package bookmanagementsystem.bookstoremanagementsystem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class customerManagementController implements Initializable {
    @FXML
    private Button customerAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label testLabel;
    @FXML
    private TextField findBox;
    @FXML
    private VBox customerContainer;
    @FXML
    private ScrollPane customersScrollPane;

    ObservableList<customer> customers = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    customer customer = null ;
    String searchText = null;

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findCustomer(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getCustomer();
    }

    @FXML
    private void addCustomer(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("customerAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for customerAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            customerAddController customerAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            customerAddController.setController(this);
            customerAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm tác giả");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshData() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT * FROM `customer`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                customers.add(new customer(
                        resultSet.getString("customerID"),
                        resultSet.getString("customerName"),
                        resultSet.getString("customerPhoneNumber"),
                        resultSet.getInt("customerPoint"),
                        resultSet.getBoolean("customerGender")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            customers.clear();
            con = dbConnect.getConnect();
            //search by name, id or phoneNumber
            query = "SELECT * FROM `customer` WHERE `customerID` LIKE ? OR `customerName` LIKE ? or `customerPhoneNumber` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            preparedStatement.setString(3,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                customers.add(new customer(
                        resultSet.getString("customerID"),
                        resultSet.getString("customerName"),
                        resultSet.getString("customerPhoneNumber"),
                        resultSet.getInt("customerPoint"),
                        resultSet.getBoolean("customerGender")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = customerContainer.getChildren().size();
        if (numChildren > 1) {
            customerContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        customers.clear(); //clear the list customers
        refreshData();
        getCustomer();
    }
    public void refresh(String searchText){
        clearTable();
        customers.clear(); //clear the list customers
        refreshData(searchText);
        getCustomer();
    }
    private void getCustomer() {
        con = dbConnect.getConnect();

        for (customer customer : customers) {
            HBox customerBox = new HBox(); //Create container to hold the customers
            customerBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            customerBox.setPrefHeight(36);
            customerBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label customerIdLabel = new Label(customer.getCustomerID());
            Label customerNameLabel = new Label(customer.getCustomerName());
            Label customerPhoneNumberLabel = new Label(customer.getCustomerPhoneNumber());
            Label customerPointLabel = new Label(String.valueOf(customer.getCustomerPoint()));
            Label customerGenderLabel;
            if (customer.isCustomerGender()) //If customer gender = true then he's a Male because women always false (no no, im kidding, don't cancel me, i take that back)
                customerGenderLabel = new Label("Nam");
            else customerGenderLabel = new Label("Nữ");

            customerIdLabel.setMinWidth(185);
            customerNameLabel.setMinWidth(185);
            customerPhoneNumberLabel.setMinWidth(210);
            customerPointLabel.setMinWidth(130);
            customerGenderLabel.setMinWidth(170);

            customerBox.setMargin(customerIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `customer` WHERE customerID = '" + customer.getCustomerID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(customerManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa khách hàng, sao lại đi xóa khách hàng?");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("customerModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for customerAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    customerModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(customer.getCustomerID(), customer.getCustomerName(), customer.getCustomerPhoneNumber(), customer.getCustomerPoint(), customer.isCustomerGender());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Chỉnh sửa thông tin khách hàng");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    warning("Không thể mở edit");
                }
            });

            HBox buttonBox = new HBox(editIcon, deleteIcon);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setSpacing(10);

            customerBox.getChildren().addAll(customerIdLabel, customerNameLabel, customerPhoneNumberLabel, customerGenderLabel, customerPointLabel, buttonBox);

            // Add the HBox for each customer to your layout
            customerContainer.getChildren().add(customerBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getCustomer();
        //hide the scroll bar of the scroll pane
        customersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        customersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
