package mainpackage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EnergyRate {

	public static double[] energyRates = new double[24];

	public static void getDailyRates() {
		String csvFile = "/opt/lampp/htdocs/20151122-da.csv";
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";

		try {
			int readLine = 0;
			int column = 0;

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] rates = line.split(csvSplitBy);

				if (readLine == 3) {
					for (String str : rates) {
						if (column == 0) {
							column++;
							continue;
						}
						setDailyRates(column - 1, Double.parseDouble(str));
						column++;
					}
				}

				if (readLine >= 4)
					break;
				else
					readLine++;
			}

			System.out.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// hour ranges from '00' to '23'
	public static void setDailyRates(int hour, double rate) {
		energyRates[hour] = rate;
	}

	public static double getRateFor(int hour) {
		return energyRates[hour];
	}

	public static double[] getRateFor(String day) {
		return energyRates;
	}
}
