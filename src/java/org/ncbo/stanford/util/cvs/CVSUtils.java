package org.ncbo.stanford.util.cvs;

import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.ice.cvsc.CVSArgumentVector;
import com.ice.cvsc.CVSCUtilities;
import com.ice.cvsc.CVSClient;
import com.ice.cvsc.CVSEntry;
import com.ice.cvsc.CVSEntryVector;
import com.ice.cvsc.CVSProject;
import com.ice.cvsc.CVSProjectDef;
import com.ice.cvsc.CVSRequest;
import com.ice.cvsc.CVSResponse;
import com.ice.cvsc.CVSScramble;

public class CVSUtils {

	private String cvsUsername;
	private String cvsPassword;
	private String cvsHostname;
	private String cvsModule;
	private String cvsRootDirectory;
	private String cvsArgumentString;
	private String cvsCheckoutDir;
	private String tempDir;

	/**
	 * @param cvsUsername
	 * @param cvsPassword
	 * @param cvsHostname
	 * @param cvsModule
	 * @param cvsRootDirectory
	 * @param cvsArgumentString
	 * @param cvsCheckoutDir
	 * @param tempDir
	 */
	public CVSUtils(String cvsUsername, String cvsPassword, String cvsHostname,
			String cvsModule, String cvsRootDirectory,
			String cvsArgumentString, String cvsCheckoutDir, String tempDir) {
		super();
		this.cvsUsername = cvsUsername;
		this.cvsPassword = cvsPassword;
		this.cvsHostname = cvsHostname;
		this.cvsModule = cvsModule;
		this.cvsRootDirectory = cvsRootDirectory;
		this.cvsArgumentString = cvsArgumentString;
		this.cvsCheckoutDir = cvsCheckoutDir;
		this.tempDir = tempDir;
	}

	public HashMap<String, CVSFile> getAllCVSEntries() throws Exception {
		CVSClient client = new CVSClient();
		CVSProject project = new CVSProject(client);
		File wdFile = new File(cvsCheckoutDir);
		HashMap<String, CVSFile> allEntries = null;

		if (wdFile.exists()) {
			project.openProject(wdFile);
			CVSEntry rootEntry = project.getRootEntry();

			if (rootEntry != null) {
				CVSEntryVector entries = new CVSEntryVector();
				addProjectEntries(entries, rootEntry);
				allEntries = getFiles(entries);
			}
		}

		return allEntries;
	}

