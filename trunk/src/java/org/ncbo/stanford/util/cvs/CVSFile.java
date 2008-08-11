package org.ncbo.stanford.util.cvs;

import java.util.Calendar;

public class CVSFile {

	private String path;
	private Calendar time;
	private String version;

	public CVSFile(String path, Calendar time, String version) {
		this.path = path;
		this.time = time;
		this.version = version;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}

	public Calendar getTimeStamp() {
		return this.time;
	}

	public void setTimeStamp(Calendar cal) {
		this.time = cal;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}