package net.seinberg.photo.gps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * @author Dave
 * 
 */
public class GpsDataItem {
	public static final String DATE_MASK = "yyyy-MM-dd";
	public static final String TIME_MASK = "HH:mm:ss";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			DATE_MASK);
	private static SimpleDateFormat timeFormatter = new SimpleDateFormat(
			TIME_MASK);
	private static Logger log = Logger.getLogger(GpsDataItem.class);
	private double latitude;
	private double longitude;
	private double altitude;
	private double speed;
	private String description;
	private Calendar fixTime;

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Returns "N" if latitude is positive, "S" if negative.
	 * @return
	 */
	public String getLatitudeRef() {
		if(this.latitude > 0.0)
			return "N";
		return "S";
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns "E" if longitude is positive, "W" otherwise.
	 * @return
	 */
	public String getLongitudeRef() {
		if(this.longitude > 0.0)
			return "E";
		return "W";
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the altitude
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Returns "1" if altitude is positive, "0" otherwise.
	 * @return
	 */
	public String getAltitudeRef() {
		if(this.altitude > 0.0)
			return "1";
		return "0";
	}

	/**
	 * @param altitude
	 *            the altitude to set
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the fixTime
	 */
	public Calendar getFixTime() {
		return fixTime;
	}

	/**
	 * @param fixTime
	 *            the fixTime to set
	 */
	public void setFixTime(Calendar fixTime) {
		this.fixTime = fixTime;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String toStr = "GPSDataItem: [altitude: " + this.altitude + "], [latitude: "
				+ this.latitude + "], [longitude: " + this.longitude
				+ "], [speed: " + this.speed
				+ "], [description: " + this.description
				+ "], [fix date/time: " + this.fixTime.getTime() + "]";
		return toStr;
	}

	/**
	 * @param header
	 * @param line
	 * @return
	 */
	public static GpsDataItem fromLine(String[] header, String[] line) {
		log.debug("Enter: fromLine(header, line)");
		if (header == null || line == null || header.length != line.length)
			throw new IllegalArgumentException(
					"Header and line must be non-null and the same size!");
		GpsDataItem di = new GpsDataItem();
		Calendar tmpDate = null;
		Calendar tmpTime = null;

		for (int i = 0; i < header.length; i++) {
			String hl = header[i].toLowerCase();
			if (hl.contains("lat")) {
				di.setLatitude(toDecimalDegrees(line[i]));
			}
			else if (hl.contains("lon")) {
				di.setLongitude(toDecimalDegrees(line[i]));
			}
			else if (hl.contains("alt"))
				di.setAltitude(Double.parseDouble(line[i]));
			else if (hl.contains("speed"))
				di.setSpeed(Double.parseDouble(line[i]));
			else if (hl.contains("desc"))
				di.setDescription(line[i]);
			else if (hl.contains("time")) {
				try {
					//log.debug("Time is " + line[i]);
					tmpTime = GregorianCalendar.getInstance();
					tmpTime.setTime(timeFormatter.parse(line[i]));
				} catch (ParseException e) {
					log.warn("Unable to parse time: " + line[i]
							+ "; must be in format " + TIME_MASK);
					tmpTime = null;
				}
			} else if (hl.contains("date")) {
				try {
					//log.debug("Date is " + line[i]);
					tmpDate = GregorianCalendar.getInstance();
					tmpDate.setTime(dateFormatter.parse(line[i]));
				} catch (ParseException e) {
					log.warn("Unable to parse date: " + line[i]
							+ "; must be in format " + DATE_MASK);
					tmpDate = null;
				}
			}
		}
		if (tmpDate != null && tmpTime != null) {
			tmpDate.set(Calendar.HOUR_OF_DAY, tmpTime.get(Calendar.HOUR_OF_DAY));
			tmpDate.set(Calendar.MINUTE, tmpTime.get(Calendar.MINUTE));
			tmpDate.set(Calendar.SECOND, tmpTime.get(Calendar.SECOND));
			log.debug("Now the date/time is " + tmpDate.getTime());
			di.setFixTime(tmpDate);
		}
		log.debug("Exit: fromLine(header, line)");
		return di;
	}

	/**
	 * @param latOrLon
	 * @return
	 */
	private static double toDecimalDegrees(String latOrLon) {
		double decDeg = 0.0;
		double multiplier = 1.0;
		if(Math.abs(Double.parseDouble(latOrLon)) > 400) {
			double degreeParts[] = toDegreeParts(latOrLon.replace("-", ""));	
			if(Double.parseDouble(latOrLon) < 0)
				multiplier = -1.0;
			log.debug("Multiplier: " + multiplier);
			double totalSeconds = ( (degreeParts[1] * 60) + degreeParts[2] );
			double decimalPart = totalSeconds / 3600.0;
			decDeg = degreeParts[0] + decimalPart;
			decDeg *= multiplier;
		}
		else {
			decDeg = Double.parseDouble(latOrLon);
		}
		return decDeg;
	}

	/**
	 * Currently this is a hack to deal with the Globastat GPS Logger DG100's screwy lat/lon format.
	 * @param number
	 * @return
	 */
	private static double[] toDegreeParts(String number) {
		log.debug("Enter: toDegreeParts(number)");
		double[] parts = new double[3];
		if(number.length() == 9) {
			// here's the hack
			// 3918.0310 = +39 18' 1.84" <-- an example
			log.debug("Parsing " + number);
			parts[0] = Double.parseDouble(number.substring(0, 2)); // degrees (e.g. 39)
			log.debug("Degrees: " + parts[0]);
			parts[1] = Double.parseDouble(number.substring(2, 4)); // minutes (e.g. 18)
			log.debug("Minutes: " + parts[1]);
			double[] p = new double[2];
			p[0] = Double.parseDouble(number.substring(5, 7)); // % part one (e.g. 03)
			log.debug("Seconds p0: " + p[0]);
			p[1] = Double.parseDouble(number.substring(7, 9)); // % part two (e.g. 10)
			log.debug("Seconds p1: " + p[1]);
			// i have no idea where this equation came from, but it seems to be linked
			//  to minutes/seconds in some way based on the division.
			// can't figure out why i need to subtract .02, though...
			p[0] = (p[0] * 60.0) / 100;
			p[1] = ( (p[1] * 0.6) / 100) - 0.02;
			parts[2] = p[0] + p[1]; // seconds (e.g. 1.84)
			log.debug("Seconds: " + parts[2]);
		}
		else {
			log.debug("This should never happen, until I start using non globalsat data...");
			log.debug("Value I got was: " + number);
			throw new IllegalArgumentException("Don't know what to do with this data: " + number);
		}
		log.debug("Exit: toDegreeParts(number)");
		return parts;
	}
}
