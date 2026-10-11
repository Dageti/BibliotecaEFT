package cl.view;

import cl.dao.EstudianteDAO;
import cl.dao.PrestamoDAO;
import cl.model.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz gráfica encargada de mostrar información relevante sobre el comportamiento del flujo de la biblioteca.
 */
public class VentanaReportes extends JFrame {

	private final VentanaPrincipal ventanaPrincipal;
	private final PrestamoDAO prestamoDAO;
	private final EstudianteDAO estudianteDAO;

	private JComboBox<Estudiante> cmbEstudiantes;
	private JTable tablaReportes;
	private DefaultTableModel modeloTabla;

	private JButton btnMasPrestados;
	private JButton btnHistorial;
	private JButton btnActivos;
	private JButton btnVolver;

	public VentanaReportes(VentanaPrincipal ventanaPrincipal) {
		this.ventanaPrincipal = ventanaPrincipal;
		this.prestamoDAO = new PrestamoDAO();
		this.estudianteDAO = new EstudianteDAO();

		setTitle("Reportes de Biblioteca");
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JPanel panelNorte = new JPanel(new GridLayout(2, 1));
		JLabel lblTitulo = new JLabel("Reportes del sistema", SwingConstants.CENTER);
		panelNorte.add(lblTitulo);

		JPanel panelFiltro = new JPanel(new FlowLayout());
		panelFiltro.add(new JLabel("Historial del estudiante:"));
		cmbEstudiantes = new JComboBox<>();
		panelFiltro.add(cmbEstudiantes);
		panelNorte.add(panelFiltro);

		add(panelNorte, BorderLayout.NORTH);

		modeloTabla = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tablaReportes = new JTable(modeloTabla);
		add(new JScrollPane(tablaReportes), BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new FlowLayout());
		btnMasPrestados = new JButton("Libros más prestados");
		btnHistorial = new JButton("Historial por estudiante");
		btnActivos = new JButton("Prestamos con atraso");
		btnVolver = new JButton("Volver");

		panelBotones.add(btnMasPrestados);
		panelBotones.add(btnHistorial);
		panelBotones.add(btnActivos);
		panelBotones.add(btnVolver);
		add(panelBotones, BorderLayout.SOUTH);

		cargarEstudiantes();
		configurarEventos();

		cargarLibrosMasPrestados();
	}

	private void cargarEstudiantes() {
		try {
			cmbEstudiantes.removeAllItems();
			List<Estudiante> lista = estudianteDAO.listarTodos();
			for (Estudiante e : lista) {
				cmbEstudiantes.addItem(e);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al cargar estudiantes: " + ex.getMessage());
		}
	}

	private void configurarEventos() {
		btnMasPrestados.addActionListener(e -> cargarLibrosMasPrestados());
		btnHistorial.addActionListener(e -> cargarHistorialEstudiante());
		btnActivos.addActionListener(e -> cargarLibrosActivos());
		btnVolver.addActionListener(e -> {
			dispose();
			ventanaPrincipal.setVisible(true);
		});
	}

	private void cargarLibrosMasPrestados() {
		String[] columnas = {"ID libro", "Título", "Autor", "Total préstamos"};
		modeloTabla.setColumnIdentifiers(columnas);
		modeloTabla.setRowCount(0);

		try {
			List<Object[]> datos = prestamoDAO.librosMasPrestados();
			for (Object[] fila : datos) {
				modeloTabla.addRow(fila);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al cargar reporte: " + ex.getMessage());
		}
	}

	private void cargarHistorialEstudiante() {
		Estudiante estudiante = (Estudiante) cmbEstudiantes.getSelectedItem();
		if (estudiante == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la lista.");
			return;
		}

		String[] columnas = {"ID Préstamo", "Título", "Fecha préstamo", "Fecha devolución", "Estado"};
		modeloTabla.setColumnIdentifiers(columnas);
		modeloTabla.setRowCount(0);

		try {
			List<Object[]> datos = prestamoDAO.historialEstudiante(estudiante.getId());
			for (Object[] fila : datos) {
				modeloTabla.addRow(fila);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al cargar historial: " + ex.getMessage());
		}
	}

	private void cargarLibrosActivos() {
		String[] columnas = {"ID préstamo", "Estudiante", "Título", "Fecha préstamo", "Fecha vencimiento", "Estado"};
		modeloTabla.setColumnIdentifiers(columnas);
		modeloTabla.setRowCount(0);

		try {
			List<Object[]> datos = prestamoDAO.librosEnPrestamo();
			for (Object[] fila : datos) {
				modeloTabla.addRow(fila);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al cargar libros en préstamo: " + ex.getMessage());
		}
	}
}