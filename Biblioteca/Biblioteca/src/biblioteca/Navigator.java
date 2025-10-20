package biblioteca;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Navigator {

    public static void goTo(Node node, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Navigator.class.getResource(fxmlPath)));
            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a página: " + fxmlPath);
            e.printStackTrace();
        }
    }
}