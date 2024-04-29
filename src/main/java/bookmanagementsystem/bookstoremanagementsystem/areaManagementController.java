package bookmanagementsystem.bookstoremanagementsystem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class areaManagementController implements Initializable {
    @FXML
    private TableView<area> AreaTable;
    @FXML
    private TableColumn<area, String> areaIdCol;
    @FXML
    private TableColumn<area, String> areaNameCol;
    @FXML
    private TableColumn<area, String> FloorIDCol;
    @FXML
    private TableColumn<area,String> Action;
    @FXML
    private Button areaAddButton;
    @FXML
    private Button refreshButton;

    @FXML
    private void refresh(){
        refreshTable();
    }
    @FXML
    private void addArea(){
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("areaAdd.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm khu vực");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    ObservableList<area> areas = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;
    area area = null ;
    @FXML
//    private Label lalala;
    private void refreshTable() {
        try {
            areas.clear();
            con = dbConnect.getConnect();
            query = "SELECT * FROM `area`";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);

            while (resultSet.next()){
                areas.add(new area(
                        resultSet.getString("AreaID"),
                        resultSet.getString("AreaName"),
                        resultSet.getString("FloorID")));
                AreaTable.setItems((ObservableList<bookmanagementsystem.bookstoremanagementsystem.area>) areas);

            }

            con.close();


        } catch (SQLException ex) {
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    private void getArea() {

        con = dbConnect.getConnect();
        refreshTable();

        areaIdCol.setCellValueFactory(new PropertyValueFactory<>("AreaID"));
        areaNameCol.setCellValueFactory(new PropertyValueFactory<>("AreaName"));
        FloorIDCol.setCellValueFactory(new PropertyValueFactory<>("FloorID"));

        Callback<TableColumn<area, String>, TableCell<area, String>> cellFoctory = (TableColumn<area, String> param) -> {
            // make cell containing buttons
            final TableCell<area, String> cell = new TableCell<area, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);

                    } else {

                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);

                        deleteIcon.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#ff1744;"
                        );
                        editIcon.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#00E676;"
                        );
                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            try {
                                area = AreaTable.getSelectionModel().getSelectedItem();
                                query = "DELETE FROM `area` WHERE AreaID  ='"+area.getAreaID() +"'";
                                con = dbConnect.getConnect();
                                preparedStatement = con.prepareStatement(query);
                                preparedStatement.execute();
                                refreshTable();

                            } catch (SQLException ex) {
                                Logger.getLogger(areaManagementController.class.getName()).log(Level.SEVERE, null, ex);
                            }





                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {

                            area = AreaTable.getSelectionModel().getSelectedItem();


                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("areaModify.fxml"));
                                Parent root = loader.load();
                                areaModifyController amc = loader.getController();
                                if (amc != null) {
                                    amc.setUpdate(true);
                                    amc.setValue(area.getAreaID(), area.getAreaName(),area.getFloorID());
                                } else {
                                    System.err.println("Controller is null.");
                                }
                                Stage stage = new Stage();
                                stage.setTitle("Modify Floor");
                                stage.setScene(new Scene(root));
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });

                        HBox managebtn = new HBox(editIcon, deleteIcon);
                        managebtn.setStyle("-fx-alignment:center");
                        HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));
                        HBox.setMargin(editIcon, new Insets(2, 3, 0, 2));

                        setGraphic(managebtn);

                        setText(null);

                    }
                }

            };

            return cell;
        };
        Action.setCellFactory(cellFoctory);
        AreaTable.setItems(areas);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getArea();
    }
}
