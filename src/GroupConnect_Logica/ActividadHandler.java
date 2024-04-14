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

    public List<String> obtenerActividadesPorGrupo() throws SQLException {
        List<String> actividades = new ArrayList<>();
        String nombreGrupo = obtenerNombreGrupo();
        if (nombreGrupo == null) {
            // Si el usuario no pertenece a ningún grupo, devuelve una lista vacía
            return actividades;
        }
        List<Integer> userIds = obtenerUserIdsPorGrupo(nombreGrupo);
        actividades = obtenerActividadesPorUsuarios(userIds);
        return actividades;
    }

    // Las actividades solo las puede borrar el usuario que ha creado la actividad
    public void eliminarActividad(String nombreActividad) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM actividades WHERE nombre = ? AND creador_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombreActividad);
                statement.setInt(2, this.currentUserId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("La actividad no pudo ser eliminada. Es posible que no tengas permiso para eliminar esta actividad.");
                }
            }
        }
    }

    private String obtenerNombreGrupo() throws SQLException {
        String groupName = null;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombreGrupo FROM participantes WHERE id_usuario = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, this.currentUserId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        groupName = resultSet.getString("nombreGrupo");
                    }
                }
            }
        }
        return groupName;
    }

    private List<Integer> obtenerUserIdsPorGrupo(String groupName) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT id_usuario FROM participantes WHERE nombreGrupo = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, groupName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int userId = resultSet.getInt("id_usuario");
                        userIds.add(userId);
                    }
                }
            }
        }
        return userIds;
    }

    private List<String> obtenerActividadesPorUsuarios(List<Integer> userIds) throws SQLException {
        List<String> actividades = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombre FROM actividades WHERE creador_id IN (";
            for (int i = 0; i < userIds.size(); i++) {
                if (i > 0) {
                    sql += ",";
                }
                sql += "?";
            }
            sql += ")";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < userIds.size(); i++) {
                    statement.setInt(i + 1, userIds.get(i));
                }
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
