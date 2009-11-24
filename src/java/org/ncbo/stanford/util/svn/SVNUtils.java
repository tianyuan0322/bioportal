/**
 * 
 */
package org.ncbo.stanford.util.svn;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.cvs.CVSFile;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author s.reddy
 * 
 */
public class SVNUtils {

	private static final Log log = LogFactory.getLog(SVNUtils.class);

	private String svnUsername;
	private String svnPassword;
	private String svnHostname;
	private String svnModule;
	private String svnRootDirectory;
	private String svnArgumentString;
	private String svnCheckoutDir;
	private String tempDir;

	public SVNUtils() {
	}

	public SVNUtils(String svnUsername, String svnPassword, String svnHostname,
			String svnModule, String svnRootDirectory,
			String svnArgumentString, String svnCheckoutDir, String tempDir) {
		super();
		this.svnUsername = svnUsername;
		this.svnPassword = svnPassword;
		this.svnHostname = svnHostname;
		this.svnModule = svnModule;
		this.svnRootDirectory = svnRootDirectory;
		this.svnArgumentString = svnArgumentString;
		this.svnCheckoutDir = svnCheckoutDir;
		this.tempDir = tempDir;
	}

	public void svnCheckout() {

		// Setup connection protocols support.
		DAVRepositoryFactory.setup();
		SVNRepository repository = null;
		ISVNAuthenticationManager authManager;
		File dstPath = null;
		
		svnHostname=svnHostname+svnRootDirectory;
		try {
			// encode host URL.
			SVNURL svnurl = SVNURL.parseURIEncoded(svnHostname);
			repository = DAVRepositoryFactory.create(svnurl);
			// set Authentication details.
			authManager = SVNWCUtil.createDefaultAuthenticationManager(
					svnUsername, svnPassword);
			SVNClientManager cm = SVNClientManager.newInstance();
			SVNUpdateClient uc = cm.getUpdateClient();
			repository.setAuthenticationManager(authManager);
			// create checkout directory if it's not exit.
			dstPath = new File(svnCheckoutDir);
			if (!dstPath.exists()) {
				dstPath.mkdir();
			}
			// checkout data from SVN.
			uc.doCheckout(svnurl, dstPath, SVNRevision.UNDEFINED,
					SVNRevision.HEAD, true);

			if (log.isDebugEnabled()) {
				log.debug("**** SVN Checkout completed successfully *****");
			}
		} catch (SVNException svne) {
			svne.printStackTrace();
		} finally {
			try {
				repository.closeSession();
			} catch (Exception e) {
			}
		}
	}

	public HashMap<String, CVSFile> listEntries() throws SVNException {

		final HashMap<String, CVSFile> update = new HashMap<String, CVSFile>();
		// Setup connection protocols support.
		DAVRepositoryFactory.setup();

		// set Authentication details.
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(svnUsername, svnPassword);
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNLogClient svnLogClient = new SVNLogClient(authManager, options);

		// get all downloaded file details(revision,local_file_path and time)
		try {
			svnLogClient.doList(new File(svnCheckoutDir), SVNRevision.HEAD,
					SVNRevision.HEAD, false, true, new ISVNDirEntryHandler() {
						public void handleDirEntry(SVNDirEntry entry)
								throws SVNException {
							if (entry.getKind() == SVNNodeKind.FILE) {
								Calendar cal = Calendar.getInstance();
								cal.setTimeInMillis(entry.getDate().getTime());
								GregorianCalendar gCal = new GregorianCalendar();
								gCal.set(cal.get(Calendar.YEAR), cal
										.get(Calendar.MONTH), cal
										.get(Calendar.DAY_OF_MONTH));
								update.put(entry.getName(), new CVSFile(
										svnCheckoutDir + "/"
												+ entry.getRelativePath(),
										gCal, new Long(entry.getRevision())
												.toString()));
							} // End of if
						} // End of handleDirEntry method
					} // End of ISVNDirEntryHandler
			);
		} catch (SVNException svnEx) {
			log.error("Error while getting file info :" + svnEx);
			log.info("trying to get files info again");
			return listEntries();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return update;
	}
}