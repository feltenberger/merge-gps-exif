# Requirements #

  * Windows.  But only temporarily. _I promise_.
  * [Java 1.5 or later](http://java.sun.com/j2se/1.5.0/) with `JAVA_HOME` set
  * [Maven2](http://maven.apache.org) with `mvn.bat` or `mvn.sh` in your path
  * [A Subversion Client](http://subversion.tigris.org/)

# Installation #

Grab a copy of the source code from the SVN repository using TortoiseSVN or `svn` from the command line, e.g.
```
svn checkout http://merge-gps-exif.googlecode.com/svn/trunk/ merge-gps-exif
```

Then, from the `merge-gps-exif` directory, run a build:
```
mvn clean install
```

This will place all the necessary files in the target directory.

# CSV Format #

The lat/lon values are currently calculated from a weird format that my GlobalSat Data Logger DG-100 exports to, but it should accept standard decimal, i.e. non-degree/hour/minute, format as well.  Eventually I'll add GPX support, as that's the standard and probably more useful.

The following is an example.  The data in this table:

| Record Number |Date | Time | Latitude | Longitude | Speed(mile/hour) | Altitude(feet) |
|:--------------|:----|:-----|:---------|:----------|:-----------------|:---------------|
|1              |2007-08-09|06:42:34|3918.5949 |-7637.3061 |58.69             |23.0            |
|2              |2007-08-09|06:43:05|3918.3556 |-7636.8363 |50.86             |78.7            |
|3              |2007-08-09|06:43:35|3918.0310 |-7636.6964 |48.70             |120596.9        |
|4              |2007-08-09|06:44:05|3917.6153 |-7636.6420 |45.68             |120583.8        |

has a CSV equivalent of:

```
Record Number,Date,Time,Latitude,Longitude,Speed(mile/hour),Altitude(feet)
1,2007-08-09,06:42:34,3918.5949,-7637.3061,58.69,23.0
2,2007-08-09,06:43:05,3918.3556,-7636.8363,50.86,78.7
3,2007-08-09,06:43:35,3918.0310,-7636.6964,48.70,120596.9
4,2007-08-09,06:44:05,3917.6153,-7636.6420,45.68,120583.8
```

Any variation on the "Necessary text" column -- e.g. "Latitude Degrees" for "lat" -- will be recognized.  It's case-insensitive and column order doesn't matter.  Be sure that you don't have more than one term in a single column, however.  Also note the date/time format: this is important.

| **Column data** | **Necessary text** |
|:----------------|:-------------------|
| Latitude        | lat                |
| Longitude       | lon                |
| Date of fix     | date               |
| Time of fix     | time               |
| Speed at reading time | speed              |



# Usage #

From the target directory, type

```
java -jar merge-gps-exif-1.0-SNAPSHOT.jar imagesDirectory recursiveFetch[true or false] csvGpsFile maxTimeDiffInSeconds
```

Here is an example:

```
java -jar merge-gps-exif-1.0-SNAPSHOT.jar "C:\Documents and Settings\Dave\Desktop\Chicago Pictures" true "C:\data\gps\Chicago GPS Points.csv" 35
```

merge.log has a list of what was merged, what wasn't able to be merged, and what failed.

# What it does #

The program will loop through all your images and merge the closest GPS fix with the date of image capture (as long as it's less than or equal to the max seconds you specify in the arguments list).  Your original -- untouched -- image file will be copied, just in case something goes wrong. If your image is named myImage.CR2, it will be renamed myImage.CR2\_original.  Which brings up a good point: I've only tested this on Canon's CR2 format and TIFFs, but since the manipulation of the EXIF data is done by Phil Harvey's ExifTool, I'm reasonably certain all the formats he says he supports actually are supported (and there are many!).

# Excuses #

**Note:** This is incredibly inefficient at the moment - it's an n^2 worst case, which it will often hit.  I plan to make this way, way more efficient by using nested hash tables on the dates/times, so instead of looping, you can hash a couple times to find data closer to yours.  In the meantime, with small directory sizes, it should be okay.