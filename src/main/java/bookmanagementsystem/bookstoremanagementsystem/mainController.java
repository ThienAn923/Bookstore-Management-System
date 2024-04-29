package bookmanagementsystem.bookstoremanagementsystem;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private Pane mainPane;
    @FXML
    private Pane dashboardPane;
    @FXML
    private Pane tempPane; //the reason tempPane exist is because i somehow cannot get the position of panes like DashboardPane, temp pane is used to set the position for panes inside it at the same position as tempPane.
    @FXML
    private Button button;

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
    @FXML
    protected void bookManagementLoad(){
        loadPage("bookManagement");
    }
    @FXML
    protected void dashBoardLoad(){
        loadPage("dashBoard");
    }

    boolean isPopOut_managementBox = false;//Animation is build so shit that if u move your mouse in and out fast enough
                                            // so i build this flag to prevent it
    private int sidebarMove;
    private boolean isMouseInManagementOption = false;
    private boolean isMouseInManagementBox = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //animation
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.2));

        managementBox.setOnMouseEntered(mouseEvent -> {
            sidebarMove = isPopOut_managementBox?0:200;
            transition.setNode(managementOption);
            transition.setByX(sidebarMove);
            transition.play();
            isPopOut_managementBox = true;
        });

        managementBox.setOnMouseExited(mouseEvent -> {
            sidebarMove = isPopOut_managementBox?-200:0;
            transition.setNode(managementOption);
            transition.setByX(sidebarMove);
            transition.play();
            isPopOut_managementBox = false;
        });

    }
}
