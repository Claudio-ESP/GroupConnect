package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuWindow extends JFrame {

    public MenuWindow() {
        setTitle("Menú");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        // Creamos un panel con un fondo
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Carga la imagen de fondo desde la ruta relativa
                ImageIcon backgroundImage = new ImageIcon("Images/imagenFiesta.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        setContentPane(contentPane);

        // Creamos un panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Creamos los botones
        JButton joinGroupButton = new JButton("Unirse a grupo");
        JButton createGroupButton = new JButton("Crear Grupo");
        JButton activitiesButton = new JButton("Actividades");
        JButton myGroupButton = new JButton("Mi grupo");

        // Añadimos un espacio fijo a la izquierda y a la derecha del panel de botones
        buttonPanel.add(Box.createVerticalStrut(50)); // Espacio al principio
        buttonPanel.add(joinGroupButton);
        buttonPanel.add(createGroupButton);
        buttonPanel.add(activitiesButton);
        buttonPanel.add(myGroupButton);
        buttonPanel.add(Box.createVerticalStrut(50)); // Espacio al final

        // Añadimos el panel de botones al contenido principal
        contentPane.add(Box.createHorizontalStrut(130)); // Espacio a la izquierda
        contentPane.add(buttonPanel);
        contentPane.add(Box.createHorizontalStrut(130)); // Espacio a la derecha

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

        // Centramos la ventana en la pantalla
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuWindow menuWindow = new MenuWindow();
            menuWindow.setVisible(true);
        });
    }
}
