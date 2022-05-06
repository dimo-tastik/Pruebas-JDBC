package ejercicio;

public class NombreAutorException extends RuntimeException {
	public NombreAutorException() {
		super("Nombre incorrecto");
	}
	public NombreAutorException(String mensa) {
		super("Nombre incorrecto" + mensa);
	}
}
