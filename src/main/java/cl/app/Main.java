package cl.app;

import cl.view.VentanaLogin;

import javax.swing.*;

/**
 * Punto de acceso al gestor de Biblioteca.
 * Inicializa en la vista de Login para validar credenciales antes de dar acceso al sistema.
 */
public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new VentanaLogin().setVisible(true);
		});
	}
}
