package cl.dao;

import cl.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable del manejo de categorías de los libros para la interacción con Swing.
 */
public class CategoriaDAO {

	public List<Categoria> listarCategorias() throws SQLException {
		List<Categoria> lista = new ArrayList<>();
		String sql = "SELECT id, nombre FROM categorias ORDER by id ASC";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				lista.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
			}
		}
		return lista;
	}

	public Categoria filtrarPorId(int id) throws SQLException {
		String sql = "SELECT id, nombre FROM categorias WHERE id = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();
		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Categoria(rs.getInt("id"), rs.getString("nombre"));
				}
			}
		}
		return null;
	}
}
