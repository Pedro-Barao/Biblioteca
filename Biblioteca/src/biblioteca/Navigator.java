package biblioteca;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

public class Navigator {
  public static void goTo(Node node, String fxmlPath) {
    try {
      var url = Navigator.class.getResource(fxmlPath);
      if (url == null) throw new IllegalArgumentException("FXML não encontrado: " + fxmlPath);
      var root = javafx.fxml.FXMLLoader.load(url);
      var stage = (javafx.stage.Stage) node.getScene().getWindow();
      if (stage.getScene() == null) stage.setScene(new javafx.scene.Scene(root));
      else stage.getScene().setRoot(root);
      stage.show();
    } catch (Exception ex) {
      ex.printStackTrace(); // <-- console mostra "Caused by: ..."
      var sw = new java.io.StringWriter();
      ex.printStackTrace(new java.io.PrintWriter(sw));
      new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
        "Falha ao abrir " + fxmlPath + ":\n\n" + sw).showAndWait();
    }
  }
}
