/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package biblioteca;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FXMLLivroController implements Initializable {

    @FXML private Button excluirBtn;
    @FXML private Button pesquisarBtn;
    @FXML private Button cadastrarBtn;
    @FXML private Button alterarBtn;
    @FXML private Button voltarBtn;

    @FXML private TextField nomeCampo;
    @FXML private TextField isbnCampo;
    @FXML private TextField anoCampo;
    @FXML private TextField autorCampo;
    @FXML private TextField retirada_entregaCampo;
    @FXML private TextField generoCampo;

    
    @FXML private TableView<Livros> Tabela;
    @FXML private TableColumn<Livros, String> colunaNome;
    @FXML private TableColumn<Livros, String> colunaISBN;
    @FXML private TableColumn<Livros, String> colunaAno;
    @FXML private TableColumn<Livros, String> colunaAutor;
    @FXML private TableColumn<Livros, String> colunaGenero;
    @FXML private TableColumn<Livros, String> colunaRetEnt;

    private final List<Livros> livros = new ArrayList<>();
    private List<Livros> ultimosResultados = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        
        System.out.println("Sistema Pronto! Status: 1=Retirado, 2=Estoque, 3=Atrasado.");

        if (cadastrarBtn != null) 
        {
            
            cadastrarBtn.setOnAction(this::cadastrar);
            
        }
        
        if (pesquisarBtn != null) 
        {
            
            pesquisarBtn.setOnAction(this::pesquisar);
            
        }
        
        if (alterarBtn   != null) 
        {
            
            alterarBtn.setOnAction(this::alterar);
            
        }
        
        if (excluirBtn   != null) 
        {
            
            excluirBtn.setOnAction(this::excluir);
            
        }
        
        
        if (voltarBtn   != null) 
        {
            
            voltarBtn.setOnAction(this::voltar);
            
        }
        
        
        Tabela.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                alterarBtn.setDisable(false);
                excluirBtn.setDisable(false);
                preencherCampos(newSelection);
            } else {
                alterarBtn.setDisable(true);
                excluirBtn.setDisable(true);
            }
        });
        
    }

    private void preencherCampos(ActionEvent e) {
        isbnCampo.setText(Livros.getISBN());
        textField_Nome.setText(cliente.getNome());
        textField_CPF.setText(cliente.getCpf());
        textField_Telefone.setText(cliente.getTelefone());
        textField_Endereco.setText(cliente.getEndereco());
        textField_Numero.setText(cliente.getNumero());
        textField_Cidade.setText(cliente.getCidade());
        textField_UF.setText(cliente.getUf());
        textField_CEP.setText(cliente.getCep());
        datePicker_Nascimento.setValue(cliente.getDataNascimento() != null ? LocalDate.parse(cliente.getDataNascimento()) : null);
    }

    @FXML
    private void cadastrar(ActionEvent e) 
    {
        
        List<String> faltando = obrigatoriosFaltando();
        
        if (!faltando.isEmpty()) 
        {
            
            System.out.println("Preencha: " + String.join(", ", faltando));
            return;
            
        }

        Status status = obterStatusDaUI();
        if (status == null) 
        {
            
            System.out.println("Status inválido. Use 1=Retirado, 2=Estoque, 3=Atrasado.");
            return;
            
        }

        String isbn   = isbnCampo.getText().trim();
        String nome   = nomeCampo.getText().trim();
        String ano    = anoCampo.getText().trim();
        String autor  = autorCampo.getText().trim();
        String genero = generoCampo.getText().trim();

        boolean jaExiste = livros.stream().anyMatch(x -> isbn.equalsIgnoreCase(x.getISBN()));
        
        if (jaExiste) 
        {
            
            System.out.println("Já existe livro com ISBN " + isbn);
            return;
            
        }

        Livros liv = new Livros(isbn, nome, ano, autor, genero, status);
        livros.add(liv);
        
        System.out.println("Cadastrado: " + liv.resumo());
        limparCampos();
        
    }

    @FXML
    private void pesquisar(ActionEvent e) 
    {
        
        clearOutput();

        String nome   = safeLower(nomeCampo);
        String isbn   = safeLower(isbnCampo);
        String ano    = safeLower(anoCampo);
        String autor  = safeLower(autorCampo);
        String genero = safeLower(generoCampo);
        String sStat  = safeTrim(retirada_entregaCampo);

        if (nome.isEmpty() && isbn.isEmpty() && ano.isEmpty() && autor.isEmpty() && genero.isEmpty() && sStat.isEmpty()) 
        {
            
            System.out.println("Informe ao menos um critério de pesquisa.");
            return;
            
        }

        Status filtroStatus = sStat.isEmpty() ? null : Status.fromCodigo(sStat);
        if (!sStat.isEmpty() && filtroStatus == null) 
        {
            
            System.out.println("Status inválido para pesquisa. Use 1/2/3.");
            return;
            
        }

        ultimosResultados = livros.stream()
                .filter(l -> nome.isEmpty()   || l.getNome().toLowerCase().contains(nome))
                .filter(l -> isbn.isEmpty()   || l.getISBN().toLowerCase().contains(isbn))
                .filter(l -> ano.isEmpty()    || l.getAno().toLowerCase().contains(ano))
                .filter(l -> autor.isEmpty()  || l.getAutor().toLowerCase().contains(autor))
                .filter(l -> genero.isEmpty() || l.getGenero().toLowerCase().contains(genero))
                .filter(l -> filtroStatus == null || l.getRetirada_Entrega() == filtroStatus)
                .collect(Collectors.toList());

        if (ultimosResultados.isEmpty()) 
        {
            
            System.out.println("Nenhum livro encontrado.");
            
        } 
        
        else 
        {
            
            System.out.println("Resultados (" + ultimosResultados.size() + "):");
            
            ultimosResultados.forEach(l -> System.out.println(" - " + l.resumo()));
            
            System.out.println("Dica: para ALTERAR/EXCLUIR, refine a pesquisa até restar 1 item.");
            
        }
        
    }

    @FXML
    private void alterar(ActionEvent e) 
    {
        
        if (ultimosResultados.size() != 1) 
        {
            
            println("Para ALTERAR, a pesquisa deve retornar exatamente 1 livro.");
            return;
            
        }

        Livros alvo = ultimosResultados.get(0);


        if (!vazio(nomeCampo))   
        {
            
            alvo.setNome(nomeCampo.getText().trim());
            
        }
        
        if (!vazio(anoCampo))    
        {
            
            alvo.setAno(anoCampo.getText().trim());
            
        }
        
        if (!vazio(autorCampo))  
        {
            
            alvo.setAutor(autorCampo.getText().trim());
            
        }
        
        if (!vazio(generoCampo)) 
        {
            
            alvo.setGenero(generoCampo.getText().trim());
            
        }

        String s = safeTrim(retirada_entregaCampo);
        if (!s.isEmpty()) 
        {
            
            Status st = Status.fromCodigo(s);
            
            if (st == null) 
            {
                
                println("Status inválido (use 1/2/3). Alteração de status ignorada.");
                
            } 
            
            else 
            {
                
                alvo.setRetirada_Entrega(st);
                
            }
            
        }

        println("✏️ Alterado: " + alvo.resumo());
        
    }

    
    @FXML
    private void excluir(ActionEvent e) 
    {
        
        if (ultimosResultados.size() != 1) 
        {
            
            println("Para EXCLUIR, a pesquisa deve retornar exatamente 1 livro.");
            return;
            
        }

        Livros alvo = ultimosResultados.get(0);
        String chave = alvo.getISBN();

        boolean removido = livros.removeIf(l -> l.getISBN().equalsIgnoreCase(chave));
        
        if (removido) 
        {
            
            println("🗑️ Excluído ISBN " + chave);
            ultimosResultados = new ArrayList<>();
            
        } 
        
        else 
        {
            
            println("Falha ao excluir: ISBN não encontrado.");
            
        }
        
    }

    @FXML private void voltar(ActionEvent e) 
    {
        
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
        
    }

    private List<String> obrigatoriosFaltando() 
    {
        
        List<String> faltando = new ArrayList<>();
        
        if (vazio(nomeCampo))   
        {
            
            faltando.add("Nome");
            
        }
        
        if (vazio(isbnCampo))   
        {
            
            faltando.add("ISBN");
            
        }
        
        if (vazio(anoCampo))    
        {
            
            faltando.add("Ano");
            
        }
        
        if (vazio(autorCampo))  
        {
            
            faltando.add("Autor");
            
        }
        
        if (vazio(generoCampo)) 
        {
            
            faltando.add("Gênero");
            
        }

        String s = safeTrim(retirada_entregaCampo);
        if (s.isEmpty()) faltando.add("Status (1=Retirado, 2=Estoque, 3=Atrasado)");
        return faltando;
        
    }

    private Status obterStatusDaUI() 
    {
        
        return Status.fromCodigo(safeTrim(retirada_entregaCampo));
        
    }

    private boolean vazio(TextField tf) 
    {
        
        return tf == null || tf.getText() == null || tf.getText().trim().isEmpty();
        
    }

    private String safeLower(TextField tf) 
    {
        
        return (tf == null || tf.getText() == null) ? "" : tf.getText().trim().toLowerCase();
        
    }

    private String safeTrim(TextField tf) 
    {
        
        return (tf == null || tf.getText() == null) ? "" : tf.getText().trim();
        
    }

    private void limparCampos() 
    {
        
        if (nomeCampo != null)   
        {
            
            nomeCampo.clear();
            
        }
        
        if (isbnCampo != null)   
        {
            
            isbnCampo.clear();
            
        }
        
        if (anoCampo != null)    
        {
            
            anoCampo.clear();
            
        }
        
        if (autorCampo != null)  
        {
            
            autorCampo.clear();
            
        }
        
        if (generoCampo != null) 
        {
            
            generoCampo.clear();
            
        }
        
        if (retirada_entregaCampo != null) 
        {
            
            retirada_entregaCampo.clear();
            
        }
        
    }

 
    
}
