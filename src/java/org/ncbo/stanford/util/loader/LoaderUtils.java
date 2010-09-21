package org.ncbo.stanford.util.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;

public class LoaderUtils {
	public static boolean hasDownloadLocationBeenUpdated(String url_location, OntologyBean latestOntology) {
	   boolean isUpdated= true;
	   String url_md5= computeMD5(url_location);
	   String latest_md5= computeMD5(latestOntology);
	   if (url_md5.equalsIgnoreCase(latest_md5)) {
		   isUpdated= false;
	   }
	   
	   return isUpdated;
	}
	
	
	
	public static long getLastModifiedDate(String url_location) throws Exception {
		long lastModified=0;
		URL url= new URL(url_location);
		URLConnection uc= url.openConnection();
		lastModified= uc.getLastModified();
		return lastModified;
		
	}
	
	public static int getContentLength(String url_location) throws Exception {
		int contentLength=0;
		URL url= new URL(url_location);
		URLConnection uc= url.openConnection();
		contentLength= uc.getContentLength();
		return contentLength;	
	}	
	
	public static boolean isValidDownloadLocation(String url_location){
		boolean isValid= false;
		try {
			URL url= new URL(url_location);
			URLConnection uc= url.openConnection();
			int contentLength= uc.getContentLength();
			if (contentLength != 0) {
				isValid=true;
			}
		} catch(Exception ex) {
			
		}
		
		return isValid;
	}
	
	public static String computeMD5(String downLoadLocation) {
		String md5sum= null;
		try {
			System.out.println("Time=" + new Date());

			URI uri = new URI(downLoadLocation);

			BufferedInputStream in = new BufferedInputStream(uri.toURL()
					.openStream());
			md5sum = DigestUtils.md5Hex(in);
			System.out.println(md5sum);
			in.close();
			System.out.println("Time=" + new Date());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
        return md5sum;
		
	}
	
	public static String computeMD5(OntologyBean ontologyBean) {
		return computeMD5(ontologyBean.getDownloadLocation());					
	}

	public static void storeMd5ToFile(OntologyBean ontologyBean) throws IOException {
		String filePath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);
		String fileName = getMD5FileName(ontologyBean);
        String md5= computeMD5(ontologyBean);
		// continue only if there is input file
		if (filePath != null && fileName != null && md5!= null) {
			// now create output file
			File outputDirectories = new File(filePath);
			outputDirectories.mkdirs();

			File outputFile = new File(filePath, fileName);

			BufferedWriter outputStream = new BufferedWriter( new FileWriter(outputFile));
			outputStream.write(md5);
			outputStream.flush();
			outputStream.close();
		}
	}
	
	public static String fetchMd5FromFile(OntologyBean ontologyBean) throws IOException {
		String md5= null;
		String filePath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);
		String fileName = getMD5FileName(ontologyBean);        
		// continue only if there is input file
		if (filePath != null && fileName != null ) {			
			File inputFile = new File(filePath, fileName);
			BufferedReader inputStream = new BufferedReader( new FileReader(inputFile));
			md5= inputStream.readLine();
			inputStream.close();
		}
		return md5;
	}	
	
	public static String getMD5FileName(OntologyBean ontologyBean) {
		String md5FileName = null;

		if (StringUtils.isNotBlank(ontologyBean.getDownloadLocation())) {
			String fileName = OntologyDescriptorParser.getFileName(ontologyBean
					.getDownloadLocation());
			int indexOfFileNameWithoutExt = fileName.toString()
					.lastIndexOf(".");

			// the whole filename
			if (indexOfFileNameWithoutExt == -1) {
				indexOfFileNameWithoutExt = fileName.toString().length();
			}

			String fileNameWithoutExt = fileName.substring(0,
					indexOfFileNameWithoutExt);
			if (StringUtils.isNotBlank(fileNameWithoutExt)) {
				md5FileName = fileNameWithoutExt + ".md5";
			}

		}
		return md5FileName;

	}
	
	
	// "http://github.com/cmungall/uberon/raw/master/uberon_edit.obo");
	// URI uri = new
	// URI("http://efo.svn.sourceforge.net/viewvc/efo/trunk/src/softwareontologyinowl/softwareontology.owl");
	// URI uri = new URI(
	// "file:///C:/apps/bmir.apps/bioportal_resources/uploads/6000/1/uberon_edit.obo");
}
