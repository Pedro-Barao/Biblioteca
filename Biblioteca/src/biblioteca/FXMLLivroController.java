package biblioteca;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

// Nota: A classe modelo correta é Livros.
public class FXMLLivroController implements Initializable {

    @FXML private TextField nomeCampo;
    @FXML private TextField isbnCampo;
    @FXML private TextField anoCampo;
    @FXML private TextField autorCampo;
    @FXML private TextField generoCampo;

    @FXML private TableView<Livros> tabelaLivros; // CORREÇÃO: Usando Livros

    // CORREÇÕES: Todos os tipos de coluna são String (exceto status, que usará Status, ou será mapeado para uma String)
    @FXML private TableColumn<Livros, String> colunaNome;
    @FXML private TableColumn<Livros, String> colunaISBN;
    @FXML private TableColumn<Livros, String> colunaAno; // CORREÇÃO: Tipo String
    @FXML private TableColumn<Livros, String> colunaAutor;
    @FXML private TableColumn<Livros, String> colunaGenero;
    // O status é complexo (Status/boolean), usaremos uma coluna String e o método toString()
    @FXML private TableColumn<Livros, String> colunaStatus;

    // Gerenciador de dados simulado
    // private final GerenciadorDeDados dados = new GerenciadorDeDados();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializa as colunas da tabela
        // CORREÇÕES: PropertyValueFactory deve usar o nome da propriedade do getter (ex: getNome -> "nome")
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN")); // CORREÇÃO: "ISBN" (Nome do getter é getISBN())
        colunaAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colunaAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colunaGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));

        // CORREÇÃO PARA O STATUS: Mapeia para a propriedade retirada_Entrega e confia no toString() do objeto Status.
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("retirada_Entrega"));

        // Exemplo de como carregar dados (você deve adaptar isso ao seu 'GerenciadorDeDados')
        // tabelaLivros.setItems(dados.listarLivros());
    }

    @FXML
    private void cadastrar(ActionEvent event) {
        String isbn = isbnCampo.getText();
        String nome = nomeCampo.getText();

        if (isbn.isEmpty() || nome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Nome e ISBN são obrigatórios.");
            return;
        }

        // Lógica para cadastrar o novo livro (ex: dados.cadastrarLivro(new Livros(...)))
        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Livro " + nome + " cadastrado.");
        limparCampos();
    }

    @FXML
    private void alterar(ActionEvent event) {
        // Lógica para alterar o livro selecionado
        mostrarAlerta(Alert.AlertType.INFORMATION, "Ação", "Função Alterar (Deve usar o ISBN para localizar).");
    }

    @FXML
    private void pesquisar(ActionEvent event) {
        // Lógica para pesquisar (filtrar a tabela)
        mostrarAlerta(Alert.AlertType.INFORMATION, "Ação", "Função Pesquisar.");
    }

    @FXML
    private void excluir(ActionEvent event) {
        // Lógica para excluir o livro selecionado
        mostrarAlerta(Alert.AlertType.ERROR, "Ação", "Função Excluir (Confirmação necessária).");
    }

    @FXML
    private void voltar(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
    }

    private void limparCampos() {
        nomeCampo.clear();
        isbnCampo.clear();
        anoCampo.clear();
        autorCampo.clear();
        generoCampo.clear();
        tabelaLivros.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensagem) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}