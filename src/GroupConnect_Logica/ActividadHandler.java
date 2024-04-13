package GroupConnect_Logica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActividadHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/GroupConnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private int currentUserId; // Campo para almacenar el userId actual

    public ActividadHandler(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void crearActividad(String nombre, String descripcion, String lugar, String provincia) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO actividades (nombre, descripcion, lugar, provincia, creador_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombre);
                statement.setString(2, descripcion);
                statement.setString(3, lugar);
                statement.setString(4, provincia);
                statement.setInt(5, this.currentUserId); // Usamos this.currentUserId para el creador_id
                statement.executeUpdate();
            }
        }
    }


    public List<String> obtenerActividadesPorProvincia(String provincia) throws SQLException {
        List<String> actividades = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombre FROM actividades WHERE provincia = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, provincia);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String nombre = resultSet.getString("nombre");
                        actividades.add(nombre);
                    }
                }
            }
        }
        return actividades;
    }
}
