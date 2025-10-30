package biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class Biblioteca extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXMLDocument.fxml")));

        // Corrected: Create a single scene with the desired size (600x600) or without.
        // The sizes in FXMLDocument.fxml are prefHeight="347.0" prefWidth="551.0",
        // so we'll use a size closer to the FXML's preferred size, or let the stage size to scene.

        // Option 1: Let the FXML determine the size (recommended since you have a setResizable(false) later)
        Scene scene = new Scene(root);

        // If you absolutely need 600x600, use this instead and remove setResizable(false) if you want it to be resizable:
        // Scene scene = new Scene(root, 600, 600);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Biblioteca");
        stage.show();
        // REMOVED THE PROBLEMATIC LINE: stage.setScene(new Scene(root, 600, 600));
    }

    public static void main(String[] args) {
        launch(args);
    }
}