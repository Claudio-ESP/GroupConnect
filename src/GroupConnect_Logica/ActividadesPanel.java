package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ActividadesPanel extends JPanel {
    private ActividadHandler actividadHandler;

    public ActividadesPanel(ActividadHandler actividadHandler) {
        this.actividadHandler = actividadHandler;

        setLayout(new GridLayout(2, 1));

        JButton crearActividadButton = new JButton("Crear Actividad");
        crearActividadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = JOptionPane.showInputDialog(null, "Nombre de la actividad:");
                String descripcion = JOptionPane.showInputDialog(null, "Descripción de la actividad:");
                String lugar = JOptionPane.showInputDialog(null, "Lugar de la actividad:");
                String provincia = JOptionPane.showInputDialog(null, "Provincia de la actividad:");
                try {
                    actividadHandler.crearActividad(nombre, descripcion, lugar, provincia);
                    JOptionPane.showMessageDialog(null, "Actividad creada exitosamente");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al crear actividad");
                }
            }
        });

        add(crearActividadButton);

        JButton unirseActividadButton = new JButton("Unirse a Actividad");
        unirseActividadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String provincia = JOptionPane.showInputDialog(null, "Ingresa tu provincia:");
                try {
                    List<String> actividades = actividadHandler.obtenerActividadesPorProvincia(provincia);
                    if (actividades.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay actividades disponibles en tu provincia");
                    } else {
                        String[] actividadesArray = actividades.toArray(new String[0]);
                        String actividadElegida = (String) JOptionPane.showInputDialog(null, "Selecciona una actividad:",
                                "Actividades Disponibles", JOptionPane.QUESTION_MESSAGE, null, actividadesArray, actividadesArray[0]);
                        if (actividadElegida != null) {
                            // Implementa la lógica para unirse a la actividad elegida
                            JOptionPane.showMessageDialog(null, "Te has unido a la actividad: " + actividadElegida);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener actividades");
                }
            }
        });
        add(unirseActividadButton);
    }
}


