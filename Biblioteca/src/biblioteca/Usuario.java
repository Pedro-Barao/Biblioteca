package biblioteca;

public class Usuario {
    // id = CPF
    private String id;
    private String nome;
    private String telefone;
    private String endereco;

    // Controle de empréstimo
    private boolean retirouLivro = false;        // true se tem empréstimo ativo
    private String livroEmprestadoId = "";       // ISBN do livro atual (se houver)
    private String statusEmprestimo = "Nenhum";  // "Nenhum", "Emprestado", "Livro atrasado"

    public Usuario() {
        // necessário para (de)serialização e ferramentas de UI
    }

    public Usuario(String id, String nome, String telefone, String endereco) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    // --- básicos ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }


    public boolean isRetirouLivro() { return retirouLivro; }
    public boolean getRetirouLivro() { return retirouLivro; } 
    public void setRetirouLivro(boolean retirouLivro) { this.retirouLivro = retirouLivro; }

    public String getLivroEmprestadoId() { return livroEmprestadoId; }
    public void setLivroEmprestadoId(String livroEmprestadoId) {
        this.livroEmprestadoId = livroEmprestadoId == null ? "" : livroEmprestadoId;
    }

    public String getStatusEmprestimo() { return statusEmprestimo; }
    public void setStatusEmprestimo(String statusEmprestimo) {
        this.statusEmprestimo = (statusEmprestimo == null || statusEmprestimo.trim().isEmpty())
                ? "Nenhum" : statusEmprestimo;
    }

    // --- util ---
    public String resumo() {
        return String.format(
            "ID: %s | Nome: %s | Tel: %s | Status: %s%s",
            safe(id), safe(nome), safe(telefone),
            safe(statusEmprestimo),
            livroEmprestadoId == null || livroEmprestadoId.trim().isEmpty() ? "" : " (ISBN: " + livroEmprestadoId + ")"
        );
    }

    private String safe(String s) { return s == null ? "" : s; }
}
