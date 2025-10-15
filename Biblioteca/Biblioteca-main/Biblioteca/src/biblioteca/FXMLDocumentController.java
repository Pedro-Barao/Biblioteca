package biblioteca;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FXMLDocumentController implements Initializable {

    @FXML private Button entrarBtn;
    @FXML private TextField loginCampo;
    @FXML private PasswordField senhaCampo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void entrarAction(ActionEvent event) {
        String login = loginCampo.getText();
        String senha = senhaCampo.getText();

        if ("admin".equals(login) && "123".equals(senha)) {
            System.out.println("Login bem-sucedido!");
            Navigator.goTo((Node) event.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
        } else {
            System.out.println("Login ou senha incorretos.");
            mostrarAlerta("Erro de Login", "O login ou a senha estão incorretos. Tente novamente.");
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}