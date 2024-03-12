package GroupConnect_Logica;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UnirseGrupoAction implements ActionListener {

    private static final String URL = "jdbc:mysql://localhost:3306/GroupConnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    @Override
    public void actionPerformed(ActionEvent e) {
        String nombrePersona = JOptionPane.showInputDialog(null, "Ingrese su nombre:");
        String codigoGrupo = JOptionPane.showInputDialog(null, "Ingrese el código del grupo:");

        // Buscar el nombre del grupo correspondiente al código ingresado
        String nombreGrupo = buscarNombreGrupo(codigoGrupo);

        if (nombreGrupo != null) {
            // Obtener el id_usuario
            int idUsuario = obtenerIdUsuario(nombrePersona);

            if (idUsuario != -1) {
                // Guardar los datos en la base de datos
                try {
                    Connection connection = getConnection();
                    String sql = "INSERT INTO participantes (id_usuario, nombreGrupo, participante) VALUES (?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, idUsuario); // Aquí se guarda el id_usuario
                    statement.setString(2, nombreGrupo); // El nombre del grupo
                    statement.setString(3, nombrePersona); // El nombre de la persona que se une
                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Te has unido al grupo: " + nombreGrupo);
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al unirse al grupo");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Código de grupo no encontrado");
        }
    }

    // Método para buscar el nombre del grupo correspondiente al código
    private String buscarNombreGrupo(String codigoGrupo) {
        String nombreGrupo = null;
        try {
            Connection connection = getConnection();
            String sql = "SELECT nombre FROM Grupo WHERE codigo = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, codigoGrupo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                nombreGrupo = resultSet.getString("nombre");
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return nombreGrupo;
    }

    // Método para obtener el id_usuario basado en el nombre de la persona
    private int obtenerIdUsuario(String nombrePersona) {
        int idUsuario = -1;
        try {
            Connection connection = getConnection();
            String sql = "SELECT id FROM users WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nombrePersona);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                idUsuario = resultSet.getInt("id");
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return idUsuario;
    }

    // Método para obtener una conexión a la base de datos
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
