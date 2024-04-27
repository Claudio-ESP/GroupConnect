package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public final class LoginRegisterPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private DatabaseHandler databaseHandler;

    public LoginRegisterPanel(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;

        // Creamos un JLayeredPane en lugar de usar BorderLayout
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 400));

        // Carga la imagen de fondo desde la ruta relativa
        ImageIcon backgroundImageIcon = new ImageIcon("Images/imagen.png");
        JLabel backgroundLabel = new JLabel(backgroundImageIcon);
        backgroundLabel.setBounds(0, 0, backgroundImageIcon.getIconWidth(), backgroundImageIcon.getIconHeight());

        // Panel secundario para los campos de entrada y botones
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setOpaque(false); // Hace que el panel sea transparente
        fieldsPanel.setBounds(0, 0, 800, 400);

        JLabel emailLabel = new JLabel("Correo:");
        fieldsPanel.add(emailLabel);
        emailField = new JTextField();
        fieldsPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Contraseña:");
        fieldsPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        fieldsPanel.add(passwordField);


        // Botón "Iniciar sesión"
        JButton loginButton = new JButton("Iniciar sesión");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                // Verifica si alguno de los campos está vacío
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos");
                } else {
                    try {
                        // Verifica las credenciales
                        int userId = databaseHandler.checkLogin(email, password);
                        if (userId != -1) {
                            JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso");
                            // Cerrar la ventana actual
                            Window window = SwingUtilities.getWindowAncestor(LoginRegisterPanel.this);
                            window.dispose();

                            // Abrir la nueva ventana con los 4 botones
                            MenuWindow menuWindow = new MenuWindow(userId); // Pasa el userId al constructor
                            menuWindow.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Credenciales incorrectas");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al intentar iniciar sesión");
                    }
                }
            }
        });


        JButton registerButton = new JButton("Registrarse");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear una ventana para el registro
                JFrame registerFrame = new JFrame("Registro");
                registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                registerFrame.setSize(400, 200);

                // Panel para el registro
                JPanel registerPanel = new JPanel(new GridLayout(0, 2));
                registerFrame.add(registerPanel);

                // Componentes para el registro
                JLabel emailLabel = new JLabel("Correo:");
                registerPanel.add(emailLabel);
                JTextField registerEmailField = new JTextField();
                registerPanel.add(registerEmailField);

                JLabel passwordLabel = new JLabel("Contraseña:");
                registerPanel.add(passwordLabel);
                JPasswordField registerPasswordField = new JPasswordField();
                registerPanel.add(registerPasswordField);

                JLabel nameLabel = new JLabel("Nombre:");
                registerPanel.add(nameLabel);
                JTextField registerNameField = new JTextField();
                registerPanel.add(registerNameField);

                // Botón de registro
                JButton confirmButton = new JButton("Registrarse");
                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String email = registerEmailField.getText();
                        String password = new String(registerPasswordField.getPassword());
                        String name = registerNameField.getText();

                        // Verifica si alguno de los campos está vacío
                        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos");
                        } else {
                            try {
                                // Intenta registrar al usuario
                                databaseHandler.insertUser(email, password, name);
                                JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente");

                                // Cierra la ventana de registro
                                registerFrame.dispose();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error al intentar registrar usuario");
                            }
                        }
                    }
                });
                registerPanel.add(confirmButton);

                // Centra la ventana de registro
                registerFrame.setLocationRelativeTo(null);
                registerFrame.setVisible(true);
            }
        });

        // Agrega los botones al panel secundario
        fieldsPanel.add(loginButton);
        fieldsPanel.add(registerButton);

        // Añadir componentes al JLayeredPane
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(fieldsPanel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane); // Agrega el JLayeredPane al panel principal
    }

    public static void main(String[] args) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        JFrame frame = new JFrame("Iniciar Sesion/Registrarse");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 450);
        frame.getContentPane().add(new LoginRegisterPanel(databaseHandler));
        frame.setVisible(true);
    }
}