package GroupConnect_Logica;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/GroupConnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void insertUser(String email, String password, String name) throws SQLException {
        String query = "INSERT INTO users (email, password, name) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, name);
            statement.executeUpdate();
        }
    }

    public static boolean checkLogin(String email, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Si hay alguna fila, las credenciales son válidas
            }
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            System.out.println("Conexión exitosa");

            // Código para crear la tabla si no existe
            String createTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "email VARCHAR(100) UNIQUE," +
                    "password VARCHAR(100)," +
                    "name VARCHAR(100))";
            Statement statement = conn.createStatement();
            statement.executeUpdate(createTableQuery);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
