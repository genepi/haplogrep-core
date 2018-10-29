package contamination;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import contamination.objects.Sample;
import contamination.objects.Variant;
import importer.VcfImporter;
import importer.VcfImporterImproved;

public class VcfTests {

	/*@Test
	public void test() throws Exception {

		String input = "/home/seb/Desktop/variants.vcf";
		input = "/home/seb/Desktop/testdiploid.vcf";

		VcfImporter importer = new VcfImporter();
		VcfImporterImproved importer3 = new VcfImporterImproved();

		ArrayList<String> samples = importer.load(new File(input), false);
		HashMap<String, Sample> samples2 = importer3.load(new File(input), false);

		writeHsd(samples2);
	

	}*/

	private void writeHsd(HashMap<String, Sample> samples2) {
		ArrayList<String> samplesArray = new ArrayList<>();
		
		for (Sample a : samples2.values()) {
			StringBuilder build = new StringBuilder();
			build.append(a.getId() + "\t" + "1-16569" + "\t" + "?");
			for (Variant ab : a.getVariants()) {
				if (ab.getType() == 5) {
					build.append("\t" + ab.getInsertion());
				} else {
					build.append("\t" + ab.getPos() + "" + ab.getVariant());
					System.out.println(build.toString());
				}
			}
			samplesArray.add(build.toString());
		}
	}
}
