package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.SystemUtils;

import genepi.io.FileUtil;

public class Reference {

	String name;
	String refFilename;
	String sequence;
	String jbwaDir;
	int length;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public String getJbwaDir() {
		return jbwaDir;
	}

	public void setJbwaDir(String jbwaDir) {
		this.jbwaDir = jbwaDir;
	}

	public String getRefFilename() {
		return refFilename;
	}

	public void setRefFilename(String refFilename) {
		this.refFilename = refFilename;
	}


	public Reference(String refFilename) {

		this.refFilename = refFilename;

		loadReference(refFilename);

		loadJbwa();

	}

	public void loadReference(String refFilename) {
		
		System.out.println(new File(refFilename).getAbsolutePath());

		StringBuilder stringBuilder = null;
		try {

			BufferedReader reader = new BufferedReader(new FileReader(new File(refFilename).getAbsolutePath()));
			String line = null;
			stringBuilder = new StringBuilder();

			while ((line = reader.readLine()) != null) {

				if (!line.startsWith(">"))
					stringBuilder.append(line);

			}

			reader.close();

			if (!new File(refFilename + ".bwt").exists()) {
				System.err.println("WARNING: reference.bwt file not found. Run bwa index command on reference");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String seq = stringBuilder.toString();
		
		this.sequence = seq;
		this.length = seq.length();
	}

	private void loadJbwa() {

		//TODO Should we read it from classpath???
		String jbwaDir = FileUtil.path("jbwa-" + System.currentTimeMillis() + "");

		try {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream("jbwa.zip");

			ZipInputStream zis = new ZipInputStream(stream);

			ZipEntry entry = zis.getNextEntry();

			FileUtil.createDirectory(jbwaDir);

			while (entry != null) {
				String fileName = entry.getName();
				byte[] buffer = new byte[1024];
				File newFile = new File(FileUtil.path(jbwaDir, fileName));
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				entry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String jbwaLib = FileUtil.path(new File(jbwaDir + "/libbwajni.so").getAbsolutePath());

		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
			jbwaLib = FileUtil.path(new File(jbwaDir + "/libbwajni.jnilib").getAbsolutePath());
		}

		System.load(jbwaLib);

		this.jbwaDir = jbwaDir;

	}

}