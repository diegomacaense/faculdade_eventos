package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String email;
    private String telefone;
    private List<Evento> eventosConfirmados;

    public Usuario(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.eventosConfirmados = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public List<Evento> getEventosConfirmados() {
        return eventosConfirmados;
    }

    public void participarEvento(Evento evento) {
        if (!eventosConfirmados.contains(evento)) {
            eventosConfirmados.add(evento);
        }
    }

    public void cancelarEvento(Evento evento) {
        eventosConfirmados.remove(evento);
    }

    public void listarEventosParticipando() {
        if (eventosConfirmados.isEmpty()) {
            System.out.println("Você não está participando de nenhum evento.");
            return;
        }
        System.out.println("\nSeus eventos:");
        for (Evento evento : eventosConfirmados) {
            System.out.println(evento);
        }
    }
}