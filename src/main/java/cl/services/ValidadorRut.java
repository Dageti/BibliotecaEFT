package cl.services;

/**
 * Dedicado a comprobar la validez del rut mediante REGEX.
 */

public class ValidadorRut {
	private static final  String REGEX = "[0-9]{7,8}-[0-9kK]";

	public static boolean rutValido (String rut) {
		return rut != null && rut.matches(REGEX);
	}

	public static String mayuscula (String rut) {
		if (rut == null) {
			return null;
		}
		return rut.trim().toUpperCase();
	}
}
