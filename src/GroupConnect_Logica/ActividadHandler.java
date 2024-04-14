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

    public void unirseActividad(String nombreActividad) throws SQLException {
        // Obtener el nombre del grupo del usuario actual
        String nombreGrupo = obtenerNombreGrupo();

        // Obtener el ID de la actividad
        int idActividad = obtenerIdActividadPorNombre(nombreActividad);

        // Insertar datos en la tabla matches
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO matches (id_usuario, nombre_grupo, id_actividad) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, currentUserId);
                statement.setString(2, nombreGrupo);
                statement.setInt(3, idActividad);
                statement.executeUpdate();
            }
        }
    }


   /* private int obtenerIdGrupoPorNombre(String nombreGrupo) throws SQLException {
        int idGrupo = -1; // Valor por defecto en caso de que no se encuentre el grupo
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT Grupo.id " +
                    "FROM Grupo " +
                    "JOIN participantes ON Grupo.nombre = participantes.nombreGrupo " +
                    "WHERE participantes.id_usuario = ? AND participantes.nombreGrupo = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, this.currentUserId);
                statement.setString(2, nombreGrupo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idGrupo = resultSet.getInt("id");
                    }
                }
            }
        }
        return idGrupo;
    }*/



    //Para obtener el id de la actividad en curso
    private int obtenerIdActividadPorNombre(String nombreActividad) throws SQLException {
        int idActividad = -1;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT id FROM actividades WHERE nombre = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombreActividad);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idActividad = resultSet.getInt("id");
                    }
                }
            }
        }
        return idActividad;
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
