package cl.view;

import cl.model.Persona;
import cl.model.Usuario;
import cl.services.BibliotecaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Menú principal de la App, maneja el flujo a traves de las distintas vistas.
 * Accesos restringidos según rol del usuario.
 */

public class VentanaPrincipal extends JFrame {
	private final Usuario loginUsuario;
	private final BibliotecaService service;


	private JButton btnGestionLibros;
	private JButton btnGestionEstudiantes;
	private JButton btnGestionPrestamos;
	private JButton btnReportes;
	private JButton btnLogout;

	public VentanaPrincipal(Usuario loginUsuario) {
		this.loginUsuario = loginUsuario;
		this.service = new BibliotecaService();
		Persona personaActiva = loginUsuario;

		setTitle("Gestor Biblioteca");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout(10, 10));

		JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));
		panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel lblTitulo = new JLabel("Sistema de gestión biblioteca escolar", SwingConstants.CENTER);
		JLabel lblSubtitulo = new JLabel("Usuario: " + personaActiva.getNombre() + " " + personaActiva.getRolPersona(), SwingConstants.CENTER);

		panelSuperior.add(lblTitulo);
		panelSuperior.add(lblSubtitulo);
		add(panelSuperior, BorderLayout.NORTH);

		JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelCentro.setBorder(new EmptyBorder(10, 10, 10, 10));
		btnGestionLibros = new JButton("Gestionar Libros");
		btnGestionEstudiantes = new JButton("Gestionar Estudiantes");
		btnGestionPrestamos = new JButton("Gestionar Prestamos");
		btnReportes = new JButton("Reportes");
		btnLogout = new JButton("Cerrar sesión");

		if ("estudiante".equalsIgnoreCase(loginUsuario.getRol())) {
			btnGestionEstudiantes.setEnabled(false);
			btnReportes.setEnabled(false);
		}
		panelCentro.add(btnGestionLibros);
		panelCentro.add(btnGestionEstudiantes);
		panelCentro.add(btnGestionPrestamos);
		panelCentro.add(btnReportes);
		panelCentro.add(btnLogout);
		add(panelCentro, BorderLayout.CENTER);

		configurarEventos();
	}

	private void configurarEventos() {
		btnGestionLibros.addActionListener(e -> {
			VentanaLibros ventanaLibros = new VentanaLibros(this);
			ventanaLibros.setVisible(true);
			this.setVisible(false);
		});
		btnGestionEstudiantes.addActionListener(e -> {
			VentanaEstudiantes ventanaEstudiantes = new VentanaEstudiantes(this);
			ventanaEstudiantes.setVisible(true);
			this.setVisible(false);
		});
		btnGestionPrestamos.addActionListener(e -> {
			VentanaPrestamos ventanaPrestamos = new VentanaPrestamos(service, this);
			ventanaPrestamos.setVisible(true);
			this.setVisible(false);
		});
		btnReportes.addActionListener(e -> {
			VentanaReportes ventanaReportes = new VentanaReportes(this);
			ventanaReportes.setVisible(true);
			this.setVisible(false);
		});
		btnLogout.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Desea cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				new VentanaLogin().setVisible(true);
				this.dispose();
			}
		});
	}

	public Usuario getLoginUsuario() {
		return loginUsuario;
	}

	public BibliotecaService getService() {
		return service;
	}
}
