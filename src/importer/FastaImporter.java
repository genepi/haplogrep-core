package importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.SystemUtils;

import com.github.lindenb.jbwa.jni.AlnRgn;
import com.github.lindenb.jbwa.jni.BwaIndex;
import com.github.lindenb.jbwa.jni.BwaMem;
import com.github.lindenb.jbwa.jni.ShortRead;

import core.Reference;
import genepi.io.FileUtil;
import htsjdk.samtools.reference.FastaSequenceFile;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMLineParser;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.ReferenceSequence;

public class FastaImporter {
	
	static 
	{
		String jbwaDir = FileUtil.path("jbwa-" + System.currentTimeMillis() + "");

		try {
			
			InputStream stream = FastaImporter.class.getClassLoader().getResourceAsStream("jbwa.zip");

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
	}

	private String range;

	public ArrayList<String> load(File file, Reference reference) throws FileNotFoundException, IOException {
		
		// load required 
	
		ArrayList<String> lines = new ArrayList<String>();
		
		BwaIndex index = new BwaIndex(new File(reference.getRefFilename()));
		
		BwaMem mem = new BwaMem(index);

		FastaSequenceFile refFasta = new FastaSequenceFile(file, true);

		ReferenceSequence sequence;

		while ((sequence = refFasta.nextSequence()) != null) {

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
					header.addSequence(new SAMSequenceRecord(alignedRead.getChrom(), reference.getLength()));
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

				String variants = readCigar(samRecord, reference.getSequence());
				
				if (first) {
					profile.append(sequence.getName() + "\t" + range + "\t" + "?");
					first = false;
				}

				profile.append(variants);

			}

			lines.add(profile.toString());

		}

		refFasta.close();
		
		return lines;
	}

	private String readCigar(SAMRecord samRecord, String reference) {

		String readString = samRecord.getReadString();

		//System.out.println(readString.length() + " " + samRecord.getCigarString());

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
					//System.out.println("START " + start);
				}
			}

			if (i == 0 && currentPos != 0) {

				start = currentPos;
			}

			char inputBase = readString.charAt(i);

			// e.g. INS and DEL having currentPos 0
			if (currentPos > 0) {
				lastpos = currentPos;
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
					// pos.append("\t" + currentPos + "" + inputBase);
					continue;
				}

				char referenceBase = reference.charAt(currentPos - 1);

				if (inputBase != referenceBase) {

					pos.append("\t" + currentPos + "" + inputBase);

				}

			}
		}
		Integer currentReferencePos = samRecord.getAlignmentStart();
		
		//System.out.println("CurrentREF POS " + currentReferencePos + " cigar " + samRecord.getCigarString());

		int sequencePos = 0;

		int first=0;
		//System.out.println("CONTIG " + samRecord.getContig());
		
		for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {

			Integer cigarElementLength = cigarElement.getLength();

			if (first==0) {
				if (cigarElement.getOperator()==CigarOperator.S) {
					currentReferencePos=1;
					//System.out.println("HERE Cigar.S");
				}
				first++;
			}
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
			//System.out.println("Check Operator BEFORE " + cigarElement.getOperator() + " " + samRecord.getCigarString());

			if (cigarElement.getOperator().consumesReferenceBases()) {
				//System.out.println("Check Operator AFTER " + cigarElement.getOperator());
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
		// System.out.println("start " + start + " + " + stop);
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
		range += lastpos + "-" + stop + ";";
		return range;
	}


	/*
	 * public String invertRange(String unavailablePositions, int start, int
	 * stop, int length) {
	 * 
	 * StringBuilder range = new StringBuilder();
	 * 
	 * int lastPos = start;
	 * 
	 * if (unavailablePositions.length() == 0) { return (start + "-" + stop +
	 * ";"); }
	 * 
	 * String[] splits = unavailablePositions.split(";");
	 * 
	 * for (String split : splits) {
	 * 
	 * int currentN = Integer.valueOf(split);
	 * 
	 * if (currentN > lastPos) { range.append(lastPos + "-" + (currentN - 1) +
	 * ";"); lastPos = currentN + 1; } else if (currentN == lastPos) {
	 * lastPos++; } } if (lastPos <= length) { range.append(lastPos + "-" + stop
	 * + ";"); } return range.toString(); }
	 */

}
