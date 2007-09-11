package net.seinberg.photo.data;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.seinberg.photo.gps.GpsDataItem;
import net.seinberg.util.ProcessUtil;

import org.apache.log4j.Logger;

/**
 * @author Dave
 *
 */
public class Photo {
	private static Logger log = Logger.getLogger(Photo.class);
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	private File file = null;
	private Calendar captureTime = null;
	private static String exifToolExecutable = "exif-tool-6.96.exe";
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * @return the captureTime
	 */
	public Calendar getCaptureTime() {
		return captureTime;
	}
	/**
	 * @param captureTime the captureTime to set
	 */
	public void setCaptureTime(Calendar captureTime) {
		this.captureTime = captureTime;
	}

	/**
	 * @return the exifToolExecutable
	 */
	public String getExifToolExecutable() {
		return exifToolExecutable;
	}

	/**
	 * @param exifToolExecutable
	 *            the exifToolExecutable to set
	 */
	public void setExifToolExecutable(String toolExec) {
		exifToolExecutable = toolExec;
	}

	/**
	 * The magic method.  This will add the GPS info to the exif data!
	 * @param gpsData
	 * @throws Exception
	 */
	public void updateGpsExif(GpsDataItem gpsData) throws Exception{
		log.debug("Enter: updateGpsExif(gpsData)");
		DecimalFormat decForm = new DecimalFormat("####.000000");
		String[] commands = new String[] {
				exifToolExecutable
				,"-gpslatitude=" + gpsData.getLatitude()
				,"-gpslatituderef=" + gpsData.getLatitudeRef()
				,"-gpslongitude=" + gpsData.getLongitude()
				,"-gpslongituderef=" + gpsData.getLongitudeRef()
				,"-gpsaltitude=" + gpsData.getAltitude()
				,"-gpsaltituderef=" + gpsData.getAltitudeRef()
				,"-keywords+=geotagged"
				,"-keywords+=geo:lon=" + decForm.format(gpsData.getLongitude())
				,"-keywords+=geo:lat=" + decForm.format(gpsData.getLatitude())
				,"\"" + this.file.getAbsolutePath() + "\""
		};
		try {
			String results = ProcessUtil.runProcess(commands);
			log.debug("Results of update: " + results);
		} catch (Exception e) {
			throw e;
		}
		log.debug("Exit: updateGpsExif(gpsData)");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String theStr = "Photo: [file: " + this.file + "], [captureTime: ";
		if(captureTime != null)
			theStr += captureTime.getTime();
		else
			theStr += null;
		theStr += "]";
		return theStr;
	}

	/**
	 * @param thePhoto
	 * @return
	 * @throws Exception
	 */
	public static Photo fromFile(File photoFile) throws Exception {
		log.debug("Enter: fromFile(photoFile)");
		Photo thePhoto = new Photo();

		String[] commands = new String[] {
			exifToolExecutable,
			"-datetimeoriginal",
			"-gpsinfo",
			"\"" + photoFile.getAbsolutePath() + "\""
		};

		thePhoto.setFile(photoFile);
		String output = ProcessUtil.runProcess(commands);
		if(output == null) return thePhoto;

		String lines[] = output.split("\n");
		for (String line : lines) {
			if(line.toLowerCase().contains("date/time")) {
				String datePart =  line.split(": ")[1];
				//log.debug("Date part: " + datePart);
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(dateFormatter.parse(datePart));
				thePhoto.setCaptureTime(cal);
				break;
			}
		}

		log.debug("Exit: fromFile(photoFile)");
		return thePhoto;
	}

}
