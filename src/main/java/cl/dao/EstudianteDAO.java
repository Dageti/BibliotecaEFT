package cl.dao;

import cl.model.Estudiante;
import cl.util.RutInvalidoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable de operaciones CRUD para la tabla de estudiantes.
 */
public class EstudianteDAO {

	public boolean guardar(Estudiante estudiante) throws SQLException {
		String sql = "INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?,?, ?, ?)";
		Connection conexion = DatabaseConnection.getInstance().getConnection();

		try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, estudiante.getNombre());
			ps.setString(2, estudiante.getRut());
			ps.setString(3, estudiante.getCurso());
			ps.setString(4, estudiante.getEmail());
			int filas = ps.executeUpdate();
			if (filas > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						estudiante.setId(rs.getInt(1));
					}
				}
				return true;
			}
		}
		return false;
	}

	public List<Estudiante> listarTodos() throws SQLException {
		List<Estudiante> lista = new ArrayList<>();
		String sql = "SELECT id, nombre,rut, curso, correo FROM estudiantes ORDER BY id ASC";
		Connection conexion = DatabaseConnection.getInstance().getConnection();

		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					try {
						Estudiante estudiante = new Estudiante(
								rs.getInt("id"),
								rs.getString("nombre"),
								rs.getString("rut"),
								rs.getString("correo"),
								rs.getString("curso")
						);
						lista.add(estudiante);
					} catch (RutInvalidoException e) {
						System.err.println("Error al cargar estudiante: " + e.getMessage());
					}
				}
			}
			return lista;
		}
	}

	public Estudiante filtrarPorId(int id) throws SQLException {
		String sql = "SELECT id, nombre, rut, curso, correo FROM estudiantes WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();

		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					try {
						return new Estudiante(
								rs.getInt("id"),
								rs.getString("nombre"),
								rs.getString("rut"),
								rs.getString("correo"),
								rs.getString("curso")
						);
					} catch (RutInvalidoException e) {
						System.err.println("Error al cargar estudiante: " + e.getMessage());
					}
				}
			}
		}
		return null;
	}

	public boolean actualizar(Estudiante estudiante) throws SQLException {
		String sql = "UPDATE estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setString(1, estudiante.getNombre());
			ps.setString(2, estudiante.getRut());
			ps.setString(3, estudiante.getCurso());
			ps.setString(4, estudiante.getEmail());
			ps.setInt(5, estudiante.getId());

			return ps.executeUpdate() > 0;
		}
	}

	public boolean eliminar(int id) throws SQLException {
		String sql = "DELETE FROM estudiantes WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}
}


