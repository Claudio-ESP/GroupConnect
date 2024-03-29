package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MenuWindow extends JFrame {

    private int currentUserId; // Campo para almacenar el userId actual

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


        myGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MyGroupWindow myGroupWindow = new MyGroupWindow(MenuWindow.this, currentUserId); // Pasar el userId a MyGroupWindow
                myGroupWindow.setVisible(true);
            }
        });

        // Resto de tu código
    }

    // Se puede eliminar, de momento lo dejo para que si lo ejecutamos diga credenciales invalidas
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
