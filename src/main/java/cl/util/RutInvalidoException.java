package cl.util;

/**
 * Excepción personalizada para el incumplimiento del formato del rut.
 */
public class RutInvalidoException extends Exception {
	public RutInvalidoException(String mensaje) {
		super(mensaje);
	}

}
