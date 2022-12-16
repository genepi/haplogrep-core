package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Reference {

	String name;
	String refFilename;
	String sequence;
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
	
	public String getRefFilename() {
		return refFilename;
	}

	public void setRefFilename(String refFilename) {
		this.refFilename = refFilename;
	}


	public Reference(String refFilename) {

		this.refFilename = refFilename;

		loadReference(refFilename);

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


}