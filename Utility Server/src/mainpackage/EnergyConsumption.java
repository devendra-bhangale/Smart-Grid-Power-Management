package mainpackage;

import java.util.HashMap;
import java.util.Random;

public class EnergyConsumption {

	public static HashMap<String, Double> energyThreshold	= new HashMap<String, Double>();
	public static HashMap<String, Double> energyConsumption	= new HashMap<String, Double>();
	
	public static void setEnergyProduction(){
		Random random = new Random();
		
		for(int i = 0; i < 24; i++){
			for(int j = 0; j < 60; j++){
				String str = i + ":" + j;
				
				// random threshold => random power generation for an instant (per Mega Watt minute)
				// scaled down to a scale of 0 to 100
				Double threshold = 75 + (100-75)*random.nextDouble();
				
				energyThreshold.put(str, threshold);
			}
		}
	}
	
	public static void setEnergyConsumption(){
		Random random = new Random();
				
		for(int i = 0; i < 24; i++){
			for(int j = 0; j < 60; j++){
				String str = i + ":" + j;
				
				// random consumption => random power consumption for an instant (per Mega Watt minute)
				// scaled down to a scale of 0 to 100
				 Double consumption = 75 + (100-75)*random.nextDouble();
				// Double consumption = EnergyRate.getRateFor(i) + EnergyRate.getRateFor(i)*random.nextDouble();
				
				energyConsumption.put(str, consumption);
			}
		}		
	}
	
	public void updateEnergyConsumption(String atTime, double add, double sub){
		
		double initial = energyConsumption.get(atTime);
		
		if(sub == 0.0){
			energyConsumption.put(atTime, (initial+add));
		}
		else if(add == 0.0){
			energyConsumption.put(atTime, (initial-sub));
		}
		
	}
	
}
