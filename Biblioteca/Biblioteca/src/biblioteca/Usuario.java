package biblioteca;

public class Usuario {
    private String id;
    private String nome;
    private String telefone;
    private String endereco;

    private transient String statusEmprestimo;
    private transient String livroEmprestadoId;

    public Usuario(String id, String nome, String telefone, String endereco) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.statusEmprestimo = "Nenhum livro emprestado";
        this.livroEmprestadoId = "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getStatusEmprestimo() { return statusEmprestimo; }
    public void setStatusEmprestimo(String statusEmprestimo) { this.statusEmprestimo = statusEmprestimo; }
    public String getLivroEmprestadoId() { return livroEmprestadoId; }
    public void setLivroEmprestadoId(String livroEmprestadoId) { this.livroEmprestadoId = livroEmprestadoId; }

    public String resumo() {
        return String.format("ID: %s, Nome: %s, Telefone: %s", id, nome, telefone);
    }
}