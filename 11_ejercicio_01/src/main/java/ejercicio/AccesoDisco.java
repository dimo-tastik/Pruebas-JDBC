package ejercicio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class AccesoDisco {
	private Map<Integer, Autor> hash = new HashMap();

	public void cargaFicheroDeTexto(String nombre) throws FileNotFoundException, IOException {
		File f = new File(nombre);
		String linea;
		try (FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr)) {
			linea = br.readLine();
			linea = br.readLine();
			while (linea != null) {
				Autor autor = new Autor(linea);
				hash.put(autor.getId(), autor);
				linea = br.readLine();
			}
		}
	}

	private String[] getColumnas(String fiTxt) throws IOException {
		try (FileReader fr = new FileReader(fiTxt); BufferedReader br = new BufferedReader(fr)) {
			return br.readLine().split(",");
		}
	}

	public void CreaDDL(String db, String fiTxt) throws SQLException, IOException {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
				Statement st = conn.createStatement();) {
			conn.setAutoCommit(false);
			String[] col = getColumnas(fiTxt);
			String create = String.format("create table if not exists %s (" + "%s integer primary key autoincrement,"
					+ "%s varchar(29)," + "%s varchar(20)," + "%s text);", "autores", col[0], col[1], col[2], col[3]);
			st.executeUpdate(create);
			conn.commit();
		} catch (SQLException e) {
			// conn.rollback();
		}
	}

	public void grabaHashDB(String db, String fiTxt) throws IOException, SQLException {
		String insert = "insert into autores values (?, ?, ?, ?)";
		cargaFicheroDeTexto(fiTxt);
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
				PreparedStatement st = conn.prepareStatement(insert);) {
			for (Entry<Integer, Autor> autor : hash.entrySet()) {
				// System.out.println(autor);
				// st.setInt(1, autor.getKey());
				if (autor.getKey() < 0) {
					st.setObject(1, null);
				} else {
					st.setInt(1, autor.getKey());
				}
				st.setString(2, autor.getValue().getNombre());
				st.setString(3, autor.getValue().getNacionalidad());
				st.setString(4, autor.getValue().getBlog());
				st.executeUpdate();
				try {
					st.executeUpdate();
				} catch (SQLException e) {
					System.out.println("repetida: " + autor.getKey());
				}
			}

		}
	}

	public void serializaDB(String db, String fiSer) throws IOException, SQLException {
		String query = "select * from autores";
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
				PreparedStatement st = conn.prepareStatement(query);
				ResultSet rs = st.executeQuery();
				FileOutputStream fos = new FileOutputStream(fiSer);
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			while (rs.next()) {
				oos.writeObject(getAutor(rs));
			}
		}
	}

	public Autor getAutor(ResultSet rs) throws SQLException {
		Autor autor = new Autor(rs.getInt(1), rs.getString("nombre"), rs.getString(3), rs.getString(4));
		return autor;

	}

	public void grabaJSON(String db, String json) throws IOException, SQLException {
		String query = "select * from autores";
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
				PreparedStatement st = conn.prepareStatement(query);
				ResultSet rs = st.executeQuery();
				FileOutputStream fos = new FileOutputStream(json);) {
			JSONArray autores = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				JSONObject a = new JSONObject();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					a.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				autores.put(a);
			}
			System.out.println(autores.toString(4));

			fos.write(autores.toString(4).getBytes());
		}
	}

	public void leeJSON(String json, String db) throws IOException, SQLException {
		Scanner sc = new Scanner(new File(json));
		StringBuilder SB = new StringBuilder();
		while (sc.hasNext()) {
			SB.append(sc.nextLine());
		}
//		System.out.println(SB);
		JSONArray autores = new JSONArray(SB.toString());

		try (PrintWriter pw = new PrintWriter(new File("json.csv"));) {
			pw.println("id,nombre,nacionalidad,blog");

			for (int i = 0; i < autores.length(); i++) {
				JSONObject a = autores.getJSONObject(i);

				int id = a.getInt("id");
				String nombre = a.getString("nombre");
				String nacionalidad = a.getString("nacionalidad");
				String blog = a.getString("blog");

				Autor autor = new Autor(id, nombre, nacionalidad, blog);

//				System.out.println(autor.toString());
				pw.printf("%s,%s,%s,%s\n", autor.getId(), autor.getNombre(), autor.getNacionalidad(), autor.getBlog());
			}
		}

		CreaDDL(db, "json.csv");
	}
}
