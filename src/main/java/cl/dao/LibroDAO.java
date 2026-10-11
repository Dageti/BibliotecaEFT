package cl.dao;

import cl.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable de operaciones CRUD para manejar inventario e iterar sobre tabla de libros.
 */
public class LibroDAO {

	public boolean guardar(Libro libro) throws SQLException {
		String sql = "INSERT INTO libros (titulo,autor,isbn, editorial, stock, id_categoria) VALUES (?,?,?,?,?,?)";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, libro.getTitulo());
			ps.setString(2, libro.getAutor());
			ps.setString(3, libro.getIsbn());
			ps.setString(4, libro.getEditorial());
			ps.setInt(5, libro.getStock());
			ps.setInt(6, libro.getIdCategoria());
			ps.setInt(7, libro.getId());

			int filas = ps.executeUpdate();
			if (filas > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						libro.setId(rs.getInt(1));
					}
				}
				return true;
			}
		}
		return false;
	}

	public Libro filtrarPorId(int id) throws SQLException {
		String sql = "SELECT id, titulo, autor, isbn, editorial, stock, id_categoria FROM libros WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Libro(
							rs.getInt("id"),
							rs.getString("titulo"),
							rs.getString("autor"),
							rs.getString("isbn"),
							rs.getString("editorial"),
							rs.getInt("stock"),
							rs.getInt("id_categoria")
					);
				}
			}
		}
		return null;
	}

	public boolean actualizarLibro(Libro libro) throws SQLException {
		String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ? WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setString(1, libro.getTitulo());
			ps.setString(2, libro.getAutor());
			ps.setString(3, libro.getIsbn());
			ps.setString(4, libro.getEditorial());
			ps.setInt(5, libro.getStock());
			ps.setInt(6, libro.getId());

			return ps.executeUpdate() > 0;
		}
	}

	public boolean actualizarStock(int idLibro, int nuevoStock) throws SQLException {
		String sql = "UPDATE libros SET stock = ? WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, nuevoStock);
			ps.setInt(2, idLibro);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean eliminarLibro(int id) throws SQLException {
		String sql = "DELETE FROM libros WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	public List<Libro> listarTodos() throws SQLException {
		List<Libro> lista = new ArrayList<>();
		String sql = "SELECT id, titulo, autor, isbn, editorial, stock, id_categoria FROM libros ORDER BY id ASC";
		Connection conexion = DatabaseConnection.getInstance().getConnection();

		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Libro libro = new Libro(
						rs.getInt("id"),
						rs.getString("titulo"),
						rs.getString("autor"),
						rs.getString("isbn"),
						rs.getString("editorial"),
						rs.getInt("stock"),
						rs.getInt("id_categoria")
				);
				lista.add(libro);
			}
		}
		return lista;
	}
}
