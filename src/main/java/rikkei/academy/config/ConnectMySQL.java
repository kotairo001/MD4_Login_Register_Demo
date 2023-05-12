package rikkei.academy.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectMySQL {
    private static Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/form_login";
    private static final String user = "root";
    private static final String password = "Kimsoohyun0216.";
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL,user,password);
            System.out.println("Connected successful");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("ERROR");
            throw new RuntimeException(e);
        }
        return connection;
    }

}
