//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import dao.ComponenteDao;
import dao.ServicoDao;
import model.*;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static ServicoDao servicoDao = new ServicoDao();
    static ComponenteDao componenteDao = new ComponenteDao();

    public static void main(String[] args) {



        int opcaoUtilizador;
        String csvPath = "servicosConcluidos.csv";


        do {

            System.out.println("\n--- GESTÃO DE SERVIÇOS (MYSQL) ---");
            System.out.println("0. Sair do programa");
            System.out.println("1. Registar um novo serviço");
            System.out.println("2. Concluir um serviço");
            System.out.println("3. Exibir todos os serviços");
            System.out.println("4. Remover serviço");
            System.out.println("5. Calcular valor a pagar de um serviço");
            System.out.println("6. Listar serviços por ano");
            System.out.println("7. Ordenar serviços por ordem alfabética do cliente");
            System.out.println("8. Ordenar serviços concluídos por duração");
            System.out.println("9. Estatísticas de duração (Mínima, Máxima e Média)");
            System.out.println("10. Exportar serviços concluídos para CSV");
            System.out.println("11. Editar/Adicionar Componentes");
            System.out.println("12. Editar preços");
            System.out.print("Escolha uma opção: ");

            opcaoUtilizador = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoUtilizador) {

                case 1:
                    Servico novoServico = criarNovoServico();
                    if (novoServico != null) {
                        int idGerado = servicoDao.salvar(novoServico);
                        System.out.println("✅ Serviço #" + idGerado + " gravado na BD com sucesso!");
                    }

                    break;
                case 2:
                    System.out.print("Introduza o ID do serviço a concluir: ");
                    int idConcluir = scanner.nextInt();
                    scanner.nextLine();

                    Servico servico = servicoDao.buscarPorId(idConcluir);

                    if(servico == null){
                        System.out.println("❌ Serviço com ID #" + idConcluir + " não encontrado.");
                        break;
                    }

                    if (servico.isConcluido()) {
                        System.out.println("⚠️ Este serviço já se encontra concluído!");
                        break;
                    }

                    System.out.println("Data de fim do serviço:");
                    LocalDateTime dataFim = lerData();


                    if (!dataFim.isAfter(servico.getDataInicio())) {
                        System.out.println("❌ Erro: A data de fim (" + dataFim + ") tem de ser posterior à data de início (" + servico.getDataInicio() + ")!");
                        break;
                    }

                    servicoDao.concluirServico(idConcluir, Timestamp.valueOf(dataFim));

                    break;
                case 3:
                    List<Servico> todos = servicoDao.listarTodos();
                    if (todos.isEmpty()) {
                        System.out.println("⚠️ Nenhum serviço encontrado na base de dados.");
                    } else {
                        for (Servico s : todos) {
                            System.out.println(s);
                            System.out.printf("  Preço base: %.2f € | Cliente: %s | Pago: %s\n",
                                    s.getPreco(), s.getCliente(), s.isPago() ? "Sim" : "Não");
                        }
                    }


                    break;
                case 4:
                    System.out.print("Digite o ID do serviço que deseja remover: ");
                    int idRemover = scanner.nextInt();
                    scanner.nextLine();
                    servicoDao.remover(idRemover);

                    break;
                case 5:
                    System.out.print("Introduza o ID do serviço: ");
                    int idValor = scanner.nextInt();
                    scanner.nextLine();

                    Servico s = servicoDao.buscarPorId(idValor);

                    if(s == null){
                        System.out.println("Serviço não encontrado!");
                        break;
                    }

                    if(!(s.isConcluido())){
                        System.out.println("O serviço tem de estar concluido!");
                        break;
                    }

                    double valorPagar = s.calcularValorPagar();
                    double totalComp =  componenteDao.somarPrecoComponentes(idValor);
                    System.out.printf("Horas trabalhadas: %.2fh | Valor total: %.2f\n", s.getDuracaoEmHoras() , valorPagar);
                    System.out.printf("Total de componentes: %.2f\n" , totalComp);

                    break;
                case 6:
                    System.out.print("Introduza o ano de início: ");
                    int ano = scanner.nextInt();
                    scanner.nextLine();
                    servicoDao.listarTodos().stream()
                            .filter(ser -> ser.getDataInicio().getYear() == ano)
                            .forEach(System.out::println);
                    break;
                case 7:
                    List <Servico> porCliente = servicoDao.consultarViewOrdenada();

                    System.out.println("\n--- SERVIÇOS ORDENADOS POR CLIENTE (SQL) ---");
                    for(Servico ser : porCliente){
                        System.out.println("Cliente: " + ser.getCliente() + " | " + ser);
                    }
                    break;
                case 8:
                    List <Servico> porDuracao = servicoDao.consultarViewOrdenadaPorDuracao();
                    System.out.println("\n--- SERVIÇOS CONCLUÍDOS POR DURAÇÃO (SQL) ---");
                    for (Servico ser : porDuracao) {
                        System.out.println("Cliente: " + ser.getCliente() + " | Duração: " + ser.getDuracaoFormatada());
                    }

                    break;
                case 9:
                    exibirEstatisticasDuracao(servicoDao.listarTodos());
                    break;
                case 10:
                    exportarCsv(servicoDao.listarTodos(), csvPath);
                    break;
                case 11:

                    System.out.println("Introduza o id do serviço: ");
                    int idServico = scanner.nextInt();
                    scanner.nextLine();

                    Servico  buscaServico = servicoDao.buscarPorId(idServico);
                    if(buscaServico == null){
                        System.out.println("❌ Serviço não encontrado!");
                        break;
                    }

                    if (buscaServico.isConcluido()) {
                        System.out.println("❌ Não é possível editar componentes de um serviço concluído!");
                        break;
                    }

                    if(buscaServico instanceof Manutencao || buscaServico instanceof Reparacao){

                        System.out.print("Nome do componente: ");
                        String nomeComp = scanner.nextLine();
                        System.out.print("Preco do componente: ");
                        double precoComp = scanner.nextDouble();
                        scanner.nextLine();

                        buscaServico.adicionarComponentes(nomeComp , precoComp);
                        componenteDao.adicionarComponente(buscaServico.getCodigoServico() ,
                                new Componente(nomeComp , precoComp));

                    }

                    else{
                        buscaServico.adicionarComponentes("", 0.0);
                    }

                    break;
                case 12:
                    System.out.println("\n--- ATUALIZAR PREÇO ---");
                    System.out.println("1. Atualizar preço de um serviço específico (por ID)");
                    System.out.println("2. Atualizar tabela de preços por tipo de serviço");
                    System.out.print("Escolha a opção: ");
                    int opcaoPreco = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcaoPreco){
                        case 1:
                            System.out.print("Introduza o ID do serviço: ");
                            int idServicoAux = scanner.nextInt();
                            System.out.print("Introduza o novo preço (€): ");
                            double novoPreco = scanner.nextDouble();
                            scanner.nextLine();

                            boolean atualizado = servicoDao.atualizarPrecoPorId(idServicoAux , novoPreco);

                            if(atualizado){
                                System.out.println("✅ Preço do serviço #" + idServicoAux + " atualizado para " + novoPreco + " € com sucesso!");
                                break;
                            }

                            else{
                                System.out.println("❌ Não foi possível atualizar. Verifique se o ID existe ou se o serviço já está concluído.");
                                break;
                            }
                        case 2:
                            System.out.print("Introduza o tipo de serviço (Manutencao, Reparacao, Diagnostico, Limpeza): ");
                            String tipoServico = scanner.nextLine();
                            System.out.print("Introduza o novo preço tabelado (€): ");
                            double novoPrecoTipo = scanner.nextDouble();
                            scanner.nextLine();

                            int totalAlterados = servicoDao.atualizarPrecoPorTipo(tipoServico , novoPrecoTipo);

                            if(totalAlterados > 0){
                                System.out.println("✅ Foram atualizados " + totalAlterados + " serviço(s) do tipo '" + tipoServico + "' para " + novoPrecoTipo + " €!");
                                break;
                            }

                            System.out.println("⚠️ Nenhum serviço em aberto do tipo '" + tipoServico + "' foi encontrado para atualizar.");

                            break;
                        default:
                            System.out.println("Opção Inválida!");
                            break;

                    }
                    break;

                default:
                    System.out.println("Opção inválida!");
                    break;


            }


        } while (opcaoUtilizador != 0);


    }

    public static Servico criarNovoServico() {
        System.out.println("Escolha o tipo de serviço:");
        System.out.println("1. Manutenção | 2. Reparação | 3. Diagnóstico | 4. Limpeza");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nome do cliente: ");
        String cliente = scanner.nextLine();
        System.out.print("Nome do funcionário: ");
        String funcionario = scanner.nextLine();
        System.out.print("Nome do equipamento: ");
        String equipamento = scanner.nextLine();

        System.out.println("Data de Início do Serviço:");
        LocalDateTime dataInicio = lerData();

        return switch (opcao) {
            case 1 -> new Manutencao("Manutencao", dataInicio, cliente, funcionario, equipamento);
            case 2 -> new Reparacao("Reparacao", dataInicio, cliente, funcionario, equipamento);
            case 3 -> new Diagnostico("Diagnostico", dataInicio, cliente, funcionario, equipamento);
            case 4 -> new Limpeza("Limpeza", dataInicio, cliente, funcionario, equipamento);
            default -> null;
        };

    }

    public static LocalDateTime lerData() {
        System.out.println("Introduza a data em numeros: ");
        System.out.print("Ano: ");
        int ano = scanner.nextInt();
        System.out.print("Mês: ");
        int mes = scanner.nextInt();
        System.out.print("Dia: ");
        int dia = scanner.nextInt();
        System.out.print("Hora: ");
        int hora = scanner.nextInt();
        System.out.print("Minuto: ");
        int minuto = scanner.nextInt();

        scanner.nextLine();

        return LocalDateTime.of(ano, mes, dia, hora, minuto);
    }

    public static void exibirEstatisticasDuracao(List<Servico> lista) {
        List<Servico> concluidos = lista.stream().filter(Servico::isConcluido).toList();
        if (concluidos.isEmpty()) {
            System.out.println("⚠️ Nenhum serviço concluído registado para calcular médias.");
            return;
        }

        double min = concluidos.stream().mapToDouble(Servico::getDuracaoEmHoras).min().orElse(0.0);
        double max = concluidos.stream().mapToDouble(Servico::getDuracaoEmHoras).max().orElse(0.0);
        double media = concluidos.stream().mapToDouble(Servico::getDuracaoEmHoras).average().orElse(0.0);

        System.out.printf("\nDuração Mínima: %.2f horas\n", min);
        System.out.printf("Duração Máxima: %.2f horas\n", max);
        System.out.printf("Duração Média:  %.2f horas\n", media);
    }

    public static void exportarCsv(List<Servico> lista, String filePath) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.US);
        File file = new File(filePath);
        boolean existe = file.exists();

        try (FileWriter writer = new FileWriter(filePath, true)) {
            if (!existe) {
                writer.write("Código;Tipo;Cliente;Início;Fim\n");
            }
            for (Servico s : lista) {
                if (s.isConcluido() && s.getDataFim() != null) {
                    writer.write(s.getCodigoServico() + ";" +
                            s.getTipoServico() + ";" +
                            s.getCliente() + ";" +
                            s.getDataInicio().format(formatter) + ";" +
                            s.getDataFim().format(formatter) + "\n");
                }
            }
            System.out.println("✅ Serviços concluídos exportados para " + filePath);
        } catch (IOException e) {
            System.err.println("Erro ao exportar CSV: " + e.getMessage());
        }
    }
}




