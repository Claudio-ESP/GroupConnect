package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;

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
                ImageIcon backgroundImage = new ImageIcon("Images/fondo.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        setContentPane(contentPane);

        // Creamos los botones
        JButton joinGroupButton = new JButton("Unirse a grupo");
        JButton createGroupButton = new JButton("Crear Grupo");
        JButton activitiesButton = new JButton("Actividades");
        JButton myGroupButton = new JButton("Mi grupo");

        // Añadimos los botones al panel
        contentPane.add(Box.createVerticalGlue());
        contentPane.add(joinGroupButton);
        contentPane.add(createGroupButton);
        contentPane.add(activitiesButton);
        contentPane.add(myGroupButton);
        contentPane.add(Box.createVerticalGlue());

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
