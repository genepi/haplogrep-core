package importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.SystemUtils;

import com.github.lindenb.jbwa.jni.AlnRgn;
import com.github.lindenb.jbwa.jni.BwaIndex;
import com.github.lindenb.jbwa.jni.BwaMem;
import com.github.lindenb.jbwa.jni.ShortRead;

import htsjdk.samtools.reference.FastaSequenceFile;
import genepi.io.FileUtil;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMLineParser;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.ReferenceSequence;

public class FastaImporter {

	public enum References {
		RCRS, RSRS, HORSE, CATTLE;
	}
	
	public ArrayList<String> load(File file, References referenceType) throws FileNotFoundException, IOException {
	
		return load(file, referenceType, 16569);
		
	}

	public ArrayList<String> load(File file, References referenceType, int length) throws FileNotFoundException, IOException {

		final String BWA_VERSION = "0.7.17";

		String jbwaDir = FileUtil.path("jbwa-" + BWA_VERSION);

		String ref = "";

		if (referenceType == References.RCRS) {
			ref = "rCRS.fasta";
		}

		else if (referenceType == References.RSRS) {
			ref = "rsrs.fasta";
		}
		
		else if (referenceType == References.HORSE) {
			ref = "horse.fasta";
		}
		
		else if (referenceType == References.CATTLE) {
			ref = "cattle.fasta";
		}


		ArrayList<String> lines = new ArrayList<String>();

		extractZip(jbwaDir);

		String referenceAsString = readInReference(FileUtil.path(jbwaDir, ref));

		String jbwaLib = FileUtil.path(new File(jbwaDir + "/libbwajni.so").getAbsolutePath());

		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
			jbwaLib = FileUtil.path(new File(jbwaDir + "/libbwajni.jnilib").getAbsolutePath());
		}

		System.load(jbwaLib);
		BwaIndex index = new BwaIndex(new File(FileUtil.path(jbwaDir, ref)));
		BwaMem mem = new BwaMem(index);

		FastaSequenceFile refFasta = new FastaSequenceFile(file, true);

		ReferenceSequence sequence;

		while ((sequence = refFasta.nextSequence()) != null) {

			ShortRead read = new ShortRead(sequence.getName(), sequence.getBaseString().getBytes(), null);
			SAMFileHeader header = new SAMFileHeader();
			SAMLineParser parser = new SAMLineParser(header);

			StringBuilder profile = new StringBuilder();

			profile.append(sequence.getName() + "\t" + "1-"+length + "\t" + "?");

			// also include supplemental alignments ("chimeric reads")
			for (AlnRgn alignedRead : mem.align(read)) {

				// as defined by BWA
				if (alignedRead.getAs() < 30) {
					continue;
				}

				if (header.getSequence(alignedRead.getChrom()) == null) {
					// add contig with mtSequence length
					header.addSequence(new SAMSequenceRecord(alignedRead.getChrom(), length));
				}

				StringBuilder samRecordBulder = new StringBuilder();

				samRecordBulder.append(sequence.getName()); // READNAME

				samRecordBulder.append("\t");

				samRecordBulder.append(0); // FLAGS FORWARD, PRIMARY ALIGNMENT

				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getChrom()); // REFERENCE

				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getPos()); // LEFT MOST POS

				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getMQual()); // QUAL

				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getCigar()); // CIGAR

				samRecordBulder.append("\t");

				samRecordBulder.append("*\t0\t0\t"); // RNEXT (REF NAME OF THE MATE)
														// PNEXT

				samRecordBulder.append(sequence.getBaseString() + "\t"); // SEQ FORWARD

				samRecordBulder.append("*\t");

				samRecordBulder.append("AS:i:" + alignedRead.getAs());

				SAMRecord samRecord = parser.parseLine(samRecordBulder.toString());

				String variants = readCigar(samRecord, referenceAsString);

				profile.append(variants);
				
			}

			lines.add(profile.toString());

		}

		refFasta.close();

		return lines;
	}

	private String readCigar(SAMRecord samRecord, String reference) {

		String readString = samRecord.getReadString();

		StringBuilder pos = new StringBuilder();

		for (int i = 0; i < readString.length(); i++) {

			int currentPos = samRecord.getReferencePositionAtReadPosition(i + 1);

			char inputBase = readString.charAt(i);

			// e.g. INS having currentPos 0
			if (currentPos > 0) {

				char referenceBase = reference.charAt(currentPos - 1);

				if (inputBase != referenceBase) {

					pos.append("\t" + currentPos + "" + inputBase);

				}

			} else {

			}
		}

		Integer currentReferencePos = samRecord.getAlignmentStart();

		int sequencePos = 0;

		for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {

			Integer cigarElementLength = cigarElement.getLength();
			
			StringBuilder buildDeletion = new StringBuilder();

			if (cigarElement.getOperator() == CigarOperator.D) {

				// start of D is currentRefPos: Don't add 1 before!
				Integer cigarElementStart = currentReferencePos;

				Integer cigarElementEnd = currentReferencePos + cigarElementLength;
				
				buildDeletion.append(cigarElementStart+"-"+(cigarElementEnd-1)+"d");

				while (cigarElementStart < cigarElementEnd) {

					//pos.append("\t" + cigarElementStart + "d");
					cigarElementStart++;
				}
				
				pos.append("\t" + buildDeletion.toString());

			}

			if (cigarElement.getOperator() == CigarOperator.I) {

				// returns e.g. 310 but Insertion need to be added to last pos (so 309)
				int currentReferencePosIns = currentReferencePos - 1;

				int i = 1;

				int length = cigarElement.getLength();
				
				StringBuilder buildInsertion = new StringBuilder();

				while (i <= length) {

					char insBase = samRecord.getReadString().charAt(sequencePos + i - 1);
					
					buildInsertion.append(insBase);
					// pos.append("\t" + currentReferencePosIns + "." + i + "" + insBase);
					i++;
				}
				
				pos.append("\t" + currentReferencePosIns + ".1" + buildInsertion.toString());

			}

			// only M and D operators consume bases
			if (cigarElement.getOperator().consumesReferenceBases()) {
				currentReferencePos = currentReferencePos + cigarElement.getLength();
			}

			// give back current readPos, only increase if read bases are consumed!
			if (cigarElement.getOperator().consumesReadBases()) {
				sequencePos = sequencePos + cigarElement.getLength();
			}

		}

		return pos.toString();
	}

	public static String readInReference(String file) {
		StringBuilder stringBuilder = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			stringBuilder = new StringBuilder();

			while ((line = reader.readLine()) != null) {

				if (!line.startsWith(">"))
					stringBuilder.append(line);

			}

			reader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}

	private void extractZip(String jbwaDir) throws IOException, FileNotFoundException {

		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("jbwa.zip");

		ZipInputStream zis = new ZipInputStream(stream);

		ZipEntry entry = zis.getNextEntry();

		if (!new File(jbwaDir).exists()) {

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
		}
	}

}
