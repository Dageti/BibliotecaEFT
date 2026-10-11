package cl.view;

import cl.dao.EstudianteDAO;
import cl.dao.LibroDAO;
import cl.dao.PrestamoDAO;
import cl.model.Estudiante;
import cl.model.Libro;
import cl.model.Prestamo;
import cl.services.BibliotecaService;
import cl.threads.PrestamoHilo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz gráfica encargada de gestionar el flujo de la biblioteca.
 * Permite operaciones según rol.
 */
public class VentanaPrestamos extends JFrame {

	private final BibliotecaService servicio;
	private final VentanaPrincipal ventanaPrincipal;

	private final EstudianteDAO estudianteDAO;
	private final LibroDAO libroDAO;
	private final PrestamoDAO prestamoDAO;

	private JComboBox<Estudiante> cmbEstudiantes;
	private JComboBox<Libro> cmbLibros;

	private JTable tablaPrestamos;
	private DefaultTableModel modeloTabla;

	private JButton btnPrestar;
	private JButton btnDevolver;
	private JButton btnEliminar;
	private JButton btnRefrescar;
	private JButton btnVolver;

	public VentanaPrestamos(BibliotecaService servicio, VentanaPrincipal ventanaPrincipal) {
		this.servicio = servicio;
		this.ventanaPrincipal = ventanaPrincipal;

		this.estudianteDAO = new EstudianteDAO();
		this.libroDAO = new LibroDAO();
		this.prestamoDAO = new PrestamoDAO();

		setTitle("Gestor de préstamos");
		setSize(850, 580);
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

		JLabel lblTitulo = new JLabel("Préstamos y devoluciones", SwingConstants.CENTER);
		panelNorte.add(lblTitulo, BorderLayout.NORTH);

		JPanel panelForm = new JPanel(new GridLayout(2, 3, 10, 8));
		panelForm.setBorder(new EmptyBorder(10, 5, 10, 5));

		cmbEstudiantes = new JComboBox<>();
		cmbLibros = new JComboBox<>();
		btnPrestar = new JButton("Registrar préstamo");

		panelForm.add(new JLabel("Seleccionar estudiante:"));
		panelForm.add(new JLabel("Seleccionar libro:"));
		panelForm.add(new JLabel(""));

		panelForm.add(cmbEstudiantes);
		panelForm.add(cmbLibros);
		panelForm.add(btnPrestar);

		panelNorte.add(panelForm, BorderLayout.CENTER);
		add(panelNorte, BorderLayout.NORTH);

		inicializarTabla();
		JScrollPane scrollTabla = new JScrollPane(tablaPrestamos);
		scrollTabla.setBorder(new EmptyBorder(5, 15, 5, 15));
		add(scrollTabla, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		btnDevolver = new JButton("Marcar devolución");
		btnEliminar = new JButton("Eliminar préstamo");
		btnRefrescar = new JButton("Refrescar");
		btnVolver = new JButton("Volver");

		panelBotones.add(btnDevolver);
		panelBotones.add(btnEliminar);
		panelBotones.add(btnRefrescar);
		panelBotones.add(btnVolver);
		add(panelBotones, BorderLayout.SOUTH);

		cargarCombos();

		boolean esEstudiante = "estudiante".equalsIgnoreCase(ventanaPrincipal.getLoginUsuario().getRol());
		if (esEstudiante) {
			btnEliminar.setEnabled(false);
			cmbEstudiantes.setSelectedItem(getEstudianteLogueado());
			cmbEstudiantes.setEnabled(false);
		}

		cargarDatosTabla();
		configurarEventos();
	}

	private Estudiante getEstudianteLogueado() {
		String rutLogueado = ventanaPrincipal.getLoginUsuario().getRut();
		for (int i = 0; i < cmbEstudiantes.getItemCount(); i++) {
			Estudiante est = cmbEstudiantes.getItemAt(i);
			if (est.getRut().equalsIgnoreCase(rutLogueado)) {
				return est;
			}
		}
		return null;
	}

	private void inicializarTabla() {
		String[] columnas = {"ID", "ID Estudiante", "ID Libro", "Fecha Préstamo", "Fecha Devolución", "Estado"};
		modeloTabla = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tablaPrestamos = new JTable(modeloTabla);
	}

	public void cargarCombos() {
		try {
			cmbEstudiantes.removeAllItems();
			List<Estudiante> estudiantes = estudianteDAO.listarTodos();
			for (Estudiante e : estudiantes) {
				cmbEstudiantes.addItem(e);
			}

			cmbLibros.removeAllItems();
			List<Libro> libros = libroDAO.listarTodos();
			for (Libro l : libros) {
				cmbLibros.addItem(l);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al cargar listas: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cargarDatosTabla() {
		modeloTabla.setRowCount(0);
		try {
			boolean esEstudiante = "estudiante".equalsIgnoreCase(ventanaPrincipal.getLoginUsuario().getRol());
			Estudiante alumno = esEstudiante ? getEstudianteLogueado() : null;

			List<Prestamo> lista = prestamoDAO.listarPrestamos();
			for (Prestamo p : lista) {
				if (alumno != null && p.getIdEstudiante() != alumno.getId()) {
					continue;
				}

				Object[] fila = {
						p.getId(),
						p.getIdEstudiante(),
						p.getIdLibro(),
						p.getFechaPrestamo(),
						p.getFechaDevolucion(),
						p.isDevuelto() ? "Devuelto" : "Pendiente"
				};
				modeloTabla.addRow(fila);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al cargar préstamos: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void configurarEventos() {
		btnPrestar.addActionListener(e -> registrarPrestamoEnSegundoPlano());
		btnDevolver.addActionListener(e -> marcarDevolucion());
		btnEliminar.addActionListener(e -> eliminarPrestamo());
		btnRefrescar.addActionListener(e -> {
			cargarCombos();
			if ("estudiante".equalsIgnoreCase(ventanaPrincipal.getLoginUsuario().getRol())) {
				cmbEstudiantes.setSelectedItem(getEstudianteLogueado());
			}
			cargarDatosTabla();
			JOptionPane.showMessageDialog(this, "Datos actualizados.", "Información", JOptionPane.INFORMATION_MESSAGE);
		});
		btnVolver.addActionListener(e -> volverAlMenu());
	}

	private void registrarPrestamoEnSegundoPlano() {
		Estudiante estudianteSeleccionado = (Estudiante) cmbEstudiantes.getSelectedItem();
		Libro libroSeleccionado = (Libro) cmbLibros.getSelectedItem();

		if (estudianteSeleccionado == null || libroSeleccionado == null) {
			JOptionPane.showMessageDialog(this, "Debe seleccionar un estudiante y un libro.", "Validación", JOptionPane.WARNING_MESSAGE);
			return;
		}

		btnPrestar.setEnabled(false);

		PrestamoHilo tarea = new PrestamoHilo(
				servicio,
				estudianteSeleccionado.getId(),
				libroSeleccionado.getId(),
				this,
				() -> {
					btnPrestar.setEnabled(true);
					cargarCombos();
					if ("estudiante".equalsIgnoreCase(ventanaPrincipal.getLoginUsuario().getRol())) {
						cmbEstudiantes.setSelectedItem(getEstudianteLogueado());
					}
					cargarDatosTabla();
				}
		);

		new Thread(tarea).start();
	}

	private void marcarDevolucion() {
		int fila = tablaPrestamos.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un préstamo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int id = (int) modeloTabla.getValueAt(fila, 0);
		String estado = modeloTabla.getValueAt(fila, 5).toString();

		if ("Devuelto".equalsIgnoreCase(estado)) {
			JOptionPane.showMessageDialog(this, "Este préstamo ya se encuentra devuelto.", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			if (servicio.devolucionPrestamo(id)) {
				JOptionPane.showMessageDialog(this, "Devolución registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
				cargarCombos();
				if ("estudiante".equalsIgnoreCase(ventanaPrincipal.getLoginUsuario().getRol())) {
					cmbEstudiantes.setSelectedItem(getEstudianteLogueado());
				}
				cargarDatosTabla();
			} else {
				JOptionPane.showMessageDialog(this, "No se pudo registrar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al registrar devolución: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void eliminarPrestamo() {
		int fila = tablaPrestamos.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un préstamo para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int id = (int) modeloTabla.getValueAt(fila, 0);

		int confirm = JOptionPane.showConfirmDialog(this, "Seguro que desea eliminar el préstamo ID: " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				if (prestamoDAO.eliminarPrestamo(id)) {
					JOptionPane.showMessageDialog(this, "Préstamo eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
					cargarDatosTabla();
				}
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this, "Error al eliminar préstamo: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void volverAlMenu() {
		ventanaPrincipal.setVisible(true);
		this.dispose();
	}
}