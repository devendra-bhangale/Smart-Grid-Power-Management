package mainpackage;

import java.util.HashMap;
import java.util.Random;

public class RPS {

	public static HashMap<String, Double> rpsThreshold	= new HashMap<String, Double>();
	public static HashMap<String, Double> rpsConsumption	= new HashMap<String, Double>();
	
	public static void setRpsProduction(){
		Random random = new Random();
		
		for(int i = 0; i < 24; i++){
			for(int j = 0; j < 60; j++){
				String str = i + ":" + j;
				
				// random threshold => random rps generation for an instant 
				// scaled down to a scale of 0 to 100
				Double threshold = 75 + (100-75)*random.nextDouble();
				
				rpsThreshold.put(str, threshold);
			}
		}
	}
	
	public static void setRpsConsumption(){
		Random random = new Random();
				
		for(int i = 0; i < 24; i++){
			for(int j = 0; j < 60; j++){
				String str = i + ":" + j;
				
				// random consumption => random rps consumption for an instant 
				// scaled down to a scale of 0 to 100
				 Double consumption = 75 + (100-75)*random.nextDouble();
				// Double consumption = EnergyRate.getRateFor(i) + EnergyRate.getRateFor(i)*random.nextDouble();
				
				 rpsConsumption.put(str, consumption);
			}
		}		
	}
	
	public void updateRpsConsumption(String atTime, double add, double sub){
		
		double initial = rpsConsumption.get(atTime);
		
		if(sub == 0.0){
			rpsConsumption.put(atTime, (initial+add));
		}
		else if(add == 0.0){
			rpsConsumption.put(atTime, (initial-sub));
		}
		
	}
	
}
