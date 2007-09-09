package net.seinberg.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * @author Dave
 *
 */
public class ProcessUtil {
	private static Logger log = Logger.getLogger(ProcessUtil.class);
	/**
	 * Does an exec command and returns the std out back.
	 * @param commands
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String runProcess(String[] commands) throws IOException, InterruptedException {
		log.debug("Enter: runProcess(commands)");
		Process process = Runtime.getRuntime().exec(commands);
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (line != null) {
			//log.debug(line);
			sb.append(line);
			line = br.readLine();
		}
		process.waitFor();
		log.debug("Exit: runProcess(commands)");
		return sb.toString();
	}

}
