package banco;


import banco.dao.ClienteDAO;
import banco.dao.ContaDAO;
import banco.database.ConnectionFactory;
import banco.exception.SaldoInsuficienteException;
import banco.exception.ValorInvalidoException;
import banco.model.Cliente;
import banco.model.Conta;
import banco.model.ContaCorrente;
import banco.model.ContaPoupanca;
import banco.service.BancoService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static BancoService bancoService = new BancoService();

    public static void main(String[] args) throws ValorInvalidoException {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();

            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
                scanner.nextLine();
                processarOpcao(opcao);
            } else {
                System.out.println("❌ Opção inválida. Digite um número.");
                scanner.nextLine();
            }
        }


    }

    public static void exibirMenu() {
        System.out.println("\n========== BANCO DIGITAL ==========");
        System.out.println("1. Criar Nova Conta");
        System.out.println("2. Consultar Saldo / Detalhes da Conta");
        System.out.println("3. Realizar Depósito");
        System.out.println("4. Realizar Levantamento (Saque)");
        System.out.println("5. Transferência entre Contas");
        System.out.println("6. Ver Histórico de Transações (Extrato)");
        System.out.println("0. Sair");
        System.out.println("===================================");
        System.out.print("Escolha uma opção: ");
    }

    public static void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                criarConta();
                break;
            case 2:
                consultarConta();
                break;
            case 3:
                realizarDeposito();
                break;
            case 4:
                realizarLevantamento();
                break;
            case 5:
                realizarTransferencia();
                break;
            case 6:
                historicoTransacoes();
                break;
            case 0:
                System.out.println("A encerrar o sistema bancário. Até breve!");
                break;

            default:
                System.out.println("❌ Opção inválida.");
                break;

        }
    }

    public static void criarConta() {
        System.out.print("Nome do cliente: ");
        String nomeCliente = scanner.nextLine();

        System.out.print("Nif: ");
        String nifCliente = scanner.nextLine();

        Cliente novoCliente = new Cliente(nomeCliente , nifCliente);

        System.out.print("Numero da conta: ");
        int numero = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Numero da agencia: ");
        int numeroAgencia = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Tipo de Conta (1- Corrente | 2- Poupança): ");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Depósito inicial (€) (0 para nenhum): ");
        double depositoInicial = scanner.nextDouble();
        scanner.nextLine();

        Conta novaConta;

        if(tipo == 1){
            novaConta = new ContaCorrente(numero , numeroAgencia , novoCliente);
        }

        else{
            novaConta = new ContaPoupanca(numero , numeroAgencia , novoCliente);
        }

        if(depositoInicial > 0){

            try{
                novaConta.depositar(depositoInicial);
            }

            catch (ValorInvalidoException e){
                System.out.println("Valor Invalido!");
                return;
            }
        }

        bancoService.cadastrarClienteEConta(novoCliente , novaConta);
        bancoService.adicionarConta(novaConta);


    }

    public static void consultarConta(){
        System.out.print("Introduza o numero da conta: ");
        int numeroConta = scanner.nextInt();
        scanner.nextLine();

        bancoService.consultarConta(numeroConta);
    }


    public static void realizarDeposito(){
        System.out.println("Introduza o numero da conta: ");
        int numeroConta = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Introduza o valor que deseja depositar: ");
        double valorDepositar = scanner.nextDouble();
        scanner.nextLine();

        bancoService.depositarValor(numeroConta , valorDepositar);
    }

    public static void realizarLevantamento(){
        System.out.println("Introduza o numero da conta: ");
        int numeroConta = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Introduza o valor a levantar: ");
        double valorLevantar = scanner.nextDouble();
        scanner.nextLine();

        if(valorLevantar <= 0){
            System.out.println("Nao possivel levantar quantias negativas ou iguais a zero");
            return;
        }

        bancoService.levantamento(numeroConta , valorLevantar);
    }

    public static void realizarTransferencia(){

        System.out.println("Introduza o numero da conta de origem: ");
        int numeroContaOrigem = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Introduza o numero da conta de destino: ");
        int numeroContaDestino = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Introduza a quantia que deseja transferir: ");
        double valorTransferir = scanner.nextDouble();
        scanner.nextLine();

        try{
            bancoService.transferirSaldo(numeroContaOrigem , numeroContaDestino , valorTransferir);
        } catch (ValorInvalidoException e) {

            System.out.println("O valor da transferencia tem de ser superior a zero!");
        } catch (SaldoInsuficienteException e) {

            System.out.println("Saldo insuficiente !");
        }

    }

    public static void  historicoTransacoes(){
        bancoService.listarTodasAsContas();
    }
}

