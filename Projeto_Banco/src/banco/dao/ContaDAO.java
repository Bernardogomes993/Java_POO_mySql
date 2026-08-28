package banco.dao;

import banco.database.ConnectionFactory;
import banco.exception.ValorInvalidoException;
import banco.model.Cliente;
import banco.model.Conta;
import banco.model.ContaCorrente;
import banco.model.ContaPoupanca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {
    public void salvar(Connection conn , Conta conta , int clienteId) throws SQLException {
        String sql = "INSERT INTO contas (numero , agencia , saldo , tipo , cliente_id)" +
                "VALUES(? , ? , ? , ? , ?)";

        try(PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1 , conta.getNumero());
            stmt.setInt(2 , conta.getAgencia());
            stmt.setDouble(3 , conta.getSaldo());
            stmt.setString(4 , (conta instanceof ContaCorrente) ? "CORRENTE" : "POUPANACA" );
            stmt.setInt(5 , clienteId);

            stmt.executeUpdate();
        }



    }

    public double buscarSaldo(Connection conn , int numeroConta) throws SQLException {

        String sql = "SELECT saldo FROM contas WHERE numero = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1 , numeroConta);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return  rs.getDouble("saldo");
                }
                else{
                    throw new SQLException("Conta número " + numeroConta + " não foi encontrada.");
                }
            }
        }

    }

    public void atualizarSaldo(Connection conn , int numeroConta , double novoSaldo) throws SQLException {
        String sql = "UPDATE contas SET saldo = ? WHERE numero = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setDouble(1 , novoSaldo);
            stmt.setInt(2 , numeroConta);

            int linhasAfetadas = stmt.executeUpdate();
            if(linhasAfetadas == 0){
                throw new SQLException("Falha ao atualizar o saldo da conta " + numeroConta);
            }

        }

    }

    public  void consultarConta(Connection conn , int numeroConta){

        String sql = "SELECT c.numero , c.agencia , c.saldo , c.tipo , " +
                "cl.id AS cliente_id , cl.nome AS cliente_nome , cl.nif AS cliente_nif " +
                "FROM contas c " +
                "INNER JOIN clientes cl ON c.cliente_id = cl.id " +
                "WHERE c.numero = ? ";

        try(PreparedStatement stmt = conn.prepareStatement
                (sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1 , numeroConta);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){

                    System.out.println("\n========== DETALHES DA CONTA ==========");
                    System.out.println("Titular: " + rs.getString("cliente_nome"));
                    System.out.println("Cliente Id: " + rs.getInt("cliente_id"));
                    System.out.println("NIF: " + rs.getString("cliente_nif"));
                    System.out.println("Conta: " + rs.getInt("numero") + " | Agência: " + rs.getInt("agencia"));
                    System.out.println("Tipo: " + rs.getString("tipo"));
                    System.out.println("Saldo: € " + String.format("%.2f", rs.getDouble("saldo")));
                    System.out.println("=======================================");
                }

                else {
                    System.out.println("❌ Nenhuma conta encontrada com o número: " + numeroConta);
                }
            }


        }

        catch (SQLException e){

            System.err.println("❌ Erro ao consultar a conta: " + e.getMessage());

        }

    }

    public Conta buscarPorNumero(Connection conn , int numeroConta){
        String sql = "SELECT c.numero , c.agencia , c.saldo , c.tipo, " +
                "cl.id AS cliente_id , cl.nome AS cliente_nome , cl.nif AS cliente_nif " +
                "FROM contas c " +
                "INNER JOIN clientes cl ON c.cliente_id = cl.id " +
                "WHERE c.numero = ? ";
        try(PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1 , numeroConta);

            try(ResultSet rs = stmt.executeQuery()){

                if(rs.next()){
                    Cliente titular = new Cliente(
                            rs.getString("cliente_nome") ,
                            rs.getString("cliente_nif")
                    );

                    int numero = rs.getInt("numero");
                    int agencia = rs.getInt("agencia");
                    double saldo = rs.getDouble("saldo");
                    String tipo = rs.getString("tipo");


                    Conta conta;

                    if("CORRENTE".equalsIgnoreCase(tipo)){
                        conta = new ContaCorrente(agencia , numero , titular);
                    }

                    else{
                        conta = new ContaPoupanca(agencia , numero , titular);
                    }

                    if(saldo > 0){

                        try{
                            conta.depositar(saldo);
                        }

                        catch (ValorInvalidoException e){
                            System.out.println("Valor invalido");
                        }
                    }

                    return conta;

                }

            }

        }
        catch (SQLException e){
            System.out.println("Erro no banco de dados");
        }

        return null;
    }

    public List<Conta> listarTodasContas(Connection conn){
        List <Conta> lista = new ArrayList<>();

        String sql = "SELECT c.numero , c.agencia , c.saldo , c.tipo , " +
                "cl.id AS cliente_id  , cl.nome AS cliente_nome , cl.nif AS cliente_nif " +
                "FROM contas c " +
                "INNER JOIN clientes cl ON c.cliente_id = cl.id";

        try(PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.executeQuery()){

            while (rs.next()){

                Cliente titular = new Cliente(
                        rs.getString("cliente_nome") ,
                        rs.getString("cliente_nif")
                );

                int numero = rs.getInt("numero");
                int agencia = rs.getInt("agencia");
                double saldo = rs.getDouble("saldo");
                String tipo = rs.getString("tipo");

                Conta conta;

                if("CORRENTE".equalsIgnoreCase(tipo)){
                    conta = new ContaCorrente(agencia , numero , titular);
                }

                else{
                    conta = new ContaPoupanca(agencia , numero , titular);
                }

                if(saldo > 0){
                    try{
                        conta.depositar(saldo);
                    } catch (ValorInvalidoException ignored) {

                    }
                }

                lista.add(conta);
            }


        }
        catch (SQLException e){

            System.out.println("Nao e possivel ligar ao banco de dados");

        }

        return lista;
    }
}