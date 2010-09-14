package org.ncbo.stanford.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ncbo.stanford.util.constants.ApplicationConstants;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

public class CompressionUtils {

	private CompressionUtils() {
	}

	private static class CompressionUtilsHolder {
		private final static CompressionUtils instance = new CompressionUtils();
	}

	public static CompressionUtils getInstance() {
		return CompressionUtilsHolder.instance;
	}

	public List<String> uncompress(String filePath, String filename)
			throws FileNotFoundException, IOException {
		List<String> allFiles = new ArrayList<String>(1);

		if (isZip(filename)) {
			allFiles = unzip(filePath, filename);
		} else if (isGZip(filename)) {
			allFiles = gunzip(filePath, filename);
		} else if (isJar(filename)) {
			allFiles = unjar(filePath, filename);
		} else if (isTar(filename)) {
			allFiles = untar(filePath, filename);
		}

		return allFiles;
	}

	public List<String> unzip(String zipFilePath, String zipFileName)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(zipFilePath + File.separator
				+ zipFileName);

		return extractFiles(new ZipCompressedInputStream(fis), zipFilePath,
				zipFileName);
	}

	public List<String> gunzip(String gzipFilePath, String gzipFileName)
			throws FileNotFoundException, IOException {
		List<String> lst = new ArrayList<String>(1);
		BufferedOutputStream dest = null;
		int count;
		byte data[] = new byte[ApplicationConstants.BUFFER_SIZE];

		FileInputStream fis = new FileInputStream(gzipFilePath + File.separator
				+ gzipFileName);
		GZIPInputStream gzis= new GZIPInputStream(new BufferedInputStream(fis));
		//get filename without the .gz extension
		String fileName = gzipFileName.substring(0, gzipFileName.length() - 3);
		FileOutputStream fos = new FileOutputStream(gzipFilePath
				+ File.separator + fileName);

		
		dest = new BufferedOutputStream(fos, ApplicationConstants.BUFFER_SIZE);

		while ((count = gzis.read(data, 0, ApplicationConstants.BUFFER_SIZE)) != -1) {
			dest.write(data, 0, count);
		}

		dest.flush();
		dest.close();
		if (isCompressed(fileName)) {
			lst= uncompress(gzipFilePath, fileName);
		} else {
		  lst.add(fileName);
		}
		
		return lst;
	}

	public List<String> untar(String tarFilePath, String tarFileName)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(tarFilePath + File.separator
				+ tarFileName);

		return extractFiles(new TarCompressedInputStream(fis), tarFilePath,
				tarFileName);
	}

	public List<String> unjar(String jarFilePath, String jarFileName)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(jarFilePath + File.separator
				+ jarFileName);

		return extractFiles(new JarCompressedInputStream(fis), jarFilePath,
				jarFileName);
	}

	private List<String> extractFiles(CompressedInputStream cis,
			String compressedFilePath, String compressedFilename)
			throws FileNotFoundException, IOException {
		ArrayList<String> lst = new ArrayList<String>(1);
		BufferedOutputStream dest = null;
		CompressedEntry entry;

		while ((entry = cis.getNextEntry()) != null && entry.getName() != null) {
			int count;
			byte data[] = new byte[ApplicationConstants.BUFFER_SIZE];
			String str = getOnlyFileName(entry.getName());

			// write the files to the disk
			FileOutputStream fos = new FileOutputStream(compressedFilePath
					+ File.separator + str);
			lst.add(str);
			dest = new BufferedOutputStream(fos,
					ApplicationConstants.BUFFER_SIZE);

			while ((count = cis.read(data, 0, ApplicationConstants.BUFFER_SIZE)) != -1) {
				dest.write(data, 0, count);
			}

			dest.flush();
			dest.close();
		}

		cis.close();

		return lst;
	}

	public static boolean isZip(String filename) {
		return filename.toLowerCase().endsWith(
				ApplicationConstants.ZIP_EXTENSION);
	}

	public static boolean isGZip(String filename) {
		return filename.toLowerCase().endsWith(
				ApplicationConstants.GZIP_EXTENSION);
	}

	public static boolean isJar(String filename) {
		return filename.toLowerCase().endsWith(
				ApplicationConstants.JAR_EXTENSION);
	}

	public static boolean isTar(String filename) {
		return filename.toLowerCase().endsWith(
				ApplicationConstants.TAR_EXTENSION);
	}

	public static boolean isCompressed(String filename) {
		return isZip(filename) || isJar(filename) || isTar(filename);
	}

	public static String getOnlyFileName(String name) {
		String retStr = name;

		if (name.indexOf("/") >= 0) {
			retStr = name.substring(name.lastIndexOf("/") + 1);
		} else if (name.indexOf("\\") >= 0) {
			retStr = name.substring(name.lastIndexOf("\\") + 1);
		}

		return retStr;
	}

	private interface CompressedInputStream {
		public CompressedEntry getNextEntry() throws IOException;

		public int read(byte[] b, int off, int len) throws IOException;

		public void close() throws IOException;
	}

	private interface CompressedEntry {
		public String getName();
	}

	private class ZipCompressedEntry implements CompressedEntry {

		private ZipEntry entry;

		public ZipCompressedEntry(ZipEntry entry) {
			this.entry = entry;
		}

		public String getName() {
			return (entry == null) ? null : entry.getName();
		}
	}

	private class ZipCompressedInputStream implements CompressedInputStream {

		private ZipInputStream zipInputStream;

		public ZipCompressedInputStream(FileInputStream fis) {
			zipInputStream = new ZipInputStream(new BufferedInputStream(fis));
		}

		public CompressedEntry getNextEntry() throws IOException {
			ZipEntry entry = zipInputStream.getNextEntry();

			return new ZipCompressedEntry(entry);
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return zipInputStream.read(b, off, len);
		}

		public void close() throws IOException {
			zipInputStream.close();
		}
	}

	private class TarCompressedEntry implements CompressedEntry {

		private TarEntry entry;

		public TarCompressedEntry(TarEntry entry) {
			this.entry = entry;
		}

		public String getName() {
			return (entry == null) ? null : entry.getName();
		}
	}

	private class TarCompressedInputStream implements CompressedInputStream {

		private TarInputStream tarInputStream;

		public TarCompressedInputStream(FileInputStream fis) {
			tarInputStream = new TarInputStream(new BufferedInputStream(fis));
		}

		public CompressedEntry getNextEntry() throws IOException {
			TarEntry entry = tarInputStream.getNextEntry();

			return new TarCompressedEntry(entry);
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return tarInputStream.read(b, off, len);
		}

		public void close() throws IOException {
			tarInputStream.close();
		}
	}

	private class JarCompressedEntry implements CompressedEntry {

		private JarEntry entry;

		public JarCompressedEntry(JarEntry entry) {
			this.entry = entry;
		}

		public String getName() {
			return (entry == null) ? null : entry.getName();
		}
	}

	private class JarCompressedInputStream implements CompressedInputStream {

		private JarInputStream jarInputStream;

		public JarCompressedInputStream(FileInputStream fis) throws IOException {
			jarInputStream = new JarInputStream(new BufferedInputStream(fis));
		}

		public CompressedEntry getNextEntry() throws IOException {
			JarEntry entry = jarInputStream.getNextJarEntry();

			return new JarCompressedEntry(entry);
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return jarInputStream.read(b, off, len);
		}

		public void close() throws IOException {
			jarInputStream.close();
		}
	}
}
