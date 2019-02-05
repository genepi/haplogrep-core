package importer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import vcf.Sample;
import vcf.Variant;

public class VcfImporter {

	public HashMap<String, Sample> load(File file, boolean chip) throws Exception {

		final VCFFileReader vcfReader = new VCFFileReader(file, false);

		VCFHeader vcfHeader = vcfReader.getFileHeader();

		HashMap<String, Sample> samples = new HashMap<String, Sample>();

		StringBuilder range = new StringBuilder();

		if (chip) {

			for (VariantContext vc : vcfReader) {

				range.append(vc.getStart() + ";");

			}

			vcfReader.close();

		} else {

			range.append("1-16569");

		}

		for (final VariantContext vc : vcfReader) {

			if (vc.getStart() > 16569) {

				System.out
						.println("Error! Position " + vc.getStart() + " outside the range. Please double check if VCF includes variants mapped to rCRS only.");
			}

			String reference = vc.getReference().getBaseString();

			for (String sampleVcf : vcfHeader.getSampleNamesInOrder()) {

				Sample sample = samples.get(sampleVcf);

				if (sample == null) {
					sample = new Sample();
					sample.setId(sampleVcf);
					sample.setRange(range.toString());
				}

				Genotype genotype = vc.getGenotype(sampleVcf);

				// only HOM_VAR is expected! (special handling for multiallelics below)
				if (genotype.getType() == GenotypeType.HOM_VAR) {

					if (genotype.getPloidy() > 1) {

						Allele altAllele = Allele.create(genotype.getAlleles().get(0), false);

						final List<Allele> alleles = new ArrayList<Allele>();

						alleles.add(altAllele);

						genotype = new GenotypeBuilder(genotype).alleles(alleles).make();

					}

					String genotypeString = genotype.getGenotypeString(true);

					if (genotypeString.length() == reference.length()) {

						if (genotypeString.length() == 1) {

							char base = genotypeString.charAt(0);
							Variant variant = new Variant();
							variant.setPos(vc.getStart());
							variant.setRef(reference.charAt(0));

							if (genotype.getGenotypeString().equals("*")) {
								variant.setVariantBase('d');
								variant.setType(4);
							} else {
								variant.setVariantBase(base);
								variant.setType(1);
							}

							if (genotype.hasAnyAttribute("DP")) {
								int coverage = (int) vc.getGenotype(sampleVcf).getAnyAttribute("DP");
								variant.setCoverage(coverage);
							}

							sample.addVariant(variant);

						} else {

							// check for SNPS with complex genotypes (REF: ACA; GENOTYPE-> ACT --> SNP is T)
							for (int i = 0; i < genotypeString.length(); i++) {

								if (reference.charAt(i) != genotypeString.charAt(i)) {

									int pos = vc.getStart() + i;
									char base = genotypeString.charAt(i);
									Variant variant = new Variant();
									variant.setPos(pos);
									variant.setRef(reference.charAt(0));
									variant.setVariantBase(base);
									variant.setType(1);

									if (genotype.hasAnyAttribute("DP")) {
										int coverage = (int) vc.getGenotype(sampleVcf).getAnyAttribute("DP");
										variant.setCoverage(coverage);
									}
									sample.addVariant(variant);
								}

							}
						}

					}

					// DELETIONS
					else if (reference.length() > genotypeString.length()) {

						int diff = reference.length() - genotypeString.length();

						for (int i = 0; i < diff; i++) {
							int pos = vc.getStart() + genotypeString.length() + i;
							Variant variant = new Variant();
							variant.setPos(pos);
							variant.setRef(reference.charAt(0));
							variant.setVariantBase('d');
							variant.setType(4);
							sample.addVariant(variant);

							if (genotype.hasAnyAttribute("DP")) {
								int coverage = (int) vc.getGenotype(sampleVcf).getAnyAttribute("DP");
								variant.setCoverage(coverage);
							}
						}
					}

					// INSERTIONS
					// TODO CASE CC to CCC a thing?
					else if (reference.length() < genotypeString.length()) {

						// reference completely included in genotype string, only new bases at the end
						Variant variant = new Variant();

						if (reference.length() == 1) {
							int pos = vc.getStart();
							variant.setPos(pos);
							variant.setRef(reference.charAt(0));
							variant.setType(5);
							String insertion = pos + "." + 1 + genotypeString.substring(reference.length(), (genotypeString.length()));
							variant.setInsertion(insertion);
							sample.addVariant(variant);
						} else {
							// insertions are added "left": from CT to CCCT therefore start from 0 of
							// geno-string and go until geno.length-ref.length
							int pos = vc.getStart();
							variant.setPos(pos);
							variant.setRef(reference.charAt(0));
							variant.setType(5);
							String insertion = pos + "." + 1 + genotypeString.substring(0, (genotypeString.length() - reference.length()));
							variant.setInsertion(insertion);
							sample.addVariant(variant);
						}

						if (genotype.hasAnyAttribute("DP")) {
							int coverage = (int) vc.getGenotype(sampleVcf).getAnyAttribute("DP");
							variant.setCoverage(coverage);
						}
					}

				} else if (genotype.getType() == GenotypeType.HET) {

					if (genotype.hasAnyAttribute("AF")) {

						String afTag = (String) vc.getGenotype(sampleVcf).getAnyAttribute("AF");
						double hetFrequency;
						double hetFrequencySecond;
						
						String[] splits = afTag.split(",");
						
						hetFrequency = Double.valueOf(splits[0]);

						if (splits.length > 1) {
							hetFrequencySecond = Double.valueOf(splits[1]);
						} else {
							hetFrequencySecond = 1 - hetFrequency;
						}
						
						char allele1 = genotype.getAlleles().get(0).getBaseString().charAt(0);
						char allele2 = genotype.getAlleles().get(1).getBaseString().charAt(0);
						char major;
						char var;
						double majorLevel;
						double minorLevel;
						char minor;

						// if a reference allele is available its always allele1!! (that means it does
						// not matter if 0/1 or 1/0)

						// HP always includes non-reference heteroplasmy level!
						// Can therefore be smaller OR larger then 0.5!
						if (allele1 == reference.charAt(0)) {
							var = allele2;
							// non-reference frequency greater than 0.5
							if (hetFrequency >= 0.5) {
								majorLevel = hetFrequency;
								minorLevel = hetFrequencySecond;
								major = allele2;
								minor = allele1;
							} else {
								// non-reference frequency smaller than 0.5
								majorLevel = hetFrequencySecond;
								minorLevel = hetFrequency;
								major = allele1;
								minor = allele2;
							}
						} else {
							// GT 1/2: no ref included
							var = allele1;
							majorLevel = hetFrequency;
							minorLevel = hetFrequencySecond;
							major = allele1;
							minor = allele2;
						}

						Variant variant = new Variant();
						int pos = vc.getStart();
						variant.setPos(pos);
						variant.setRef(reference.charAt(0));
						variant.setVariantBase(var);
						variant.setLevel(hetFrequency);
						variant.setMajor(major);
						variant.setMajorLevel(majorLevel);
						variant.setMinor(minor);
						variant.setMinorLevel(minorLevel);
						variant.setType(2);

						if (genotype.hasAnyAttribute("DP")) {
							int coverage = (int) vc.getGenotype(sampleVcf).getAnyAttribute("DP");
							variant.setCoverage(coverage);
						}
						sample.addVariant(variant);
					}
				}

				samples.put(sampleVcf, sample);
			} // end samples

		} // end variants

		vcfReader.close();

		return samples;

	}

}