	public void cvsCheckout() {
		// Listing modules happens with the "-c" option.
		// We will not use it in this example.
		boolean listingModules = false;

		String localDirectory = CVSCUtilities
				.stripFinalSeparator(cvsCheckoutDir);
		String tempDirectory = CVSCUtilities.stripFinalSeparator(tempDir);

		boolean isPServer = true;
		int connMethod = CVSRequest.METHOD_INETD;
		int cvsPort = CVSClient.DEFAULT_CVS_PORT;

		CVSArgumentVector arguments = CVSArgumentVector
				.parseArgumentString(cvsArgumentString);

		File localRootDir = new File(localDirectory);

		if (!localRootDir.exists() && !listingModules) {
			if (!localRootDir.mkdirs()) {
				System.err.println("Could not create local directory '"
						+ localRootDir.getPath() + "'");
				return;
			}
		}

		//
		// CREATE AND DESCRIBE THE REQUEST
		//
		CVSRequest request = new CVSRequest();

		// NOTE
		// The "command spec" you see below, ":co:N:ANP:deou:",
		// is fully documented in the CVSCommandSpec.html file
		// in doc/manual/dev/ in the jCVS II distribution, as
		// 
		String checkOutCommand = ":co:N:ANP:deou:";

		if (!request.parseControlString(checkOutCommand)) {
			System.err.println("Could not parse command specification '"
					+ checkOutCommand + "'");
			return;
		}

		//
		// Because we are checking out, there is no local working
		// directory, and thus there are no CVSEntrys to send.
		//
		CVSEntryVector entries = new CVSEntryVector();

		//
		// If we are not listing modules, then we need to tell
		// the server which module we wish to checkout, so append
		// the module name onto the argument list.
		//
		if (!listingModules) {
			arguments.appendArgument(cvsModule);
		}

		// Create the client that will connect to the server.
		// NOTE you must set the host and port...

		CVSClient client = new CVSClient();
		client.setHostName(cvsHostname);
		client.setPort(cvsPort);

		// Create the CVSProject that will handle the checkout
		CVSProject project = new CVSProject(client);

		// CVSProjects are defined by a CVSProjectDef
		CVSProjectDef projectDef = new CVSProjectDef(connMethod, isPServer,
				false, cvsHostname, cvsUsername, cvsRootDirectory, cvsModule);

		// Now we establish information required by CVSProject
		project.setProjectDef(projectDef);

		// My bad, there are several fields here that are still
		// redundant to fields in the project definition...
		project.setUserName(cvsUsername);
		project.setTempDirectory(tempDirectory);
		project.setRepository(cvsModule);
		project.setRootDirectory(cvsRootDirectory);
		project.setLocalRootDirectory(localDirectory);
		project.setPServer(isPServer);
		project.setConnectionPort(cvsPort);
		project.setConnectionMethod(connMethod);
		project.setServerCommand("cvs server"); // The standard command for cvs
		// server
		project.setAllowsGzipFileMode(false); // gzipping files is rarely used
		project.setGzipStreamLevel(0); // Lets leave gzip out of this example.

		// CVS uses a simple password scramble to avoid clear passwords.
		String scrambled = CVSScramble.scramblePassword(cvsPassword, 'A');

		project.setPassword(scrambled);

		// NOTE, we must make sure that the Project has its root entry, as
		// CVSProject will not be able to create it from the context that the
		// server will send with the checkout. This will create a proper
		// CVSEntry object for the root directory.

		project.establishRootEntry(cvsRootDirectory);

		// NOTE that all of these redundant setters on request are
		// needed because we are not using the typicall call to
		// CVSProject.performCVSCommand(), which calls most of
		// these setters for us.

		request.setPServer(isPServer);
		request.setUserName(cvsUsername);
		request.setPassword(project.getPassword());
		request.setConnectionMethod(connMethod);
		request.setServerCommand(project.getServerCommand());
		request.setRshProcess(project.getRshProcess());
		request.setPort(cvsPort);
		request.setHostName(client.getHostName());
		request.setRepository(cvsModule);
		request.setRootDirectory(cvsRootDirectory);
		request.setRootRepository(cvsRootDirectory);
		request.setLocalDirectory(localRootDir.getPath());
		request.setSetVariables(project.getSetVariables());

		// Indicate that the CVSProject we created above will process
		// the CVS commands we receive for this request.

		request.responseHandler = project;

		// Once you are confident of the code, you can turn these off.
		request.traceRequest = true;
		request.traceResponse = true;
		request.traceTCPData = true;
		request.traceProcessing = true;
		request.allowGzipFileMode = project.allowsGzipFileMode();
		request.setGzipStreamLevel(project.getGzipStreamLevel());
		request.setEntries(entries);
		request.appendArguments(arguments);
		request.setUserInterface(new CVSTestUI());

		CVSResponse response = new CVSResponse();

		//
		// FINALLY We initiate the communication and processing...
		//
		client.processCVSRequest(request, response);

		// NOTE, the "prune directories" option "-P" must be handled by
		// the client, so if you included this option, then you must make
		// a call to CVSProject.pruneEmptySubDirs() here....
		//
		// project.pruneEmptySubDirs( request.handleEntries );
		//

		// NOTE, You MUST make a call to CVSProject.writeAdminFile() ANY
		// time that you perform an operation that modifies the CVSEntrys
		// in the project, including "update", "ci", and other commands.
		project.writeAdminFiles();
	}

	public HashMap<String, CVSFile> getFiles(CVSEntryVector entries) {
		HashMap<String, CVSFile> files = new HashMap<String, CVSFile>(1);

		for (int i = 0; i < entries.size(); i++) {
			CVSEntry entry = entries.entryAt(i);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(entry.getCVSTime().getTime());
			GregorianCalendar gCal = new GregorianCalendar();
			gCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
					.get(Calendar.DAY_OF_MONTH));
			files.put(entry.getName(), new CVSFile(entry.getLocalPathName(),
					gCal, entry.getVersion()));
		}

		return files;
	}

	@SuppressWarnings("unchecked")
	public void addProjectEntries(CVSEntryVector entries, CVSEntry parent) {
		CVSEntryVector children = parent.getEntryList();

		for (Enumeration chEnum = children.elements(); chEnum.hasMoreElements();) {
			CVSEntry child = (CVSEntry) chEnum.nextElement();

			if (child.isDirectory()) {
				this.addProjectEntries(entries, child);
			} else {
				// Add this file CVSEntry.
				entries.addElement(child);
			}
		}
	}
}
