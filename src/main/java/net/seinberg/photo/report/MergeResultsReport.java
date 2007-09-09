package net.seinberg.photo.report;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author Dave
 * 
 */
public class MergeResultsReport {
	private static Logger log = Logger.getLogger(MergeResultsReport.class);
	private ArrayList<String> s = null;
	private ArrayList<String> f = null;
	private ArrayList<String> n = null;
	private Date start = null;
	private Date end = null;

	public MergeResultsReport(ArrayList<String> successes,
			ArrayList<String> failures, ArrayList<String> notFound, Date start, Date end) {
		this.s = successes;
		this.f = failures;
		this.n = notFound;
		this.start = start;
		this.end = end;
	}

	public void runReport() {
		
		long milli = Math.abs(this.end.getTime() - this.start.getTime());
		long seconds = milli / 1000;
		log.info("");
		log.info("");
		log.info("-------------------------------------------------------------------------------");
		log.info("Started merge at " + this.start);
		log.info("Merge finished at " + this.end);
		log.info("Merge took " + seconds + " seconds to complete.");
		log.info("-------------------------------------------------------------------------------");
		log.info("");
		log.info("Successful files merged");
		log.info("-----------------------");
		for (String str : this.s) {
			log.info(str);
		}

		log.info("");
		log.info("Unable to match");
		log.info("---------------");
		for (String str : this.n) {
			log.info(str);
		}

		log.info("");
		log.info("Failures while merging");
		log.info("----------------------");
		for (String str : this.f) {
			log.info(str);
		}
	}

}
