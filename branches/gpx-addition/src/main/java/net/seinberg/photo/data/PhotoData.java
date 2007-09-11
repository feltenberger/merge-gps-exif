package net.seinberg.photo.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Dave
 * 
 */
public class PhotoData {
	private static Logger log = Logger.getLogger(PhotoData.class);
	public static final String[] VALID_FILE_EXTENSIONS = null;
	private ArrayList<Photo> listOfImages = new ArrayList<Photo>();
	private File imagesDirectory = null;
	private boolean recurseStructure = false;

	/**
	 * @param imagesDirectory
	 * @param recurse
	 */
	public PhotoData(String imagesDirectory, boolean recurse) {
		log.debug("Enter: PhotoDataImporter(imagesDirectory, recurse)");
		this.imagesDirectory = new File(imagesDirectory);
		this.recurseStructure = recurse;
		this.loadData();
		log.debug("Exit: PhotoDataImporter(imagesDirectory, recurse)");
	}

	/**
	 * @return the listOfImages
	 */
	public ArrayList<Photo> getListOfImages() {
		return listOfImages;
	}

	/**
	 * Loads the data.
	 */
	private void loadData() {
		log.debug("Enter: loadData()");
		Collection<File> files = FileUtils.listFiles(this.imagesDirectory,
				VALID_FILE_EXTENSIONS, this.recurseStructure);
		for (File file : files) {
			try {
				Photo photo = Photo.fromFile(file);
				listOfImages.add(photo);
				log.debug(photo);
			} catch (Exception e) {
				log.warn("Unable to load photo's exif data; skipping file "
						+ file.getAbsolutePath());
				log.error("Error running process", e);
			}
		}
		log.debug("Exit: loadData()");
	}

}
