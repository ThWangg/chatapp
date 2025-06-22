package Model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
	private static final String url = "jdbc:mysql://localhost:3306/chatapp";
	private static final String user = "root";
	private static final String pass = "";
	
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(url, user, pass);
	}
}