package dao;

import com.mysql.cj.protocol.Resultset;
import database.ConnectionFactory;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServicoDao{


    public int salvar(Servico s){

            String sql = "INSERT INTO servicos(tipo , data_inicio , cliente , funcionario , " +
                    "equipamento , preco_hora_fixo , pago , concluido)" +
                    "VALUES(? , ? , ? , ? , ? , ? , ? , ?)";

            try(Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

                stmt.setString(1 , s.getTipoServico());
                stmt.setTimestamp(2 , Timestamp.valueOf(s.getDataInicio()));
                stmt.setString(3 , s.getCliente());
                stmt.setString(4 , s.getFuncionario());
                stmt.setString(5, s.getEquipamento());
                stmt.setDouble(6 , s.getPreco());
                stmt.setBoolean(7 , s.isPago());
                stmt.setBoolean(8 , s.isConcluido());

                stmt.executeUpdate();


                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        return rs.getInt(1);
                    }
                }

            } catch (SQLException e) {
                System.err.println("Erro ao salvar serviço: " + e.getMessage());
            }

            return -1;

    }

    public Servico buscarPorId(int id){
        String sql = "SELECT * FROM servicos WHERE id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1 , id);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    String tipo = rs.getString("tipo");
                    Timestamp tsInicio = rs.getTimestamp("data_inicio");
                    String cliente = rs.getString("cliente");
                    String funcionario = rs.getString("funcionario");
                    String equipamento = rs.getString("equipamento");
                    double preco = rs.getDouble("preco_hora_fixo");

                    Servico s = switch (tipo.toLowerCase()) {
                        case "manutencao" -> new Manutencao(tipo, tsInicio.toLocalDateTime(), cliente, funcionario, equipamento, preco);
                        case "reparacao" -> new Reparacao(tipo, tsInicio.toLocalDateTime(), cliente, funcionario, equipamento, preco);
                        case "diagnostico" -> new Diagnostico(tipo, tsInicio.toLocalDateTime(), cliente, funcionario, equipamento);
                        case "limpeza" -> new Limpeza(tipo, tsInicio.toLocalDateTime(), cliente, funcionario, equipamento);
                        default -> null;
                    };

                    if (s != null) {
                        s.setCodigoServico(rs.getInt("id"));
                        s.setConcluido(rs.getBoolean("concluido"));
                        Timestamp tsFim = rs.getTimestamp("data_fim");
                        if (tsFim != null) {
                            s.preencherDataFim(tsFim.toLocalDateTime());
                        }

                        if(s instanceof Manutencao || s instanceof Reparacao){
                             ComponenteDao componenteDao = new ComponenteDao();
                             List <Componente> componentesDoBanco =
                                     componenteDao.listarPorServico(s.getCodigoServico());

                            if (s instanceof Manutencao) {
                                ((Manutencao) s).setListaComponentes(componentesDoBanco);
                            }

                            else {
                                ((Reparacao) s).setListaComponentes(componentesDoBanco);
                            }

                        }
                    }

                    return s;
                }

            }

        }
        catch (SQLException e) {
            System.err.println("Erro ao procurar serviço: " + e.getMessage());
        }

        return null;
    }

    public void concluirServico(int id , Timestamp dataFim){

        String sql = "UPDATE servicos SET data_fim = ? , concluido = TRUE " +
                "WHERE id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setTimestamp(1 , dataFim);
            stmt.setInt(2 , id);

            int linhas = stmt.executeUpdate();

            if(linhas > 0){
                System.out.println("✅ Serviço #" + id +
                        " concluído com sucesso na base de dados!");
            }

            else{
                System.out.println("Serviço não encontrado!");
            }
        }

        catch (SQLException e){

            System.err.println("Erro ao concluir serviço: " + e.getMessage());

        }

    }

    public List<Servico> listarTodos(){
        List <Servico> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicos";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.executeQuery()){


            while(rs.next()){
                String tipo = rs.getString("tipo");
                Timestamp tsInicio = rs.getTimestamp("data_inicio");
                String cliente = rs.getString("cliente");
                String funcionario = rs.getString("funcionario");
                String equipamento = rs.getString("equipamento");
                double preco = rs.getDouble("preco_hora_fixo");

                Servico s = null;

                if("Manutencao".equalsIgnoreCase(tipo)){
                    s = new Manutencao(tipo , tsInicio.toLocalDateTime() , cliente , funcionario , equipamento , preco);
                }

                else if("Reparacao".equalsIgnoreCase(tipo)){
                    s = new Reparacao(tipo , tsInicio.toLocalDateTime() , cliente , funcionario  , equipamento);
                }

                else if ("Diagnostico".equalsIgnoreCase(tipo)){
                    s = new Diagnostico(tipo , tsInicio.toLocalDateTime() , cliente , funcionario , equipamento);

                }

                else if ("Limpeza".equalsIgnoreCase(tipo)){
                    s = new Limpeza(tipo , tsInicio.toLocalDateTime() , cliente , funcionario , equipamento);
                }

                if(s != null){
                    s.setCodigoServico(rs.getInt("id"));
                    Timestamp tsFim = rs.getTimestamp("data_fim");

                    if(tsFim != null){
                        s.preencherDataFim(tsFim.toLocalDateTime());
                    }

                    if(rs.getBoolean("concluido")){
                        s.setConcluido(true);
                    }

                    lista.add(s);
                }
            }
        }

        catch (SQLException e){
            System.err.println("Erro ao listar serviços: " + e.getMessage());

        }

        return lista;
    }

    public void remover(int id){

        String sql = "DELETE FROM servicos WHERE id = ?";
        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1 , id);

            int linhas = stmt.executeUpdate();

            if(linhas > 0){
                System.out.println("✅ Serviço #" + id + " removido com sucesso!");
            }

            else{
                System.out.println("❌ Serviço não encontrado.");
            }


        }

        catch (SQLException e){
            System.err.println("Erro ao remover: " + e.getMessage());
        }
    }

    private List <Servico> executarConsulta(String sql){
        List <Servico> lista = new ArrayList<>();

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                String tipo = rs.getString("tipo");
                Timestamp tsInicio = rs.getTimestamp("data_inicio");
                String cliente = rs.getString("cliente");
                String funcionario = rs.getString("funcionario");
                String equipamento = rs.getString("equipamento");
                double preco = rs.getDouble("preco_hora_fixo");

                Servico s = switch (tipo.toLowerCase()){
                    case "manutencao" -> new Manutencao(tipo , tsInicio.toLocalDateTime() ,
                            cliente , funcionario , equipamento , preco);
                    case "reparacao" -> new Reparacao(tipo , tsInicio.toLocalDateTime() ,
                            cliente , funcionario , equipamento , preco);
                    case "diagnostico" -> new Diagnostico(tipo , tsInicio.toLocalDateTime(),
                    cliente , funcionario , equipamento);
                    case "limpeza" -> new Limpeza(tipo , tsInicio.toLocalDateTime() ,
                            cliente , funcionario , equipamento);
                    default -> null;
                };

                if(s != null){
                    s.setCodigoServico(rs.getInt("id"));
                    s.setConcluido(rs.getBoolean("concluido"));
                    Timestamp tsFim = rs.getTimestamp("data_fim");

                    if(tsFim != null){
                        s.preencherDataFim(tsFim.toLocalDateTime());
                    }

                    lista.add(s);
                }




            }

        }

        catch (SQLException e){
            System.err.println("Erro ao listar dados ordenados: " + e.getMessage());

        }

        return lista;
    }

    public boolean atualizarPrecoPorId(int id , double preco){

        String sql = "UPDATE servicos SET preco_hora_fixo = ? WHERE id = ? " +
                "AND concluido = FALSE";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setDouble(1 , preco);
            stmt.setInt(2 , id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        }

        catch (SQLException e){
            System.err.println("Erro ao atualizar preço por ID: " + e.getMessage());
            return false;

        }

    }

    public int atualizarPrecoPorTipo(String tipo , double preco){

        String sql = "UPDATE servicos SET preco_hora_fixo = ? WHERE LOWER(tipo) = LOWER(?)" +
                "AND concluido = FALSE";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setDouble(1 , preco);
            stmt.setString(2 , tipo);

            return  stmt.executeUpdate();

        }

        catch (SQLException e){

            System.err.println("Erro ao atualizar preço por tipo: " + e.getMessage());
            return 0;

        }

    }



    public List<Servico> consultarViewOrdenada() {
        return executarConsulta("SELECT * FROM vw_servicos_ordenados_cliente");
    }

    public List<Servico> consultarViewOrdenadaPorDuracao() {
        String sql = "SELECT * FROM vw_servicos_ordenados_duracao";
        return executarConsulta(sql);
    }



}
