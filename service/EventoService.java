package service;

import model.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventoService implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Evento> eventos;

    public EventoService() {
        eventos = new ArrayList<>();
    }

    public void adicionarEvento(Evento evento) {
        eventos.add(evento);
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<Evento> buscarPorNome(String nome) {
        return eventos.stream()
                .filter(e -> e.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Evento> buscarPorCategoria(Categoria categoria) {
        return eventos.stream()
                .filter(e -> e.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public List<Evento> buscarPorData(LocalDateTime data) {
        return eventos.stream()
                .filter(e -> e.getHorario().toLocalDate().equals(data.toLocalDate()))
                .collect(Collectors.toList());
    }

    public List<Evento> eventosFuturos() {
        return eventos.stream()
                .filter(e -> e.getHorario().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Evento::getHorario))
                .collect(Collectors.toList());
    }

    public List<Evento> eventosOcorrendoAgora() {
        return eventos.stream()
                .filter(e -> e.getHorario().isBefore(LocalDateTime.now().plusHours(1))
                          && e.getHorario().isAfter(LocalDateTime.now().minusHours(1)))
                .collect(Collectors.toList());
    }

    public List<Evento> eventosPassados() {
        return eventos.stream()
                .filter(e -> e.getHorario().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Evento::getHorario).reversed())
                .collect(Collectors.toList());
    }

    public void listarEventosOrdenados() {
        if (eventos.isEmpty()) {
            System.out.println("Não há eventos cadastrados.");
            return;
        }
        System.out.println("\nEventos cadastrados:");
        eventos.stream()
              .sorted(Comparator.comparing(Evento::getHorario))
              .forEach(System.out::println);
    }

    public void listarEventosEmAndamento() {
        List<Evento> emAndamento = eventosOcorrendoAgora();
        if (emAndamento.isEmpty()) {
            System.out.println("Não há eventos em andamento no momento.");
            return;
        }
        System.out.println("\nEventos em andamento:");
        emAndamento.forEach(System.out::println);
    }

    public void listarEventosPassados() {
        List<Evento> passados = eventosPassados();
        if (passados.isEmpty()) {
            System.out.println("Não há eventos passados.");
            return;
        }
        System.out.println("\nEventos passados:");
        passados.forEach(System.out::println);
    }

    public void verificarNotificacoes(Usuario usuario) {
        LocalDateTime agora = LocalDateTime.now();
        List<Evento> eventosProximos = usuario.getEventosConfirmados().stream()
                .filter(e -> e.getHorario().isAfter(agora))
                .sorted(Comparator.comparing(Evento::getHorario))
                .collect(Collectors.toList());

        if (!eventosProximos.isEmpty()) {
            System.out.println("\n=== Notificações de Eventos ===");
            for (Evento evento : eventosProximos) {
                long horasAteEvento = ChronoUnit.HOURS.between(agora, evento.getHorario());
                if (horasAteEvento <= 24) {
                    System.out.println("\nEvento próximo: " + evento.getNome());
                    System.out.println("Horário: " + evento.getHorario());
                    System.out.println("Local: " + evento.getEndereco());
                    if (horasAteEvento < 1) {
                        System.out.println("O evento começa em menos de 1 hora!");
                    } else {
                        System.out.println("O evento começa em " + horasAteEvento + " horas!");
                    }
                }
            }
        }
    }

    public void gerarRelatorioEventos() {
        System.out.println("\n=== Relatório de Eventos ===");
        System.out.println("\nTotal de eventos: " + eventos.size());
        
        System.out.println("\nEventos por categoria:");
        eventos.stream()
              .collect(Collectors.groupingBy(Evento::getCategoria, Collectors.counting()))
              .forEach((categoria, quantidade) -> 
                  System.out.println(categoria + ": " + quantidade + " eventos")
              );

        System.out.println("\nEventos futuros: " + eventosFuturos().size());
        System.out.println("Eventos em andamento: " + eventosOcorrendoAgora().size());
        System.out.println("Eventos passados: " + eventosPassados().size());

        System.out.println("\nEventos com mais participantes:");
        eventos.stream()
              .sorted(Comparator.comparing(e -> e.getParticipantes().size(), Comparator.reverseOrder()))
              .limit(5)
              .forEach(e -> System.out.println(e.getNome() + ": " + e.getParticipantes().size() + " participantes"));
    }
}