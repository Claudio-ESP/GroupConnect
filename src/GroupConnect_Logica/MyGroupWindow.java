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

        // Resto de acciones de los botones omitidas por simplicidad

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
