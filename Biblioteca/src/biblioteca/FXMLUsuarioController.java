package biblioteca;

import java.net.URL;
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
    private List<Emprestimo> emprestimos = new ArrayList<>();

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
        java.time.LocalDate hoje = java.time.LocalDate.now();

        for (Usuario usuario : usuarios) {
            String cpf = (usuario.getId() == null) ? "" : usuario.getId().trim();

            Optional<Emprestimo> emprestimoEncontrado = emprestimos.stream()
                    .filter(emp -> !emp.isDevolvido())
                    .filter(emp -> emp.getCpf() != null && emp.getCpf().trim().equals(cpf))
                    .findFirst();

            if (emprestimoEncontrado.isPresent()) {
                Emprestimo emprestimo = emprestimoEncontrado.get();
                usuario.setStatusEmprestimo(emprestimo.isAtrasado(hoje) ? "Livro atrasado" : "Emprestado");
                usuario.setLivroEmprestadoId(emprestimo.getIsbn());
            } else {
                usuario.setStatusEmprestimo("Nenhum");
                usuario.setLivroEmprestadoId("");
            }
        }
        Tabela.refresh();
    }


    @FXML
    private void cadastrar(ActionEvent e) {
        if (!validarCamposObrigatorios()) return;
        String id = idCampo.getText().trim();
        if (usuarios.stream().anyMatch(usuario -> usuario.getId().equalsIgnoreCase(id))) { System.out.println("Já existe usuário com o ID " + id); return; }
        Usuario novoUsuario = new Usuario(id, nomeCampo.getText().trim(), telefoneCampo.getText().trim(), enderecoCampo.getText().trim());
        usuarios.add(novoUsuario);
        GerenciadorDeDados.salvarUsuarios(new ArrayList<>(usuarios));
        System.out.println("Cadastrado: " + novoUsuario.resumo());
        limparCampos();
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
        if (selecionado == null) { System.out.println("Selecione um usuário para alterar."); return; }
        if (!validarCamposObrigatorios()) return;
        selecionado.setNome(nomeCampo.getText().trim());
        selecionado.setTelefone(telefoneCampo.getText().trim());
        selecionado.setEndereco(enderecoCampo.getText().trim());
        Tabela.refresh();
        GerenciadorDeDados.salvarUsuarios(new ArrayList<>(usuarios));
        System.out.println("Alterado: " + selecionado.resumo());
        limparCampos();
    }

    @FXML
    private void excluir(ActionEvent e) {
        Usuario selecionado = Tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) { System.out.println("Selecione um usuário para excluir."); return; }
        usuarios.remove(selecionado);
        GerenciadorDeDados.salvarUsuarios(new ArrayList<>(usuarios));
        System.out.println("Excluído: " + selecionado.resumo());
    }

    @FXML
    private void voltar(ActionEvent e) { Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml"); }
    private void preencherCampos(Usuario usuario) { if (usuario == null) return; nomeCampo.setText(usuario.getNome()); idCampo.setText(usuario.getId()); telefoneCampo.setText(usuario.getTelefone()); enderecoCampo.setText(usuario.getEndereco()); }
    private void limparCampos() { nomeCampo.clear(); idCampo.clear(); telefoneCampo.clear(); enderecoCampo.clear(); Tabela.getSelectionModel().clearSelection(); }
    private boolean validarCamposObrigatorios() { if (vazio(nomeCampo) || vazio(idCampo)) { System.out.println("Campos obrigatórios: Nome, ID"); return false; } return true; }
    private boolean vazio(TextField tf) { return tf == null || tf.getText() == null || tf.getText().trim().isEmpty(); }
    private String safeLower(TextField tf) { return (tf == null || tf.getText() == null) ? "" : tf.getText().trim().toLowerCase(); }
}