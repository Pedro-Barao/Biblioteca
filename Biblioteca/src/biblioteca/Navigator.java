package biblioteca;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigator {

    /**
     * Changes the scene of the Stage containing the given Node to a new FXML.
     * @param node The current JavaFX Node.
     * @param fxmlPath The path to the new FXML file (e.g., "/biblioteca/FXMLDecisao_de_CRUD.fxml").
     */
    public static void goTo(Node node, String fxmlPath) {
        try {
            // Get the stage from the current node's scene
            Stage stage = (Stage) node.getScene().getWindow();

            // The problematic line requires an explicit cast to Parent.
            // The FXMLLoader.load(URL) method returns Object, which is incompatible with Parent.
            Parent root = (Parent) FXMLLoader.load(
                    Objects.requireNonNull(Navigator.class.getResource(fxmlPath))
            ); // <-- Explicit cast to (Parent) fixes the error

            // Set the new scene on the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene(); // Optional: adjust stage size to fit the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // In a real app, you might show an Alert here
            System.err.println("Error navigating to FXML: " + fxmlPath);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("FXML file not found or Resource not available: " + fxmlPath);
        }
    }
}