package cl.model;

import cl.util.RutInvalidoException;

/**
 * Clase usuario representa una persona con acceso al sistema mediante credenciales, cuenta con un rol asignado.
 * Hereda de {@link Persona}
 */
public class Usuario extends Persona {
	protected String password;
	protected String rol;

	public Usuario() {
	}

	public Usuario(int id, String nombre, String rut, String correo, String password, String rol) throws RutInvalidoException {
		super(id, nombre, rut, correo);
		this.password = password;
		this.rol = rol;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	@Override
	public String getRolPersona() {
		return rol;
	}

	@Override
	public String toString() {
		return nombre + ", "+ rol;
	}
}
