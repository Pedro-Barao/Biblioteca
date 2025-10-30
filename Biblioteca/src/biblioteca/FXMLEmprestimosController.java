package biblioteca;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLEmprestimosController implements Initializable {

    // SIMULAÇÃO DE DADOS (Substituir pela sua classe Usuario e GerenciadorDeDados real)
    private static class Usuario {
        String cpf;
        String nome;
        public Usuario(String cpf, String nome) {
            this.cpf = cpf;
            this.nome = nome;
        }
    }

    private final List<Usuario> USUARIOS_CADASTRADOS = Arrays.asList(
            new Usuario("123.456.789-00", "Maria da Silva"),
            new Usuario("111.222.333-44", "João Pereira"),
            new Usuario("999.888.777-66", "Ana Costa")
    );
    // FIM DA SIMULAÇÃO

    @FXML private TextField cpfField;
    @FXML private TextField isbnField;
    @FXML private DatePicker retiradaPicker;
    @FXML private Label devolucaoLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Emprestimo> tabela;
    @FXML private TableColumn<Emprestimo, String> colCpf;
    @FXML private TableColumn<Emprestimo, String> colNome; // NOVA COLUNA
    @FXML private TableColumn<Emprestimo, String> colIsbn;
    @FXML private TableColumn<Emprestimo, LocalDate> colRetirada;
    @FXML private TableColumn<Emprestimo, LocalDate> colDevolucao;
    @FXML private TableColumn<Emprestimo, Boolean> colDevolvido;

    private final GerenciadorDeDados dados = new GerenciadorDeDados();
    private FilteredList<Emprestimo> filtrados;

    private final javafx.collections.ObservableList<Emprestimo> baseEmprestimos =
            javafx.collections.FXCollections.observableArrayList();

    private javafx.collections.transformation.FilteredList<Emprestimo> filtrado;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dados.carregarTudo();

        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colRetirada.setCellValueFactory(new PropertyValueFactory<>("retirada"));
        colDevolucao.setCellValueFactory(new PropertyValueFactory<>("devolucao"));
        colDevolvido.setCellValueFactory(new PropertyValueFactory<>("devolvido"));

        // NOVO: Preenche os nomes de empréstimos antigos antes de carregar na tabela
        baseEmprestimos.setAll(dados.listarEmprestimos());
        baseEmprestimos.forEach(emp -> {
            if (emp.getNomeUsuario() == null || emp.getNomeUsuario().equals("N/D")) {
                String nome = procurarNomePorCpf(emp.getCpf());
                if (nome != null) {
                    emp.setNomeUsuario(nome);
                }
            }
        });

        filtrados = new FilteredList<>(
                baseEmprestimos,
                p -> true
        );
        tabela.setItems(filtrados);

        retiradaPicker.setValue(LocalDate.now());
        atualizarDataDevolucao();
        retiradaPicker.valueProperty().addListener((obs, old, val) -> atualizarDataDevolucao());

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                cpfField.setText(sel.getCpf());
                isbnField.setText(sel.getIsbn());
                retiradaPicker.setValue(sel.getRetirada());
                devolucaoLabel.setText(sel.getDevolucao() != null ? sel.getDevolucao().toString() : "");
                statusLabel.setText(sel.isDevolvido()
                        ? "Devolvido"
                        : (sel.isAtrasado(LocalDate.now()) ? "Atrasado" : "Ativo"));
            }
        });
    }

    private void atualizarDataDevolucao() {
        LocalDate r = retiradaPicker.getValue();
        if (r == null) r = LocalDate.now();
        devolucaoLabel.setText(r.plusDays(14).toString());
    }

    private String procurarNomePorCpf(String cpf) {
        Optional<Usuario> user = USUARIOS_CADASTRADOS.stream()
                .filter(u -> u.cpf.equals(cpf))
                .findFirst();
        return user.isPresent() ? user.get().nome : null;
    }

    @FXML
    public void cadastrar(ActionEvent e) {
        String cpf = safeTrim(cpfField.getText());
        String isbn = safeTrim(isbnField.getText());
        LocalDate retirada = retiradaPicker.getValue();

        if (cpf.isEmpty() || isbn.isEmpty()) {
            warn("Informe CPF e ISBN.");
            return;
        }

        String nome = procurarNomePorCpf(cpf);
        if (nome == null) {
            warn("CPF não encontrado na base de usuários. Verifique o cadastro.");
            return;
        }

        try {
            // Cria um objeto Emprestimo para fins de feedback e para a tabela
            Emprestimo emp = new Emprestimo(cpf, isbn, retirada);
            emp.setNomeUsuario(nome);

            // CHAMADA CORRIGIDA: Usa a assinatura esperada (String, String, LocalDate) para compilar.
            dados.registrarEmprestimo(cpf, isbn, retirada);

            info("Empréstimo cadastrado para " + nome + ". Devolução até " + emp.getDevolucao() + ".");
            refreshTable();
            limparCampos();
        } catch (Exception ex) {
            warn(ex.getMessage());
        }
    }

    @FXML
    public void alterar(ActionEvent e) {
        String cpf = safeTrim(cpfField.getText());
        String isbn = safeTrim(isbnField.getText());

        if (cpf.isEmpty() || isbn.isEmpty()) {
            warn("Informe CPF e ISBN para registrar a devolução.");
            return;
        }

        try {
            dados.registrarDevolucao(cpf, isbn);
            info("Devolução registrada com sucesso.");
            refreshTable();
            limparCampos();
        } catch (Exception ex) {
            warn(ex.getMessage());
        }
    }

    @FXML
    public void salvar(ActionEvent e) {
        dados.salvarTudo();
        info("Dados salvos.");
    }

    @FXML
    public void pesquisar(ActionEvent e) {
        String cpf = safeTrim(cpfField.getText());
        String isbn = safeTrim(isbnField.getText());
        filtrados.setPredicate(emp ->
                (cpf.isEmpty() || cpf.equals(emp.getCpf())) &&
                        (isbn.isEmpty() || isbn.equals(emp.getIsbn()))
        );
    }

    @FXML
    public void voltar(ActionEvent e) {
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
    }

    private void refreshTable() {
        // Recarrega a base de dados
        baseEmprestimos.setAll(dados.listarEmprestimos());

        // Preenche o nome para todos os itens recém-carregados
        baseEmprestimos.forEach(emp -> {
            if (emp.getNomeUsuario() == null || emp.getNomeUsuario().equals("N/D")) {
                String nome = procurarNomePorCpf(emp.getCpf());
                if (nome != null) {
                    emp.setNomeUsuario(nome);
                }
            }
        });

        // Força a tabela a redesenhar todas as linhas, exibindo o novo item e o nome.
        tabela.refresh();
    }


    private void limparCampos() {
        cpfField.clear();
        isbnField.clear();
        retiradaPicker.setValue(LocalDate.now());
        atualizarDataDevolucao();
        statusLabel.setText("");
        tabela.getSelectionModel().clearSelection();
        filtrados.setPredicate(p -> true);
    }

    private String safeTrim(String s) { return s == null ? "" : s.trim(); }

    private void warn(String msg) {
        statusLabel.setText(msg);
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void info(String msg) {
        statusLabel.setText(msg);
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}