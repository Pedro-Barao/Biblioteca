package biblioteca;

import java.time.LocalDate;

public class Emprestimo {
    private String usuarioId;
    private String livroIsbn;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;

    public Emprestimo(String usuarioId, String livroIsbn, LocalDate dataEmprestimo, LocalDate dataDevolucao) {
        this.usuarioId = usuarioId;
        this.livroIsbn = livroIsbn;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
    }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getLivroIsbn() { return livroIsbn; }
    public void setLivroIsbn(String livroIsbn) { this.livroIsbn = livroIsbn; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public boolean isAtrasado() {
        return LocalDate.now().isAfter(this.dataDevolucao);
    }
}