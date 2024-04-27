package GroupConnect_Logica;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class CrearGrupoAction implements ActionListener {

    private static final String URL = "jdbc:mysql://localhost:3306/GroupConnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    @Override
    public void actionPerformed(ActionEvent e) {
        // Crear una ventana para que el usuario ingrese el nombre del grupo
        String nombreGrupo = JOptionPane.showInputDialog(null, "Ingrese el nombre del grupo:");

        // Generar un código aleatorio de 6 dígitos
        String codigoGrupo = generarCodigo();

        // Guardar los datos en la base de datos
        try {
            Connection connection = getConnection();
            String sql = "INSERT INTO Grupo (nombre, codigo) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nombreGrupo);
            statement.setString(2, codigoGrupo);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se ha creado el grupo con el nombre: " + nombreGrupo +
                    "\nCódigo del grupo: " + codigoGrupo);
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar en la base de datos");
        }
    }

    // Método para generar un código aleatorio de 6 dígitos
    private String generarCodigo() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            codigo.append(random.nextInt(10)); // Añadir un dígito aleatorio de 0 a 9
        }
        return codigo.toString();
    }

    // Método para obtener una conexión a la base de datos
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}