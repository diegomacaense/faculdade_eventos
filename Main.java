import model.*;
import service.*;
import util.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static boolean validarEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static boolean validarTelefone(String telefone) {
        return PHONE_PATTERN.matcher(telefone.replaceAll("[^0-9]", "")).matches();
    }

    private static boolean validarData(String data) {
        if (!DATE_PATTERN.matcher(data).matches()) {
            return false;
        }
        try {
            LocalDate.parse(data, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean validarHora(String hora) {
        return TIME_PATTERN.matcher(hora).matches();
    }

    private static LocalDateTime converterDataHora(String data, String hora) {
        LocalDate dataEvento = LocalDate.parse(data, DATE_FORMATTER);
        LocalTime horaEvento = LocalTime.parse(hora, TIME_FORMATTER);
        return LocalDateTime.of(dataEvento, horaEvento);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UsuarioService usuarioService = new UsuarioService();
        EventoService eventoService = new EventoService();
        FileManager fileManager = new FileManager("events.data", "users.data");

        EventoService eventosCarregados = fileManager.carregarEventos();
        eventoService.setEventos(eventosCarregados.getEventos());
        
        UsuarioService usuariosCarregados = fileManager.carregarUsuarios();
        usuarioService = usuariosCarregados;

        Usuario usuarioAtual = null;
        System.out.println("Bem-vindo ao Sistema de Eventos!");

        while (usuarioAtual == null) {
            try {
                System.out.println("\n1. Login");
                System.out.println("2. Cadastrar novo usuário");
                System.out.print("Escolha uma opção: ");
                int opcaoLogin = Integer.parseInt(scanner.nextLine());

                if (opcaoLogin == 1) {
                    System.out.print("Digite seu e-mail: ");
                    String email = scanner.nextLine().trim();
                    usuarioAtual = usuarioService.buscarPorEmail(email);
                    if (usuarioAtual == null) {
                        System.out.println("Usuário não encontrado!");
                    }
                } else if (opcaoLogin == 2) {
                    System.out.print("Digite seu nome: ");
                    String nome = scanner.nextLine().trim();
                    if (nome.isEmpty()) {
                        System.out.println("Nome não pode estar vazio!");
                        continue;
                    }

                    System.out.print("Digite seu e-mail: ");
                    String email = scanner.nextLine().trim();
                    if (!validarEmail(email)) {
                        System.out.println("Email inválido!");
                        continue;
                    }

                    System.out.print("Digite seu telefone: ");
                    String telefone = scanner.nextLine().trim();
                    if (!validarTelefone(telefone)) {
                        System.out.println("Telefone inválido! Use apenas números (10 ou 11 dígitos)");
                        continue;
                    }

                    usuarioAtual = new Usuario(nome, email, telefone);
                    usuarioService.adicionarUsuario(usuarioAtual);
                    fileManager.salvarUsuarios(usuarioService);
                    System.out.println("Usuário cadastrado com sucesso!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida! Digite um número.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }

        int opcao = -1;
        while (opcao != 0) {
            try {
                System.out.println("\n1. Cadastrar evento");
                System.out.println("2. Listar eventos");
                System.out.println("3. Participar de evento");
                System.out.println("4. Cancelar participação");
                System.out.println("5. Meus eventos");
                System.out.println("6. Eventos em andamento");
                System.out.println("7. Eventos passados");
                System.out.println("8. Buscar eventos");
                System.out.println("9. Verificar notificações");
                System.out.println("10. Gerar relatórios");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        try {
                            System.out.print("Nome do evento: ");
                            String nome = scanner.nextLine().trim();
                            if (nome.isEmpty()) {
                                System.out.println("Nome do evento não pode estar vazio!");
                                break;
                            }

                            System.out.print("Endereço: ");
                            String endereco = scanner.nextLine().trim();
                            if (endereco.isEmpty()) {
                                System.out.println("Endereço não pode estar vazio!");
                                break;
                            }

                            System.out.print("Categoria (Festa, Show, Esporte, Outro): ");
                            Categoria categoria = Categoria.valueOf(scanner.nextLine().toUpperCase());

                            System.out.print("Descrição: ");
                            String descricao = scanner.nextLine().trim();
                            if (descricao.isEmpty()) {
                                System.out.println("Descrição não pode estar vazia!");
                                break;
                            }

                            String data;
                            do {
                                System.out.print("Data do evento (dd/mm/aaaa): ");
                                data = scanner.nextLine().trim();
                                if (!validarData(data)) {
                                    System.out.println("Data inválida! Use o formato dd/mm/aaaa");
                                }
                            } while (!validarData(data));

                            String hora;
                            do {
                                System.out.print("Horário do evento (HH:mm): ");
                                hora = scanner.nextLine().trim();
                                if (!validarHora(hora)) {
                                    System.out.println("Horário inválido! Use o formato HH:mm (24 horas)");
                                }
                            } while (!validarHora(hora));

                            LocalDateTime horario = converterDataHora(data, hora);

                            if (horario.isBefore(LocalDateTime.now())) {
                                System.out.println("Não é possível criar eventos no passado!");
                                break;
                            }

                            Evento evento = new Evento(nome, endereco, categoria, horario, descricao);
                            eventoService.adicionarEvento(evento);
                            fileManager.salvarEventos(eventoService);
                            System.out.println("Evento cadastrado!");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Categoria inválida! Use: Festa, Show, Esporte ou Outro");
                        } catch (Exception e) {
                            System.out.println("Erro ao cadastrar evento: " + e.getMessage());
                        }
                        break;
                    case 2:
                        eventoService.listarEventosOrdenados();
                        break;
                    case 3:
                        System.out.print("Digite o nome do evento para participar: ");
                        String nomeEvento = scanner.nextLine();
                        boolean achou = false;
                        for (Evento ev : eventoService.getEventos()) {
                            if (ev.getNome().equalsIgnoreCase(nomeEvento)) {
                                usuarioAtual.participarEvento(ev);
                                ev.adicionarParticipante(usuarioAtual);
                                fileManager.salvarEventos(eventoService);
                                fileManager.salvarUsuarios(usuarioService);
                                System.out.println("Participação confirmada!");
                                achou = true;
                                break;
                            }
                        }
                        if (!achou) System.out.println("Evento não encontrado.");
                        break;
                    case 4:
                        System.out.print("Digite o nome do evento para cancelar: ");
                        String nomeCancel = scanner.nextLine();
                        boolean encontrado = false;
                        for (Evento ev : eventoService.getEventos()) {
                            if (ev.getNome().equalsIgnoreCase(nomeCancel)) {
                                usuarioAtual.cancelarEvento(ev);
                                ev.removerParticipante(usuarioAtual);
                                fileManager.salvarEventos(eventoService);
                                fileManager.salvarUsuarios(usuarioService);
                                System.out.println("Participação cancelada!");
                                encontrado = true;
                                break;
                            }
                        }
                        if (!encontrado) System.out.println("Evento não encontrado.");
                        break;
                    case 5:
                        usuarioAtual.listarEventosParticipando();
                        break;
                    case 6:
                        eventoService.listarEventosEmAndamento();
                        break;
                    case 7:
                        eventoService.listarEventosPassados();
                        break;
                    case 8:
                        System.out.println("\n1. Buscar por nome");
                        System.out.println("2. Buscar por categoria");
                        System.out.println("3. Buscar por data");
                        System.out.print("Escolha uma opção: ");
                        int opcaoBusca = Integer.parseInt(scanner.nextLine());
                        
                        switch (opcaoBusca) {
                            case 1:
                                System.out.print("Digite o nome do evento: ");
                                String nomeBusca = scanner.nextLine();
                                List<Evento> eventosNome = eventoService.buscarPorNome(nomeBusca);
                                if (eventosNome.isEmpty()) {
                                    System.out.println("Nenhum evento encontrado com esse nome.");
                                } else {
                                    System.out.println("\nEventos encontrados:");
                                    eventosNome.forEach(System.out::println);
                                }
                                break;
                            case 2:
                                System.out.print("Digite a categoria (Festa, Show, Esporte, Outro): ");
                                Categoria categoriaBusca = Categoria.valueOf(scanner.nextLine().toUpperCase());
                                List<Evento> eventosCategoria = eventoService.buscarPorCategoria(categoriaBusca);
                                if (eventosCategoria.isEmpty()) {
                                    System.out.println("Nenhum evento encontrado nessa categoria.");
                                } else {
                                    System.out.println("\nEventos encontrados:");
                                    eventosCategoria.forEach(System.out::println);
                                }
                                break;
                            case 3:
                                String dataBusca;
                                do {
                                    System.out.print("Digite a data (dd/mm/aaaa): ");
                                    dataBusca = scanner.nextLine().trim();
                                    if (!validarData(dataBusca)) {
                                        System.out.println("Data inválida! Use o formato dd/mm/aaaa");
                                    }
                                } while (!validarData(dataBusca));
                                
                                LocalDateTime dataBuscaConvertida = converterDataHora(dataBusca, "00:00");
                                List<Evento> eventosData = eventoService.buscarPorData(dataBuscaConvertida);
                                if (eventosData.isEmpty()) {
                                    System.out.println("Nenhum evento encontrado nessa data.");
                                } else {
                                    System.out.println("\nEventos encontrados:");
                                    eventosData.forEach(System.out::println);
                                }
                                break;
                            default:
                                System.out.println("Opção inválida!");
                        }
                        break;
                    case 9:
                        eventoService.verificarNotificacoes(usuarioAtual);
                        break;
                    case 10:
                        System.out.println("\n1. Relatório de eventos");
                        System.out.println("2. Relatório de participação");
                        System.out.println("3. Relatório de categorias");
                        System.out.print("Escolha uma opção: ");
                        int opcaoRelatorio = Integer.parseInt(scanner.nextLine());
                        
                        switch (opcaoRelatorio) {
                            case 1:
                                eventoService.gerarRelatorioEventos();
                                break;
                            case 2:
                                usuarioService.gerarRelatorioParticipacao();
                                break;
                            case 3:
                                usuarioService.gerarRelatorioCategorias();
                                break;
                            default:
                                System.out.println("Opção inválida!");
                        }
                        break;
                    case 0:
                        fileManager.salvarEventos(eventoService);
                        fileManager.salvarUsuarios(usuarioService);
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida! Digite um número.");
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }
}