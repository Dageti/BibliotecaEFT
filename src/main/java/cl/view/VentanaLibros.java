package cl.view;

import cl.dao.CategoriaDAO;
import cl.dao.LibroDAO;
import cl.model.Categoria;
import cl.model.Libro;

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
 * Interfaz gráfica para administración y/o visualización del inventario de libros según rol.
 */
public class VentanaLibros extends JFrame {

	private final VentanaPrincipal ventanaPrincipal;
	private final LibroDAO libroDAO;
	private final CategoriaDAO categoriaDAO;

	private JTable tablaLibros;
	private DefaultTableModel modeloTabla;

	private JTextField txtId;
	private JTextField txtTitulo;
	private JTextField txtAutor;
	private JTextField txtIsbn;
	private JTextField txtEditorial;
	private JTextField txtStock;
	private JComboBox<Categoria> cmbCategorias;

	private JButton btnGuardar;
	private JButton btnActualizar;
	private JButton btnEliminar;
	private JButton btnLimpiar;
	private JButton btnRefrescar;
	private JButton btnVolver;

	public VentanaLibros(VentanaPrincipal ventanaPrincipal) {
		this.ventanaPrincipal = ventanaPrincipal;
		this.libroDAO = new LibroDAO();
		this.categoriaDAO = new CategoriaDAO();

		setTitle("Gestor de libros");
		setSize(850, 600);
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

		JLabel lblTitulo = new JLabel("Libros disponibles", SwingConstants.CENTER);
		panelNorte.add(lblTitulo, BorderLayout.NORTH);

		JPanel panelForm = new JPanel(new GridLayout(4, 4, 10, 8));
		panelForm.setBorder(new EmptyBorder(10, 5, 10, 5));

		txtId = new JTextField();
		txtId.setEditable(false);
		txtTitulo = new JTextField();
		txtAutor = new JTextField();
		txtIsbn = new JTextField();
		txtEditorial = new JTextField();
		txtStock = new JTextField();
		cmbCategorias = new JComboBox<>();

		panelForm.add(new JLabel("ID:"));
		panelForm.add(txtId);
		panelForm.add(new JLabel("Título:"));
		panelForm.add(txtTitulo);

		panelForm.add(new JLabel("Autor:"));
		panelForm.add(txtAutor);
		panelForm.add(new JLabel("ISBN:"));
		panelForm.add(txtIsbn);

		panelForm.add(new JLabel("Editorial:"));
		panelForm.add(txtEditorial);
		panelForm.add(new JLabel("Stock:"));
		panelForm.add(txtStock);

		panelForm.add(new JLabel("Categoría:"));
		panelForm.add(cmbCategorias);
		panelForm.add(new JLabel(""));

		panelNorte.add(panelForm, BorderLayout.CENTER);
		add(panelNorte, BorderLayout.NORTH);

		inicializarTabla();
		JScrollPane scrollTabla = new JScrollPane(tablaLibros);
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

		cargarCategorias();
		cargarDatosTabla();
		configurarEventos();
	}

	private void inicializarTabla() {
		String[] columnas = {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "ID Categoria"};
		modeloTabla = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tablaLibros = new JTable(modeloTabla);
	}

