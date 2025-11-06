package biblioteca;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import biblioteca.Validators;
import biblioteca.Formatters;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLLivroController implements Initializable {

    private final ObservableList<Livros> livros = FXCollections.observableArrayList(GerenciadorDeDados.carregarLivros());
    @FXML
    private Button excluirBtn, pesquisarBtn, cadastrarBtn, alterarBtn, voltarBtn;
    @FXML
    private TextField nomeCampo, isbnCampo, anoCampo, autorCampo, retirada_entregaCampo, generoCampo;
    @FXML
    private TableView<Livros> Tabela;
    @FXML
    private TableColumn<Livros, String> colunaNome, colunaISBN, colunaAno, colunaAutor, colunaGenero;
    @FXML
    private TableColumn<Livros, Status> colunaRetEnt; // Mapeado para o Status (Enum)

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Assume-se que Formatters existe
        Formatters.applyIsbnFilter(isbnCampo);

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN")); // Propriedade com I maiúsculo
        colunaAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colunaAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colunaGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colunaRetEnt.setCellValueFactory(new PropertyValueFactory<>("retirada_Entrega"));
        Tabela.setItems(livros);

        Tabela.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean selecionado = n != null;
            alterarBtn.setDisable(!selecionado);
            excluirBtn.setDisable(!selecionado);
            if (selecionado) preencherCampos(n);
            else limparCampos();
        });

        alterarBtn.setDisable(true);
        excluirBtn.setDisable(true);
    }

    @FXML
    private void cadastrar(ActionEvent e) {
        if (!Validators.isValidISBN(isbnCampo.getText())) { mostrarAlerta("Erro", "ISBN inválido (aceita ISBN-10 ou ISBN-13)."); return; }

        if (!validarCamposObrigatorios()) return;

        Status status = obterStatusDaUI();
        if (status == null) {
            mostrarAlerta("Erro", "Status inválido. Use 1 (Retirado), 2 (Estoque) ou 3 (Atrasado).");
            return;
        }

        String isbn = isbnCampo.getText().trim();
        if (livros.stream().anyMatch(l -> l.getISBN().equalsIgnoreCase(isbn))) {
            mostrarAlerta("Erro", "ISBN já cadastrado.");
            return;
        }

        Livros novoLivro = new Livros(isbn, nomeCampo.getText().trim(), anoCampo.getText().trim(), autorCampo.getText().trim(), generoCampo.getText().trim(), status);
        livros.add(novoLivro);

        GerenciadorDeDados.salvarLivros(new ArrayList<>(livros));

        mostrarAlerta("Sucesso", "Livro cadastrado.");
        Tabela.refresh();
        limparCampos();
    }

    @FXML
    private void pesquisar(ActionEvent e) {
        String nome = safeLower(nomeCampo), isbn = safeLower(isbnCampo), ano = safeLower(anoCampo), autor = safeLower(autorCampo), genero = safeLower(generoCampo), sStat = safeTrim(retirada_entregaCampo);

        if (nome.isEmpty() && isbn.isEmpty() && ano.isEmpty() && autor.isEmpty() && genero.isEmpty() && sStat.isEmpty()) {
            Tabela.setItems(livros);
            return;
        }

        Status filtroStatus = sStat.isEmpty() ? null : Status.fromCodigo(sStat);
        if (!sStat.isEmpty() && filtroStatus == null) {
            mostrarAlerta("Erro", "Código de Status inválido. Use 1, 2 ou 3.");
            return;
        }

        List<Livros> resultados = livros.stream()
                .filter(l -> nome.isEmpty() || l.getNome().toLowerCase().contains(nome))
                .filter(l -> isbn.isEmpty() || l.getISBN().toLowerCase().contains(isbn))
                .filter(l -> ano.isEmpty() || l.getAno().toLowerCase().contains(ano))
                .filter(l -> autor.isEmpty() || l.getAutor().toLowerCase().contains(autor))
                .filter(l -> genero.isEmpty() || l.getGenero().toLowerCase().contains(genero))
                .filter(l -> filtroStatus == null || l.getRetirada_Entrega() == filtroStatus)
                .collect(Collectors.toList());

        Tabela.setItems(FXCollections.observableArrayList(resultados));
    }

    @FXML
    private void alterar(ActionEvent e) {
        Livros selecionado = Tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Erro", "Selecione um livro na tabela para alterar.");
            return;
        }
        if (!validarCamposObrigatorios()) return;

        Status novoStatus = obterStatusDaUI();
        if (novoStatus == null) {
            mostrarAlerta("Erro", "Status inválido. Use 1, 2 ou 3.");
            return;
        }

        selecionado.setNome(nomeCampo.getText().trim());
        selecionado.setAno(anoCampo.getText().trim());
        selecionado.setAutor(autorCampo.getText().trim());
        selecionado.setGenero(generoCampo.getText().trim());
        selecionado.setRetirada_Entrega(novoStatus);
        Tabela.refresh();

        GerenciadorDeDados.salvarLivros(new ArrayList<>(livros));

        mostrarAlerta("Sucesso", "Livro alterado.");
        limparCampos();
    }

    @FXML
    private void excluir(ActionEvent e) {
        Livros selecionado = Tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Erro", "Selecione um livro na tabela para excluir.");
            return;
        }

        // Validação: Não permite exclusão se estiver retirado
        if (selecionado.getRetirada_Entrega() == Status.RETIRADO || selecionado.getRetirada_Entrega() == Status.ATRASADO) {
            mostrarAlerta("Erro", "Não é possível excluir um livro que está atualmente emprestado/retirado.");
            return;
        }

        livros.remove(selecionado);

        GerenciadorDeDados.salvarLivros(new ArrayList<>(livros));

        mostrarAlerta("Sucesso", "Livro excluído.");
        limparCampos();
    }

    @FXML
    private void voltar(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
    }

    private void preencherCampos(Livros livro) {
        if (livro == null) return;
        nomeCampo.setText(livro.getNome());
        isbnCampo.setText(livro.getISBN());
        anoCampo.setText(livro.getAno());
        autorCampo.setText(livro.getAutor());
        generoCampo.setText(livro.getGenero());
        retirada_entregaCampo.setText(livro.getRetirada_Entrega().getCodigo());
    }

    private void limparCampos() {
        nomeCampo.clear();
        isbnCampo.clear();
        anoCampo.clear();
        autorCampo.clear();
        generoCampo.clear();
        retirada_entregaCampo.clear();
        Tabela.getSelectionModel().clearSelection();
        Tabela.setItems(livros); // Volta a mostrar a lista completa
    }

    private boolean validarCamposObrigatorios() {
        if (vazio(nomeCampo) || vazio(isbnCampo) || vazio(anoCampo) || vazio(autorCampo) || vazio(generoCampo) || vazio(retirada_entregaCampo)) {
            mostrarAlerta("Erro", "Todos os campos são obrigatórios.");
            return false;
        }
        return true;
    }

    private Status obterStatusDaUI() {
        return Status.fromCodigo(safeTrim(retirada_entregaCampo));
    }

    private boolean vazio(TextField tf) {
        return tf == null || tf.getText() == null || tf.getText().trim().isEmpty();
    }

    private String safeLower(TextField tf) {
        return (tf == null || tf.getText() == null) ? "" : tf.getText().trim().toLowerCase();
    }

    private String safeTrim(TextField tf) {
        return (tf == null || tf.getText() == null) ? "" : tf.getText().trim();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert;
        if (titulo.toLowerCase().equals("erro")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}