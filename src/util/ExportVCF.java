package util;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;

public class ExportVCF {

	//static final Log log = LogFactory.getLog(ExportVCF.class);
	NumberFormat outFormat = new DecimalFormat("0.###", DecimalFormatSymbols.getInstance(Locale.US));
	String dosageField = "DS";

	public static File stream2file (InputStream in) throws IOException {
        final File tempFile = File.createTempFile("chrm","vcf");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
            out.close();
        }
        in.close();
        
        return tempFile;
    }
	
	
	/**
	 * Save VCF file
	 * 
	 * @param session
	 * @return
	 * @throws InvalidBaseException
	 */
	public File generateVCF(Collection<TestSample> sampleCollection, String outname) throws Exception {

		String fastaResult = Polymorphism.rCRS;

		Collections.sort((List<TestSample>) sampleCollection);
		
		/** we don't need some indexed VCFs */
		boolean requireIndex = false;

		InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream("chrm.vcf");
		   
		final VCFFileReader reader = new VCFFileReader(stream2file(phyloFile), requireIndex);
			
		final VCFHeader header = new VCFHeader(reader.getFileHeader());
		header.addMetaDataLine(new VCFFormatHeaderLine(dosageField, 1, VCFHeaderLineType.Float, "Genotype dosage"));
		reader.close();

		header.getGenotypeSamples().remove("ID1");

		final SAMSequenceDictionary sequenceDictionary = header.getSequenceDictionary();
		File f = new File(outname + ".vcf");


		final VariantContextWriterBuilder builder = new VariantContextWriterBuilder().setOutputFile(f).setReferenceDictionary(sequenceDictionary);

		VariantContextWriter writer = builder.build();

		if (sampleCollection != null) {
			for (TestSample sample : sampleCollection) {
				header.getGenotypeSamples().add(sample.getSampleID()); // add
																		// IDS
			}
		}
		/* write the header */
		writer.writeHeader(header);

		Vector<Integer> vPosFound = new Vector<Integer>();

		if (sampleCollection != null) {
			for (TestSample sample : sampleCollection) {

				for (Polymorphism poly : sample.getSample().getPolymorphisms()) {
					Polymorphism pos = poly;
					if (!vPosFound.contains(pos.getPosition()) && pos.getPosition()!=0) { //check for 0 needed for H2a2a1 samples
						vPosFound.add(pos.getPosition());
					}
				}
			}
		}

		Collections.sort(vPosFound);
		

		writePosition(sampleCollection, fastaResult, writer, vPosFound, true);

		Vector<Integer> vPosNotFound = new Vector<Integer>();

		if (sampleCollection != null) {
			for (TestSample sample : sampleCollection) {
				for (Polymorphism poly : sample.getResults().get(0).getSearchResult().getDetailedResult().getFoundNotFoundPolysArray()) {
					Polymorphism pos = poly;
					if (!vPosNotFound.contains(pos.getPosition()) && pos.getPosition()!=0 ) { //check for 0 needed for H2a2a1 samples
					if (!vPosFound.contains(pos.getPosition()))
						vPosNotFound.add(pos.getPosition());
					}
				}
			}
		}

		Collections.sort(vPosNotFound);

		writePosition(sampleCollection, fastaResult, writer, vPosNotFound, false);

		System.out.println(f.getAbsolutePath());
		writer.close();
		return f;
	}

	/**
	 * @param sampleCollection
	 * @param fastaResult
	 * @param writer
	 * @param vPos
	 */
	private void writePosition(Collection<TestSample> sampleCollection, String fastaResult, VariantContextWriter writer, Vector<Integer> vPos,
			boolean expected ) {
		double dosage = 0.0;
		for (int i = 0; i < vPos.size(); i++) {
			final List<Genotype> genotypes = new ArrayList<Genotype>();
			ArrayList<String> base = new ArrayList<String>();
			for (TestSample sample : sampleCollection) {
				String id = sample.getSampleID();

				boolean found = false;
				Genotype g1 = null;

				try {
					if (expected) {
						for (Polymorphism poly : sample.getSample().getPolymorphisms()) {

							if (poly.getPosition() == vPos.get(i)) {
								if (!poly.isHeteroplasmy()) {
									if (!poly.getMutation().toString().contains("I") && !poly.getMutation().toString().contains("D")
											&& !poly.getMutation().toString().contains("N") && !poly.getMutation().toString().contains("Y")
											&& !poly.getMutation().toString().contains("R") && !poly.getMutation().toString().contains("M")
											&& !poly.getMutation().toString().contains("K") && !poly.getMutation().toString().contains("W")
											&& !poly.getMutation().toString().contains("S")) {
										g1 = GenotypeBuilder.create(id, Arrays.asList(Allele.create(poly.getMutation().toString().charAt(0) + ""),
												Allele.create(poly.getMutation().toString().charAt(0) + "")));

										if (!base.contains(fastaResult.charAt(vPos.get(i) - 1) + "")) {
											base.add(fastaResult.charAt(vPos.get(i) - 1) + "");
										}

										if (!base.contains(poly.getMutation().toString().charAt(0) + "")) {
											base.add(poly.getMutation().toString().charAt(0) + "");
										}
										found = true;
										dosage = 2.0;
									}

									else if (poly.getMutation().toString().contains("I")) {
										g1 = GenotypeBuilder.create(id,
												Arrays.asList(Allele.create(fastaResult.charAt(vPos.get(i) - 1) + poly.getInsertedPolys()),
														Allele.create(fastaResult.charAt(vPos.get(i) - 1) + poly.getInsertedPolys())));

										if (!base.contains(fastaResult.charAt(vPos.get(i) - 1) + "")) { // REF
											base.add(fastaResult.charAt(vPos.get(i) - 1) + "");
										}
										if (!base.contains(fastaResult.charAt(vPos.get(i) - 1) + (poly.getInsertedPolys()))) {
											base.add(fastaResult.charAt(vPos.get(i) - 1) + (poly.getInsertedPolys()));
										}
										found = true;
										dosage = 2.0;

									}
								}
							}
						}

					}
					//
					if (!expected) {
						for (Polymorphism poly : sample.getResults().get(0).getSearchResult().getDetailedResult().getFoundNotFoundPolysArray()) {

							if (poly.getPosition() == vPos.get(i)) {
								if (!poly.isHeteroplasmy()) {
									if (!poly.getMutation().toString().contains("I") && !poly.getMutation().toString().contains("D")
											&& !poly.getMutation().toString().contains("N") && !poly.getMutation().toString().contains("Y")
											&& !poly.getMutation().toString().contains("R") && !poly.getMutation().toString().contains("M")
											&& !poly.getMutation().toString().contains("K") && !poly.getMutation().toString().contains("W")
											&& !poly.getMutation().toString().contains("S")) {
										g1 = GenotypeBuilder.create(id, Arrays.asList(Allele.create(poly.getMutation().toString().charAt(0) + ""),
												Allele.create(poly.getMutation().toString().charAt(0) + "")));

										if (!base.contains(fastaResult.charAt(vPos.get(i) - 1) + "")) {
											base.add(fastaResult.charAt(vPos.get(i) - 1) + "");
										}

										if (!base.contains(poly.getMutation().toString().charAt(0) + "")) {
											base.add(poly.getMutation().toString().charAt(0) + "");
										}
										found = true;
										dosage = 2;
									}

									else if (poly.getMutation().toString().contains("I")) {
										g1 = GenotypeBuilder.create(id,
												Arrays.asList(Allele.create(fastaResult.charAt(vPos.get(i) - 1) + poly.getInsertedPolys()),
														Allele.create(fastaResult.charAt(vPos.get(i) - 1) + poly.getInsertedPolys())));

										if (!base.contains(fastaResult.charAt(vPos.get(i) - 1) + "")) { // REF
											base.add(fastaResult.charAt(vPos.get(i) - 1) + "");
										}
										if (!base.contains(fastaResult.charAt(vPos.get(i) - 1) + (poly.getInsertedPolys()))) {
											base.add(fastaResult.charAt(vPos.get(i) - 1) + (poly.getInsertedPolys()));
										}
										found = true;
										dosage = 2;
									}
								}
							}
						}
					}

					if (!found) 
					{
						if (vPos.get(i) < 16569 && vPos.get(i) > 0)
							g1 = GenotypeBuilder.create(id, Arrays.asList(Allele.create(fastaResult.charAt(vPos.get(i) - 1) + "", true),
									Allele.create(fastaResult.charAt(vPos.get(i) - 1) + "", true)));
							dosage = 0.0;
					}
					if (vPos.get(i)!=0) {

					Genotype g = new GenotypeBuilder(g1).attribute(dosageField, outFormat.format(dosage)).make();
				//	if (!g1.getAllele(0).getBaseString().toUpperCase().equals(g1.getAllele(1).getBaseString().toUpperCase()))
					genotypes.add(new GenotypeBuilder(g).phased(true).make());
				//	else
				//		System.out.println("g1" + g1.getSampleName() +" "+ g1.getAllele(0).getBaseString() + " " +g1.getAllele(1).getBaseString());

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// if (vPos.get(i).getPosition() < 309 || vPos.get(i).getPosition()
			// > 525)
			{
				if (base.size() != 0) {
					if (!base.get(0).toUpperCase().equals(base.get(1).toUpperCase())) 
					{
					VariantContext vc = new VariantContextBuilder().start(vPos.get(i)).stop(vPos.get(i)).alleles(base).genotypes(genotypes).chr("MT").make();
					writer.add(vc);
					}
				}
			}
		}
	}

	public static void main(String args[]) throws Exception {

		/** we don't need some indexed VCFs */
		boolean requireIndex = false;

		final VCFFileReader reader = new VCFFileReader(new File("src/server/rest/export/chrm.vcf"), requireIndex);
		final VCFHeader header = new VCFHeader(reader.getFileHeader());
		header.getGenotypeSamples().remove("ID1");
		header.getGenotypeSamples().add("ID2"); // add sample IDS
		header.getGenotypeSamples().add("ID3"); // add sample IDS
		final SAMSequenceDictionary sequenceDictionary = header.getSequenceDictionary();

		/* loop over each vcf */
		for (int i = 0; i < 1; ++i) {
			File fileout = new File("tmp" + i + ".vcf");

			final VariantContextWriterBuilder builder = new VariantContextWriterBuilder().setOutputFile(fileout).setReferenceDictionary(sequenceDictionary);

			VariantContextWriter writer = builder.build();

			/* write the header */
			writer.writeHeader(header);
			/** loop over each Variation */
			Allele G = Allele.create("G");
			Allele Cref = Allele.create("C", true);
			Allele C = Allele.create("C");
			Allele CA = Allele.create("CA");
			Allele CC = Allele.create("CC");
			Allele CAC = Allele.create("CAC", true);
			Allele Aref = Allele.create("A", true);
			Allele Nref = Allele.create("CN", true);

			/* get next variation and save it */
			final List<Genotype> genotypes = new ArrayList<Genotype>();
			Genotype g = GenotypeBuilder.create("ID2", Arrays.asList(G, G));
			Genotype g2 = GenotypeBuilder.create("ID3", Arrays.asList(C, C));
			genotypes.add(g);
			genotypes.add(g2);
			final List<Genotype> genotypes2 = new ArrayList<Genotype>();
			Genotype g1 = GenotypeBuilder.create("ID2", Arrays.asList(Aref, Aref));
			Genotype g12 = GenotypeBuilder.create("ID3", Arrays.asList(G, G));
			genotypes2.add(g1);
			genotypes2.add(g12);
			final List<Genotype> genotypes3 = new ArrayList<Genotype>();
			Genotype g3 = GenotypeBuilder.create("ID2", Arrays.asList(C, C));
			Genotype g4 = GenotypeBuilder.create("ID3", Arrays.asList(CAC, CAC));
			genotypes3.add(g3);
			genotypes3.add(g4);

			final List<Genotype> genotypes4 = new ArrayList<Genotype>();
			Genotype g5 = GenotypeBuilder.create("ID2", Arrays.asList(Cref, Cref));
			Genotype g6 = GenotypeBuilder.create("ID3", Arrays.asList(CC, CC));
			genotypes4.add(g5);
			genotypes4.add(g6);

			final List<Genotype> genotypes5 = new ArrayList<Genotype>();
			Genotype g7 = GenotypeBuilder.create("ID2", Arrays.asList(Nref, Nref));
			Genotype g8 = GenotypeBuilder.create("ID3", Arrays.asList(C, C));
			genotypes5.add(g7);
			genotypes5.add(g7);

			String s = "";
			for (int j = 0; j < genotypes3.size(); j++) {
				s += genotypes3.get(j).getLikelihoodsString();
			}

			VariantContext vc = new VariantContextBuilder().stop(73).start(73).alleles("A", "G", "C").genotypes(genotypes).chr("MT").make();
			writer.add(vc);
			vc = new VariantContextBuilder().stop(263).start(263).alleles("A", "G").genotypes(genotypes2).chr("MT").make();
			writer.add(vc);
			vc = new VariantContextBuilder().stop(524).start(522).alleles("CAC", "C").genotypes(genotypes3).chr("MT").make();
			writer.add(vc);
			vc = new VariantContextBuilder().stop(315).start(315).alleles("C", "CC").genotypes(genotypes4).chr("MT").make();
			writer.add(vc);
			vc = new VariantContextBuilder().stop(3107).start(3106).alleles("CN", "C").genotypes(genotypes5).chr("MT").make();
			writer.add(vc);

			/* we're done */
			reader.close();
			writer.close();
			fileout.delete();
		}
	}

}
