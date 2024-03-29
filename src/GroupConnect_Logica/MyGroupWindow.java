package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class MyGroupWindow extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/GroupConnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private int currentUserId;

    public MyGroupWindow(MenuWindow menuWindow, int userId) {
        this.currentUserId = userId;

        setTitle("Mi Grupo");
        setSize(400, 300);

        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("Images/imagenFiesta.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        setContentPane(contentPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton participantsButton = new JButton("Participantes");
        JButton photoButton = new JButton("Foto");
        JButton yourActivitiesButton = new JButton("Tus actividades");
        JButton matchActivitiesButton = new JButton("Actividades con Match");
        JButton exitGroupButton = new JButton("Salir de grupo");
        JButton deleteAccountButton = new JButton("Eliminar cuenta");

        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(participantsButton);
        buttonPanel.add(photoButton);
        buttonPanel.add(yourActivitiesButton);
        buttonPanel.add(matchActivitiesButton);
        buttonPanel.add(exitGroupButton);
        buttonPanel.add(deleteAccountButton);
        buttonPanel.add(Box.createVerticalStrut(20));

        contentPane.add(Box.createHorizontalStrut(130));
        contentPane.add(buttonPanel);
        contentPane.add(Box.createHorizontalStrut(130));

        setLocationRelativeTo(null);

        participantsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = getConnection();
                    String query = "SELECT id_usuario, participante FROM participantes WHERE nombreGrupo = " +
                            "(SELECT nombreGrupo FROM participantes WHERE id_usuario = ?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, currentUserId);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        StringBuilder participants = new StringBuilder("Participantes:\n");
                        do {
                            int participantUserId = resultSet.getInt("id_usuario");
                            String participantName = resultSet.getString("participante");
                            participants.append(participantName).append(" (ID: ").append(participantUserId).append(")\n");
                        } while (resultSet.next());
                        JOptionPane.showMessageDialog(null, participants.toString());
                    } else {
                        JOptionPane.showMessageDialog(null, "No perteneces a ningún grupo.");
                    }

                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener los participantes.");
                }
            }
        });

        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmation = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar tu cuenta?");
                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        Connection connection = getConnection();

                        // Eliminar al usuario de la tabla 'participantes' si pertenece a un grupo
                        String sqlDeleteParticipant = "DELETE FROM participantes WHERE id_usuario = ?";
                        PreparedStatement statementDeleteParticipant = connection.prepareStatement(sqlDeleteParticipant);
                        statementDeleteParticipant.setInt(1, currentUserId);
                        statementDeleteParticipant.executeUpdate();
                        statementDeleteParticipant.close();

                        // Eliminar al usuario de la tabla 'users'
                        String sqlDeleteUser = "DELETE FROM users WHERE id = ?";
                        PreparedStatement statementDeleteUser = connection.prepareStatement(sqlDeleteUser);
                        statementDeleteUser.setInt(1, currentUserId);
                        int rowsAffected = statementDeleteUser.executeUpdate();
                        statementDeleteUser.close();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Tu cuenta ha sido eliminada exitosamente.");
                            // Realizar otras acciones si es necesario, como cerrar la ventana actual
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al intentar eliminar tu cuenta.");
                        }

                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al eliminar tu cuenta.");
                    }
                }
            }
        });





        exitGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear un array con las opciones "Sí" y "No"
                String[] options = {"Sí", "No"};
                // Mostrar el diálogo de confirmación con las opciones específicas
                int confirmation = JOptionPane.showOptionDialog(null, "¿Estás seguro de que deseas salir del grupo?", "Confirmación",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                // Verificar la opción seleccionada
                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        Connection connection = getConnection();
                        // Consulta SQL para obtener el nombre del grupo del usuario actual
                        String sqlGroupName = "SELECT nombreGrupo FROM participantes WHERE id_usuario = ?";
                        PreparedStatement statementGroupName = connection.prepareStatement(sqlGroupName);
                        statementGroupName.setInt(1, currentUserId);
                        ResultSet resultSetGroupName = statementGroupName.executeQuery();

                        String nombreGrupo = null;
                        if (resultSetGroupName.next()) {
                            nombreGrupo = resultSetGroupName.getString("nombreGrupo");
                        }
                        resultSetGroupName.close();
                        statementGroupName.close();

                        if (nombreGrupo != null) {
                            // Realiza la eliminación del usuario del grupo
                            String sql = "DELETE FROM participantes WHERE id_usuario = ? AND nombreGrupo = ?";
                            PreparedStatement statement = connection.prepareStatement(sql);
                            statement.setInt(1, currentUserId); // Establece el id_usuario
                            statement.setString(2, nombreGrupo); // Establece el nombre del grupo obtenido
                            int rowsAffected = statement.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Has salido del grupo exitosamente.");
                                // Realiza otras acciones si es necesario, como actualizar la interfaz de usuario
                            } else {
                                JOptionPane.showMessageDialog(null, "No se encontró la combinación de usuario y grupo.");
                            }
                            statement.close();
                        } else {
                            JOptionPane.showMessageDialog(null, "No se encontró el nombre del grupo para este usuario.");
                        }
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al salir del grupo.");
                    }
                }
            }
        });




        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                menuWindow.setVisible(true);
            }
        });
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
