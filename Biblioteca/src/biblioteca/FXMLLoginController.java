package biblioteca;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class FXMLLoginController implements Initializable {


    @FXML
    private TextField nomeCampo;
    @FXML
    private PasswordField senhaCampo;
    @FXML
    private Button entrarBtn;
    @FXML
    private Label saida;


    @Override
    public void initialize(URL url, ResourceBundle rb) {


        entrarBtn.disableProperty().bind(
                nomeCampo.textProperty().isEmpty()
                        .or(senhaCampo.textProperty().isEmpty())

        );


        nomeCampo.setOnAction(this::entrar);
        senhaCampo.setOnAction(this::entrar);

    }


    @FXML
    private void entrar(ActionEvent e) {


        String login = nomeCampo.getText();
        String senha = senhaCampo.getText();
        String loginCorreto = "admin";
        String senhaCorreta = "123";


        // CORREÇÃO: Usar comparação estrita para login e senha.
        if (loginCorreto.equals(login) && senhaCorreta.equals(senha)) {

            saida.setStyle("-fx-text-fill: #32CD32");
            saida.setText("Usuário encontrado com Sucesso!");

            Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");

        } else {

            saida.setStyle("-fx-text-fill: #b00020");
            saida.setText("Login ou Senha inválida");

        }


    }


}