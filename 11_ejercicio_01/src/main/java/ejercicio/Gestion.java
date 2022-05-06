package ejercicio;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONObject;

public class Gestion {
	public static void main(String[] args) throws Exception {
		AccesoDisco ad= new AccesoDisco();
		try {
			//System.out.println(ad.cargaFicheroDeTexto("autores.csv"));
			//ad.CreaDDL("libreria1.sqlite", "autores.csv");
			//ad.grabaHashDB("libreria1.sqlite", "autores.csv");
			//ad.serializaDB("libreria1.sqlite", "autores.ser");
			ad.grabaJSON("libreria1.sqlite", "autores1.json");
			ad.leeJSON("autores.json", "libreria2.sqlite");
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
