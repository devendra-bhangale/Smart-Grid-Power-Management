package simulatedannealing;

import java.util.ArrayList;

import mainpackage.Appliance;

public class ApplianceManager {

	// Holds our appliances
    private static ArrayList<Appliance> applianceManager = new ArrayList<Appliance>();

    // Adds an appliance
    public static void addAppliance(Appliance appliance) {
    	applianceManager.add(appliance);
    }
    
    // Get an appliance
    public static Appliance getAppliance(int index){
        return (Appliance)applianceManager.get(index);
    }
    
    // Get the number of appliances
    public static int numberOfAppliances(){
        return applianceManager.size();
    }
    
}
