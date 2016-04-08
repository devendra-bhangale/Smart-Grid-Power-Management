package mainpackage;

public class Appliance {

	private int updateNo = 0;
	private String house = null;
	private String appliance = null;
	private double wattage = 0;
	private Time starttime = null;
	private Time deadline = null;
	private Time runtime = null;
	private String type = null;
	private String flexibility = null;
	private String rps = null;
	
	public String getRps() {
		return rps;
	}

	public void setRps(String rps) {
		this.rps = rps;
	}
	
	public int getUpdateNo() {
		return updateNo;
	}

	public void setUpdateNo(String updateNo) {
		this.updateNo = Integer.parseInt(updateNo);
	}

	public void setUpdateNo(int updateNo) {
		this.updateNo = updateNo;
	}

	public String getHouse() {
		return house;
	}

	public void setHouse(String house) {
		this.house = house;
	}

	public String getAppliance() {
		return appliance;
	}

	public void setAppliance(String appliance) {
		this.appliance = appliance;
	}

	public double getWattage() {
		return wattage;
	}

	public void setWattage(String wattage) {
		this.wattage = Double.parseDouble(wattage);
	}

	public void setWattage(double wattage) {
		this.wattage = wattage;
	}

	public Time getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = new Time(starttime);
	}

	public void setStarttime(Time starttime) {
		this.starttime = new Time(starttime.getHour() + ":" + starttime.getMinute());
	}

	public Time getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = new Time(deadline);
	}

	public void setDeadline(Time deadline) {
		this.deadline = new Time(deadline.getHour() + ":" + deadline.getMinute());
	}

	public Time getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = new Time(runtime);
	}

	public void setRuntime(Time runtime) {
		this.runtime = new Time(runtime.getHour() + ":" + runtime.getMinute());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFlexibility() {
		return flexibility;
	}

	public void setFlexibility() {
		int startHour = getStarttime().getHour();
		int startMin = getStarttime().getMinute();

		int deadHour = getDeadline().getHour();
		int deadMin = getDeadline().getMinute();

		int runHour = getRuntime().getHour();
		int runMin = getRuntime().getMinute();

		Time endTime = Time.addTime(startHour, startMin, runHour, runMin);

		if (deadHour == endTime.getHour() && deadMin == endTime.getMinute()) {
			flexibility = "hard";
		} else {
			flexibility = "soft";
		}
	}

	public int[] timeFormat(String timeStr) {
		String splitBy = ":";
		String[] time = timeStr.split(splitBy);

		int index = 0;
		int[] timeFormat = new int[time.length];
		for (String str : time) {
			timeFormat[index] = Integer.parseInt(str);
			index++;
		}

		return timeFormat;
	}

}
