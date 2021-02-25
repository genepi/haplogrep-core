package core;

import java.io.IOException;

import importer.FastaImporter;

public class Reference {

	String name;
	String sequence;
	int length;
	String filename;

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
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Reference() {
		if (this.sequence == null) {
			FastaImporter fasta = new FastaImporter();
			try {
			Reference ref=	fasta.loadrCRS();
			this.length=ref.length;
			this.name=ref.name;
			this.sequence=ref.sequence;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Reference(String name, String sequence, int length, String filename) {
		this.name = name;
		this.sequence = sequence;
		this.length = length;
		this.filename = filename;
	}

}
