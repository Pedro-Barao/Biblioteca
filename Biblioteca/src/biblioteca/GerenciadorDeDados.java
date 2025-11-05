package biblioteca;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorDeDados {

    private static final String ARQUIVO_LIVROS = "livros.json";
    private static final String ARQUIVO_USUARIOS = "usuarios.json";
    private static final String ARQUIVO_EMPRESTIMOS = "emprestimos.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // --- MÉTODOS PARA LIVROS ---
    public static void salvarLivros(List<Livros> livros) {
        try (FileWriter writer = new FileWriter(ARQUIVO_LIVROS)) {
            gson.toJson(livros, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados dos livros: " + e.getMessage());
        }
    }

    public static List<Livros> carregarLivros() {
        try (FileReader reader = new FileReader(ARQUIVO_LIVROS)) {
            Type tipoLista = new TypeToken<ArrayList<Livros>>() {
            }.getType();
            List<Livros> livros = gson.fromJson(reader, tipoLista);
            return livros != null ? livros : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void salvarUsuarios(List<Usuario> usuarios) {
        try (FileWriter writer = new FileWriter(ARQUIVO_USUARIOS)) {
            gson.toJson(usuarios, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados dos usuários: " + e.getMessage());
        }
    }

    public static List<Usuario> carregarUsuarios() {
        try (FileReader reader = new FileReader(ARQUIVO_USUARIOS)) {
            Type tipoLista = new TypeToken<ArrayList<Usuario>>() {
            }.getType();
            List<Usuario> usuarios = gson.fromJson(reader, tipoLista);
            return usuarios != null ? usuarios : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void salvarEmprestimos(List<Emprestimo> emprestimos) {
        try (FileWriter writer = new FileWriter(ARQUIVO_EMPRESTIMOS)) {
            gson.toJson(emprestimos, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados dos empréstimos: " + e.getMessage());
        }
    }

    public static List<Emprestimo> carregarEmprestimos() {
        try (FileReader reader = new FileReader(ARQUIVO_EMPRESTIMOS)) {
            Type tipoLista = new TypeToken<ArrayList<Emprestimo>>() {
            }.getType();
            List<Emprestimo> emprestimos = gson.fromJson(reader, tipoLista);
            return emprestimos != null ? emprestimos : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}