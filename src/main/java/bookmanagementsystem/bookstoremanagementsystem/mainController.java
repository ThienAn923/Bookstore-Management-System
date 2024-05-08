package bookmanagementsystem.bookstoremanagementsystem;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
public class mainController implements Initializable{
    @FXML
    private StackPane mainSidebar;
    @FXML
    private VBox spaceManagementOption;
    @FXML
    private VBox bookManagementOption;
    @FXML
    private VBox managementOption;
    @FXML
    private VBox sidebarOption;
    @FXML
    private HBox managementBox;
    @FXML
    private HBox spaceManagementBox;
    @FXML
    private HBox bookManagementBox;

    @FXML
    private Pane mainPane;
    @FXML
    private Pane dashboardPane;
    @FXML
    private Pane tempPane; //the reason tempPane exist is because i somehow cannot get the position of panes like DashboardPane, temp pane is used to set the position for panes inside it at the same position as tempPane.
    @FXML
    private Button button;
    @FXML
    private Label staffName;

    private String staffID;
    private boolean accountAuthorized = false;

    String query = null;
    PreparedStatement preparedStatement = null ;
    Connection con = dbConnect.getConnect();
    ResultSet resultSet = null ;

    LoginController LoginController;
    public void setController(LoginController LoginController) {
        this.LoginController = LoginController;
    }



    void getStaffID(String staffID, boolean accountAuthorized){
        this.staffID = staffID;
        this.accountAuthorized = accountAuthorized;
        try {
            con = dbConnect.getConnect();
            query = "SELECT staffName FROM `staff` WHERE staffID = '" + staffID + "'";
            preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery(query);
            if(resultSet.next()){
                staffName.setText(staffName.getText() + resultSet.getString("staffName"));
            }
        }catch (SQLException ex){
            Logger.getLogger(bookManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Load new page
    private void loadPage(String page){
        try{
            tempPane.getChildren().remove(0); //This command is use to remove whatever pane before load new pane
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page + ".fxml"));
            Parent newPage = loader.load();
//            int index = tempPane.getChildren().indexOf(dashboardPane);
            tempPane.getChildren().add(0, newPage);
        }catch (IOException i) {
            System.out.println("Error loading " + page + ".fxml: " + i.getMessage());
        }
    }
    void warning(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    protected void bookManagementLoad(){
        loadPage("bookManagement");
    }
    @FXML
    protected void paymentLoad(){
        try{
            tempPane.getChildren().remove(0); //This command is use to remove whatever pane before load new pane
            FXMLLoader loader = new FXMLLoader(getClass().getResource(  "payment.fxml"));
            Parent newPage = loader.load();

            paymentController fac = loader.getController();
            fac.setController(this);
            if (fac != null) {
                fac.setStaffID(staffID);
            } else {
                System.err.println("Controller is null.");
            }
//            int index = tempPane.getChildren().indexOf(dashboardPane);
            tempPane.getChildren().add(0, newPage);
        }catch (IOException i) {

        }
    }
    @FXML
    protected void logout(){}
    @FXML
    protected void lockApp(){}
    @FXML
    protected void areaLoad(){
        loadPage("areaManagement");
    }
    @FXML
    protected void shelveLoad(){
        loadPage("shelveManagement");
    }
    @FXML
    protected void floorLoad(){
        loadPage("floorManagement2");
    }
    @FXML
    protected void authorLoad(){
        loadPage("authorManagement");
    }
    @FXML
    protected void categoryLoad(){
        loadPage("categoryManagement");
    }
    @FXML
    protected void bookLoad(){
        loadPage("bookManagement");
    }
    @FXML
    protected void publisherLoad(){
        loadPage("publisherManagement");
    }
    @FXML
    protected void staffLoad(){
        loadPage("staffManagement");
    }
    @FXML
    protected void invoiceLoad(){
        loadPage("invoiceManagement");
    }
    @FXML
    protected void customerLoad(){
        loadPage("customerManagement");
    }
    @FXML
    protected void departmentLoad(){
        loadPage("departmentManagement");
    }
    @FXML
    protected void positionLoad(){
        loadPage("positionManagement");
    }
    @FXML
    protected void promotionLoad(){
        loadPage("promotionManagement");
    }
    @FXML
    protected void accountLoad(){
        loadPage("accountManagement");
    }



    boolean isPopOut_managementBox = false;//Animation is build so shit that if u move your mouse in and out fast enough
                                            // so i build this flag to prevent it

    boolean isPopOut_spaceManagementBox = false;
    boolean isPopOut_bookManagementBox = false;
    private int sidebarMove;
    private boolean isMouseInManagementOption = false;
    private boolean isMouseInManagementBox = false;

    private boolean isMouseInSpaceManagementOption = false;
    private boolean isMouseInSpaceManagementBox = false;

    private boolean isMouseInBookManagementOption = false;
    private boolean isMouseInBookManagementBox = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //animation
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.2));

        managementBox.setOnMouseClicked(mouseEvent -> {
            if(accountAuthorized){
                if(!isPopOut_managementBox) {
                    sidebarMove = 200;
                    transition.setNode(managementOption);
                    transition.setByX(sidebarMove);
                    transition.play();
                    isPopOut_managementBox = true;
                }
                else if(isPopOut_managementBox) {

                    sidebarMove = -200;
                    transition.setNode(managementOption);
                    transition.setByX(sidebarMove);
                    transition.play();
                    isPopOut_managementBox = false;
                }
            }
            else{
                warning("Chỉ người dùng quản lý mới có thể dùng chức năng này.");
            }
        });

        spaceManagementBox.setOnMouseClicked(mouseEvent -> {
            if(!isPopOut_spaceManagementBox) {
                sidebarMove = 400;
                transition.setNode(spaceManagementOption);
                transition.setByX(sidebarMove);
                transition.play();
                isPopOut_spaceManagementBox = true;
            }
            else if(isPopOut_spaceManagementBox) {

                sidebarMove = -400;
                transition.setNode(spaceManagementOption);
                transition.setByX(sidebarMove);
                transition.play();
                isPopOut_spaceManagementBox = false;
            }
        });

        bookManagementBox.setOnMouseClicked(mouseEvent -> {
            if(!isPopOut_bookManagementBox) {
                sidebarMove = 400;
                transition.setNode(bookManagementOption);
                transition.setByX(sidebarMove);
                transition.play();
                isPopOut_bookManagementBox = true;
            }
            else if(isPopOut_bookManagementBox) {

                sidebarMove = -400;
                transition.setNode(bookManagementOption);
                transition.setByX(sidebarMove);
                transition.play();
                isPopOut_bookManagementBox = false;
            }
        });

        paymentLoad();

    }
}
