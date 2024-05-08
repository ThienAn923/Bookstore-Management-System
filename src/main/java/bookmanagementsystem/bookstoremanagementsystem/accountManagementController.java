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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class accountManagementController implements Initializable {
    @FXML
    private Button addAccountButton;
    @FXML
    private Button refreshButton;
    @FXML
    private VBox accountsContainer;
    @FXML
    private ScrollPane accountsScrollPane;
    @FXML
    private TextField findBox;

    ObservableList<account> accounts = FXCollections.observableArrayList();
    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    account account = null ;
    String searchText = "";
    Map<String, String> staffsMap = new HashMap<>();

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findAccount(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getAccount();
    }

    void getAllStaff(){
        try {
            //Database stuff
            con = dbConnect.getConnect();
            query = "SELECT staffID, staffName FROM `staff`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                String staffID = resultSet.getString("staffID");
                String staffName =resultSet.getString("staffName");

                //add to map table and staff list
                staffsMap.put(staffID, staffName);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String getStaffName(String staffID){
        String staffName = staffsMap.get(staffID);
        return staffName;
    }

    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain accounts infomation)
        int numChildren = accountsContainer.getChildren().size();
        if (numChildren > 1) {
            accountsContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        accounts.clear(); //clear the list accounts
        refreshData();
        getAccount();
    }
    public void refresh(String searchText){
        clearTable();
        accounts.clear(); //clear the list accounts
        refreshData(searchText);
        getAccount();
    }

    @FXML
    private void addAccount(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("accountAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for accountAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            accountAddController accountAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            accountAddController.setController(this);
            accountAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm Tài Khoản");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshData() {
        try {
            accounts.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `account`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                accounts.add(new account(
                        resultSet.getString("accountID"),
                        resultSet.getString("accountUsername"),
                        resultSet.getString("accountPassword"),
                        resultSet.getBoolean("accountAuthorized"),
                        resultSet.getBoolean("accountHidden"),
                        resultSet.getString("staffID")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            accounts.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `account` WHERE `accountID` LIKE ? OR `accountUsername` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                accounts.add(new account(
                        resultSet.getString("accountID"),
                        resultSet.getString("accountUsername"),
                        resultSet.getString("accountPassword"),
                        resultSet.getBoolean("accountAuthorized"),
                        resultSet.getBoolean("accountHidden"),
                        resultSet.getString("staffID")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private void getAccount() {
        con = dbConnect.getConnect();

        for (account account : accounts) {
            if(!account.isAccountHidden()) {
                HBox accountBox = new HBox(); //Create container to hold the accounts
                accountBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                        "-fx-border-width: 0 1px 1px 1px; " +
                        "-fx-border-style: solid;");

                accountBox.setPrefHeight(36);
                accountBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

                Label accountIdLabel = new Label(account.getAccountID());
                Label accountNameLabel = new Label(account.getAccountUsername());
                Label staffName = new Label(getStaffName(account.getStaffID()));
                Label accountUsernameLabel = new Label(account.getAccountUsername());

                accountIdLabel.setMinWidth(185);
                staffName.setMinWidth(255);
                accountUsernameLabel.setMinWidth(255);
                accountNameLabel.setMinWidth(185);
                CheckBox checkBox = new CheckBox("");
                checkBox.setMinWidth(185);
                if(account.isAccountAuthorized()) checkBox.setSelected(true);
                checkBox.setDisable(true);


                accountBox.setMargin(accountIdLabel, new Insets(0, 0, 0, 30));

                FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);


                deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");


                deleteIcon.setOnMouseClicked(event -> {
                    try {
//                        query = "UPDATE INTO `account` WHERE accountID = '" + account.getAccountID() + "'";
                        query = "UPDATE `account` SET accountHidden = true WHERE accountID = '" + account.getAccountID() + "'";
                        con = dbConnect.getConnect();
                        preparedStatement = con.prepareStatement(query);
                        preparedStatement.execute();
                        refreshData();
                        refresh(); //refresh the table after delete
                    } catch (SQLException ex) {
                        Logger.getLogger(accountManagementController.class.getName()).log(Level.SEVERE, null, ex);
                        warning("Không thể xóa tài khoản.");
                    }
                });

                HBox buttonBox = new HBox(deleteIcon);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);
//            buttonBox.setStyle("-fx-alignment:center");
                buttonBox.setSpacing(10);

                accountBox.getChildren().addAll(accountIdLabel, staffName, accountUsernameLabel, checkBox, buttonBox);

                // Add the HBox for each account to your layout
                accountsContainer.getChildren().add(accountBox);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getAllStaff(); //get all the Staff and put to HashMap so i don't have to search for StaffName in database every time
        refreshData(); //get accounts data
        getAccount(); //Create accounts hbox

        //Hide the scroll bars
        accountsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        accountsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
