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

	public ArrayList<String> load(File file, boolean rsrs) throws FileNotFoundException, IOException {

		String jbwaDir = "jbwa";

		String ref = "rCRS.fasta";

		if (rsrs) {
			ref = "rsrs.fasta";
		}

		ArrayList<String> lines = new ArrayList<String>();

		extractZip(jbwaDir);

		String reference = readInReference(FileUtil.path(jbwaDir, ref));

		String jbwaLib = FileUtil.path(new File(jbwaDir + "/libbwajni.so").getAbsolutePath());

		System.load(jbwaLib);
		BwaIndex index = new BwaIndex(new File(FileUtil.path(jbwaDir, ref)));
		BwaMem mem = new BwaMem(index);

		FastaSequenceFile refFasta = new FastaSequenceFile(file, true);

		ReferenceSequence sequence;

		while ((sequence = refFasta.nextSequence()) != null) {

			ShortRead read = new ShortRead(sequence.getName(), sequence.getBaseString().getBytes(), null);
			SAMFileHeader header = new SAMFileHeader();
			SAMLineParser parser = new SAMLineParser(header);

			for (AlnRgn alignedRead : mem.align(read)) {

				if (header.getSequence(alignedRead.getChrom()) == null) {
					// add contig with mtSequence length
					header.addSequence(new SAMSequenceRecord(alignedRead.getChrom(), 16569));
				}

				StringBuilder samRecordBulder = new StringBuilder();
				samRecordBulder.append(sequence.getName()); // READNAME
				samRecordBulder.append("\t");

				samRecordBulder.append(0); // FLAGS FORWARD, PRIMARY ALIGNMENT

				// samRecordBulder.append(key.toString()); // READNAME
				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getChrom()); // REFERENCE

				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getPos() + 1); // LEFT MOST POS

				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getMQual()); // QUAL

				samRecordBulder.append("\t");

				samRecordBulder.append(alignedRead.getCigar()); // CIGAR

				samRecordBulder.append("\t");

				samRecordBulder.append("*\t0\t0\t"); // RNEXT (REF NAME OF THE MATE)
														// PNEXT

				samRecordBulder.append(sequence.getBaseString() + "\t"); // SEQ FORWARD

				samRecordBulder.append("*\t");

				samRecordBulder.append("AS:i:" + alignedRead.getNm());

				SAMRecord samRecord = parser.parseLine(samRecordBulder.toString());

				String line = samToHsd(samRecord, reference);

				lines.add(line);

			}

		}

		refFasta.close();
		return lines;
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

	private String samToHsd(SAMRecord samRecord, String reference) {

		String readString = samRecord.getReadString();

		StringBuilder pos = new StringBuilder();

		System.out.println("cc " + samRecord.getCigar());

		for (int i = 0; i < readString.length(); i++) {

			int currentPos = samRecord.getReferencePositionAtReadPosition(i + 1);

			char inputBase = readString.charAt(i);

			if (currentPos > 0) {

				char referenceBase = reference.charAt(currentPos - 1);

				if (inputBase != referenceBase) {

					pos.append("\t" + currentPos + "" + inputBase);

				}

			} else {

			}
		}

		Integer currentReferencePos = samRecord.getAlignmentStart();

		int currentPosForIns = 0;

		for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {

			Integer cigarElementLength = cigarElement.getLength();

			if (cigarElement.getOperator() == CigarOperator.D) {

				// start of D is currentRefPos: Don't add 1 before!
				Integer cigarElementStart = currentReferencePos;

				Integer cigarElementEnd = currentReferencePos + cigarElementLength;

				while (cigarElementStart < cigarElementEnd) {

					pos.append("\t" + cigarElementStart + "d");

					cigarElementStart++;
				}

			}

			// update read position (not included in consumesReferenceBases)
			if (cigarElement.getOperator() == CigarOperator.S) {

				currentPosForIns = currentPosForIns + cigarElement.getLength();

			}

			if (cigarElement.getOperator() == CigarOperator.I) {

				int i = 1;

				int length = cigarElement.getLength();

				while (i <= length) {

					// -1 since array starts at 0
					int arrayPos = currentPosForIns + i - 1;

					char insBase = samRecord.getReadString().charAt(arrayPos);

					pos.append("\t" + currentPosForIns + "." + i + "" + insBase);

					i++;
				}

				// update read position (not included in consumesReferenceBases)
				currentPosForIns = currentPosForIns + cigarElement.getLength();

			}

			// only M and D operators consume bases
			if (cigarElement.getOperator().consumesReferenceBases()) {
				currentReferencePos = currentReferencePos + cigarElement.getLength();

				// don't increase D, since not included in base string!
				if (cigarElement.getOperator() != CigarOperator.D) {
					currentPosForIns = currentPosForIns + cigarElement.getLength();
				}
			}

		}

		StringBuilder profile = new StringBuilder();

		profile.append(samRecord.getReadName() + "\t" + "1-16569" + "\t" + "?" + pos.toString());

		return profile.toString();
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
}
