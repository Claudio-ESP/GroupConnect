package GroupConnect_Logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ActividadesPanel extends JPanel {
    private ActividadHandler actividadHandler;
    private String groupName; // Campo para almacenar el nombre del grupo
    private int currentUserId;


    public ActividadesPanel(ActividadHandler actividadHandler, String groupName) {
        this.actividadHandler = actividadHandler;
        this.groupName = groupName;
        this.currentUserId = currentUserId;

        setLayout(new GridLayout(4, 1));


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
                            try {
                                // Unirse a la actividad sin pasar el nombre del grupo que la creó
                                actividadHandler.unirseActividad(actividadElegida);
                                JOptionPane.showMessageDialog(null, "Te has unido a la actividad: " + actividadElegida);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error al unirse a la actividad");
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener actividades");
                }
            }
        });




        add(unirseActividadButton);

        JButton tusActividadesButton = new JButton("Actividades del grupo");
        tusActividadesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> actividades = actividadHandler.obtenerActividadesPorGrupo();
                    if (actividades.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay actividades disponibles para tu grupo");
                    } else {
                        StringBuilder message = new StringBuilder("Actividades de tu grupo:\n");
                        for (String actividad : actividades) {
                            message.append(actividad).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, message.toString());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener actividades");
                }
            }
        });
        add(tusActividadesButton);


        JButton actividadesConMatchButton = new JButton("Solicitudes");
        actividadesConMatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Llama al método obteniendo currentGroupName de la instancia de MenuWindow
                    String currentGroupName = MenuWindow.getCurrentGroupName();

                    // Obtener todas las actividades del grupo actual con solicitudes pendientes
                    Map<String, List<String>> gruposConSolicitudes = actividadHandler.obtenerGruposConSolicitudes(currentGroupName);

                    if (gruposConSolicitudes.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay grupos con solicitudes pendientes para tus actividades.");
                    } else {
                        JFrame frame = new JFrame("Solicitudes de Grupo");
                        JPanel panel = new JPanel(new GridLayout(gruposConSolicitudes.size() + 1, 3));

                        // Encabezados de la tabla
                      //  panel.add(new JLabel("Actividad"));
                      //  panel.add(new JLabel("Grupo"));
                      //  panel.add(new JLabel("Acción"));

                        // Mostrar las actividades con solicitudes pendientes
                        for (Map.Entry<String, List<String>> entry : gruposConSolicitudes.entrySet()) {
                            String grupo = entry.getKey();
                            List<String> actividades = entry.getValue();
                            for (String actividad : actividades) {
                                JLabel actividadLabel = new JLabel(actividad);
                                JLabel grupoLabel = new JLabel(grupo);
                                JButton aceptarButton = new JButton("Aceptar");
                                JButton rechazarButton = new JButton("Rechazar");

                                aceptarButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            actividadHandler.aceptarSolicitudUnion(grupo, actividad);
                                            JOptionPane.showMessageDialog(null, "Solicitud aceptada");
                                            // Eliminar la entrada correspondiente de la interfaz
                                            panel.remove(actividadLabel);
                                            panel.remove(grupoLabel);
                                            panel.remove(aceptarButton);
                                            panel.remove(rechazarButton);
                                            frame.pack();
                                        } catch (SQLException ex) {
                                            ex.printStackTrace();
                                            JOptionPane.showMessageDialog(null, "Error al aceptar la solicitud");
                                        }
                                    }
                                });

                                rechazarButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            actividadHandler.rechazarSolicitudUnion(grupo, actividad);
                                            JOptionPane.showMessageDialog(null, "Solicitud rechazada");
                                            // Eliminar la entrada correspondiente de la interfaz
                                            panel.remove(actividadLabel);
                                            panel.remove(grupoLabel);
                                            panel.remove(aceptarButton);
                                            panel.remove(rechazarButton);
                                            frame.pack();
                                        } catch (SQLException ex) {
                                            ex.printStackTrace();
                                            JOptionPane.showMessageDialog(null, "Error al rechazar la solicitud");
                                        }
                                    }
                                });

                                panel.add(actividadLabel);
                                panel.add(grupoLabel);
                                panel.add(aceptarButton);
                                panel.add(rechazarButton);
                            }
                        }

                        frame.add(panel);
                        frame.pack();
                        frame.setVisible(true);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener grupos con solicitudes");
                }
            }
        });
        add(actividadesConMatchButton);




        JButton actividadesAceptadasButton = new JButton("Actividades Aceptadas");
        actividadesAceptadasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el nombre del grupo actual
                String currentGroupName = MenuWindow.getCurrentGroupName();
                if (currentGroupName == null || currentGroupName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No se ha seleccionado un grupo.");
                    return;
                }

                // Llamar al método mostrarSolicitudesAceptadas
                actividadHandler.mostrarSolicitudesAceptadas(currentGroupName);
            }
        });
        add(actividadesAceptadasButton);







    }

}
