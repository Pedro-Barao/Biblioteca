package biblioteca;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigator {

    public static void goTo(Node node, String fxmlPath) {
        try {
            Stage stage = (Stage) node.getScene().getWindow();

            Parent root = (Parent) FXMLLoader.load(
                    Objects.requireNonNull(Navigator.class.getResource(fxmlPath)));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            System.err.println("Error navigating to FXML: " + fxmlPath);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("FXML file not found or Resource not available: " + fxmlPath);
        }
    }
}