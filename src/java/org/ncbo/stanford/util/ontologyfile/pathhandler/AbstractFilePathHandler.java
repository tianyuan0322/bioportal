package org.ncbo.stanford.util.ontologyfile.pathhandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;

/**
 * An abstract class to contain common functionality for all file handlers
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractFilePathHandler implements FilePathHandler {

	protected CompressedFileHandler compressedFileHandler;

	protected AbstractFilePathHandler(
			CompressedFileHandler compressedFileHandler) {
		this.compressedFileHandler = compressedFileHandler;
	}

	public static String getOntologyFilePath(OntologyBean ontologyBean,
			String filename) {
		return getFullOntologyDirPath(ontologyBean) + "/" + filename;
	}

	public static String getFullOntologyDirPath(OntologyBean ontologyBean) {
		return MessageUtils.getMessage("bioportal.ontology.filepath")
				+ ontologyBean.getOntologyDirPath();
	}
	
	public static String getRdfFilePath(OntologyBean ontologyBean,String filename){
		return getRdfDirPath(ontologyBean) + "/" + filename;
	}
	
	public static String getRdfDirPath(OntologyBean ontologyBean){
		return MessageUtils.getMessage("bioportal.rdf.filepath");
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();

			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}

		return path.delete();
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	public static boolean deleteDirRecursive(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirRecursive(new File(dir, children[i]));
				
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	/**
	 * Copies all files under srcDir to dstDir. If dstDir does not exist, it
	 * will be created.
	 * 
	 * @param srcDir
	 * @param dstDir
	 * @throws IOException
	 */
	public static void copyAllFiles(File srcDir, File dstDir)
			throws IOException {
		if (srcDir.isDirectory()) {
			String[] children = srcDir.list();

			if (!dstDir.exists()) {
				dstDir.mkdirs();
			}

			for (int i = 0; i < children.length; i++) {
				File f = new File(srcDir, children[i]);

				if (f.isFile()) {
					copyFile(f, new File(dstDir + "\\" + f.getName()));
				}
			}
		}
	}

	/**
	 * Copies all files under srcDir to dstDir. If dstDir does not exist, it
	 * will be created.
	 * 
	 * @param srcDir
	 * @param dstDir
	 * @throws IOException
	 */
	public static void copyDirectory(File srcDir, File dstDir)
			throws IOException {
		if (srcDir.isDirectory()) {
			if (!dstDir.exists()) {
				dstDir.mkdirs();
			}

			String[] children = srcDir.list();

			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(srcDir, children[i]), new File(dstDir,
						children[i]));
			}
		} else {
			copyFile(srcDir, dstDir);
		}
	}

	/**
	 * Copies src file to dst file. If the dst file does not exist, it is
	 * created
	 * 
	 * @param src
	 * @param dst
	 */
	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len = 0;

		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		in.close();
		out.close();
	}
}
