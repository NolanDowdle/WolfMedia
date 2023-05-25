package main.java.wolf_media.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String MARIADB_JDBC_URL = "jdbc:mariadb://%s:%d/%s";
    
    public DBManager() throws ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
    }
    
    public Connection getConnection(String host, int port, String database, String user, String password) throws SQLException {
        String url = String.format(MARIADB_JDBC_URL, host, port, database);
        Connection conn = DriverManager.getConnection(url, user, password);
        // Disable auto commit in order to bundle many operations into a transaction
        conn.setAutoCommit(false);
        return conn;
    }
}
