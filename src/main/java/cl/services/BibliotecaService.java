package cl.services;

import cl.dao.LibroDAO;
import cl.dao.PrestamoDAO;
import cl.model.Libro;
import cl.model.Prestamo;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Servicio de logica del negocio.
 * Maneja préstamos y devoluciones de manera sincrónica para prevenir condiciones de carrera.
 */
public class BibliotecaService {
	private final LibroDAO libroDAO;
	private final PrestamoDAO prestamoDAO;

	public BibliotecaService() {
		this.libroDAO = new LibroDAO();
		this.prestamoDAO = new PrestamoDAO();
	}

	public synchronized boolean realizarPrestamo(int idEstudiante, int idLibro) throws SQLException {
		Libro libro = libroDAO.filtrarPorId(idLibro);

		if (libro != null && libro.disponible()) {
			libro.quitarStock();
			libroDAO.actualizarStock(libro.getId(), libro.getStock());

			Prestamo prestamo = new Prestamo(
					idEstudiante,
					idLibro,
					LocalDate.now(),
					LocalDate.now().plusDays(7),
					false
			);
			return prestamoDAO.guardarPrestamo(prestamo);
		}
		return false;
	}

	public synchronized boolean devolucionPrestamo(int idPrestamo) throws SQLException {
		Prestamo prestamo = prestamoDAO.filtrarPrestamo(idPrestamo);

		if (prestamo != null && !prestamo.isDevuelto()) {
			prestamoDAO.devolucionPrestamo(idPrestamo);
			Libro libro = libroDAO.filtrarPorId(prestamo.getIdLibro());
			if (libro != null) {
				libro.agregarStock();
				libroDAO.actualizarStock(libro.getId(), libro.getStock());
			}
			return true;
		}
		return false;
	}
}