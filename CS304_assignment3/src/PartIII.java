import java.sql.*;

public class PartIII {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// Load and register Oracle JDBC thin driver	
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException ex) {
			System.out.println("An exception was thrown while registering the Oracle driver" + ex.getMessage());
		}
		
		String url = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug";
		String userName = "ora_x5w8";
		String passWord = "a71847065";
						
		// Connect to the database
		Connection con = DriverManager.getConnection(url, userName, passWord);

		// Create a SQL Statement object
		Statement stmt = con.createStatement();
		// Assignment #03 - Part 3 Query
		ResultSet rs = stmt.executeQuery(
				// Set of upc that are textbooks and have stock < 10
					"SELECT i.upc "
				  + "FROM item i, book b "
				  + "WHERE i.upc = b.upc "
				  +    "AND b.flag_text = 'y' "
				  +    "AND i.stock < '10' "
				// Intersected with...
				  + "INTERSECT "
				// Set of upc that sold > 50 last week
				  + "SELECT upc FROM " 
				  + 	"(SELECT upc, SUM(quantity) as soldLastWeek "
				  +      "FROM itemPurchase IP, purchase P "
				  +      "WHERE IP.t_id = P.t_id "						//natural join
				  + 	 	"AND P.purchaseDate >= '25-OCT-15' "	    //purchases made between Oct25 - Oct 31
				  + 		"AND P.purchaseDate <= '31-OCT-15' "
				  + 	 "GROUP BY upc)"
				  + "WHERE soldLastWeek > '50'"
				);
		
		System.out.println("Textbooks that sold more than 50 copies (between Oct 25 - Oct 31):");
		while(rs.next())
		{
			String upc = rs.getString("upc");
			System.out.println("(upc: " + upc + ")");
		}
		// Close the connection to free up memory
		stmt.close();
	}
}
