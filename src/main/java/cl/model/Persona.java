package cl.model;

import cl.util.RutInvalidoException;
import cl.services.ValidadorRut;

/**
 * Clase abstracta con atributos comunes.
 * encapsula datos de la persona y valida el rut.
 */
public abstract class Persona {
	protected int id;
	protected String nombre;
	protected String rut;
	protected String correo;

	public abstract String getRolPersona();

	public Persona() {
	}

	public Persona(int id, String nombre, String rut, String correo) throws RutInvalidoException {
		this.id = id;
		this.nombre = nombre;
		setRut(rut);
		this.correo = correo;
	}

	public void setRut(String rut) throws RutInvalidoException {
		if (!ValidadorRut.rutValido(rut)){
			throw new RutInvalidoException("RUT inválido, por favor ingrese RUT sin puntos y con guión");
		}
		this.rut = ValidadorRut.mayuscula(rut);
	}

	public String getRut() {
		return rut;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return correo;
	}

	public void setEmail(String correo) {
		this.correo = correo;
	}

	@Override
	public String toString() {
		return "Persona{" +
				"id=" + id +
				", nombre='" + nombre + '\'' +
				", rut='" + rut + '\'' +
				", correo='" + correo + '\'' +
				'}';
	}
}

