package GroupConnect_Logica;

import javax.swing.*;

public class ActividadesWindow extends JFrame {

    private int currentUserId;

    public ActividadesWindow(ActividadHandler actividadHandler) {
        setTitle("Gestión de Actividades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);

        // Supongamos que aquí obtienes el nombre del grupo
        String groupName = "Nombre del Grupo";

        ActividadesPanel panel = new ActividadesPanel(actividadHandler, groupName);
        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
/*
    public static void main(String[] args) {
        // Para probar la ventana de actividades
        SwingUtilities.invokeLater(() -> {
            int currentUserId = 123; // Reemplaza 123 con el ID del usuario actual
            ActividadHandler actividadHandler = new ActividadHandler(currentUserId);
            new ActividadesWindow(actividadHandler);
        });
    }*/

}