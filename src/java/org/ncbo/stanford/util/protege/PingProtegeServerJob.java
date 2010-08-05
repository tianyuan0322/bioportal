package org.ncbo.stanford.util.protege;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ProtegeJob;

public class PingProtegeServerJob extends ProtegeJob {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7302401272790927199L;
	private static final Log log = LogFactory
			.getLog(PingProtegeServerJob.class);

	public PingProtegeServerJob(KnowledgeBase kb) {
		super(kb);
	}

	public static boolean ping(KnowledgeBase kb) {
		try {
			new PingProtegeServerJob(kb).execute();
			return true;
		} catch (Throwable t) {
			log.error("Protege server is down!!!");
			return false;
		}
	}

	@Override
	public Object run() throws ProtegeException {
		return true;
	}
}
