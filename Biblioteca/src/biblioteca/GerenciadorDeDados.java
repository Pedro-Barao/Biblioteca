package biblioteca;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerenciador central de leitura/gravação e regras de negócio da Biblioteca.
 * - Mantém listas de instância (usuarios, livros, emprestimos)
 * - Disponibiliza helpers estáticos (compatíveis com código legado)
 * - Expõe operações de empréstimo e devolução
 */
public class GerenciadorDeDados {

    // ---- Arquivos JSON -------------------------------------------------------
    private static final String USUARIOS_JSON     = "dados/usuarios.json";
    private static final String LIVROS_JSON       = "dados/livros.json";
    private static final String EMPRESTIMOS_JSON  = "dados/emprestimos.json";

    // ---- Gson (com LocalDate) -----------------------------------------------
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    // ---- Estado de instância -------------------------------------------------
    private final List<Usuario> usuarios       = new ArrayList<>();
    private final List<Livros> livros          = new ArrayList<>();
    private final List<Emprestimo> emprestimos = new ArrayList<>();

    // Construtor: já carrega tudo do disco
    public GerenciadorDeDados() {
        carregarTudo();
    }

    // ---- Utilitários de IO genéricos ----------------------------------------
    private static <T> List<T> lerLista(String caminho, Type tipo) {
        File f = new File(caminho);
        if (!f.exists()) return new ArrayList<>();
        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            List<T> lidos = GSON.fromJson(r, tipo);
            return (lidos != null) ? lidos : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static <T> void escreverLista(String caminho, List<T> lista) {
        try {
            File f = new File(caminho);
            File parent = f.getParentFile();
            if (parent != null) parent.mkdirs();
            try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
                GSON.toJson(lista, w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---- Load/Save ESTÁTICOS (compatíveis com controllers legados) ----------
    public static List<Usuario> carregarUsuarios() {
        return lerLista(USUARIOS_JSON, new TypeToken<List<Usuario>>() {}.getType());
    }
    public static void salvarUsuarios(List<Usuario> lista) {
        escreverLista(USUARIOS_JSON, lista);
    }

    public static List<Livros> carregarLivros() {
        return lerLista(LIVROS_JSON, new TypeToken<List<Livros>>() {}.getType());
    }
    public static void salvarLivros(List<Livros> lista) {
        escreverLista(LIVROS_JSON, lista);
    }

    public static List<Emprestimo> carregarEmprestimos() {
        return lerLista(EMPRESTIMOS_JSON, new TypeToken<List<Emprestimo>>() {}.getType());
    }
    public static void salvarEmprestimos(List<Emprestimo> lista) {
        escreverLista(EMPRESTIMOS_JSON, lista);
    }

    // ---- Load/Save de INSTÂNCIA (listas internas) ---------------------------
    public void carregarTudo() {
        usuarios.clear();
        usuarios.addAll(carregarUsuarios());

        livros.clear();
        livros.addAll(carregarLivros());

        emprestimos.clear();
        emprestimos.addAll(carregarEmprestimos());
    }

    public void salvarTudo() {
        salvarUsuarios(usuarios);
        salvarLivros(livros);
        salvarEmprestimos(emprestimos);
    }

    // ---- Acesso seguro às listas (cópias) -----------------------------------
    public List<Usuario> listarUsuarios()    { return new ArrayList<>(usuarios); }
    public List<Livros> listarLivros()       { return new ArrayList<>(livros); }
    public List<Emprestimo> listarEmprestimos() { return new ArrayList<>(emprestimos); }

    // ---- Find helpers --------------------------------------------------------
    /** Seu "id" de Usuario é o CPF. */
    public Usuario findUsuarioByCpf(String cpf) {
        if (cpf == null) return null;
        String key = cpf.trim();
        for (Usuario u : usuarios) {
            String id = (u.getId() == null) ? "" : u.getId().trim();
            if (key.equals(id)) return u;
        }
        return null;
    }

    public Livros findLivroByIsbn(String isbn) {
        if (isbn == null) return null;
        String key = isbn.trim();
        for (Livros l : livros) {
            String id = (l.getISBN() == null) ? "" : l.getISBN().trim();
            if (key.equals(id)) return l;
        }
        return null;
    }

    public Emprestimo findEmprestimoAtivo(String cpf, String isbn) {
        String c = (cpf  == null) ? "" : cpf.trim();
        String i = (isbn == null) ? "" : isbn.trim();
        for (Emprestimo e : emprestimos) {
            if (!e.isDevolvido()
                    && c.equals(e.getCpf())
                    && i.equals(e.getIsbn())) {
                return e;
            }
        }
        return null;
    }

    public Emprestimo registrarEmprestimo(String cpf, String isbn, LocalDate retirada) {
        Usuario u = findUsuarioByCpf(cpf);
        if (u == null) throw new IllegalArgumentException("Usuário (CPF) não encontrado.");

        Livros l = findLivroByIsbn(isbn);
        if (l == null) throw new IllegalArgumentException("Livro (ISBN) não encontrado.");

        if (!l.getEmEstoque()) throw new IllegalStateException("Livro não está em estoque.");
        if (u.isRetirouLivro()) throw new IllegalStateException("Usuário já está com empréstimo ativo.");

        Emprestimo emp = new Emprestimo(cpf.trim(), isbn.trim(), retirada);
        emprestimos.add(emp);

        l.setEmEstoque(false);
        l.setRetirada_Entrega(Status.RETIRADO);

        u.setRetirouLivro(true);
        u.setLivroEmprestadoId(isbn.trim());
        u.setStatusEmprestimo("Emprestado");

        salvarTudo();
        return emp;
    }

    public void registrarDevolucao(String cpf, String isbn) {
        Emprestimo emp = findEmprestimoAtivo(cpf, isbn);
        if (emp == null) throw new IllegalStateException("Não há empréstimo ativo para este CPF/ISBN.");

        emp.setDevolvido(true);

        Livros l = findLivroByIsbn(isbn);
        if (l != null) {
            l.setEmEstoque(true);
            l.setRetirada_Entrega(Status.ESTOQUE);
        }

        Usuario u = findUsuarioByCpf(cpf);
        if (u != null) {
            u.setRetirouLivro(false);
            u.setLivroEmprestadoId("");
            u.setStatusEmprestimo("Nenhum");
        }

        salvarTudo();
    }

    public static void normalizarUsuariosELivros(List<Usuario> usuarios, List<Livros> livros) {
        for (Livros l : livros) {
            if (l.getRetirada_Entrega() != null) {
                boolean deveriaEstarEmEstoque = (l.getRetirada_Entrega() == Status.ESTOQUE);
                if (l.getEmEstoque() != deveriaEstarEmEstoque) {
                    l.setEmEstoque(deveriaEstarEmEstoque);
                }
            }
        }
        for (Usuario u : usuarios) {
            boolean temLivro = u.getLivroEmprestadoId() != null && !u.getLivroEmprestadoId().isEmpty();
            if (temLivro && !u.isRetirouLivro()) {
                u.setRetirouLivro(true);
                if (u.getStatusEmprestimo() == null || u.getStatusEmprestimo().trim().isEmpty()) {
                    u.setStatusEmprestimo("Emprestado");
                }
            }
            if (!temLivro && u.isRetirouLivro()) {
                u.setRetirouLivro(false);
                if (u.getStatusEmprestimo() == null || u.getStatusEmprestimo().trim().isEmpty()) {
                    u.setStatusEmprestimo("Nenhum");
                }
            }
        }
    }
}

