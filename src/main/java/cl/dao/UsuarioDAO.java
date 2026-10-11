package cl.dao;

import cl.model.Usuario;
import cl.util.RutInvalidoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para autenticación y consulta de usuarios en la base de datos.
 */
public class UsuarioDAO {

	public Usuario login(String correo, String password) throws SQLException {
		String sql = "SELECT id, nombre, rut, correo, contraseña, rol FROM usuarios WHERE correo = ? AND contraseña = ?";
		Connection conexion = DatabaseConnection.getInstance().getConnection();

		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setString(1, correo);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					try {
						return new Usuario(
								rs.getInt("id"),
								rs.getString("nombre"),
								rs.getString("rut"),
								rs.getString("correo"),
								rs.getString("contraseña"),
								rs.getString("rol")
						);
					} catch (RutInvalidoException e) {
						System.err.println("error al cargar usuario: " + e.getMessage());
					}
				}
			}
		}
		return null;
	}
}
