package dao;

import com.mysql.cj.protocol.Resultset;
import database.ConnectionFactory;
import model.Componente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComponenteDao {

    public void adicionarComponente(int servicoId , Componente componente){

        String sql = "INSERT INTO componentes (servico_id , nome , preco) " +
                "VALUES (? , ? , ?)";

        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1 , servicoId);
            stmt.setString(2 , componente.getNome());
            stmt.setDouble(3 , componente.getPreco());

            stmt.executeUpdate();

            System.out.println("✅ Componente '" + componente.getNome() + "' adicionado ao Serviço #" + servicoId);
        }

        catch (SQLException e){

            System.err.println("Erro ao adicionar componente: " + e.getMessage());

        }

    }

    public List<Componente> listarPorServico(int servicoId){

        List <Componente> lista = new ArrayList<>();
        String sql = "SELECT * FROM componentes WHERE servico_id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1 , servicoId);

            try(ResultSet rs = stmt.executeQuery()){

                while(rs.next()){
                   int idComponente = rs.getInt("id");
                   int id_servico = rs.getInt("servico_id");
                   String nome = rs.getString("nome");
                   double preco = rs.getDouble("preco");

                   Componente novoComponente = new Componente(idComponente ,
                           id_servico , nome , preco);
                   lista.add(novoComponente);
                }
            }

        }

        catch (SQLException e){
            System.out.println("Erro ao listar componentes: " + e.getMessage());
        }

        return lista;

    }

    public double somarPrecoComponentes(int servicoId){
        String sql = "SELECT SUM(preco) AS total FROM componentes WHERE servico_id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1 , servicoId);

            try(ResultSet rs = stmt.executeQuery()){

                if(rs.next()){
                    return  rs.getDouble("total");
                }
            }
        }

        catch (SQLException e){

            System.out.println("Erro ao calcular total dos componentes: " + e.getMessage());

        }

        return 0.0;

    }
}