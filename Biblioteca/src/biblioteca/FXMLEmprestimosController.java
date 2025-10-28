package biblioteca;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class FXMLEmprestimosController {

    @FXML private TextField cpfField;
    @FXML private TextField isbnField;
    @FXML private DatePicker retiradaPicker;
    @FXML private Label devolucaoLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Emprestimo> tabela;
    @FXML private TableColumn<Emprestimo, String> colCpf;
    @FXML private TableColumn<Emprestimo, String> colIsbn;
    @FXML private TableColumn<Emprestimo, LocalDate> colRetirada;
    @FXML private TableColumn<Emprestimo, LocalDate> colDevolucao;
    @FXML private TableColumn<Emprestimo, Boolean> colDevolvido;

    private final GerenciadorDeDados dados = new GerenciadorDeDados();
    private FilteredList<Emprestimo> filtrados;

    private final javafx.collections.ObservableList<Emprestimo> baseEmprestimos =
        javafx.collections.FXCollections.observableArrayList();

    private javafx.collections.transformation.FilteredList<Emprestimo> filtrado;
    
    
    @FXML
    public void initialize() {
        dados.carregarTudo();

        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colRetirada.setCellValueFactory(new PropertyValueFactory<>("retirada"));
        colDevolucao.setCellValueFactory(new PropertyValueFactory<>("devolucao"));
        colDevolvido.setCellValueFactory(new PropertyValueFactory<>("devolvido"));

        filtrados = new FilteredList<>(
                FXCollections.observableArrayList(dados.listarEmprestimos()),
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

    @FXML
    public void cadastrar(ActionEvent e) {
        String cpf = safeTrim(cpfField.getText());
        String isbn = safeTrim(isbnField.getText());
        LocalDate retirada = retiradaPicker.getValue();

        if (cpf.isEmpty() || isbn.isEmpty()) {
            warn("Informe CPF e ISBN.");
            return;
        }

        try {
            Emprestimo emp = dados.registrarEmprestimo(cpf, isbn, retirada);
            info("Empréstimo cadastrado. Devolução até " + emp.getDevolucao() + ".");
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
        baseEmprestimos.setAll(dados.listarEmprestimos());
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
