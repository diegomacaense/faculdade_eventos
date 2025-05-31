package util;

import model.Usuario;
import service.EventoService;
import service.UsuarioService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private String eventosFileName;
    private String usuariosFileName;

    public FileManager(String eventosFileName, String usuariosFileName) {
        this.eventosFileName = eventosFileName;
        this.usuariosFileName = usuariosFileName;
    }

    public void salvarEventos(EventoService eventoService) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(eventosFileName))) {
            oos.writeObject(eventoService);
        } catch (IOException e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    public EventoService carregarEventos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(eventosFileName))) {
            return (EventoService) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Arquivo de eventos não encontrado, criando novo.");
            return new EventoService();
        }
    }

    public void salvarUsuarios(UsuarioService usuarioService) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(usuariosFileName))) {
            oos.writeObject(usuarioService);
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    public UsuarioService carregarUsuarios() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usuariosFileName))) {
            return (UsuarioService) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Arquivo de usuários não encontrado, criando novo.");
            return new UsuarioService();
        }
    }
}