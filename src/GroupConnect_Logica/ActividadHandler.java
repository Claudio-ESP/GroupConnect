package GroupConnect_Logica;

import javax.swing.*;
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


    public Map<String, List<String>> obtenerGruposConSolicitudes(String currentGroupName) throws SQLException {
        Map<String, List<String>> gruposConSolicitudes = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT DISTINCT A.nombre_grupo, A.id_actividad, B.nombre AS nombre_actividad " +
                    "FROM matches A " +
                    "INNER JOIN actividades B ON A.id_actividad = B.id " +
                    "WHERE A.actividad_grupo = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, currentGroupName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String grupo = resultSet.getString("nombre_grupo");
                        String actividad = resultSet.getString("nombre_actividad");
                        if (!gruposConSolicitudes.containsKey(grupo)) {
                            gruposConSolicitudes.put(grupo, new ArrayList<>());
                        }
                        gruposConSolicitudes.get(grupo).add(actividad);
                    }
                }
            }
        }
        return gruposConSolicitudes;
    }



    public void aceptarSolicitudUnion(String nombreGrupo, String nombreActividad) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO solicitudes (id_actividad, nombre_grupo, grupo_actual, estado) " +
                    "VALUES ((SELECT id FROM actividades WHERE nombre = ?), ?, ?, 'aceptado')";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombreActividad);
                statement.setString(2, nombreGrupo);
                statement.setString(3, MenuWindow.getCurrentGroupName()); // Agregar grupo_actual
                statement.executeUpdate();
            }
        }
    }

    public void rechazarSolicitudUnion(String nombreGrupo, String nombreActividad) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO solicitudes (id_actividad, nombre_grupo, grupo_actual, estado) " +
                    "VALUES ((SELECT id FROM actividades WHERE nombre = ?), ?, ?, 'rechazado')";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombreActividad);
                statement.setString(2, nombreGrupo);
                statement.setString(3, MenuWindow.getCurrentGroupName()); // Agregar grupo_actual
                statement.executeUpdate();
            }
        }
    }




    // Método para obtener el nombre de la actividad por su ID desde la tabla actividades
    private String obtenerNombreActividadPorId(int idActividad) {
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
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener el nombre de la actividad");
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

    public void mostrarSolicitudesAceptadas(String currentGroupName) {
        StringBuilder message = new StringBuilder("Solicitudes Aceptadas:\n");
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT nombre_grupo, grupo_actual, id_actividad " +
                    "FROM solicitudes " +
                    "WHERE estado = 'aceptado' AND (grupo_actual = ? OR nombre_grupo = ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, currentGroupName);
                statement.setString(2, currentGroupName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    boolean firstGroup = true;
                    while (resultSet.next()) {
                        String nombreGrupo = resultSet.getString("nombre_grupo");
                        String grupoActual = resultSet.getString("grupo_actual");
                        int idActividad = resultSet.getInt("id_actividad");
                        String nombreActividad = obtenerNombreActividadPorId(idActividad);
                        String descripcionActividad = obtenerDescripcionActividadPorId(idActividad);
                        if (!nombreGrupo.equals(currentGroupName)) {
                            if (firstGroup) {
                                message.append("\nSolicitudes hacia otros grupos:\n");
                                firstGroup = false;
                            }
                            message.append("Grupo destino: ").append(nombreGrupo).append("\n");
                            message.append("Grupo origen: ").append(grupoActual).append("\n");
                        } else {
                            message.append("\nSolicitudes de nuestro grupo:\n");
                            message.append("Grupo que nos ha aceptado: ").append(grupoActual).append("\n");
                        }
                       // message.append("ID de Actividad: ").append(idActividad).append("\n");
                        message.append("Nombre Actividad: ").append(nombreActividad).append("\n");
                        message.append("Descripción Actividad: ").append(descripcionActividad).append("\n\n");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener actividades aceptadas");
        }
        JOptionPane.showMessageDialog(null, message.toString());
    }





    // Método para obtener la descripción de la actividad por su ID desde la tabla actividades
    private String obtenerDescripcionActividadPorId(int idActividad) {
        String descripcionActividad = null;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT descripcion FROM actividades WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idActividad);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        descripcionActividad = resultSet.getString("descripcion");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener la descripción de la actividad");
        }
        return descripcionActividad;
    }


}
