package mainpackage;

import java.util.Random;

public class Time {

	private int hour;
	private int minute;

	public Time(String timeStr) {
		String splitBy = ":";
		String[] time = timeStr.split(splitBy);

		hour = Integer.parseInt(time[0]);
		minute = Integer.parseInt(time[1]);
	}

	public static Time addTime(int hour1, int min1, int hour2, int min2) {
		int hour = hour1 + hour2;
		int min = min1 + min2;

		if (min >= 60) {
			hour += 1;
			min = min % 60;
		}

		hour %= 24;

		Time time = new Time((hour + ":" + min));
		return time;
	}

	public static Time subTime(int hour1, int min1, int hour2, int min2) {
		int hour = (hour1 - hour2);
		int min = (min1 - min2);

		if (min < 0) {
			hour -= 1;
			min += 60;
		}

		if (hour < 0)
			hour += 24;

		Time time = new Time((hour + ":" + min));
		return time;
	}

	public static Time randomTimeWithin(Time startTime, Time deadLine, Time runTime) {
		Random random = new Random();

		// as late as possible time to start appliance to meet deadline
		Time startBy = subTime(deadLine.getHour(), deadLine.getMinute(), runTime.getHour(), runTime.getMinute());
		Time range = subTime(startBy.getHour(), startBy.getMinute(), startTime.getHour(), startTime.getMinute());

		Time hhmm = null;
		if (range.getHour() <= 0 && range.getMinute() > 0)
			hhmm = addTime(startTime.getHour(), startTime.getMinute(), random.nextInt(range.getHour() + 1),
					random.nextInt(range.getMinute()));
		else if (range.getHour() > 0 && range.getMinute() <= 0)
			hhmm = addTime(startTime.getHour(), startTime.getMinute(), random.nextInt(range.getHour()),
					random.nextInt(range.getMinute() + 1));
		else if (range.getHour() <= 0 && range.getMinute() <= 0)
			hhmm = addTime(startTime.getHour(), startTime.getMinute(), random.nextInt(range.getHour() + 1),
					random.nextInt(range.getMinute() + 1));
		else
			hhmm = addTime(startTime.getHour(), startTime.getMinute(), random.nextInt(range.getHour()),
					random.nextInt(range.getMinute()));

		// random time in HH:MM format
		String time = hhmm.getHour() + ":" + hhmm.getMinute();
		Time randomTime = new Time(time);

		return randomTime;
	}

	public static Time randomTimeWithin(String day) {
		Random random = new Random();

		// random time in HH:MM format
		String time = random.nextInt(24) + ":" + random.nextInt(60);
		Time randomTime = new Time(time);

		return randomTime;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

}
