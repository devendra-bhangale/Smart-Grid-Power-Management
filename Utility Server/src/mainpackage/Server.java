package mainpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Server implements MySQL_DATABASE {

	public static Connection conn = null;
	public static Statement stmt = null;
	public static String sql = "";

	public void connect() {
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to a host...");
			conn = DriverManager.getConnection(HOST_URL, USER, PASS); // Open a
																		// connection
			System.out.println("Connected to host successfully...");

			stmt = conn.createStatement(); // Execute a query

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createDatabase(String db_name) {
		try {
			System.out.println("Creating database '" + db_name + "'...");
			sql = "CREATE DATABASE IF NOT EXISTS " + db_name;
			stmt.executeUpdate(sql);
			System.out.println("Database '" + db_name + "' created successfully...");

			String DB_URL = HOST_URL + db_name;
			System.out.println("Connecting to database '" + db_name + "'...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS); // Open a
																	// connection
			System.out.println("Connected to database '" + db_name + "' successfully...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createHouseTable(String db_name, String tbl_name) {
		try {
			System.out.println("Creating table '" + tbl_name + "' in database '" + db_name + "'...");
			sql = "CREATE TABLE IF NOT EXISTS `" + tbl_name 
					+ "` (updateNo INTEGER(10) not NULL AUTO_INCREMENT, "
					+ " house VARCHAR(20), " 
					+ " appliance VARCHAR(20), " 
					+ " wattage VARCHAR(20), "
					+ " starttime VARCHAR(20), " 
					+ " deadline VARCHAR(20), " 
					+ " runtime VARCHAR(20), "
					+ " type VARCHAR(20), "
					+ " PRIMARY KEY ( updateNo ))";

			stmt = conn.createStatement(); // Execute a query
			stmt.executeUpdate(sql);
			System.out.println("Created table '" + tbl_name + "' in database '" + db_name + "'...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Appliance> read_from_Houses(String house) {
		try {
			String sql = "SELECT * FROM houses WHERE house = " + house;
			ResultSet rs = stmt.executeQuery(sql);

			ArrayList<String> appliances = new ArrayList<String>();
			appliances.clear();
			
			ArrayList<Appliance> householdAppliances = new ArrayList<Appliance>();
			householdAppliances.clear();

			// get added appliance list for a house
			while (rs.next()) {
				appliances.remove(rs.getString("appliance"));
				appliances.add(rs.getString("appliance"));
			}

			int index = 0;
			while (index < appliances.size()) {
				sql = "SELECT * FROM houses WHERE house = " + house + " AND appliance = \"" + appliances.get(index)
						+ "\"";
				rs = stmt.executeQuery(sql);

				// Extract data from result set
				Appliance appliance = new Appliance();
				
				while (rs.next()) {
					// Retrieve by column name
					appliance.setUpdateNo(rs.getString("updateNo"));
					appliance.setHouse(rs.getString("house"));
					appliance.setAppliance(rs.getString("appliance"));
					appliance.setWattage(rs.getString("wattage"));
					appliance.setStarttime(rs.getString("starttime"));
					appliance.setDeadline(rs.getString("deadline"));
					appliance.setRuntime(rs.getString("runtime"));
					appliance.setType(rs.getString("type"));
					appliance.setFlexibility();
				}

				householdAppliances.add(appliance);
				
				index++;
			}
			rs.close();
			return householdAppliances;
		} catch (Exception e) {
			e.printStackTrace();
			return null;	// failure returns nulls
		}
	}

}
