package net.seinberg.photo.gps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.seinberg.photo.data.Photo;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Dave
 * 
 */
public class GpsData {
	private static Logger log = Logger.getLogger(GpsData.class);
	private File gpsFile = null;
	private ArrayList<GpsDataItem> gpsDataItems = new ArrayList<GpsDataItem>();

	/**
	 * @param gpsFile
	 * @throws IOException
	 *             if the file can't be read
	 */
	public GpsData(String gpsFile) throws IOException {
		log.debug("Enter: GpsData(gpsFile)");
		if (gpsFile == null)
			throw new IllegalArgumentException("Gps File can't be null!");
		this.gpsFile = new File(gpsFile);
		this.loadData();
		log.debug("Exit: GpsData(gpsFile)");
	}

	/**
	 * @return the gpsDataItems
	 */
	public ArrayList<GpsDataItem> getGpsDataItems() {
		return gpsDataItems;
	}

	/**
	 * @param photo
	 * @param maxTimeDiffSeconds
	 * @return
	 * @throws GpsReadingNotFoundException
	 *             if nothing is found
	 */
	public GpsDataItem getClosestGpsReading(Photo photo, int maxTimeDiffSeconds)
			throws GpsReadingNotFoundException {
		log.debug("Enter: getClosestGpsReading(photo, maxTimeDiffSeconds)");
		if(photo == null) throw new IllegalArgumentException("Photo must not be null!");
		if(maxTimeDiffSeconds < 0) throw new IllegalArgumentException("Time difference must be a positive value!");

		GpsDataItem di = null;
		long smallestDiff = maxTimeDiffSeconds + 1;

		for (GpsDataItem item : this.gpsDataItems) {
			log.debug(item);
			log.debug(photo);
			long timeDiffMilli = item.getFixTime().getTimeInMillis() - photo.getCaptureTime().getTimeInMillis();
			timeDiffMilli = Math.abs(timeDiffMilli);
			long timeDiffSec = timeDiffMilli / 1000;
			log.debug("Time difference is " + timeDiffSec + " seconds.");
			log.debug("Maximum time difference is " + maxTimeDiffSeconds + " seconds.");
			if(timeDiffSec == 0) {
				di = item; break;
			}
			if(timeDiffSec <= maxTimeDiffSeconds && timeDiffSec < smallestDiff) {
				smallestDiff = timeDiffSec;
				di = item;
			}
		}
		
		if(di == null) {
			throw new GpsReadingNotFoundException(
					"Can't find a reading within " + maxTimeDiffSeconds
							+ " seconds of " + photo.getCaptureTime().getTime()
							+ ".");
		}
		log.debug("Exit: getClosestGpsReading(photo, maxTimeDiffSeconds)");
		return di;
	}

	/**
	 * Loads the data.
	 * 
	 * @throws IOException
	 *             if the file can't be read
	 */
	private void loadData() throws IOException {
		log.debug("Enter: loadData()");
		List data = FileUtils.readLines(this.gpsFile);
		String[] theHeader = null;
		for (Object oLine : data) {
			String line = (String) oLine;
			String[] lineArr = line.split(",");
			if (theHeader == null) {
				theHeader = lineArr;
				continue;
			}
			GpsDataItem di = GpsDataItem.fromLine(theHeader, lineArr);
			log.debug(di.toString());
			gpsDataItems.add(di);
		}
		log.debug("Exit: loadData()");
	}
}
