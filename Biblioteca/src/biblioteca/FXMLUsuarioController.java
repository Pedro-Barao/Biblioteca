package biblioteca;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

// Nota: Esta classe assume que você tem uma classe 'Usuario' e uma classe 'GerenciadorDeDados'
// para as funcionalidades de CRUD.
public class FXMLUsuarioController implements Initializable {

    @FXML private TextField nomeCampo;
    @FXML private TextField cpfCampo;
    @FXML private TextField telefoneCampo;
    @FXML private TextField enderecoCampo;

    @FXML private TableView<Usuario> tabelaUsuarios;
    @FXML private TableColumn<Usuario, String> colunaNome;
    @FXML private TableColumn<Usuario, String> colunaCPF;
    @FXML private TableColumn<Usuario, String> colunaTelefone;
    @FXML private TableColumn<Usuario, String> colunaEndereco;

    // Gerenciador de dados simulado
    // private final GerenciadorDeDados dados = new GerenciadorDeDados();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializa as colunas da tabela
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCPF.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colunaEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        // Exemplo de como carregar dados (você deve adaptar isso ao seu 'GerenciadorDeDados')
        // tabelaUsuarios.setItems(dados.listarUsuarios());
    }

    @FXML
    private void cadastrar(ActionEvent event) {
        String cpf = cpfCampo.getText();
        String nome = nomeCampo.getText();

        if (cpf.isEmpty() || nome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Nome e CPF são obrigatórios.");
            return;
        }

        // Lógica para cadastrar o novo usuário (ex: dados.cadastrarUsuario(new Usuario(...)))
        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário " + nome + " cadastrado.");
        limparCampos();
    }

    @FXML
    private void alterar(ActionEvent event) {
        // Lógica para alterar o usuário selecionado
        mostrarAlerta(Alert.AlertType.INFORMATION, "Ação", "Função Alterar (Deve usar o CPF para localizar).");
    }

    @FXML
    private void pesquisar(ActionEvent event) {
        // Lógica para pesquisar (filtrar a tabela)
        mostrarAlerta(Alert.AlertType.INFORMATION, "Ação", "Função Pesquisar.");
    }

    @FXML
    private void excluir(ActionEvent event) {
        // Lógica para excluir o usuário selecionado
        mostrarAlerta(Alert.AlertType.ERROR, "Ação", "Função Excluir (Confirmação necessária).");
    }

    @FXML
    private void voltar(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
    }

    private void limparCampos() {
        nomeCampo.clear();
        cpfCampo.clear();
        telefoneCampo.clear();
        enderecoCampo.clear();
        tabelaUsuarios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensagem) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}