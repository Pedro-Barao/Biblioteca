package biblioteca;

public enum Status {
    RETIRADO("1", "Retirado"),
    ESTOQUE("2", "Em Estoque"),
    ATRASADO("3", "Atrasado");

    private final String codigo;
    private final String descricao;

    Status(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }

    public static Status fromCodigo(String codigo) {
        if (codigo == null) return null;
        for (Status s : values()) {
            if (s.codigo.equals(codigo)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}