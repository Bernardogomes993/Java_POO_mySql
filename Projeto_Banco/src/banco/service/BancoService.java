package banco.service;

import banco.dao.ClienteDAO;
import banco.dao.ContaDAO;
import banco.database.ConnectionFactory;
import banco.exception.SaldoInsuficienteException;
import banco.exception.ValorInvalidoException;
import banco.model.Cliente;
import banco.model.Conta;
import banco.model.ContaCorrente;
import banco.model.Tributavel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BancoService {

    private final ArrayList<Conta> contas = new ArrayList<>();

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ContaDAO contaDAO = new ContaDAO();

    public void adicionarConta(Conta conta){
        contas.add(conta);
    }

    public void listarTodasAsContas(){
        System.out.println("\n--- RELATORIO DE CONTAS ---");

       try(Connection conn = ConnectionFactory.getConnection()){
           List<Conta> contas = contaDAO.listarTodasContas(conn);

           if(contas.isEmpty()){
               System.out.println("Nenhuma conta registada no sistema.");
               return;
           }
           for (Conta c : contas) {
               c.imprimirExtrato();
               System.out.println("-----------------------------------------");
           }

       }

       catch (SQLException e){
           System.err.println("❌ Erro ao listar contas da base de dados: " + e.getMessage());
       }

    }

    public void consultarConta(int numeroConta){
            try(Connection conn = ConnectionFactory.getConnection()){

                contaDAO.consultarConta(conn , numeroConta);

            }

            catch (SQLException e){

                System.err.println("❌ Erro ao ligar à base de dados para consultar a conta: " + e.getMessage());

            }
    }

    public void depositarValor(int numeroConta , double valor){
            try(Connection conn = ConnectionFactory.getConnection()){
               Conta conta = contaDAO.buscarPorNumero(conn , numeroConta);

               if(conta == null){
                   System.out.println("❌ Erro: Conta não encontrada.");
                   return;
               }


               conta.depositar(valor);

                contaDAO.atualizarSaldo(conn , numeroConta , conta.getSaldo());
                System.out.println("✅ Depósito efetuado com sucesso!");
                System.out.println("Saldo atual: € " + String.format("%.2f", conta.getSaldo()));
            }

            catch (SQLException e){
                System.err.println("❌ Erro ao ligar à base de dados para consultar a conta: " + e.getMessage());
            }

            catch (ValorInvalidoException e){
                System.err.println("❌ Operação Recusada: " + e.getMessage());
            }
    }

    public void levantamento(int numeroConta , double valor){
            try(Connection conn = ConnectionFactory.getConnection()){
                Conta conta = contaDAO.buscarPorNumero(conn , numeroConta);

                conta.levantar(valor);
                contaDAO.atualizarSaldo(conn , numeroConta , conta.getSaldo());
                System.out.println("✅ Levantamento efetuado com sucesso!");
                System.out.println("Saldo atual: € " + String.format("%.2f", conta.getSaldo()));

            }

            catch (SQLException e){
                System.err.println("❌ Erro ao ligar à base de dados para consultar a conta: " + e.getMessage());
            }

            catch (SaldoInsuficienteException e){
                System.err.println("❌ Operação Recusada: " + e.getMessage());
            }
    }

    public double processarTributos(){
        double total = 0.0;

        for(Conta c : contas){
            if(c instanceof Tributavel tributavel){
                total += tributavel.calcularTributo();
            }
        }

        return total;
    }

    public void cadastrarClienteEConta(Cliente cliente , Conta conta){
        Connection conn = null;

        try{
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            int clienteId = clienteDAO.salvarCliente(conn , cliente);
            contaDAO.salvar(conn , conta , clienteId);
            conn.commit();
            System.out.println("Cliente e conta gravados com sucesso");
        }

        catch (SQLException e){
            System.err.println("❌ Erro no processo: " + e.getMessage());

            if(conn != null){
                try{
                    conn.rollback();
                }

                catch(SQLException rollbackEx){
                    rollbackEx.printStackTrace();

                }
            }


        }

        finally {
            if(conn != null){
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void transferirSaldo(int numeroOrigem  , int numeroDestino , double valor)
    throws SaldoInsuficienteException, ValorInvalidoException {

        if(valor <= 0){
            throw new ValorInvalidoException("O valor da transferência tem de ser superior a zero.");
        }

        if (numeroOrigem == numeroDestino) {
            throw new ValorInvalidoException("Não é possível transferir saldo para a própria conta.");
        }

        Connection conn = null;

        try{
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            double saldoOrigem = contaDAO.buscarSaldo(conn , numeroOrigem);

            if (saldoOrigem < valor) {
                throw new SaldoInsuficienteException(saldoOrigem, valor);
            }

            double saldoDestino = contaDAO.buscarSaldo(conn , numeroDestino);

            double novoSaldoOrigem = saldoOrigem - valor;
            double novoSaldoDestino = saldoDestino + valor;

            //Atualizar os saldos na classe Conta nas duas contas de origem
            // e de destino.
            Conta contaOrigem = contaDAO.buscarPorNumero(conn , numeroOrigem);
            Conta contaDestino = contaDAO.buscarPorNumero(conn , numeroDestino);

            if(contaOrigem == null | contaDestino == null){
                System.out.println("A conta de origem ou de destino nao existe");
                return;
            }



            contaDAO.atualizarSaldo(conn , numeroOrigem , novoSaldoOrigem);
            contaDAO.atualizarSaldo(conn , numeroDestino , novoSaldoDestino);
            contaOrigem.transferir(valor , contaDestino);

            conn.commit();
            System.out.printf("✅ Transferência de %.2f € concluída com sucesso da conta %d para a conta %d!%n",
                    valor, numeroOrigem, numeroDestino);

        }

        catch (SQLException | SaldoInsuficienteException e){

            System.err.println("❌ Erro na transferência: " + e.getMessage());

            if(conn != null){
                try{
                    conn.rollback();
                    System.err.println("⚠️ Rollback executado: Nenhuma conta foi alterada.");
                }

                catch (SQLException rollbackEx){
                    rollbackEx.printStackTrace();
                }
            }

            if (e instanceof SaldoInsuficienteException sie) throw sie;

        }

        finally{
            if(conn != null){
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }



    }


}