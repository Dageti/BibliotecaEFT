package cl.interfaces;

/**
 *  Contrato de comportamiento para elementos de la biblioteca, define el manejo de inventario y disponibilidad.
 */

public interface Prestable {
	boolean disponible();
	void agregarStock();
	void quitarStock();
}
