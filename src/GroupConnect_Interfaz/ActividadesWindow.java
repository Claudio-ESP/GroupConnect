package GroupConnect_Interfaz;

import GroupConnect_Logica.ActividadHandler;

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

}