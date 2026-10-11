package cl.model;

import cl.interfaces.Prestable;

/**
 * Representa los libros disponibles en la biblioteca.
 * Implementa {@link cl.interfaces.Prestable} para gestionar stock y disponibilidad.
 */
public class Libro implements Prestable {
	private int id;
	private String titulo;
	private String autor;
	private String isbn;
	private String editorial;
	private int stock;
	private int idCategoria;

	public Libro() {
	}

	public Libro(int id, String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
		this.id = id;
		this.titulo = titulo;
		this.autor = autor;
		this.isbn = isbn;
		this.editorial = editorial;
		this.stock = stock;
		this.idCategoria = idCategoria;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getEditorial() {
		return editorial;
	}

	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}

	@Override
	public boolean disponible() {
		return stock > 0;
	}

	@Override
	public void agregarStock() {
		stock++;
	}

	@Override
	public void quitarStock() {
		if (stock > 0) {
			stock--;
		}
	}

	@Override
	public String toString() {
		return titulo + " " + autor + " " + isbn + " " + editorial + " " + stock;
	}
}
