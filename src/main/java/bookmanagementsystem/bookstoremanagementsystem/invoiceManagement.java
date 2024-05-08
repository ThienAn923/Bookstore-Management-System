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
import javafx.scene.input.MouseButton;
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

public class invoiceManagement implements Initializable {
    @FXML
    private Button refreshButton;
    @FXML
    private Label testLabel;
    @FXML
    private TextField findBox;
    @FXML
    private VBox invoiceContainer;
    @FXML
    private ScrollPane invoicesScrollPane;

    ObservableList<invoice> invoices = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    invoice invoice = null ;
    String searchText = "";

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findInvoice(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getInvoice();
    }


    private void refreshData() {
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT * FROM `invoice`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                invoices.add(new invoice(
                        resultSet.getString("invoiceID"),
                        resultSet.getDate("dateOfCreate"),
                        resultSet.getInt("totalMoney"),
                        resultSet.getString("taxNumber"),
                        resultSet.getInt("point"),
                        resultSet.getInt("moneyReceive"),
                        resultSet.getInt("moneyPayBack"),
                        resultSet.getString("staffID"),
                        resultSet.getString("customerID")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            invoices.clear();
            con = dbConnect.getConnect();
            //search by name, id or phoneNumber
            query = "SELECT * FROM `invoice` WHERE `invoiceID` LIKE ? OR `customerID` LIKE ? or `bookID` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            preparedStatement.setString(3,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                invoices.add(new invoice(
                        resultSet.getString("invoiceID"),
                        resultSet.getDate("dateOfCreate"),
                        resultSet.getInt("totalMoney"),
                        resultSet.getString("taxNumber"),
                        resultSet.getInt("point"),
                        resultSet.getInt("moneyReceive"),
                        resultSet.getInt("moneyPayBack"),
                        resultSet.getString("staffID "),
                        resultSet.getString("customerID ")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = invoiceContainer.getChildren().size();
        if (numChildren > 1) {
            invoiceContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        invoices.clear(); //clear the list invoices
        refreshData();
        getInvoice();
    }
    public void refresh(String searchText){
        clearTable();
        invoices.clear(); //clear the list invoices
        refreshData(searchText);
        getInvoice();
    }
    private void getInvoice() {
        con = dbConnect.getConnect();

        for (invoice invoice : invoices) {
            HBox invoiceBox = new HBox(); //Create container to hold the invoices
            invoiceBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            invoiceBox.setPrefHeight(36);
            invoiceBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label invoiceIdLabel = new Label(invoice.getInvoiceID());
            Label dateOfCreateLabel = new Label(String.valueOf(invoice.getDateOfCreate()));
            Label totalMoney = new Label(String.valueOf(invoice.totalMoney));
            Label moneyReceive = new Label(String.valueOf(invoice.getMoneyReceive()));
            Label moneyReturn = new Label(String.valueOf(invoice.getMoneyPayBack()));
            Label customerID = new Label(invoice.getInvoiceID());


            invoiceIdLabel.setMinWidth(180);
            dateOfCreateLabel.setMinWidth(180);
            totalMoney.setMinWidth(130);
            moneyReceive.setMinWidth(130);
            moneyReturn.setMinWidth(130);
            customerID.setMinWidth(130);

            invoiceBox.setMargin(invoiceIdLabel, new Insets(0,0,0,30));

            invoiceBox.getChildren().addAll(invoiceIdLabel, dateOfCreateLabel, totalMoney, moneyReceive, moneyReturn,customerID);

            // Add the HBox for each customer to your layout
            invoiceContainer.getChildren().add(invoiceBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getInvoice();
        //hide the scroll bar of the scroll pane
        invoicesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        invoicesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
