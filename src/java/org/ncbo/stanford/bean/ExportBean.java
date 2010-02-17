/**
 * 
 */
package org.ncbo.stanford.bean;
/**
 * @author s.reddy
 * 
 */
public class ExportBean {

	private String format;
	private String path;
	private String md5;
	private String timestamp;
	private String timegenerated;
	private String timetakentogenerate;
	private String size;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimegenerated() {
		return timegenerated;
	}

	public void setTimegenerated(String timegenerated) {
		this.timegenerated = timegenerated;
	}

	public String getTimetakentogenerate() {
		return timetakentogenerate;
	}

	public void setTimetakentogenerate(String timetakentogenerate) {
		this.timetakentogenerate = timetakentogenerate;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
