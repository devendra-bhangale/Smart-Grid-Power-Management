package mainpackage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import simulatedannealing.ApplianceManager;
import simulatedannealing.Schedule;

public class UtilityServer {

	private static Server utilityServer = new Server();

	public static void main(String... args) {

		utilityServer.connect();
		utilityServer.createDatabase("utilityserver");
		utilityServer.createHouseTable("utilityserver", "houses");	
		
		EnergyRate.getDailyRates(); // get daily rates from the '.csv' file
		EnergyConsumption.setEnergyProduction();
		EnergyConsumption.setEnergyConsumption();
		
		UpdateThread thread1 = new UpdateThread();
		thread1.setName("thread1.UPDATE");
		thread1.start();
		
		String house = "";
		int lineNo = 0;
				
		while (true) {
			try{
				// read the house name for which scheduling needs to be done
				try (Stream<String> lines = Files.lines(Paths.get("/opt/lampp/htdocs/utilitySchedule.txt"))) {
					house = lines.skip(lineNo).findFirst().get();
				} catch (Exception e) {
					continue;
				}
				lineNo++;
	
				// read all appliances for a house from utilityserver
				ArrayList<Appliance> householdAppliances = new ArrayList<Appliance>();
				householdAppliances = utilityServer.read_from_Houses(house);
	
				/*********************************************************/
				/************* SIMULATED ANNEALING ALGORITHM *************/
				/*********************************************************/
	
				// add all aplliances for a house into applianceManager
				for (int i = 0; i < householdAppliances.size(); i++)
					ApplianceManager.addAppliance(householdAppliances.get(i));
	
				// Set initial temp
				double temperature = 100;
				// Cooling rate
				double coolingRate = 0.15;
	
				// Initialize intial solution
				Schedule currentSolution = new Schedule();
				currentSolution.generateIndividual();
	
				double initialCost = currentSolution.getInitialCost();
				System.out.println("Initial solution cost: " + initialCost);
	
				// Set as current best
				Schedule bestSolution = new Schedule(currentSolution.getSchedule());
	
				// Loop until system has cooled
				while (temperature > 1) {
					// Create new neighbour schedule
					Schedule newSolution = new Schedule(currentSolution.getSchedule());
	
					// Get a random time in the schedule, depending on type and
					// flexibility of scheduling
					for (int applianceIndex = 0; applianceIndex < newSolution.scheduleSize(); applianceIndex++) {
						Appliance temp = newSolution.getAppliance(applianceIndex);
	
						if (temp.getType().equals("soft")) {
							Time rand = Time.randomTimeWithin("day");
							newSolution.getAppliance(applianceIndex).setStarttime(rand.getHour() + ":" + rand.getMinute());
						} else if (temp.getType().equals("hard") && temp.getFlexibility().equals("soft")) {
							Time rand = Time.randomTimeWithin(temp.getStarttime(), temp.getDeadline(), temp.getRuntime());
							newSolution.getAppliance(applianceIndex).setStarttime(rand.getHour() + ":" + rand.getMinute());
						}
					}
	
					// Get energy of solutions
					double currentEnergy = currentSolution.getCost();
					double newEnergy = newSolution.getCost();
	
					// Decide if we should accept the newEnergy
					if (acceptanceProbability(currentEnergy, newEnergy, temperature) > Math.random()) {
						currentSolution = new Schedule(newSolution.getSchedule());
					}
	
					// Keep track of the best solution found
					if (currentSolution.getCost() < bestSolution.getCost()) {
						bestSolution = new Schedule(currentSolution.getSchedule());
					}
	
					// Cool system
					temperature *= (1 - coolingRate);
				}
	
				double scheduleCost = bestSolution.getCost();
				System.out.println("Final solution cost: " + scheduleCost + "\n");
	
				// write the solution to output file
				try {
					PrintWriter writer = null;
					ArrayList<String> solution = new ArrayList<String>();
	
					for (int i = 0; i < bestSolution.scheduleSize(); i++) {
						if (bestSolution.getAppliance(i) == null)
							break;
	
						Appliance temp = bestSolution.getAppliance(i);
						solution.add(String.format("%d,%s,%.2f,%02d:%02d,%02d:%02d,%02d:%02d,%s,%.2f,%.2f",
								temp.getUpdateNo(), temp.getAppliance(), temp.getWattage(), temp.getStarttime().getHour(),
								temp.getStarttime().getMinute(), temp.getDeadline().getHour(),
								temp.getDeadline().getMinute(), temp.getRuntime().getHour(), temp.getRuntime().getMinute(),
								temp.getType(), initialCost, scheduleCost));
					}
					String[] filename = bestSolution.getAppliance(0).getHouse().split("\n");
					writer = new PrintWriter("/opt/lampp/htdocs/utilitySchedule/" + filename[0] + ".txt", "UTF-8");
					for (String str : solution)
						writer.println(str);
					writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
	
				currentSolution.clearSchedule();
				bestSolution.clearSchedule();
				
				if(!(thread1.isAlive()))
					thread1.start();
				
				try {
					System.out.println("Main Wait...");
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static double acceptanceProbability(double currentEnergy, double newEnergy, double temperature) {
		// If the new solution is better, accept it
		if (newEnergy < currentEnergy) {
			return 1.0;
		}
		// If the new solution is worse, calculate an acceptance probability
		return Math.exp((currentEnergy - newEnergy) / temperature);
	}
	
}
