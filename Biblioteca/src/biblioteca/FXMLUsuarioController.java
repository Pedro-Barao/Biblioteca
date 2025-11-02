package biblioteca;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.time.LocalDate; // Import necessário
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLUsuarioController implements Initializable {

    @FXML private Button excluirBtn, pesquisarBtn, cadastrarBtn, alterarBtn, voltarBtn;
    @FXML private TextField nomeCampo, idCampo, telefoneCampo, enderecoCampo;
    @FXML private TableView<Usuario> Tabela;
    @FXML private TableColumn<Usuario, String> colunaNome, colunaId, colunaTelefone, colunaEndereco, colunaStatusEmprestimo, colunaIdLivro;

    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList(GerenciadorDeDados.carregarUsuarios());
    private final List<Emprestimo> emprestimos = GerenciadorDeDados.carregarEmprestimos();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colunaEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colunaStatusEmprestimo.setCellValueFactory(new PropertyValueFactory<>("statusEmprestimo"));
        colunaIdLivro.setCellValueFactory(new PropertyValueFactory<>("livroEmprestadoId"));

        atualizarStatusEmprestimosDosUsuarios();
        Tabela.setItems(usuarios);

        Tabela.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean selecionado = n != null;
            alterarBtn.setDisable(!selecionado);
            excluirBtn.setDisable(!selecionado);
            if (selecionado) preencherCampos(n); else limparCampos();
        });

        alterarBtn.setDisable(true);
        excluirBtn.setDisable(true);
    }

    private void atualizarStatusEmprestimosDosUsuarios() {
        for (Usuario usuario : usuarios) {

            Optional<Emprestimo> emprestimoEncontrado = emprestimos.stream()
                    .filter(emp -> emp.getCpfUsuario().equals(usuario.getId()))
                    .filter(emp -> !emp.getStatus().equals("Devolvido")) // Correção aqui
                    .findFirst();

            if (emprestimoEncontrado.isPresent()) {
                Emprestimo emprestimo = emprestimoEncontrado.get();

                if (emprestimo.getDataDevolucao().isBefore(LocalDate.now())) {
                    emprestimo.setStatus("Atrasado");
                }
                usuario.setStatusEmprestimo(emprestimo.getStatus());
                usuario.setLivroEmprestadoId(emprestimo.getIsbnLivro());

            } else {
                usuario.setStatusEmprestimo("Nenhum");
                usuario.setLivroEmprestadoId("");
            }
        }

        GerenciadorDeDados.salvarEmprestimos(emprestimos);

        if (Tabela != null) {
            Tabela.refresh();
        }
    }

    @FXML
    private void cadastrar(ActionEvent e) {
        if (!validarCamposObrigatorios()) return;
        String id = idCampo.getText().trim();
        if (usuarios.stream().anyMatch(usuario -> usuario.getId().equalsIgnoreCase(id))) {
            mostrarAlerta("Erro", "Já existe um usuário cadastrado com este ID.");
            return;
        }

        Usuario novoUsuario = new Usuario(id, nomeCampo.getText().trim(), telefoneCampo.getText().trim(), enderecoCampo.getText().trim());
        usuarios.add(novoUsuario);
        GerenciadorDeDados.salvarUsuarios(new ArrayList<>(usuarios));

        limparCampos();
        Tabela.setItems(usuarios);
        atualizarStatusEmprestimosDosUsuarios();
        mostrarAlerta("Sucesso", "Usuário cadastrado.");
    }

    @FXML
    private void pesquisar(ActionEvent e) {
        String nome = safeLower(nomeCampo);
        String id = safeLower(idCampo);

        if (nome.isEmpty() && id.isEmpty()) {
            Tabela.setItems(usuarios);
            System.out.println("Nenhum filtro aplicado. Mostrando todos os " + usuarios.size() + " usuários.");
            return;
        }

        List<Usuario> resultados = usuarios.stream()
                .filter(u -> nome.isEmpty() || u.getNome().toLowerCase().contains(nome))
                .filter(u -> id.isEmpty() || u.getId().toLowerCase().contains(id))
                .collect(Collectors.toList());

        Tabela.setItems(FXCollections.observableArrayList(resultados));
        System.out.println("Pesquisa encontrou " + resultados.size() + " usuário(s).");
    }

    @FXML
    private void alterar(ActionEvent e) {
        Usuario selecionado = Tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Erro", "Selecione um usuário na tabela para alterar.");
            return;
        }
        if (!validarCamposObrigatorios()) return;

        selecionado.setNome(nomeCampo.getText().trim());
        selecionado.setTelefone(telefoneCampo.getText().trim());
        selecionado.setEndereco(enderecoCampo.getText().trim());
        Tabela.refresh();
        GerenciadorDeDados.salvarUsuarios(new ArrayList<>(usuarios));
        mostrarAlerta("Sucesso", "Usuário alterado.");
        limparCampos();
    }

    @FXML
    private void excluir(ActionEvent e) {
        Usuario selecionado = Tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Erro", "Selecione um usuário na tabela para excluir.");
            return;
        }
        usuarios.remove(selecionado);
        GerenciadorDeDados.salvarUsuarios(new ArrayList<>(usuarios));
        mostrarAlerta("Sucesso", "Usuário excluído.");
    }

    @FXML
    private void voltar(ActionEvent e) { Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml"); }

    // --- MÉTODOS AUXILIARES ---

    private void preencherCampos(Usuario usuario) { if (usuario == null) return; nomeCampo.setText(usuario.getNome()); idCampo.setText(usuario.getId()); telefoneCampo.setText(usuario.getTelefone()); enderecoCampo.setText(usuario.getEndereco()); }
    private void limparCampos() { nomeCampo.clear(); idCampo.clear(); telefoneCampo.clear(); enderecoCampo.clear(); Tabela.getSelectionModel().clearSelection(); }
    private boolean validarCamposObrigatorios() {
        if (vazio(nomeCampo) || vazio(idCampo)) {
            mostrarAlerta("Erro", "Campos obrigatórios: Nome, ID");
            return false;
        }
        return true;
    }
    private boolean vazio(TextField tf) { return tf == null || tf.getText() == null || tf.getText().trim().isEmpty(); }
    private String safeLower(TextField tf) { return (tf == null || tf.getText() == null) ? "" : tf.getText().trim().toLowerCase(); }

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