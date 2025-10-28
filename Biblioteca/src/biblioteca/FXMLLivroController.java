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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLLivroController implements Initializable {

    @FXML private Button excluirBtn, pesquisarBtn, cadastrarBtn, alterarBtn, voltarBtn;
    @FXML private TextField nomeCampo, isbnCampo, anoCampo, autorCampo, retirada_entregaCampo, generoCampo;
    @FXML private TableView<Livros> Tabela;
    @FXML private TableColumn<Livros, String> colunaNome, colunaISBN, colunaAno, colunaAutor, colunaGenero;
    @FXML private TableColumn<Livros, Status> colunaRetEnt;

    private final GerenciadorDeDados dados = new GerenciadorDeDados();
    private final ObservableList<Livros> livros = FXCollections.observableArrayList();
    private List<Emprestimo> emprestimos = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dados.carregarTudo();
        livros.setAll(dados.listarLivros());
        emprestimos = dados.listarEmprestimos();

        // Atenção: o nome da propriedade deve casar com o getter da sua classe Livros.
        // Se seu getter for getISBN(), use "ISBN"; se for getIsbn(), troque para "isbn".
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        colunaAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colunaAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colunaGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colunaRetEnt.setCellValueFactory(new PropertyValueFactory<>("retirada_Entrega"));

        Tabela.setItems(livros);

        Tabela.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean selecionado = n != null;
            alterarBtn.setDisable(!selecionado);
            excluirBtn.setDisable(!selecionado);
            if (selecionado) preencherCampos(n); else limparCampos();
        });

        alterarBtn.setDisable(true);
        excluirBtn.setDisable(true);
    }

    @FXML
    private void cadastrar(ActionEvent e) {
        if (!validarCamposObrigatorios()) return;

        Status status = obterStatusDaUI();
        if (status == null) { System.out.println("Status inválido (use códigos do enum Status)."); return; }

        String isbn = isbnCampo.getText().trim();
        boolean jaExiste = livros.stream().anyMatch(l -> safeStr(getISBN(l)).equalsIgnoreCase(isbn));
        if (jaExiste) { System.out.println("ISBN já existe."); return; }

        Livros novoLivro = new Livros(
                isbn,
                nomeCampo.getText().trim(),
                anoCampo.getText().trim(),
                autorCampo.getText().trim(),
                generoCampo.getText().trim(),
                status
        );

        // Sincroniza estoque com o Status
        setEmEstoqueCompat(novoLivro, status == Status.ESTOQUE);

        livros.add(novoLivro);
        GerenciadorDeDados.salvarLivros(new ArrayList<>(livros));
        System.out.println("Cadastrado: " + novoLivro.resumo());
        limparCampos();
    }

    @FXML
    private void pesquisar(ActionEvent e) {
        String nome = safeLower(nomeCampo);
        String isbn = safeLower(isbnCampo);
        String ano = safeLower(anoCampo);
        String autor = safeLower(autorCampo);
        String genero = safeLower(generoCampo);
        String sStat = safeTrim(retirada_entregaCampo);

        if (nome.isEmpty() && isbn.isEmpty() && ano.isEmpty() && autor.isEmpty() && genero.isEmpty() && sStat.isEmpty()) {
            Tabela.setItems(livros);
            return;
        }

        Status filtroStatus = sStat.isEmpty() ? null : Status.fromCodigo(sStat);

        List<Livros> resultados = livros.stream()
                .filter(l -> nome.isEmpty()   || safeStr(l.getNome()).toLowerCase().contains(nome))
                .filter(l -> isbn.isEmpty()   || safeStr(getISBN(l)).toLowerCase().contains(isbn))
                .filter(l -> ano.isEmpty()    || safeStr(l.getAno()).toLowerCase().contains(ano))
                .filter(l -> autor.isEmpty()  || safeStr(l.getAutor()).toLowerCase().contains(autor))
                .filter(l -> genero.isEmpty() || safeStr(l.getGenero()).toLowerCase().contains(genero))
                .filter(l -> filtroStatus == null || l.getRetirada_Entrega() == filtroStatus)
                .collect(Collectors.toList());

        Tabela.setItems(FXCollections.observableArrayList(resultados));
    }

    @FXML
    private void alterar(ActionEvent e) {
        Livros selecionado = Tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) { System.out.println("Selecione um livro."); return; }
        if (!validarCamposObrigatorios()) return;

        // Se tentar marcar ESTOQUE enquanto há empréstimo ativo para este ISBN, bloqueia
        Status novoStatus = obterStatusDaUI();
        if (novoStatus == null) { System.out.println("Status inválido."); return; }

        String isbn = safeStr(getISBN(selecionado)).trim();
        boolean temEmprestimoAtivo = emprestimos.stream()
                .anyMatch(emp -> !emp.isDevolvido() && isbn.equals(safeStr(emp.getIsbn()).trim()));
        if (temEmprestimoAtivo && novoStatus == Status.ESTOQUE) {
            System.out.println("Não é possível marcar 'ESTOQUE' com empréstimo ativo para este ISBN.");
            return;
        }

        selecionado.setNome(nomeCampo.getText().trim());
        selecionado.setAno(anoCampo.getText().trim());
        selecionado.setAutor(autorCampo.getText().trim());
        selecionado.setGenero(generoCampo.getText().trim());
        selecionado.setRetirada_Entrega(novoStatus);

        // Sincroniza emEstoque
        setEmEstoqueCompat(selecionado, novoStatus == Status.ESTOQUE);

        Tabela.refresh();
        GerenciadorDeDados.salvarLivros(new ArrayList<>(livros));
        System.out.println("Alterado: " + selecionado.resumo());
        limparCampos();
    }

    @FXML
    private void excluir(ActionEvent e) {
        Livros selecionado = Tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) { System.out.println("Selecione um livro."); return; }

        String isbn = safeStr(getISBN(selecionado)).trim();
        // Bloqueia exclusão se houver empréstimo ativo deste ISBN
        boolean temEmprestimoAtivo = emprestimos.stream()
                .anyMatch(emp -> !emp.isDevolvido() && isbn.equals(safeStr(emp.getIsbn()).trim()));
        if (temEmprestimoAtivo) {
            System.out.println("Não é possível excluir: existe empréstimo ativo para este ISBN.");
            return;
        }

        livros.remove(selecionado);
        GerenciadorDeDados.salvarLivros(new ArrayList<>(livros));
        System.out.println("Excluído: " + selecionado.resumo());
    }

    @FXML
    private void voltar(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
    }

    // ----------------------- Helpers de UI/Modelo -----------------------------

    private void preencherCampos(Livros livro) {
        if (livro == null) return;
        nomeCampo.setText(safeStr(livro.getNome()));
        isbnCampo.setText(safeStr(getISBN(livro)));
        anoCampo.setText(safeStr(livro.getAno()));
        autorCampo.setText(safeStr(livro.getAutor()));
        generoCampo.setText(safeStr(livro.getGenero()));
        retirada_entregaCampo.setText(livro.getRetirada_Entrega() == null ? "" : livro.getRetirada_Entrega().getCodigo());
    }

    private void limparCampos() {
        nomeCampo.clear();
        isbnCampo.clear();
        anoCampo.clear();
        autorCampo.clear();
        generoCampo.clear();
        retirada_entregaCampo.clear();
        Tabela.getSelectionModel().clearSelection();

        // Recarrega empréstimos caso outra tela tenha alterado
        emprestimos = dados.listarEmprestimos();
    }

    private boolean validarCamposObrigatorios() {
        if (vazio(nomeCampo) || vazio(isbnCampo) || vazio(anoCampo)
                || vazio(autorCampo) || vazio(generoCampo) || vazio(retirada_entregaCampo)) {
            System.out.println("Todos os campos são obrigatórios.");
            return false;
        }
        return true;
    }

    private Status obterStatusDaUI() {
        return Status.fromCodigo(safeTrim(retirada_entregaCampo));
    }

    // Compat para possíveis variações de getters (getISBN vs getIsbn)
    private String getISBN(Livros l) {
        // Se sua classe só tem getISBN(), ótimo. Se tiver getIsbn(), mude a tabela e este método.
        return l.getISBN();
    }

    private void setEmEstoqueCompat(Livros l, boolean value) {
        // Se seu modelo usa setEmEstoque(boolean):
        l.setEmEstoque(value);
        // E o Status já está sendo atualizado acima (setRetirada_Entrega)
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
    private String safeStr(String s) { return (s == null) ? "" : s; }
}
