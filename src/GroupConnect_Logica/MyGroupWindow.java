
package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.*;
import java.util.List;

public class MyGroupWindow extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/GroupConnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private int currentUserId;
    private String currentGroupName;
    private JPanel contentPane;
    private String profileImagePath;
    private JLabel backgroundLabel;

    private void showProfileImage(String imagePath) {
        // Crear un JLabel para mostrar la imagen en la parte superior derecha
        JLabel profilePictureLabel = new JLabel(new ImageIcon(imagePath));
        profilePictureLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        profilePictureLabel.setVerticalAlignment(SwingConstants.TOP);

        // Agregar el JLabel al contentPane de MyGroupWindow
        getContentPane().add(profilePictureLabel);

        // Forzar al contentPane a redibujarse
        revalidate();
        repaint();
    }

    private void loadBackgroundPhoto() {
        // Cargar la foto de fondo si existe para el grupo actual
        try (Connection connection = getConnection()) {
            String selectSql = "SELECT ruta_foto FROM foto WHERE nombre_grupo = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setString(1, currentGroupName);
                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    String imagePath = resultSet.getString("ruta_foto");
                    if (imagePath != null && !imagePath.isEmpty()) {
                        profileImagePath = imagePath;
                        repaint();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al acceder a la base de datos");
        }
    }


    public MyGroupWindow(MenuWindow menuWindow, int userId, String currentGroupName) {
        this.currentUserId = userId;
        this.currentGroupName = currentGroupName;

        setTitle("Mi Grupo");
        setSize(400, 300);

        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (profileImagePath != null && !profileImagePath.isEmpty()) {
                    ImageIcon backgroundImage = new ImageIcon(profileImagePath);
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        setContentPane(contentPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton participantsButton = new JButton("Participantes");
        JButton photoButton = new JButton("Foto");
        //JButton yourActivitiesButton = new JButton("Tus actividades"); De momento van a estar en actividades
        //JButton matchActivitiesButton = new JButton("Actividades con Match"); De momento van a estar en actividades
        JButton exitGroupButton = new JButton("Salir de grupo");
        JButton deleteAccountButton = new JButton("Eliminar cuenta");
        JButton deleteGroupActivitiesButton = new JButton("Eliminar actividades del grupo");


        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(participantsButton);
        buttonPanel.add(photoButton);
        buttonPanel.add(exitGroupButton);
        buttonPanel.add(deleteAccountButton);
        buttonPanel.add(deleteGroupActivitiesButton);
        buttonPanel.add(Box.createVerticalStrut(20));

        contentPane.add(Box.createHorizontalStrut(130));
        contentPane.add(buttonPanel);
        contentPane.add(Box.createHorizontalStrut(130));

        setLocationRelativeTo(null);

        // private String profileImagePath; // Variable para almacenar la ruta de la imagen de perfil

        photoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(MyGroupWindow.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    profileImagePath = selectedFile.getAbsolutePath();

                    // Mostrar la imagen en la parte superior derecha de la ventana
                    showProfileImage(profileImagePath);

                    try (Connection connection = getConnection()) {
                        // Comprobar si el grupo ya tiene una entrada en la tabla foto
                        boolean groupExists = false;
                        String checkSql = "SELECT COUNT(*) FROM foto WHERE nombre_grupo = ?";
                        try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                            checkStatement.setString(1, currentGroupName);
                            ResultSet resultSet = checkStatement.executeQuery();
                            if (resultSet.next()) {
                                int count = resultSet.getInt(1);
                                groupExists = count > 0;
                            }
                        }

                        if (groupExists) {
                            // Si el grupo ya existe en la tabla foto, actualizar la ruta de la foto
                            String updateSql = "UPDATE foto SET ruta_foto = ? WHERE nombre_grupo = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                                updateStatement.setString(1, profileImagePath);
                                updateStatement.setString(2, currentGroupName);
                                int rowsAffected = updateStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(null, "Ruta de la foto actualizada exitosamente en la base de datos");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Error al actualizar la ruta de la foto en la base de datos");
                                }
                            }
                        } else {
                            // Si el grupo no existe en la tabla foto, insertar una nueva fila
                            String insertSql = "INSERT INTO foto (nombre_grupo, ruta_foto) VALUES (?, ?)";
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                                insertStatement.setString(1, currentGroupName);
                                insertStatement.setString(2, profileImagePath);
                                int rowsAffected = insertStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(null, "Ruta de la foto guardada exitosamente en la base de datos");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Error al guardar la ruta de la foto en la base de datos");
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al acceder a la base de datos");
                    }
                }
            }
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Cargar la foto de fondo al abrir la ventana
                loadBackgroundPhoto();
            }
        });


        participantsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = getConnection();
                    String query = "SELECT id_usuario, participante FROM participantes WHERE nombreGrupo = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, currentGroupName);
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
                        JOptionPane.showMessageDialog(null, "No hay participantes en el grupo.");
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
                        // Realiza la eliminación del usuario del grupo
                        String sql = "DELETE FROM participantes WHERE id_usuario = ? AND nombreGrupo = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, currentUserId); // Establece el id_usuario
                        statement.setString(2, currentGroupName); // Utiliza el nombre del grupo actual
                        int rowsAffected = statement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Has salido del grupo exitosamente.");
                            // Realiza otras acciones si es necesario, como actualizar la interfaz de usuario
                        } else {
                            JOptionPane.showMessageDialog(null, "No se encontró la combinación de usuario y grupo.");
                        }
                        statement.close();
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al salir del grupo.");
                    }
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

        deleteGroupActivitiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obtener el nombre del grupo actual
                    String currentGroupName = menuWindow.getCurrentGroupName();
                    // Verificar si el nombre del grupo actual no es nulo o vacío
                    if (currentGroupName != null && !currentGroupName.isEmpty()) {
                        // Obtener las actividades del grupo
                        ActividadHandler actividadHandler = new ActividadHandler(currentUserId, currentGroupName);
                        List<String> actividades = actividadHandler.obtenerActividadesPorGrupo();
                        if (actividades.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No hay actividades disponibles para tu grupo");
                        } else {
                            // Mostrar las actividades y permitir al usuario seleccionar una para eliminar
                            String[] actividadesArray = actividades.toArray(new String[0]);
                            String actividadSeleccionada = (String) JOptionPane.showInputDialog(null, "Selecciona una actividad para eliminar:",
                                    "Eliminar actividades del grupo", JOptionPane.QUESTION_MESSAGE, null, actividadesArray, actividadesArray[0]);
                            if (actividadSeleccionada != null) {
                                // Confirmar la eliminación de la actividad
                                int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar esta actividad?", "Confirmación", JOptionPane.YES_NO_OPTION);
                                if (confirmacion == JOptionPane.YES_OPTION) {
                                    // Eliminar la actividad seleccionada
                                    actividadHandler.eliminarActividad(actividadSeleccionada);
                                    JOptionPane.showMessageDialog(null, "La actividad ha sido eliminada exitosamente");
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No se ha seleccionado un grupo.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener o eliminar actividades del grupo");
                }
            }
        });

        JButton backButton = new JButton("Atrás");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false); // Ocultar la ventana actual
                menuWindow.setVisible(true); // Hacer visible la ventana de inicio
            }
        });
        buttonPanel.add(backButton);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                menuWindow.setVisible(true);
            }
        });
    }



    private void setFotoBackground(String rutaFoto) {
        // Establecer la foto como fondo del JPanel
        contentPane.removeAll(); // Eliminar cualquier componente existente
        contentPane.setLayout(new BorderLayout()); // Cambiar al diseño BorderLayout para establecer el fondo

        ImageIcon backgroundImage = new ImageIcon(rutaFoto);
        JLabel backgroundLabel = new JLabel(backgroundImage);
        contentPane.add(backgroundLabel, BorderLayout.CENTER);

        // Actualizar la visualización
        revalidate();
        repaint();
    }




    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

