package simulatedannealing;

import java.util.ArrayList;
import java.util.Random;

import mainpackage.Appliance;
import mainpackage.EnergyConsumption;
import mainpackage.EnergyRate;
import mainpackage.Time;

public class Schedule {

	private static int lastIndexPosition = 0;
	
	
	// Holds our appliances to be scheduled
	private ArrayList<Appliance> schedule = new ArrayList<Appliance>();
	
	// Cache
	private double costVariable = 0;

	// Constructs a blank schedule
	public Schedule() {
		for (int i = 0; i < ApplianceManager.numberOfAppliances(); i++) {
			schedule.add(null);
		}
	}

	// Constructs a schedule from another schedule
	public Schedule(ArrayList<Appliance> copyThis) {
		// this.schedule = (ArrayList) copyThis.clone();

		for (int i = 0; i < copyThis.size(); i++) {
			if(copyThis.get(i) == null)
				break;
			
			this.schedule.add(new Appliance());

			this.schedule.get(i).setUpdateNo(copyThis.get(i).getUpdateNo());
			this.schedule.get(i).setHouse(copyThis.get(i).getHouse());
			this.schedule.get(i).setAppliance(copyThis.get(i).getAppliance());
			this.schedule.get(i).setWattage(copyThis.get(i).getWattage());
			this.schedule.get(i).setStarttime(copyThis.get(i).getStarttime());
			this.schedule.get(i).setDeadline(copyThis.get(i).getDeadline());
			this.schedule.get(i).setRuntime(copyThis.get(i).getRuntime());
			this.schedule.get(i).setType(copyThis.get(i).getType());
			this.schedule.get(i).setFlexibility();
		}
	}

	// Returns schedule information
	public ArrayList getSchedule() {
		return schedule;
	}

	// Creates a random individual
	public void generateIndividual() {
		// Loop through all our appliances and add them to our schedule
		int stopAt = lastIndexPosition;
		for (int applianceIndex = 0; applianceIndex < (ApplianceManager.numberOfAppliances()-stopAt); applianceIndex++) {
			setAppliance(applianceIndex, ApplianceManager.getAppliance(applianceIndex+stopAt));
			lastIndexPosition++;
		}
		// Randomly reorder the schedule
		// Collections.shuffle(schedule);
	}

	// Gets an appliance from the schedule
	public Appliance getAppliance(int schedulePosition) {
		return (Appliance) schedule.get(schedulePosition);
	}

	// Sets an appliance in a certain position within a schedule
	public void setAppliance(int schedulePosition, Appliance appliance) {
		schedule.set(schedulePosition, appliance);

		// If the schedule has been altered, reset the fitness and cost
		costVariable = 0;
	}

	// Gets the total cost of the schedule
	public synchronized double getCost() {
		double[] rates = EnergyRate.getRateFor("day");

		if (costVariable == 0) {
			double scheduleCost = 0;

			for (int applianceIndex = 0; applianceIndex < scheduleSize(); applianceIndex++) {
				if(schedule.get(applianceIndex) == null)
					break;
				
				double wattage = schedule.get(applianceIndex).getWattage();

				int startHour = schedule.get(applianceIndex).getStarttime().getHour();
				int startMin = schedule.get(applianceIndex).getStarttime().getMinute();
				int runHour = schedule.get(applianceIndex).getRuntime().getHour();
				int runMin = schedule.get(applianceIndex).getRuntime().getMinute();

				Time end = Time.addTime(startHour, startMin, runHour, runMin);
				int endHour = end.getHour();
				int endMin = end.getMinute();

				String key = startHour + ":" + startMin;
				if(EnergyConsumption.energyConsumption.get(key) <= EnergyConsumption.energyThreshold.get(key)){
					scheduleCost = (rates[startHour] / (60.0 * 1000.0)) * (double) ((60 - startMin) * wattage);
					for (int i = (startHour + 1); i < (endHour); i++) {
						scheduleCost += (rates[(i) % 24] / (60.0 * 1000.0)) * (double) (60 * wattage);
					}
					scheduleCost += (rates[endHour % 24] / (60.0 * 1000.0)) * (double) (endMin * wattage);
				}
				else {
					Random random = new Random();
					double extraCost = EnergyConsumption.energyConsumption.get(key) - EnergyConsumption.energyThreshold.get(key);
					extraCost = (extraCost/10) * random.nextDouble();	// buying rate of extra power for supplier
					
					scheduleCost = ((rates[startHour] * extraCost) / (60.0 * 1000.0)) * (double) ((60 - startMin) * wattage);
					for (int i = (startHour + 1); i < (endHour); i++) {
						scheduleCost += ((rates[(i) % 24] * extraCost) / (60.0 * 1000.0)) * (double) (60 * wattage);
					}
					scheduleCost += ((rates[endHour % 24] * extraCost) / (60.0 * 1000.0)) * (double) (endMin * wattage);
				}
			}

			costVariable = scheduleCost;
		}

		return costVariable;
	}

