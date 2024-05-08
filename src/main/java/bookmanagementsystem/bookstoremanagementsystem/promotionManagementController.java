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
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class promotionManagementController implements Initializable {
    @FXML
    private Button PromotionAddButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label testLabel;
    @FXML
    private TextField findBox;
    @FXML
    private VBox promotionContainer;
    @FXML
    private ScrollPane promotionsScrollPane;

    ObservableList<promotion> promotions = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    promotion promotion = null ;
    String searchText = "";
    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void findPromotion(){
        searchText = findBox.getText();
        refreshData(searchText);
        clearTable();
        getPromotion();
    }

    @FXML
    private void addPromotion(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("promotionAdd.fxml"));
            Parent root = (Parent) loader.load();

            //This part of code is to set the controller for promotionAddcontroller as this controller
            //so it can call the refresh function from this controller
            //this, is black magic, if i was at 17th centuary, i would be burned alive
            promotionAddController promotionAddController = loader.getController();
            // Pass a reference to the Scene A controller to Scene B
            promotionAddController.setController(this);
            promotionAddController.setSearchText(searchText);

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
            query = "SELECT * FROM `promotion`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                promotions.add(new promotion(
                        resultSet.getString("promotionID"),
                        resultSet.getString("promotionName"),
                        resultSet.getString("promotionDescription"),
                        resultSet.getDate("promotionStartDay"),
                        resultSet.getDate("promotionEndDay"),
                        resultSet.getFloat("promotionPercentage")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshData(String searchText) {
        try {
            promotions.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `promotion` WHERE `promotionID` LIKE ? OR `promotionName` LIKE ?";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            preparedStatement.setString(2,"%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                promotions.add(new promotion(
                        resultSet.getString("promotionID"),
                        resultSet.getString("promotionName"),
                        resultSet.getString("promotionDescription"),
                        resultSet.getDate("promotionStartDay"),
                        resultSet.getDate("promotionEndDay"),
                        resultSet.getFloat("promotionPercentage")));
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    public void clearTable(){
        //remove all children except the first one ( the first one is not a box contain areas infomation)
        int numChildren = promotionContainer.getChildren().size();
        if (numChildren > 1) {
            promotionContainer.getChildren().remove(1, numChildren);
        }
    }
    public void refresh(){
        clearTable();
        promotions.clear(); //clear the list promotions
        refreshData();
        getPromotion();
    }
    public void refresh(String searchText){
        clearTable();
        promotions.clear(); //clear the list promotions
        refreshData(searchText);
        getPromotion();
    }
    private void getPromotion() {
        con = dbConnect.getConnect();

        for (promotion promotion : promotions) {
            HBox promotionBox = new HBox(); //Create container to hold the promotions
            promotionBox.setStyle("-fx-border-color: rgb(128,128,128); " +
                    "-fx-border-width: 0 1px 1px 1px; " +
                    "-fx-border-style: solid;");

            promotionBox.setPrefHeight(36);
            promotionBox.setAlignment(Pos.CENTER_LEFT); //set the position of component inside the containner

            Label promotionIdLabel = new Label(promotion.getPromotionID());
            Label promotionNameLabel = new Label(promotion.getPromotionName());
            Label promotionStartDayLabel = new Label(String.valueOf(promotion.getPromotionStartDay()));
            Label promotionEndDayLabel = new Label(String.valueOf(promotion.getPromotionEndDay()));
            Label promotionPercentageLabel = new Label(String.valueOf(promotion.getPromotionPercentage()));
            Label promotionDescriptionLabel;
            //if there are no description about this promotion, show "Không có mô tả" ("No description")
            if (!promotion.getPromotionDescription().isEmpty())
                promotionDescriptionLabel = new Label(promotion.getPromotionDescription());
            else promotionDescriptionLabel = new Label("(Không có mô tả!)");

            promotionIdLabel.setMinWidth(130);
            promotionNameLabel.setMinWidth(130);

            promotionDescriptionLabel.setMinWidth(200);
            promotionStartDayLabel.setMinWidth(130);
            promotionEndDayLabel.setMinWidth(130);
            promotionPercentageLabel.setMinWidth(130);

            promotionBox.setMargin(promotionIdLabel, new Insets(0,0,0,30));

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

            deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#ff1744;");
            editIcon.setStyle("-fx-cursor: hand ; -glyph-size:28px; -fx-fill:#00E676;");

            deleteIcon.setOnMouseClicked(event -> {
                try {
                    query = "DELETE FROM `promotion` WHERE promotionID = '" + promotion.getPromotionID() + "'";
                    con = dbConnect.getConnect();
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.execute();
                    refreshData();
                    refresh(); //refresh the table after delete
                } catch (SQLException ex) {
                    Logger.getLogger(promotionManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    warning("Không thể xóa tác giả, hãy chắc chắn tác giả bạn đang xóa không còn liên kết với sách nào");
                }
            });

            editIcon.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("promotionModify.fxml"));
                    Parent root = loader.load();
                    //This part of code is to set the controller for promotionAddcontroller as this controller
                    //so it can call the refresh function from this controller
                    //this, is black magic, if i was at 17th centuary, i would be burned alive
                    promotionModifyController fac = loader.getController();
                    // Pass a reference to the Scene A controller to Scene B
                    fac.setController(this);
                    fac.setSearchText(searchText);

                    if (fac != null) {
                        fac.setValue(promotion.getPromotionID(), promotion.getPromotionName(), promotion.getPromotionDescription(),promotion.getPromotionStartDay(),promotion.getPromotionEndDay(),promotion.getPromotionPercentage());
                    } else {
                        System.err.println("Controller is null.");
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Chỉnh sửa khuyến mãi");
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



            promotionBox.getChildren().addAll(promotionIdLabel, promotionNameLabel,promotionStartDayLabel,promotionEndDayLabel,promotionPercentageLabel, promotionDescriptionLabel, buttonBox);

            // Add the HBox for each promotion to your layout
            promotionContainer.getChildren().add(promotionBox);
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();
        getPromotion();
        //hide the scroll bar of the scroll pane
        promotionsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        promotionsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
