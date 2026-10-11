package cl.model;

import cl.util.RutInvalidoException;

/**
 * Representa un estudiante registrado en la biblioteca.
 * Hereda de {@link Persona}
 */
public class Estudiante extends Persona {
	private String curso;

	public Estudiante() {
	}

	public Estudiante(int id, String nombre, String rut, String correo, String curso) throws RutInvalidoException {
		super(id, nombre, rut, correo);
		this.curso = curso;

	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	@Override
	public String getRolPersona() {
		return "Estudiante";
	}

	@Override
	public String toString() {
		return nombre + " " + rut + curso;
	}
}
