package GroupConnect_Logica;

import javax.swing.*;

public class ActividadesWindow extends JFrame {

    private int currentUserId;
    public ActividadesWindow(ActividadHandler actividadHandler) {
        setTitle("Gestión de Actividades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cambio aquí
        setSize(400, 200);

        ActividadesPanel panel = new ActividadesPanel(actividadHandler);
        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Para probar la ventana de actividades
        SwingUtilities.invokeLater(() -> {
            int currentUserId = 123; // Reemplaza 123 con el ID del usuario actual
            ActividadHandler actividadHandler = new ActividadHandler(currentUserId);
            new ActividadesWindow(actividadHandler);
        });
    }

}

