package biblioteca;

public class Livros {
    private String isbn;
    private String nome;
    private String ano;
    private String autor;
    private String genero;
    private Status retirada_Entrega;
    private boolean emEstoque;

    public Livros(String isbn, String nome, String ano, String autor, String genero, Status retirada_Entrega) {
        this.isbn = isbn;
        this.nome = nome;
        this.ano = ano;
        this.autor = autor;
        this.genero = genero;
        this.retirada_Entrega = retirada_Entrega;
        // CORREÇÃO: Inicializa emEstoque baseado no Status
        this.emEstoque = retirada_Entrega == Status.ESTOQUE;
    }

    public String getISBN() { return isbn; }
    public void setISBN(String isbn) { this.isbn = isbn; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getAno() { return ano; }
    public void setAno(String ano) { this.ano = ano; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Status getRetirada_Entrega() { return retirada_Entrega; }
    public void setRetirada_Entrega(Status retirada_Entrega) { this.retirada_Entrega = retirada_Entrega; }

    public boolean getEmEstoque() { return emEstoque; }
    public void setEmEstoque(boolean emEstoque) { this.emEstoque = emEstoque; }

    public String resumo() {
        return String.format("ISBN: %s, Nome: %s, Autor: %s, Status: %s", isbn, nome, autor, retirada_Entrega.getDescricao());
    }

    @Override
    public String toString() {
        return retirada_Entrega != null ? retirada_Entrega.getDescricao() : "";
    }
}