package importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import com.github.lindenb.jbwa.jni.AlnRgn;
import com.github.lindenb.jbwa.jni.BwaIndex;
import com.github.lindenb.jbwa.jni.BwaMem;
import com.github.lindenb.jbwa.jni.ShortRead;

import core.Reference;
import core.TestSample;
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

	/* Get actual class name to be printed on */

	final Logger log = Logger.getLogger(FastaImporter.class);

	public enum References {
		RCRS, RSRS, HORSE, CATTLE, SARSCOV2;
	}

	private String range;
	private String reference;

	public Reference loadrCRS() throws FileNotFoundException, IOException {
		String refFile = "rCRS.fasta";
		String reference = extract(refFile);
		Reference RefObj = new Reference("RCRS", reference, reference.length(), refFile);
		return RefObj;
	}

	public Reference loadRSRS() throws FileNotFoundException, IOException {
		String refFile = "rsrs.fasta";
		String reference = extract(refFile);
		Reference RefObj = new Reference("RSRS", reference, reference.length(), refFile);
		return RefObj;
	}

	public Reference loadSARSCOV2() throws FileNotFoundException, IOException {
		String refFile = "sarscov2.fasta";
		String reference = extract(refFile);
		Reference RefObj = new Reference("SARSCOV2", reference, reference.length(), refFile);
		return RefObj;
	}

	private String extract(String ref) throws IOException, FileNotFoundException {
		String jbwaDir = FileUtil.path("jbwa-" + System.currentTimeMillis() + "");
		extractZip(jbwaDir);
		String referenceAsString = readInReference(FileUtil.path(jbwaDir, ref));
		reference = referenceAsString;
		FileUtil.deleteDirectory(jbwaDir);
		return reference;
	}

	public ArrayList<String> load(File infile, Reference ref) throws FileNotFoundException, IOException {

		long startTime = System.currentTimeMillis();
		String jbwaDir = FileUtil.path("jbwa-" + System.currentTimeMillis() + "");
		extractZip(jbwaDir);

		ArrayList<String> lines = new ArrayList<String>();

		String referenceAsString = ref.getSequence();
		reference = referenceAsString;

		String jbwaLib = FileUtil.path(new File(jbwaDir + "/libbwajni.so").getAbsolutePath());

		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
			jbwaLib = FileUtil.path(new File(jbwaDir + "/libbwajni.jnilib").getAbsolutePath());
		}

		System.load(jbwaLib);
		BwaIndex index = new BwaIndex(new File(FileUtil.path(jbwaDir, ref.getFilename())));
		BwaMem mem = new BwaMem(index);

		log.info("run time for BWA index: " + (System.currentTimeMillis() - startTime));

		FastaSequenceFile inputFasta = new FastaSequenceFile(infile, true);

		ReferenceSequence sequence;
		int countFastas = 0;
		long startTimeReadFasta = System.currentTimeMillis();
		while ((sequence = inputFasta.nextSequence()) != null) {

			ShortRead read = new ShortRead(sequence.getName(), sequence.getBaseString().getBytes(), null);
			SAMFileHeader header = new SAMFileHeader();
			SAMLineParser parser = new SAMLineParser(header);

			StringBuilder profile = new StringBuilder();

			// also include supplemental alignments ("chimeric reads")

			boolean first = true;
			for (AlnRgn alignedRead : mem.align(read)) {

				// as defined by BWA
				if (alignedRead.getAs() < 30) {
					continue;
				}

				if (header.getSequence(alignedRead.getChrom()) == null) {
					// add contig with mtSequence length
					header.addSequence(new SAMSequenceRecord(alignedRead.getChrom(), reference.length()));
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

				samRecordBulder.append("*\t0\t0\t"); // RNEXT (REF NAME OF THE
														// MATE)
														// PNEXT

				samRecordBulder.append(sequence.getBaseString() + "\t"); // SEQ
																			// FORWARD

				samRecordBulder.append("*\t");

				samRecordBulder.append("AS:i:" + alignedRead.getAs());

				SAMRecord samRecord = parser.parseLine(samRecordBulder.toString());

				String variants = readCigar(samRecord, referenceAsString);

				if (first) {
					profile.append(sequence.getName() + "\t" + range + "\t" + "?");
					first = false;
				}
				profile.append(variants);

			}
			countFastas++;
			lines.add(profile.toString());
		}

		log.info("run time for alignment of " + countFastas + " sequence(s): " + (System.currentTimeMillis() - startTimeReadFasta));

		inputFasta.close();

		FileUtil.deleteDirectory(jbwaDir);

		return lines;
	}

	private String readCigar(SAMRecord samRecord, String reference) {

		String readString = samRecord.getReadString();
		System.out.println(readString);
		StringBuilder pos = new StringBuilder();
		StringBuilder _range = new StringBuilder();
		int start = 0;
		int lastpos = 0;
		int countZero = 0;
		for (int i = 0; i < readString.length(); i++) {

			int currentPos = samRecord.getReferencePositionAtReadPosition(i + 1);

			// if Ns at beginning, samrecord gets 0
			if (countZero == 0) {
				if (currentPos != 0) {
					countZero = currentPos;
					start = currentPos;
				}
			}

			if (i == 0 && currentPos != 0) {

				start = currentPos;
			}
			lastpos = currentPos;
			char inputBase = readString.charAt(i);

			// e.g. INS and DEL having currentPos 0
			if (currentPos > 0) {

				/*
				 * if (startRange && inputBase != 'N') {
				 * _range.append(currentPos); startRange = false; }
				 * 
				 * else if (inputBase == 'N') { _range.append("-" + (currentPos
				 * - 1) + "; "); startRange = true; }
				 */

				if (inputBase == 'N') {
					_range.append(currentPos + ";");
					if (start == 0)
						start = currentPos + 1;
					pos.append("\t" + currentPos + "" + inputBase);
				}

				if (inputBase != 'A' && inputBase != 'C' && inputBase != 'G' && inputBase != 'T') {
					continue;
				}

				char referenceBase = reference.charAt(currentPos - 1);

				if (inputBase != referenceBase) {

					pos.append("\t" + currentPos + "" + inputBase);

				}

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

				buildDeletion.append(cigarElementStart + "-" + (cigarElementEnd - 1) + "d");

				while (cigarElementStart < cigarElementEnd) {

					// pos.append("\t" + cigarElementStart + "d");
					cigarElementStart++;
				}
				pos.append("\t" + buildDeletion.toString());

			}

			if (cigarElement.getOperator() == CigarOperator.I) {

				// returns e.g. 310 but Insertion need to be added to last pos
				// (so 309)
				int currentReferencePosIns = currentReferencePos - 1;

				int i = 1;

				int length = cigarElement.getLength();

				StringBuilder buildInsertion = new StringBuilder();

				while (i <= length) {

					char insBase = samRecord.getReadString().charAt(sequencePos + i - 1);

					buildInsertion.append(insBase);
					// pos.append("\t" + currentReferencePosIns + "." + i + "" +
					// insBase);
					i++;
				}

				pos.append("\t" + currentReferencePosIns + ".1" + buildInsertion.toString());

			}

			// only M and D operators consume bases
			if (cigarElement.getOperator().consumesReferenceBases()) {
				currentReferencePos = currentReferencePos + cigarElement.getLength();
			}

			// give back current readPos, only increase if read bases are
			// consumed!
			if (cigarElement.getOperator().consumesReadBases()) {
				sequencePos = sequencePos + cigarElement.getLength();
			}

		}
		this.range = cleanRange(_range.toString(), start, lastpos);
		return pos.toString();
	}

	private String cleanRange(String emptyPos, int start, int stop) {
		String range = "";
		int lastpos = start;
		System.out.println("start  " + start + "  + " + stop);
		if (emptyPos.length() == 0)
			return (start + "-" + stop + ";");
		StringTokenizer st = new StringTokenizer(emptyPos, ";");
		while (st.hasMoreTokens()) {
			int posN = Integer.valueOf(st.nextToken());
			if (posN > lastpos) {
				range += lastpos + "-" + (posN - 1) + ";";
				lastpos = posN + 1;
			} else if (posN == lastpos) {
				lastpos++;
			}
		}
		range += lastpos + "-" + reference.length() + ";";
		return range;
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
