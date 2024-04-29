package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static GroupConnect_Logica.DatabaseHandler.getConnection;

public class MenuWindow extends JFrame {

    private int currentUserId; // Campo para almacenar el userId actual
    private static String currentGroupName;

    public static String getCurrentGroupName() {
        return currentGroupName;
    }

    public MenuWindow(int userId) { // Constructor modificado para aceptar el userId
        this.currentUserId = userId;

        setTitle("Menú");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Obtener la lista de grupos del usuario
        List<String> userGroups = obtenerGruposUsuario(currentUserId);

        if (userGroups.size() == 1) {
            // Si el usuario pertenece a un solo grupo, establecer directamente el nombre del grupo
            currentGroupName = userGroups.get(0);
        }

        // Si el usuario pertenece a más de un grupo, agregar el botón "Elegir grupo"
        if (userGroups.size() > 1) {
            JButton chooseGroupButton = new JButton("Elegir grupo");
            chooseGroupButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Mostrar un menú desplegable con los grupos disponibles
                    String[] groupNames = userGroups.toArray(new String[0]);
                    String selectedGroup = (String) JOptionPane.showInputDialog(
                            null,
                            "Selecciona un grupo:",
                            "Seleccionar grupo",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            groupNames,
                            groupNames[0]
                    );
                    // Actualizar el nombre del grupo seleccionado
                    if (selectedGroup != null) {
                        currentGroupName = selectedGroup;
                    }
                }
            });
            buttonPanel.add(chooseGroupButton);
        }

        JButton joinGroupButton = new JButton("Unirse a grupo");
        JButton createGroupButton = new JButton("Crear Grupo");
        JButton activitiesButton = new JButton("Actividades");
        JButton myGroupButton = new JButton("Mi grupo");

        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(joinGroupButton);
        buttonPanel.add(createGroupButton);
        buttonPanel.add(activitiesButton);
        buttonPanel.add(myGroupButton);
        buttonPanel.add(Box.createVerticalStrut(50));

        contentPane.add(Box.createHorizontalStrut(130));
        contentPane.add(buttonPanel);
        contentPane.add(Box.createHorizontalStrut(130));

        setLocationRelativeTo(null);

        // Asociamos la acción de crear grupo al botón correspondiente
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CrearGrupoAction().actionPerformed(e);
            }
        });

        // Asociamos la acción de unirse a grupo al botón correspondiente
        joinGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UnirseGrupoAction().actionPerformed(e);
            }
        });

        activitiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActividadHandler actividadHandler = new ActividadHandler(currentUserId, getCurrentGroupName()); // Pasar el nombre del grupo también
                new ActividadesWindow(actividadHandler);
            }
        });


        myGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MyGroupWindow myGroupWindow = new MyGroupWindow(MenuWindow.this, currentUserId, getCurrentGroupName());
                myGroupWindow.setVisible(true);
            }
        });

        JButton exitButton = new JButton("Salir");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar el programa entero
                System.exit(0);
            }
        });
        buttonPanel.add(exitButton);


    }

    // Método para obtener los grupos del usuario
    private List<String> obtenerGruposUsuario(int userId) {
        try {
            Connection connection = getConnection();
            String query = "SELECT nombreGrupo FROM participantes WHERE id_usuario = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            List<String> userGroups = new ArrayList<>();
            while (resultSet.next()) {
                String groupName = resultSet.getString("nombreGrupo");
                userGroups.add(groupName);
            }

            resultSet.close();
            statement.close();
            connection.close();

            return userGroups;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener los grupos del usuario.");
            return Collections.emptyList();
        }
    }


    // Se puede eliminar, de momento lo dejo para que si lo ejecutamos diga credenciales inválidas
    public static void main(String[] args) {
        try {
            // Intentamos hacer login con las credenciales
            String email = "correo@ejemplo.com"; // Reemplaza con el email correcto
            String password = "contraseña"; // Reemplaza con la contraseña correcta
            int userId = DatabaseHandler.checkLogin(email, password);

            if (userId != -1) { // Si las credenciales son válidas, mostramos el menú
                SwingUtilities.invokeLater(() -> {
                    MenuWindow menuWindow = new MenuWindow(userId);
                    menuWindow.setVisible(true);
                });
            } else {
                System.out.println("Credenciales inválidas");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}