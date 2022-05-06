package ejercicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Autor implements Comparable<Autor>, Serializable{// serializa
	private int id;
	private String nombre;
	private String nacionalidad;
	private String blog;
	//private List<Libro> libros;

	public Autor(int id, String nombre, String nacionalidad, String blog) {
		super();
		this.id = id;
		setNombre(nombre);
		this.nacionalidad = nacionalidad;
		this.blog = blog;
		//this.libros= new ArrayList<>();
	}

	public Autor(String autorTxt) {
		try (Scanner sc = new Scanner(autorTxt).useDelimiter(",");) {
			this.id = sc.nextInt();
			setNombre(sc.next());
			this.nacionalidad = sc.next();
			this.blog = sc.next();
		}
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
		if (nombre.isBlank() || nombre.length() < 7) {
			throw new NombreAutorException(nombre);
		}
		this.nombre = nombre;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	@Override
	public String toString() {
		return String.format("Autor [id=%s, nombre=%s, nacionalidad=%s, blog=%s]", id, nombre, nacionalidad,
				blog);
	}

	@Override
	public int compareTo(Autor a) {
		int comparacion = this.nombre.compareTo(a.nombre);
		if (comparacion == 0) {
			comparacion = this.id - a.id;
		}
		return comparacion;
	}
}
