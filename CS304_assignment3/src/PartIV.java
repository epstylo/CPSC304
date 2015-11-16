import java.sql.*;

public class PartIV {
	
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
		// Assignment #04 - Part 4 Query
		ResultSet rs = stmt.executeQuery(
				"SELECT * FROM "
			  + 	"(SELECT i.upc, i.sellingPrice*quantitySold AS salesAmount "
			  + 	"FROM item i, " 
			  + 		"(SELECT upc as upcSold, SUM(quantity) as quantitySold "
			  +      	"FROM itemPurchase IP, purchase P "
			  +      	"WHERE IP.t_id = P.t_id "						// natural join
			  + 	 		"AND P.purchaseDate >= '25-OCT-15' "		// purchases made between Oct25 - Oct 31
			  + 			"AND P.purchaseDate <= '31-OCT-15' "
			  + 	 	"GROUP BY upc)"
			  + 	"WHERE i.upc = upcSold "							// natural join
			  + 	"ORDER BY salesAmount DESC) "						// sort from highest sales descending
			  + "WHERE ROWNUM < 4"										// return top 3
			);
		
		System.out.println("Top 3 Sellers Last Week (between Oct 25 - Oct 31):");
		while(rs.next())
		{
			String upc = rs.getString("upc");
			String salesAmount = rs.getString("salesAmount");
			System.out.println("(upc: " + upc + ") -> $" + salesAmount);
		}
		// Close the connection to free up memory
		stmt.close();
	}
}
