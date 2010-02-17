/**
 * 
 */
package org.ncbo.stanford.service.xml.parse;

import java.util.ArrayList;

import org.ncbo.stanford.bean.MetadataFileBean;

/**
 * @author s.reddy
 * 
 */
public class OntologyMetadataList {

	private String timestarted;
	private String ontologiesindexed;
	private String ontologiesnotindexed;
	private String timecompletedvalue;
	private ArrayList<MetadataFileBean> ont;

	public String getTimecompletedvalue() {
		return timecompletedvalue;
	}

	public void setTimecompletedvalue(String timecompletedvalue) {
		this.timecompletedvalue = timecompletedvalue;
	}

	public String getTimestarted() {
		return timestarted;
	}

	public void setTimestarted(String timestarted) {
		this.timestarted = timestarted;
	}

	public String getOntologiesindexed() {
		return ontologiesindexed;
	}

	public void setOntologiesindexed(String ontologiesindexed) {
		this.ontologiesindexed = ontologiesindexed;
	}

	public String getOntologiesnotindexed() {
		return ontologiesnotindexed;
	}

	public void setOntologiesnotindexed(String ontologiesnotindexed) {
		this.ontologiesnotindexed = ontologiesnotindexed;
	}

	public ArrayList<MetadataFileBean> getOnt() {
		return ont;
	}

	public void setOnt(ArrayList<MetadataFileBean> ont) {
		this.ont = ont;
	}

}
