package banco.dao;

import banco.database.ConnectionFactory;
import banco.model.Cliente;

import java.sql.*;

public class ClienteDAO {

    public int salvarCliente(Connection conn , Cliente cliente) throws SQLException {

        String sql = "INSERT INTO clientes (nome , nif) VALUES (? , ?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1 , cliente.getNome());
            stmt.setString(2 , cliente.getNif());
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){

                if(rs.next()){
                    return rs.getInt(1);
                }
            }

        }

        return -1;


    }



}

