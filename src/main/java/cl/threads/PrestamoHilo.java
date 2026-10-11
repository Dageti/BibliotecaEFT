package cl.threads;

import cl.services.BibliotecaService;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Manejo asíncrono del proceso de registro de préstamos mediante hilos.
 */
public class PrestamoHilo implements Runnable {

	private final BibliotecaService service;
	private final int idLibro;
	private final int idEstudiante;
	private final JFrame ventanaPrincipal;
	private final Runnable finalizar;

	public PrestamoHilo(BibliotecaService service, int idEstudiante, int idLibro, JFrame ventanaPrincipal, Runnable finalizar) {
		this.service = service;
		this.idEstudiante = idEstudiante;
		this.idLibro = idLibro;
		this.ventanaPrincipal = ventanaPrincipal;
		this.finalizar = finalizar;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);

			boolean ejecucion = service.realizarPrestamo(idEstudiante, idLibro);
			SwingUtilities.invokeLater(() -> {
				if (ejecucion) {
					JOptionPane.showMessageDialog(ventanaPrincipal, "La prestamo se ha realizado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
				}
				if (finalizar != null) {
					finalizar.run();
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (SQLException e) {
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(ventanaPrincipal, "Error al realizar la prestamo", "Error", JOptionPane.ERROR_MESSAGE);
			});
		}
	}

}
