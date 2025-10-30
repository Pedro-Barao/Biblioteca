package biblioteca;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class FXMLDecisao_de_CRUDController implements Initializable {

    @FXML private Button livrosBtn;
    @FXML private Button sairBtn;
    @FXML private Button usuariosBtn;
    @FXML private Button gerenciarBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (sairBtn != null) { sairBtn.setOnAction(this::sair); }
        if (livrosBtn != null) { livrosBtn.setOnAction(this::abrirLivros); }
        if (usuariosBtn != null) { usuariosBtn.setOnAction(this::abrirUsuarios); }
        if (gerenciarBtn != null) { gerenciarBtn.setOnAction(this::abrirGerenciador);}
    }

    @FXML
    private void sair(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDocument.fxml");
    }

    @FXML
    private void abrirLivros(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLLivro.fxml");
    }

    @FXML
    private void abrirUsuarios(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLUsuario.fxml"); // Usando o novo nome
    }
    
    @FXML
    private void abrirGerenciador(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLEmprestimos.fxml");
    }
}