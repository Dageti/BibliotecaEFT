package cl.dao;

import cl.model.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable de operaciones CRUD y control del manejo en tabla de préstamos.
 */
public class PrestamoDAO {

	public boolean guardarPrestamo(Prestamo prestamo) throws SQLException {
		String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?)";
		Connection conexion = DatabaseConnection.getInstance().getConnection();

		try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, prestamo.getIdEstudiante());
			ps.setInt(2, prestamo.getIdLibro());
			ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
			ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
			ps.setBoolean(5, prestamo.isDevuelto());


			int filas = ps.executeUpdate();
			if (filas > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						prestamo.setId(rs.getInt(1));
					}
				}
				return true;
			}
		}
		return false;
	}

	public List<Prestamo> listarPrestamos() throws SQLException {
		List<Prestamo> lista = new ArrayList<Prestamo>();
		String sql = "SELECT id, id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto FROM prestamos ORDER BY id DESC";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql);
		     ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Prestamo prestamo = new Prestamo(
						rs.getInt("id"),
						rs.getInt("id_estudiante"),
						rs.getInt("id_libro"),
						rs.getDate("fecha_prestamo").toLocalDate(),
						rs.getDate("fecha_devolucion").toLocalDate(),
						rs.getBoolean("devuelto"));
				lista.add(prestamo);
			}
		}
		return lista;
	}

	public Prestamo filtrarPrestamo(int id) throws SQLException {
		String sql = "SELECT id, id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto FROM prestamos WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Prestamo(
							rs.getInt("id"),
							rs.getInt("id_estudiante"),
							rs.getInt("id_libro"),
							rs.getDate("fecha_prestamo").toLocalDate(),
							rs.getDate("fecha_devolucion").toLocalDate(),
							rs.getBoolean("devuelto"));
				}
			}
		}
		return null;
	}

	public boolean devolucionPrestamo(int id) throws SQLException {
		String sql = "UPDATE prestamos SET devuelto = TRUE WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean eliminarPrestamo(int id) throws SQLException {
		String sql = "DELETE FROM prestamos WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	public List<Object[]> librosMasPrestados() throws SQLException {
		List<Object[]> lista = new ArrayList<>();
		String sql = "SELECT l.id, l.titulo, l.autor, COUNT(p.id) AS total " +
				"FROM libros l " +
				"JOIN prestamos p ON l.id = p.id_libro " +
				"GROUP BY l.id, l.titulo, l.autor " +
				"ORDER BY total DESC";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql);
		     ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				lista.add(new Object[]{
						rs.getInt("id"),
						rs.getString("titulo"),
						rs.getString("autor"),
						rs.getInt("total")
				});
			}
		}
		return lista;
	}

	public List<Object[]> historialEstudiante(int idEstudiante) throws SQLException {
		List<Object[]> lista = new ArrayList<>();
		String sql = "SELECT p.id, l.titulo, p.fecha_prestamo, p.fecha_devolucion, p.devuelto " +
				"FROM prestamos p " +
				"JOIN libros l ON p.id_libro = l.id " +
				"WHERE p.id_estudiante = ? " +
				"ORDER BY p.id DESC";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, idEstudiante);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					java.time.LocalDate fechaDev = rs.getDate("fecha_devolucion").toLocalDate();
					boolean devuelto = rs.getBoolean("devuelto");
					String estado;
					if (devuelto) {
						estado = "Devuelto";
					} else if (java.time.LocalDate.now().isAfter(fechaDev)) {
						estado = "Atrasado";
					} else {
						estado = "Pendiente";
					}
					lista.add(new Object[]{
							rs.getInt("id"),
							rs.getString("titulo"),
							rs.getDate("fecha_prestamo").toLocalDate(),
							fechaDev,
							estado
					});
				}
			}
		}
		return lista;
	}

	public List<Object[]> librosEnPrestamo() throws SQLException {
		List<Object[]> lista = new ArrayList<>();
		String sql = "SELECT p.id, e.nombre, l.titulo, p.fecha_prestamo, p.fecha_devolucion " +
				"FROM prestamos p " +
				"JOIN estudiantes e ON p.id_estudiante = e.id " +
				"JOIN libros l ON p.id_libro = l.id " +
				"WHERE p.devuelto = FALSE " +
				"ORDER BY p.fecha_devolucion ASC";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql);
		     ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				java.time.LocalDate fechaDev = rs.getDate("fecha_devolucion").toLocalDate();
				String estado = java.time.LocalDate.now().isAfter(fechaDev) ? "Atrasado" : "Pendiente";
				lista.add(new Object[]{
						rs.getInt("id"),
						rs.getString("nombre"),
						rs.getString("titulo"),
						rs.getDate("fecha_prestamo").toLocalDate(),
						fechaDev,
						estado
				});
			}
		}
		return lista;
	}
}
