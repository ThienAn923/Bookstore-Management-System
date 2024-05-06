package bookmanagementsystem.bookstoremanagementsystem;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class TestFile extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Chooser Example");

        // Create a FileChooser object
        FileChooser fileChooser = new FileChooser();

        // Set title for the file chooser dialog
        fileChooser.setTitle("Open File");

        // Set initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Add filters to the file chooser (optional)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        // Handle the selected file
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("No file selected.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}