package biblioteca;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import biblioteca.Validators;
import biblioteca.Formatters;

public class FXMLEmprestimosController implements Initializable {

    @FXML
    private Button voltarBtn;
    @FXML
    private TextField cpfField;
    @FXML
    private TextField isbnField;
    @FXML
    private DatePicker retiradaPicker;
    @FXML
    private Label devolucaoLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TableView<Emprestimo> tabela;
    @FXML
    private TableColumn<Emprestimo, String> colCpf;
    @FXML
    private TableColumn<Emprestimo, String> colNome;
    @FXML
    private TableColumn<Emprestimo, String> colIsbn;
    @FXML
    private TableColumn<Emprestimo, LocalDate> colRetirada;
    @FXML
    private TableColumn<Emprestimo, LocalDate> colDevolucao;
    @FXML
    private TableColumn<Emprestimo, String> colDevolvido;

    private ObservableList<Emprestimo> listaDeEmprestimos;
    private List<Usuario> todosUsuarios;
    private List<Livros> todosLivros;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        todosUsuarios = GerenciadorDeDados.carregarUsuarios();
        todosLivros = GerenciadorDeDados.carregarLivros();
        listaDeEmprestimos = FXCollections.observableArrayList(GerenciadorDeDados.carregarEmprestimos());

        // Configurar colunas da tabela
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpfUsuario"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbnLivro"));
        colRetirada.setCellValueFactory(new PropertyValueFactory<>("dataRetirada"));
        colDevolucao.setCellValueFactory(new PropertyValueFactory<>("dataDevolucao"));
        colDevolvido.setCellValueFactory(new PropertyValueFactory<>("status"));

        tabela.setItems(listaDeEmprestimos);

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                preencherCampos(sel);
            }
        });

        retiradaPicker.setValue(LocalDate.now());
        Formatters.applyCpfMaskEditable(cpfField);
    }
        
    @FXML
    private void cadastrar(ActionEvent event) {
        String cpf = cpfField.getText();
        String isbn = isbnField.getText();
        LocalDate retirada = retiradaPicker.getValue();

        if (cpf == null || cpf.trim().isEmpty() || isbn == null || isbn.trim().isEmpty() || retirada == null) {
            mostrarAlerta("Erro", "CPF, ISBN e Data de Retirada são obrigatórios.");
            return;
        }
        if (!Validators.isValidCPF(cpf)) {
            mostrarAlerta("Erro", "CPF inválido.");
            return;
        }
        
            mostrarAlerta("Erro", "CPF, ISBN e Data de Retirada são obrigatórios.");
            return;
        }

        Optional<Usuario> usuarioOpt = todosUsuarios.stream().filter(u -> Validators.onlyDigits(u.getId()).equals(Validators.onlyDigits(cpf))).findFirst();
        if (usuarioOpt.isPresent()) {
            mostrarAlerta("Erro", "Nenhum usuário encontrado com este CPF/ID.");
            return;
        }
        Usuario usuario = usuarioOpt.get();

        Optional<Livros> livroOpt = todosLivros.stream().filter(l -> l.getISBN().equals(isbn)).findFirst();
        if (livroOpt.isPresent()) {
            mostrarAlerta("Erro", "Nenhum livro encontrado com este ISBN.");
            return;
        }
        Livros livro = livroOpt.get();

        if (livro.getRetirada_Entrega() != Status.ESTOQUE) {
            mostrarAlerta("Erro", "Livro não está em estoque. Status: " + livro.getRetirada_Entrega().getDescricao());
            return;
        }

        boolean jaTemEmprestimo = listaDeEmprestimos.stream()
                .anyMatch(emp -> Validators.onlyDigits(emp.getCpfUsuario()).equals(Validators.onlyDigits(cpf)) && (emp.getStatus().equals("Ativo") || emp.getStatus().equals("Atrasado")));
        if (jaTemEmprestimo) {
            mostrarAlerta("Erro", "Este usuário já possui um empréstimo ativo.");
            return;
        }

        LocalDate devolucao = retirada.plusDays(14);
        String nomeUsuario = usuario.getNome();
        String status = "Ativo";

        Emprestimo novoEmprestimo = new Emprestimo(cpf, nomeUsuario, isbn, retirada, devolucao, status);
        listaDeEmprestimos.add(novoEmprestimo);

        livro.setRetirada_Entrega(Status.RETIRADO);

        limparCampos();
        mostrarAlerta("Sucesso", "Empréstimo cadastrado. Devolução em " + devolucao.toString());
    }

    @FXML
    private void pesquisar(ActionEvent event) {
        String cpf = cpfField.getText();
        String isbn = isbnField.getText();

        List<Emprestimo> resultados = listaDeEmprestimos.stream()
                .filter(e -> cpf.isEmpty() || e.getCpfUsuario().contains(cpf))
                .filter(e -> isbn.isEmpty() || e.getIsbnLivro().contains(isbn))
                .collect(Collectors.toList());

        tabela.setItems(FXCollections.observableArrayList(resultados));
    }

    @FXML
    private void alterar(ActionEvent event) {
        Emprestimo selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Erro", "Selecione um empréstimo na tabela para alterar.");
            return;
        }

        if (selecionado.getStatus().equals("Devolvido")) {
            mostrarAlerta("Aviso", "Este livro já foi devolvido.");
            return;
        }

        selecionado.setStatus("Devolvido");

        Optional<Livros> livroOpt = todosLivros.stream().filter(l -> l.getISBN().equals(selecionado.getIsbnLivro())).findFirst();
        if (livroOpt.isPresent()) {
            livroOpt.get().setRetirada_Entrega(Status.ESTOQUE);
        }

        tabela.refresh();
        mostrarAlerta("Sucesso", "Livro marcado como devolvido.");
    }

    @FXML
    private void salvar(ActionEvent event) {
        GerenciadorDeDados.salvarEmprestimos(new ArrayList<>(listaDeEmprestimos));
        GerenciadorDeDados.salvarLivros(todosLivros);
        mostrarAlerta("Sucesso", "Todos os dados de empréstimos e livros foram salvos.");
    }

    @FXML
    private void voltar(ActionEvent event) {
        Navigator.goTo((Node) event.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
    }

    private void preencherCampos(Emprestimo emp) {
        cpfField.setText(emp.getCpfUsuario());
        isbnField.setText(emp.getIsbnLivro());
        retiradaPicker.setValue(emp.getDataRetirada());
        devolucaoLabel.setText(emp.getDataDevolucao().toString());
        statusLabel.setText(emp.getStatus());
    }

    private void limparCampos() {
        cpfField.clear();
        isbnField.clear();
        retiradaPicker.setValue(LocalDate.now());
        Formatters.applyCpfMaskEditable(cpfField);
        devolucaoLabel.setText("[Calculado automaticamente]");
        statusLabel.setText("Nenhum empréstimo selecionado");
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