	private void cargarCategorias() {
		try {
			cmbCategorias.removeAllItems();
			List<Categoria> categorias = categoriaDAO.listarCategorias();
			for (Categoria c : categorias) {
				cmbCategorias.addItem(c);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cargarDatosTabla() {
		modeloTabla.setRowCount(0);
		try {
			List<Libro> lista = libroDAO.listarTodos();
			for (Libro l : lista) {
				Object[] fila = {
						l.getId(),
						l.getTitulo(),
						l.getAutor(),
						l.getIsbn(),
						l.getEditorial(),
						l.getStock(),
						l.getIdCategoria()
				};
				modeloTabla.addRow(fila);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al cargar libros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		boolean esEstudiante = "estudiante".equalsIgnoreCase(ventanaPrincipal.getLoginUsuario().getRol());
		if (esEstudiante) {
			setTitle("Catálogo de libros");
			btnGuardar.setEnabled(false);
			btnActualizar.setEnabled(false);
			btnEliminar.setEnabled(false);
			btnLimpiar.setEnabled(false);

			txtTitulo.setEditable(false);
			txtAutor.setEditable(false);
			txtEditorial.setEditable(false);
			txtIsbn.setEditable(false);
			txtStock.setEditable(false);
			cmbCategorias.setEnabled(false);
		}
	}


	private void configurarEventos() {
		tablaLibros.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int fila = tablaLibros.getSelectedRow();
				if (fila != -1) {
					txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
					txtTitulo.setText(modeloTabla.getValueAt(fila, 1).toString());
					txtAutor.setText(modeloTabla.getValueAt(fila, 2).toString());
					txtIsbn.setText(modeloTabla.getValueAt(fila, 3).toString());
					txtEditorial.setText(modeloTabla.getValueAt(fila, 4).toString());
					txtStock.setText(modeloTabla.getValueAt(fila, 5).toString());
					int idCategoria = (int) modeloTabla.getValueAt(fila, 6);
					seleccionarCategoriaEnCombo(idCategoria);
				}
			}
		});

		btnGuardar.addActionListener(e -> guardarLibro());
		btnActualizar.addActionListener(e -> actualizarLibro());
		btnEliminar.addActionListener(e -> eliminarLibro());
		btnLimpiar.addActionListener(e -> limpiarFormulario());
		btnRefrescar.addActionListener(e -> {
			cargarDatosTabla();
			JOptionPane.showMessageDialog(this, "Tabla actualizada", "Información", JOptionPane.INFORMATION_MESSAGE);
		});
		btnVolver.addActionListener(e -> volverAlMenu());
	}

	private void guardarLibro() {
		if (!validarFormulario()) return;

		try {
			Categoria cat = (Categoria) cmbCategorias.getSelectedItem();
			Libro libro = new Libro(
					0,
					txtTitulo.getText().trim(),
					txtAutor.getText().trim(),
					txtIsbn.getText().trim(),
					txtEditorial.getText().trim(),
					Integer.parseInt(txtStock.getText().trim()),
					cat.getId()
			);

			if (libroDAO.guardar(libro)) {
				JOptionPane.showMessageDialog(this, "Libro guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
				cargarDatosTabla();
				limpiarFormulario();
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al guardar libro: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void actualizarLibro() {
		if (txtId.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla para actualizar", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!validarFormulario()) return;

		try {
			Categoria cat = (Categoria) cmbCategorias.getSelectedItem();
			Libro libro = new Libro(
					Integer.parseInt(txtId.getText()),
					txtTitulo.getText().trim(),
					txtAutor.getText().trim(),
					txtIsbn.getText().trim(),
					txtEditorial.getText().trim(),
					Integer.parseInt(txtStock.getText().trim()),
					cat.getId()
			);

			if (libroDAO.actualizarLibro(libro)) {
				JOptionPane.showMessageDialog(this, "Libro actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
				cargarDatosTabla();
				limpiarFormulario();
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error al actualizar libro: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void eliminarLibro() {
		int fila = tablaLibros.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un libro para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int id = (int) modeloTabla.getValueAt(fila, 0);
		String titulo = modeloTabla.getValueAt(fila, 1).toString();

		int confirm = JOptionPane.showConfirmDialog(this, "Seguro que desea eliminar el libro '" + titulo + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				if (libroDAO.eliminarLibro(id)) {
					JOptionPane.showMessageDialog(this, "Libro eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
					cargarDatosTabla();
					limpiarFormulario();
				}
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean validarFormulario() {
		if (txtTitulo.getText().trim().isEmpty() ||
				txtAutor.getText().trim().isEmpty() ||
				txtIsbn.getText().trim().isEmpty() ||
				txtEditorial.getText().trim().isEmpty() ||
				txtStock.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		try {
			int stock = Integer.parseInt(txtStock.getText().trim());
			if (stock < 0) {
				JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", "Validación", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El stock debe ser un número entero válido", "Validación", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (cmbCategorias.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría", "Validación", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	private void seleccionarCategoriaEnCombo(int idCategoria) {
		for (int i = 0; i < cmbCategorias.getItemCount(); i++) {
			Categoria c = cmbCategorias.getItemAt(i);
			if (c.getId() == idCategoria) {
				cmbCategorias.setSelectedIndex(i);
				break;
			}
		}
	}

	private void limpiarFormulario() {
		txtId.setText("");
		txtTitulo.setText("");
		txtAutor.setText("");
		txtIsbn.setText("");
		txtEditorial.setText("");
		txtStock.setText("");
		if (cmbCategorias.getItemCount() > 0) {
			cmbCategorias.setSelectedIndex(0);
		}
	}

	private void volverAlMenu() {
		ventanaPrincipal.setVisible(true);
		this.dispose();
	}
}