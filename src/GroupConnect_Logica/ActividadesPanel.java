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
                    List<String> actividades = actividadHandler.obtenerActividadesPorGrupo(); // Cambio aquí
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
                    Map<String, List<Integer>> gruposConSolicitudes = actividadHandler.obtenerGruposConSolicitudes();
                    if (gruposConSolicitudes.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay grupos con solicitudes pendientes para tus actividades.");
                    } else {
                        JFrame frame = new JFrame("Solicitudes de Grupo");
                        JPanel panel = new JPanel(new GridLayout(gruposConSolicitudes.size() + 1, 3));

                        // Encabezados de la tabla
                        // panel.add(new JLabel("Actividad"));
                        // panel.add(new JLabel("Grupo"));
                        // panel.add(new JLabel("Acción"));

                        // Mostrar los grupos con solicitudes pendientes
                        for (Map.Entry<String, List<Integer>> entry : gruposConSolicitudes.entrySet()) {
                            String grupo = entry.getKey();
                            List<Integer> idActividades = entry.getValue();
                            for (Integer idActividad : idActividades) {
                                String actividad = actividadHandler.obtenerNombreActividadPorId(idActividad);
                                JLabel actividadLabel = new JLabel(actividad);
                                JLabel grupoLabel = new JLabel(grupo);
                                JButton aceptarButton = new JButton("Aceptar");
                                JButton rechazarButton = new JButton("Rechazar");

                                aceptarButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            actividadHandler.aceptarSolicitudUnion(grupo, idActividad);
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
                                            actividadHandler.rechazarSolicitudUnion(grupo, idActividad);
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
                try {
                    // Verificar si el usuario pertenece a algún grupo y si ese grupo tiene actividades creadas
                    List<String> actividadesDelGrupo = actividadHandler.obtenerActividadesPorGrupo();
                    if (actividadesDelGrupo.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No tienes actividades aceptadas para mostrar");
                        return; // Salir del método si no hay actividades del grupo
                    }

                    // Obtener las actividades aceptadas con los respectivos grupos
                    Map<Integer, List<String>> actividadesConGrupos = actividadHandler.obtenerActividadesAceptadasConGrupos();
                    if (actividadesConGrupos.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay actividades aceptadas para mostrar");
                    } else {
                        StringBuilder message = new StringBuilder("Actividades Aceptadas del Grupo:\n");
                        for (Map.Entry<Integer, List<String>> entry : actividadesConGrupos.entrySet()) {
                            int idActividad = entry.getKey();
                            List<String> grupos = entry.getValue();
                            String actividad = actividadHandler.obtenerNombreActividadPorId(idActividad);
                            if (actividadesDelGrupo.contains(actividad)) {
                                message.append(actividad).append(": ");
                                for (String grupo : grupos) {
                                    message.append(grupo).append(", ");
                                }
                                // Eliminar la coma adicional al final
                                if (!grupos.isEmpty()) {
                                    message.delete(message.length() - 2, message.length());
                                }
                                message.append("\n");
                            }
                        }
                        JOptionPane.showMessageDialog(null, message.toString());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener actividades aceptadas");
                }
            }
        });
        add(actividadesAceptadasButton);


    }

}
