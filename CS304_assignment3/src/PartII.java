import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PartII {

	// command line reader 
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    private Connection con;
    
    private static final String url = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug";
	private static final String userName = "ora_x5w8";
	private static final String passWord = "a71847065";
    
    public PartII() throws SQLException {
    	connect();
    	showOptions();
    }
    
	/* (a) Load and register Oracle JDBC thin driver	
     * (b) Establish connection with the database
     */
	private void connect() {
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println("An exception was thrown while registering the Oracle driver" + e.getMessage());
		}
				
		// Connect to the database
		try {
			con = DriverManager.getConnection(url, userName, passWord);
		} catch (SQLException e) {
			System.out.println("An exception was thrown while connecting to the database" + e.getMessage());
		}
	}
	
	/*
	 * Inserts an item into the (a) Item table
	 * 							(b) Book table
	 * TODO: Refactor code to utilize insertNonBookItem() code
	 */
	private void insertBookItem() throws SQLException {
		// Insert into the Item table
		String            upc ="";
		float             sellingPrice;
		int               stock;
		String			  taxable;
		PreparedStatement  ps;
		  
		try
		{
		  ps = con.prepareStatement("INSERT INTO item VALUES (?,?,?,?)");
		
		  System.out.print("\nupc: ");
		  upc = in.readLine();
		  
		  // If the item already exists, send an error msg back
		  if(itemExists(upc)) {
			  System.out.println("The item that you specified already exists!");
			  // Don't proceed through the rest of the code.
			  return;
		  }
		  ps.setString(1, upc);

		  System.out.print("\nSelling Price: ");
		  sellingPrice = Float.parseFloat(in.readLine());
		  ps.setFloat(2, sellingPrice);

		  System.out.print("\nStock: ");
		  stock = Integer.parseInt(in.readLine());
		  ps.setInt(3, stock);
		 
		  System.out.print("\nTaxable(y/n): ");
		  taxable = in.readLine();
		  ps.setString(4, taxable);
		  
		  ps.executeUpdate();

		  // commit work 
		  con.commit();

		  ps.close();
		}
		catch (IOException e)
		{
		    System.out.println("IOException!");
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		    try 
		    {
			// undo the insert
			con.rollback();	
		    }
		    catch (SQLException ex2)
		    {
			System.out.println("Message: " + ex2.getMessage());
			System.exit(-1);
		    }
		}
		
		// --------------------------------------------------------------------------------------------------------------
		  
		// Additional params to insert into the Book table
		String             title;
		String             publisher;
		String			   flagText;
		PreparedStatement  ps2;
		
		try
		{
		  ps2 = con.prepareStatement("INSERT INTO book VALUES (?,?,?,?)");
		  
		  // Set upc to be the same as the entry in the Item table
		  ps2.setString(1, upc);

		  System.out.print("\nTitle: ");
		  title = in.readLine();
		  ps2.setString(2, title);

		  System.out.print("\nPublisher: ");
		  publisher = in.readLine();
		  ps2.setString(3, publisher);
		 
		  System.out.print("\nTextbook(y/n): ");
		  flagText = in.readLine();
		  ps2.setString(4, flagText);
		  
		  ps2.executeUpdate();

		  // commit work 
		  con.commit();

		  ps2.close();
		}
		catch (IOException e)
		{
		    System.out.println("IOException!");
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		    try 
		    {
			// undo the insert
			con.rollback();	
		    }
		    catch (SQLException ex2)
		    {
			System.out.println("Message: " + ex2.getMessage());
			System.exit(-1);
		    }
		}
	}
	
	/*
	 * Inserts an item into the Item table only
	 * 
	 * Returns upc of item inserted
	 */
	private void insertNonBookItem() {
		String            upc;
		float             sellingPrice;
		int               stock;
		String			  taxable;
		PreparedStatement  ps;
		  
		try
		{
		  ps = con.prepareStatement("INSERT INTO item VALUES (?,?,?,?)");
		
		  System.out.print("\nupc: ");
		  upc = in.readLine();
		  
		  // If the item already exists, send an error msg back
		  if(itemExists(upc)) {
			  System.out.println("The item that you specified already exists!");
			  // Don't proceed through the rest of the code.
			  return;
		  }
		  ps.setString(1, upc);

		  System.out.print("\nSelling Price: ");
		  sellingPrice = Float.parseFloat(in.readLine());
		  ps.setFloat(2, sellingPrice);

		  System.out.print("\nStock: ");
		  stock = Integer.parseInt(in.readLine());
		  ps.setInt(3, stock);
		 
		  System.out.print("\nTaxable(y/n): ");
		  taxable = in.readLine();
		  ps.setString(4, taxable);
		  
		  ps.executeUpdate();

		  // commit work 
		  con.commit();

		  ps.close();
		}
		catch (IOException e)
		{
		    System.out.println("IOException!");
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		    try 
		    {
			// undo the insert
			con.rollback();	
		    }
		    catch (SQLException ex2)
		    {
			System.out.println("Message: " + ex2.getMessage());
			System.exit(-1);
		    }
		}
	}

	/*
	 * Remove a Item with the given UPC
	 * 
	 *  (a) Delete from item table if it exists and stock = 0
	 *  (b) Delete from book table if item is a book
	 * 	(c) Delete entries from itemPurchase table if exists
	 * 
	 */
	private void removeItem() {
		
		String upc = "";
		
		// Read input upc
		try
		{
			System.out.print("\nupc: ");
			upc = in.readLine();
		}
		catch (IOException e)
		{
		    System.out.println("IOException!");
		}
		
		try {
			// Check if item to remove exists.
			if(!itemExists(upc)) {
				System.out.println("The item that you specified does not exist!");
				// Don't proceed through the rest of the code.
				return;
			}
			
			// Check if stock > 0
			if(checkIfStockGreaterThanZero(upc)) {
				System.out.println("The item that you specified has >= 1 items in stock. Cannot remove");
				// Don't proceed through the rest of the code.
				return;
			}
			
			// If entry exists in book table, remove
			if(bookExists(upc)) {
				removeFromBookTable(upc);
			}
			
			// If entry exists in itemPurchase table, remove	
			if(itemPurchaseExists(upc)) {
				removeFromItemPurchaseTable(upc);
			}
			
			// Remove entry from item table
			removeFromItemTable(upc);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void removeFromItemTable(String upc) {
		PreparedStatement removeItem;
		
		try
		{
		removeItem = con.prepareStatement("DELETE FROM item WHERE upc = ?");
		removeItem.setString(1, upc);
		removeItem.executeUpdate();
		System.out.println("Item: " + upc + " has been deleted!");
		// commit work 
		con.commit();
		removeItem.close();
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		    try 
		    {
			// undo the insert
			con.rollback();	
		    }
		    catch (SQLException ex2)
		    {
			System.out.println("Message: " + ex2.getMessage());
			System.exit(-1);
		    }
		}
	}
	
	private void removeFromBookTable(String upc) {
		PreparedStatement removeBook;
		
		try
		{
		removeBook = con.prepareStatement("DELETE FROM book WHERE upc = ?");
		removeBook.setString(1, upc);
		removeBook.executeUpdate();
		// commit work 
		con.commit();
		removeBook.close();
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		    try 
		    {
			// undo the insert
			con.rollback();	
		    }
		    catch (SQLException ex2)
		    {
			System.out.println("Message: " + ex2.getMessage());
			System.exit(-1);
		    }
		}	
	}

	private void removeFromItemPurchaseTable(String upc) {
		PreparedStatement removeItemPurchase;
		
		try
		{
		removeItemPurchase = con.prepareStatement("DELETE FROM itemPurchase WHERE upc = ?");
		removeItemPurchase.setString(1, upc);
		removeItemPurchase.executeUpdate();
		// commit work 
		con.commit();
		removeItemPurchase.close();
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		    try 
		    {
			// undo the insert
			con.rollback();	
		    }
		    catch (SQLException ex2)
		    {
			System.out.println("Message: " + ex2.getMessage());
			System.exit(-1);
		    }
		}
	}
	
	/*
	 * Check if the items upc exists in the Item table
	 */
	private boolean itemExists(String upc) throws SQLException {
		// Create a SQL Statement object
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(
				"SELECT * "
			+	"FROM item i "
			+ 	"WHERE upc = '" + upc + "'"
			);
		
		Boolean exists = false;
		while(rs.next())
		{
			// Retrieve information from the query
			String upcOfQuery = rs.getString("upc");
			exists = !rs.wasNull();
		}
		stmt.close();
		return exists;
	}
	
	/*
	 * Check if the items upc exists in the Book table
	 */
	private boolean bookExists(String upc) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(
				"SELECT * "
			+	"FROM book b "
			+ 	"WHERE upc = '" + upc + "'"
			);
		
		Boolean exists = false;
		while(rs.next())
		{
			// Retrieve information from the query
			String upcOfQuery = rs.getString("upc");
			exists = !rs.wasNull();
		}
		stmt.close();
		return exists;
	}
	
	/*
	 * Check if entries exist in the itemPurchase table for the provided upc
	 */
	private boolean itemPurchaseExists(String upc) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(
				"SELECT * "
			+	"FROM itemPurchase ip "
			+ 	"WHERE upc = '" + upc + "'"
			);
		
		Boolean exists = false;
		while(rs.next())
		{
			// Retrieve information from the query
			String upcOfQuery = rs.getString("upc");
			exists = !rs.wasNull();
		}
		stmt.close();
		return exists;
	}
	
	/*
	 * Returns true if stock of specified item > 0 
	 */
	private boolean checkIfStockGreaterThanZero(String upc) throws SQLException {
		// Create a SQL Statement object
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(
				"SELECT * "
			+	"FROM item i "
			+ 	"WHERE upc = '" + upc + "'"
			);
		
		Boolean stockExists = false;
		while(rs.next())
		{
			// Retrieve information from the query
			int stock = rs.getInt("stock");
			if (stock > 0) {
				stockExists = true;
			}
		}
		stmt.close();
		return stockExists;
	}
				
	private void showOptions() {
		int choice;
		boolean quit = false;
		
		try 
		{
		    // disable auto commit mode
		    con.setAutoCommit(false);

		    while (!quit)
		    {
			System.out.print("\n\nPlease choose one of the following: \n");
			System.out.print("1.  Insert book item into system\n");
			System.out.print("2.  Insert non-book item into system\n");
			System.out.print("3.  Remove item from system\n");
			System.out.print("4.  Quit\n>> ");

			choice = Integer.parseInt(in.readLine());
			
			System.out.println(" ");

			switch(choice)
			{
			   case 1:  insertBookItem(); break;
			   case 2:  insertNonBookItem(); break;
			   case 3:  removeItem(); break;
			   case 4:  quit = true;
			}
		    }
		    con.close();
	        in.close();
		    System.out.println("\nThanks for using the system!\n\n");
		    System.exit(0);
		}
		catch (IOException e)
		{
		    System.out.println("IOException!");
		    try
		    {
			con.close();
			System.exit(-1);
		    }
		    catch (SQLException ex)
		    {
			 System.out.println("Message: " + ex.getMessage());
		    }
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		};
	}
	
	// Start the program
	public static void main(String[] args) throws ClassNotFoundException, SQLException {	
		PartII program = new PartII();
	}
}
