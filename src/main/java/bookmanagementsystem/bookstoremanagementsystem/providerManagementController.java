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

public class providerManagementController implements Initializable {
    @FXML
    private Button providerAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField findBox;
    @FXML
    private VBox providerContainer;
    @FXML
    private ScrollPane providersScrollPane;

    ObservableList<provider> providers = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    provider provider = null ;
    String searchText = "";

    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findProvider(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getProvider();
    }

    @FXML
    private void addProvider(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("providerAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for providerAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            providerAddController providerAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            providerAddController.setController(this);
            providerAddController.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Thêm Nhà Cung Cấp");
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
            query = "SELECT * FROM `provider`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                providers.add(new provider(
                        resultSet.getString("providerID"),
                        resultSet.getString("providerName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            providers.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `provider` WHERE `providerID` LIKE ? OR `providerName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                providers.add(new provider(
                        resultSet.getString("providerID"),
                        resultSet.getString("providerName")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = providerContainer.getChildren().size();
        if (numChildren > 1) {
            providerContainer.getChildren().remove(1, numChildren);
        }
    }

    public void refresh(){
        clearTable();
        providers.clear(); //clear the list areas
        refreshData();
        getProvider();
    }
    public void refresh(String searchText){
        clearTable();
        providers.clear(); //clear the list areas
        refreshData(searchText);
        getProvider();
    }

    private void getProvider() {
        con = dbConnect.getConnect();

        for (provider provider : providers) {
            HBox providerBox = new HBox(); //Create container to hold the providers
            providerBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            providerBox.setPrefHeight(36);
            providerBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label providerIdLabel = new Label(provider.getProviderID());
            Label providerNameLabel = new Label(provider.getProviderName());

            providerIdLabel.setMinWidth(370);
            providerNameLabel.setMinWidth(510);
            providerBox.setMargin(providerIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `provider` WHERE providerID = '" + provider.getProviderID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(providerManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa nhà cung cấp");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("providerModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for providerAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    providerModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(provider.getProviderID(), provider.getProviderName());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Modify Provider");
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

            providerBox.getChildren().addAll(providerIdLabel, providerNameLabel, buttonBox);
            // Add the HBox for each floor to your layout
            providerContainer.getChildren().add(providerBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getProvider();
        //hide the scroll bar of the scroll pane
        providersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        providersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
