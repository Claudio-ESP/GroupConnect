package GroupConnect_Logica;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/GroupConnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private int currentUserId; // Campo para almacenar el userId actual
    private String currentGroupName;


    public ActividadHandler(int currentUserId, String currentGroupName) {
        this.currentUserId = currentUserId;
        this.currentGroupName = currentGroupName;
    }


    public Map<String, List<Integer>> obtenerGruposConSolicitudes(String currentGroupName) throws SQLException {
        Map<String, List<Integer>> gruposConSolicitudes = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombre_grupo, id_actividad FROM matches " +
                    "WHERE id_actividad IN (SELECT id_actividad FROM matches " +
                    "WHERE nombre_grupo = ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, currentGroupName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String grupo = resultSet.getString("nombre_grupo");
                        int idActividad = resultSet.getInt("id_actividad");
                        if (!gruposConSolicitudes.containsKey(grupo)) {
                            gruposConSolicitudes.put(grupo, new ArrayList<>());
                        }
                        gruposConSolicitudes.get(grupo).add(idActividad);
                    }
                }
            }
        }
        return gruposConSolicitudes;
    }


    public void aceptarSolicitudUnion(String nombreGrupo, int idActividad) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO solicitudes (nombre_grupo, id_actividad, estado) VALUES (?, ?, 'aceptada')";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombreGrupo);
                statement.setInt(2, idActividad);
                statement.executeUpdate();
            }
        }
    }

    public void rechazarSolicitudUnion(String nombreGrupo, int idActividad) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO solicitudes (nombre_grupo, id_actividad, estado) VALUES (?, ?, 'rechazada')";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombreGrupo);
                statement.setInt(2, idActividad);
                statement.executeUpdate();
            }
        }
    }


    String obtenerNombreActividadPorId(int idActividad) throws SQLException {
        String nombreActividad = null;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombre FROM actividades WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idActividad);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        nombreActividad = resultSet.getString("nombre");
                    }
                }
            }
        }
        return nombreActividad;
    }


    public void crearActividad(String nombre, String descripcion, String lugar, String provincia) throws SQLException {
        String nombreGrupo = MenuWindow.getCurrentGroupName(); // Obtener el nombre del grupo del MenuWindow
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO actividades (nombre, descripcion, lugar, provincia, creador_id, nombre_grupo) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombre);
                statement.setString(2, descripcion);
                statement.setString(3, lugar);
                statement.setString(4, provincia);
                statement.setInt(5, this.currentUserId); // Usamos this.currentUserId para el creador_id
                statement.setString(6, nombreGrupo); // Agregamos el nombre del grupo
                statement.executeUpdate();
            }
        }
    }


    public void unirseActividad(String actividadElegida) throws SQLException {
        // Obtener el nombre del grupo del usuario actual
        String nombreGrupo = MenuWindow.getCurrentGroupName();

        // Obtener el ID de la actividad
        int idActividad = obtenerIdActividadPorNombre(actividadElegida);

        // Obtener el nombre del grupo que creó la actividad
        String actividadGrupo = obtenerGrupoActividad(idActividad);

        // Insertar datos en la tabla matches
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO matches (id_usuario, nombre_grupo, id_actividad, actividad_grupo) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, currentUserId);
                statement.setString(2, nombreGrupo);
                statement.setInt(3, idActividad);
                statement.setString(4, actividadGrupo);
                statement.executeUpdate();
            }
        }
    }

    // Método para obtener el nombre del grupo que creó la actividad
    private String obtenerGrupoActividad(int idActividad) throws SQLException {
        String nombreGrupo = null;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombre_grupo FROM actividades WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idActividad);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        nombreGrupo = resultSet.getString("nombre_grupo");
                    }
                }
            }
        }
        return nombreGrupo;
    }





// se puede eliminar es temporal de momento
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
    }
*/


    // Métodos auxiliares para obtener el ID de una actividad por su nombre
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

        // Obtener el nombre del grupo actual
        String currentGroupName = MenuWindow.getCurrentGroupName();

        if (currentGroupName == null || currentGroupName.isEmpty()) {
            // Si el nombre del grupo es nulo o vacío, devuelve una lista vacía
            return actividades;
        }

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombre FROM actividades WHERE nombre_grupo = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, currentGroupName);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String nombreActividad = resultSet.getString("nombre");
                        actividades.add(nombreActividad);
                    }
                }
            }
        }

        return actividades;
    }



/*
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
    }*/
/*
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

 */
/*
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
    }*/

    public Map<Integer, List<String>> obtenerActividadesAceptadasConGrupos() throws SQLException {
        Map<Integer, List<String>> actividadesConGrupos = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT id_actividad, nombre_grupo " +
                    "FROM solicitudes " +
                    "WHERE estado = 'aceptada'";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idActividad = resultSet.getInt("id_actividad");
                        String nombreGrupo = resultSet.getString("nombre_grupo");
                        if (!actividadesConGrupos.containsKey(idActividad)) {
                            actividadesConGrupos.put(idActividad, new ArrayList<>());
                        }
                        if (!actividadesConGrupos.get(idActividad).contains(nombreGrupo)) {
                            actividadesConGrupos.get(idActividad).add(nombreGrupo);
                        }
                    }
                }
            }
        }
        return actividadesConGrupos;
    }


}
