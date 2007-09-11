package net.seinberg.photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import net.seinberg.photo.data.Photo;
import net.seinberg.photo.data.PhotoData;
import net.seinberg.photo.gps.GpsData;
import net.seinberg.photo.gps.GpsDataItem;
import net.seinberg.photo.gps.GpsReadingNotFoundException;
import net.seinberg.photo.report.MergeResultsReport;

import org.apache.log4j.Logger;

/**
 * @author Dave
 *
 */
public class MergeGPSData {
	private static Logger log = Logger.getLogger(MergeGPSData.class);
	private GpsData gpsData = null;
	private PhotoData photoData = null;
	private int timeDiffSeconds = 60;
	private ArrayList<String> notFounds = new ArrayList<String>();
	private ArrayList<String> failures = new ArrayList<String>();
	private ArrayList<String> successes = new ArrayList<String>();
	private Date start = null;
	private Date end = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.debug("Enter: main(args)");
		if(args.length < 4) {
			System.out.println("Usage: java -jar merge-gps-data.jar directoryOfImages recursiveFetch csvGpsFile maxTimeDiffInSeconds ");
			System.exit(1);
		}
		try {
			int timeDiff = 60;
			boolean recursive = true;
			try { recursive = Boolean.parseBoolean(args[2]); } catch (Exception e) { }
			try { timeDiff = Integer.parseInt(args[3]); } catch (Exception e) { }
			MergeGPSData merger = new MergeGPSData(args[0], recursive, args[2], timeDiff);
			merger.doMerge();
			merger.runReport();
		} catch (IOException e) {
			System.err.println("Error loading one or more files specified!!  Please see the log for more details.");
			log.error("Error merging data!", e);
		}
		log.debug("Exit: main(args)");
	}

	/**
	 * @param directory
	 * @param recursive
	 * @param mergeFile
	 * @param divisor
	 */
	public MergeGPSData(String directory, boolean recurse, String mergeFile, int timeDiffInSeconds) throws IOException {
		log.debug("Enter: MergeGPSData(directory, mergeFile, timeDiff)");
		this.photoData = new PhotoData(directory, recurse);
		this.gpsData = new GpsData(mergeFile);
		this.timeDiffSeconds = timeDiffInSeconds;
		log.debug("Exit: MergeGPSData(directory, mergeFile, timeDiff)");
	}

	/**
	 * Dumps out the results of the merge.
	 */
	public void runReport() {
		MergeResultsReport report = new MergeResultsReport(successes, failures, notFounds, start, end);
		report.runReport();
	}

	/**
	 * Merges data...
	 */
	public void doMerge() {
		log.debug("Enter: doMerge()");
		start = new Date();
		for (Photo photo : photoData.getListOfImages()) {
			try {
				GpsDataItem di = this.gpsData.getClosestGpsReading(photo, timeDiffSeconds);
				log.debug("MATCHES");
				photo.updateGpsExif(di);
				successes.add(photo.getFile().getAbsolutePath() + " successfully merged with : " + di.toString());
			} catch (GpsReadingNotFoundException e) {
				notFounds.add(photo.getFile().getAbsolutePath() + ": " + e.getMessage());
			} catch (Exception e) {
				failures.add(photo.getFile().getAbsolutePath() + ": " + e.getMessage());
			}
		}
		end = new Date();
		log.debug("Exit: doMerge()");
	}
}
