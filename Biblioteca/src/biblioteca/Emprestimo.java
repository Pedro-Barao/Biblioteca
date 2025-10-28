package biblioteca;

import java.time.LocalDate;

public class Emprestimo {
    private String cpf;       
    private String isbn;         
    private LocalDate retirada;   
    private LocalDate devolucao; 
    private boolean devolvido;  

    public Emprestimo() {}

    public Emprestimo(String cpf, String isbn, LocalDate retirada) {
        this.cpf = cpf;
        this.isbn = isbn;
        this.retirada = retirada;
        this.devolucao = retirada.plusDays(14);
        this.devolvido = false;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getRetirada() { return retirada; }
    public void setRetirada(LocalDate retirada) { this.retirada = retirada; }

    public LocalDate getDevolucao() { return devolucao; }
    public void setDevolucao(LocalDate devolucao) { this.devolucao = devolucao; }

    public boolean isDevolvido() { return devolvido; }
    public void setDevolvido(boolean devolvido) { this.devolvido = devolvido; }

    public boolean isAtrasado(LocalDate hoje) {
        return !devolvido && hoje != null && hoje.isAfter(devolucao);
    }
}
