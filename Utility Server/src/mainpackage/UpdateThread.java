package mainpackage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.stream.Stream;

import simulatedannealing.ApplianceManager;
import simulatedannealing.Schedule;



public class UpdateThread extends Thread implements MySQL_DATABASE{

	private static Server utilityServer = new Server();
	
	Connection conn	= null;
	Statement stmt	= null;
	String sql		= "";
	
	public UpdateThread() {
	}

	@Override
	public void run() {
		
		
		try{
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to a host...");
			conn = DriverManager.getConnection(HOST_URL, USER, PASS); // Open a
																		// connection
			System.out.println("Connected to host successfully...");

			stmt = conn.createStatement(); // Execute a query
		
		    String DB_URL = HOST_URL + "utilityserver";
		    System.out.println("Connecting to database '" + "utilityserver" + "'...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS); // Open a
																	// connection
			System.out.println("Connected to database '" + "utilityserver" + "' successfully...");
			
		}catch(Exception e){}
		
		String house = "";
		int lineNo = 0;
		int updateConsumption = 0;
		
		while(true){	
			try{
				synchronized(this){
					// read the house name for which scheduling needs to be done
					try (Stream<String> lines = Files.lines(Paths.get("/opt/lampp/htdocs/updateConsumption.txt"))) {
						house = lines.skip(lineNo).findFirst().get();
					} catch (Exception e) {
						System.out.println("Updating..Plz wait...");
						Thread.sleep(1000);
						continue;
					}
					lineNo++;
					
					// read all appliances for a house from utilityserver
					ArrayList<Appliance> householdAppliances = new ArrayList<Appliance>();
					householdAppliances = utilityServer.read_from_Houses(house);
					
					// add all aplliances for a house into applianceManager
					for (int i = 0; i < householdAppliances.size(); i++)
						ApplianceManager.addAppliance(householdAppliances.get(i));
					
					// Initialize intial solution
					Schedule currentSolution = new Schedule();
					currentSolution.generateIndividual();
					currentSolution.updateConsumption();					
				}
				
				try{
					System.out.println("Updating..Plz wait...");
					Thread.sleep(1000);						//1000 msec = 1 sec => 10min in fast clock
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}catch(Exception e){}
		}
	}
}
