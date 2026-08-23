package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    public static Connection getConnection() throws SQLException {

        Properties props = new Properties();

        try(FileInputStream fis = new FileInputStream("db.properties")){
            props.load(fis);
        }

        catch (IOException e){
            throw new RuntimeException("❌ Erro: Não foi possível carregar o ficheiro db.properties. " +
                    "Certifique-se de que ele existe na raiz do projeto.", e);

        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}