package service;

import model.Usuario;
import model.Evento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioService implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Usuario> usuarios;

    public UsuarioService() {
        usuarios = new ArrayList<>();
    }

    public void adicionarUsuario(Usuario usuario) {
        if (buscarPorEmail(usuario.getEmail()) == null) {
            usuarios.add(usuario);
        } else {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }
    }

    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarios.stream()
                .filter(u -> u.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Usuario cadastrarUsuario(String nome, String email, String telefone) {
        Usuario user = new Usuario(nome, email, telefone);
        adicionarUsuario(user);
        return user;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void gerarRelatorioParticipacao() {
        System.out.println("\n=== Relatório de Participação em Eventos ===");
        for (Usuario usuario : usuarios) {
            System.out.println("\nUsuário: " + usuario.getNome());
            System.out.println("E-mail: " + usuario.getEmail());
            System.out.println("Total de eventos confirmados: " + usuario.getEventosConfirmados().size());
            
            if (!usuario.getEventosConfirmados().isEmpty()) {
                System.out.println("\nEventos confirmados:");
                usuario.getEventosConfirmados().forEach(evento -> 
                    System.out.println("- " + evento.getNome() + " (" + evento.getCategoria() + ")")
                );
            }
            System.out.println("----------------------------------------");
        }
    }

    public void gerarRelatorioCategorias() {
        System.out.println("\n=== Relatório de Preferências por Categoria ===");
        for (Usuario usuario : usuarios) {
            System.out.println("\nUsuário: " + usuario.getNome());
            usuario.getEventosConfirmados().stream()
                .collect(Collectors.groupingBy(Evento::getCategoria, Collectors.counting()))
                .forEach((categoria, quantidade) -> 
                    System.out.println(categoria + ": " + quantidade + " eventos")
                );
            System.out.println("----------------------------------------");
        }
    }
}