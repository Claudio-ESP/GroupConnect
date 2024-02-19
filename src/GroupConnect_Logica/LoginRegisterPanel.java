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

        setLayout(new BorderLayout());

        // Carga la imagen de fondo desde la ruta relativa
        ImageIcon backgroundImageIcon = new ImageIcon("Images/inicio(1).png");
        JLabel backgroundLabel = new JLabel(backgroundImageIcon);
        add(backgroundLabel, BorderLayout.CENTER);

        // Panel secundario para los campos de entrada y botones
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setOpaque(false); // Hace que el panel sea transparente

        JLabel emailLabel = new JLabel("Correo:");
        fieldsPanel.add(emailLabel);
        emailField = new JTextField();
        fieldsPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Contraseña:");
        fieldsPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        fieldsPanel.add(passwordField);

        JLabel nameLabel = new JLabel("Nombre:");
        fieldsPanel.add(nameLabel);
        nameField = new JTextField();
        fieldsPanel.add(nameField);

        // Agrega el panel secundario al panel principal
        add(fieldsPanel, BorderLayout.SOUTH);

        // Botones
        JButton loginButton = new JButton("Iniciar sesión");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                try {
                    boolean loggedIn = databaseHandler.checkLogin(email, password);
                    if (loggedIn) {
                        JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso");
                    } else {
                        JOptionPane.showMessageDialog(null, "Credenciales incorrectas");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al intentar iniciar sesión");
                }
            }
        });

        JButton registerButton = new JButton("Registrarse");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String name = nameField.getText();
                try {
                    databaseHandler.insertUser(email, password, name);
                    JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al intentar registrar usuario");
                }
            }
        });

        // Agrega los botones al panel secundario
        fieldsPanel.add(loginButton);
        fieldsPanel.add(registerButton);
    }

    public static void main(String[] args) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        JFrame frame = new JFrame("Iniciar Sesion/Registrarse");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.getContentPane().add(new LoginRegisterPanel(databaseHandler));
        frame.setVisible(true);
    }
}
