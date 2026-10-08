package cl.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
	private static final String USER = "root";
	private static final String PASSWORD = "dev1";

	private static DatabaseConnection singleton;
	private Connection connection;

	private DatabaseConnection() {
		conectar();
	}

	private void conectar() {
		try {
			this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			System.err.println("Error al conectar con la base de datos: " + e.getMessage());
		}
	}

	public static synchronized DatabaseConnection getInstance() {
		if (singleton == null) {
			singleton = new DatabaseConnection();
		}
		return singleton;
	}
	public  Connection getConnection() {
		return this.connection;
	}
}
