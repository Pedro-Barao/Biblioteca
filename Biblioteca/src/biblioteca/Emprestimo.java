package biblioteca;

import java.time.LocalDate;

public class Emprestimo {

    private String cpfUsuario;
    private String nomeUsuario;
    private String isbnLivro;
    private LocalDate dataRetirada;
    private LocalDate dataDevolucao;
    private String status; // "Ativo", "Atrasado", "Devolvido"

    public Emprestimo(String cpfUsuario, String nomeUsuario, String isbnLivro, LocalDate dataRetirada, LocalDate dataDevolucao, String status) {
        this.cpfUsuario = cpfUsuario;
        this.nomeUsuario = nomeUsuario;
        this.isbnLivro = isbnLivro;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
    }

    // Getters e Setters com os nomes corretos
    public String getCpfUsuario() { return cpfUsuario; }
    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getIsbnLivro() { return isbnLivro; }
    public void setIsbnLivro(String isbnLivro) { this.isbnLivro = isbnLivro; }

    public LocalDate getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDate dataRetirada) { this.dataRetirada = dataRetirada; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}