	// Gets the total cost of the schedule
	public synchronized double getInitialCost() {
		double[] rates = EnergyRate.getRateFor("day");

		if (costVariable == 0) {
			double scheduleCost = 0;

			for (int applianceIndex = 0; applianceIndex < scheduleSize(); applianceIndex++) {
				if (schedule.get(applianceIndex) == null)
					break;

				double wattage = schedule.get(applianceIndex).getWattage();

				int startHour = schedule.get(applianceIndex).getStarttime().getHour();
				int startMin = schedule.get(applianceIndex).getStarttime().getMinute();
				int runHour = schedule.get(applianceIndex).getRuntime().getHour();
				int runMin = schedule.get(applianceIndex).getRuntime().getMinute();

				Time end = Time.addTime(startHour, startMin, runHour, runMin);
				int endHour = end.getHour();
				int endMin = end.getMinute();

				double powerConsumption = 0;
				String key = startHour + ":" + startMin;
				powerConsumption = (double) ((60 - startMin) * wattage);
				scheduleCost = (rates[startHour] / (60.0 * 1000.0)) * (double) ((60 - startMin) * wattage);
				for (int i = (startHour + 1); i < (endHour); i++) {
					scheduleCost += (rates[(i) % 24] / (60.0 * 1000.0)) * (double) (60 * wattage);
					powerConsumption += (double) (60 * wattage);
				}
				scheduleCost += (rates[endHour % 24] / (60.0 * 1000.0)) * (double) (endMin * wattage);	
				powerConsumption += (double) (endMin * wattage);
				schedule.get(applianceIndex).setPowerConsumption(powerConsumption/1000000);
			}
			
			costVariable = scheduleCost;
		}

		return costVariable;
	}
	
	// Gets the total cost of the schedule
	public synchronized double updateConsumption() {
		double powerConsumption = 0;

		for (int applianceIndex = 0; applianceIndex < scheduleSize(); applianceIndex++) {
			if (schedule.get(applianceIndex) == null)
				break;

			double wattage = schedule.get(applianceIndex).getWattage();

			int startHour = schedule.get(applianceIndex).getStarttime().getHour();
			int startMin = schedule.get(applianceIndex).getStarttime().getMinute();
			int runHour = schedule.get(applianceIndex).getRuntime().getHour();
			int runMin = schedule.get(applianceIndex).getRuntime().getMinute();

			Time end = Time.addTime(startHour, startMin, runHour, runMin);
			int endHour = end.getHour();
			int endMin = end.getMinute();

			double consumption = 0;
			for (int i = 0;; i++) {
				Time.addTime(startHour, startMin, 0, i);
				String str = startHour + ":" + startMin;
				consumption = EnergyConsumption.energyConsumption.get(str) + ((double) wattage / 1000.0);
				EnergyConsumption.energyConsumption.put(str, consumption);

				if (str.equals(endHour + ":" + endMin))
					break;
			}
		}

		return powerConsumption;
	}

	// Get number of appliances on our schedule
	public int scheduleSize() {
		return schedule.size();
	}

	public void clearSchedule(){
		this.schedule.clear();
	}
	
}
