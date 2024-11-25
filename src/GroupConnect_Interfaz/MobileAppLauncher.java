package GroupConnect_Interfaz;

import GroupConnect_MySQL.DatabaseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MobileAppLauncher extends JFrame {

    public MobileAppLauncher() {
        // Configuración de la ventana principal
        setTitle("GroupConnect - Mobile View");
        setSize(300, 600); // Dimensiones más pequeñas para simular mejor un móvil
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        // Simulación de la apariencia de un móvil
        MobilePanel mobilePanel = new MobilePanel(); // Panel personalizado con fondo rojo y corazones
        mobilePanel.setBounds(0, 0, 300, 600);
        mobilePanel.setLayout(null);
        add(mobilePanel);

        // Cargar y redimensionar el icono de la app
        ImageIcon originalIcon = new ImageIcon("Images/logo.png"); // Asegúrate de tener este archivo
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Botón con el icono redimensionado
        JButton appButton = new JButton(scaledIcon);
        appButton.setBounds(110, 250, 80, 80); // Posición del logo en el "móvil"
        appButton.setBorderPainted(false); // Sin borde
        appButton.setFocusPainted(false); // Sin indicador de foco
        appButton.setContentAreaFilled(false); // Sin fondo
        mobilePanel.add(appButton);

        // Etiqueta con el nombre de la app
        JLabel appNameLabel = new JLabel("GroupConnect");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        appNameLabel.setForeground(Color.WHITE); // Texto en blanco para resaltar sobre el fondo rojo
        appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        appNameLabel.setBounds(80, 340, 140, 30);
        mobilePanel.add(appNameLabel);

        // Acción del botón para abrir la ventana principal de la app
        appButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseHandler databaseHandler = new DatabaseHandler();

                // Abre el panel de inicio de sesión/registro
                JFrame loginFrame = new JFrame("Iniciar Sesión / Registrarse");
                loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                loginFrame.setSize(850, 450);
                loginFrame.getContentPane().add(new LoginRegisterPanel(databaseHandler));
                loginFrame.setLocationRelativeTo(null); // Centra la ventana
                loginFrame.setVisible(true);

                // Cierra la "ventana móvil"
                dispose();
            }
        });

        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
    }

    // Clase personalizada para el fondo con corazones
    private class MobilePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Fondo rojo pasión
            g.setColor(new Color(178, 34, 34)); // Rojo pasión
            g.fillRect(0, 0, getWidth(), getHeight());

            // Dibujar corazones aleatoriamente
            g.setColor(Color.PINK);
            Random random = new Random();
            int heartSize = 20;
            for (int i = 0; i < 20; i++) {
                int x = random.nextInt(getWidth() - heartSize);
                int y = random.nextInt(getHeight() - heartSize);
                drawHeart(g, x, y, heartSize, heartSize);
            }
        }

        // Método para dibujar un corazón
        private void drawHeart(Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int[] triangleX = {x, x + width / 2, x + width};
            int[] triangleY = {y + height / 3, y + height, y + height / 3};

            // Parte superior izquierda del corazón
            g2d.fillOval(x, y, width / 2, height / 2);

            // Parte superior derecha del corazón
            g2d.fillOval(x + width / 2, y, width / 2, height / 2);

            // Triángulo inferior
            g2d.fillPolygon(triangleX, triangleY, 3);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MobileAppLauncher launcher = new MobileAppLauncher();
            launcher.setVisible(true);
        });
    }
}
