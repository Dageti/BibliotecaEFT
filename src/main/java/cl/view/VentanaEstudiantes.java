package cl.view;

import cl.dao.EstudianteDAO;
import cl.model.Estudiante;
import cl.services.ValidadorRut;
import cl.util.RutInvalidoException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz gráfica para gestionar el registro de estudiantes.
 */
public class VentanaEstudiantes extends JFrame {

    private final VentanaPrincipal ventanaPrincipal;
    private final EstudianteDAO estudianteDAO;

    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtCurso;
    private JTextField txtCorreo;

    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRefrescar;
    private JButton btnVolver;

    public VentanaEstudiantes(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.estudianteDAO = new EstudianteDAO();

        setTitle("Gestor de estudiantes");
        setSize(800, 560);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volverAlMenu();
            }
        });

        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.setBorder(new EmptyBorder(10, 15, 5, 15));

        JLabel lblTitulo = new JLabel("Estudiantes registrados", SwingConstants.CENTER);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(3, 4, 10, 8));
        panelForm.setBorder(new EmptyBorder(10, 5, 10, 5));

        txtId = new JTextField();
        txtId.setEditable(false);
        txtNombre = new JTextField();
        txtRut = new JTextField();
        txtCurso = new JTextField();
        txtCorreo = new JTextField();

        panelForm.add(new JLabel("ID:"));
        panelForm.add(txtId);
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("RUT:"));
        panelForm.add(txtRut);
        panelForm.add(new JLabel("Curso:"));
        panelForm.add(txtCurso);

        panelForm.add(new JLabel("Correo:"));
        panelForm.add(txtCorreo);
        panelForm.add(new JLabel(""));

        panelNorte.add(panelForm, BorderLayout.CENTER);
        add(panelNorte, BorderLayout.NORTH);

        inicializarTabla();
        JScrollPane scrollTabla = new JScrollPane(tablaEstudiantes);
        scrollTabla.setBorder(new EmptyBorder(5, 15, 5, 15));
        add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnRefrescar = new JButton("Refrescar");
        btnVolver = new JButton("Volver");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        cargarDatosTabla();
        configurarEventos();
    }

    private void inicializarTabla() {
        String[] columnas = {"ID", "Nombre", "RUT", "Curso", "Correo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEstudiantes = new JTable(modeloTabla);
    }

    public void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Estudiante> lista = estudianteDAO.listarTodos();
            for (Estudiante est : lista) {
                Object[] fila = {
                        est.getId(),
                        est.getNombre(),
                        est.getRut(),
                        est.getCurso(),
                        est.getEmail()
                };
                modeloTabla.addRow(fila);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar estudiantes: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarEventos() {
        tablaEstudiantes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaEstudiantes.getSelectedRow();
                if (fila != -1) {
                    txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtRut.setText(modeloTabla.getValueAt(fila, 2).toString());
                    txtCurso.setText(modeloTabla.getValueAt(fila, 3).toString());
                    txtCorreo.setText(modeloTabla.getValueAt(fila, 4).toString());
                }
            }
        });

        btnGuardar.addActionListener(e -> guardarEstudiante());
        btnActualizar.addActionListener(e -> actualizarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRefrescar.addActionListener(e -> {
            cargarDatosTabla();
            JOptionPane.showMessageDialog(this, "Tabla actualizada", "Información", JOptionPane.INFORMATION_MESSAGE);
        });
        btnVolver.addActionListener(e -> volverAlMenu());
    }

    private void guardarEstudiante() {
        if (!validarFormulario()) return;

        try {
            Estudiante estudiante = new Estudiante(
                    0,
                    txtNombre.getText().trim(),
                    txtRut.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtCurso.getText().trim()
            );

            if (estudianteDAO.guardar(estudiante)) {
                JOptionPane.showMessageDialog(this, "Estudiante guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatosTabla();
                limpiarFormulario();
            }
        } catch (RutInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            txtRut.requestFocus();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar estudiante: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstudiante() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla para actualizar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarFormulario()) return;

        try {
            Estudiante estudiante = new Estudiante(
                    Integer.parseInt(txtId.getText()),
                    txtNombre.getText().trim(),
                    txtRut.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtCurso.getText().trim()
            );

            if (estudianteDAO.actualizar(estudiante)) {
                JOptionPane.showMessageDialog(this, "Estudiante actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatosTabla();
                limpiarFormulario();
            }
        } catch (RutInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            txtRut.requestFocus();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar estudiante: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEstudiante() {
        int fila = tablaEstudiantes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = modeloTabla.getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Seguro que desea eliminar al estudiante '" + nombre + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (estudianteDAO.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Estudiante eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosTabla();
                    limpiarFormulario();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String curso = txtCurso.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty() || curso.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!ValidadorRut.rutValido(rut)) {
            JOptionPane.showMessageDialog(this, "RUT inválido. Debe ingresar formato con guión (ej: 12345678-9)", "Validación", JOptionPane.WARNING_MESSAGE);
            txtRut.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
    }

    private void volverAlMenu() {
        ventanaPrincipal.setVisible(true);
        this.dispose();
    }
}