package contamination;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;

import contamination.objects.Variant;
import contamination.objects.Sample;

public class MutationServerReaderTests {

	@Test
	public void testReadVariantFile() throws Exception {

		MutationServerReader reader = new MutationServerReader("test-data/contamination/lab-mixture/variants-mixture.txt");
		HashMap<String, Sample> samples = reader.parse();
		ArrayList<Integer> posArray = new ArrayList<>();
		for (Sample sample : samples.values()) {
			Collection<Variant> variants = sample.getVariants();
			int count = 0;

			for (Variant pos : variants) {
				posArray.add(pos.getPos());
				count++;

				if (pos.getPos() == 16270) {
					assertEquals(pos.getMinor(), 'T');
					assertEquals(pos.getMajor(), 'C');
					assertEquals(pos.getCoverage(), 3848);
					assertEquals(pos.getType(), 2);
					assertEquals(pos.getVariant(), 'T');
					assertEquals(pos.getRef(), 'C');
					assertEquals(pos.getLevel(), 0.013, 0.0);
					assertEquals(pos.getMajorLevel(), 0.987, 0.0);
					assertEquals(pos.getMinorLevel(), 0.013, 0.0);
				}
			}

			assertEquals(26, count);
			assertEquals(7, sample.getAmountHomoplasmies());
			assertEquals(18, sample.getAmountHeteroplasmies());
			assertEquals(true, posArray.contains(11719));
			assertEquals(true, posArray.contains(15236));
		}
	}

	@Test
	public void testReadVariantFile2() throws Exception {

		MutationServerReader reader = new MutationServerReader("test-data/contamination/test-mixture/variants-mixture-4samples.txt");
		HashMap<String, Sample> samples = reader.parse();
		ArrayList<Integer> posArray = new ArrayList<>();

		int countS1 = 0;
		int countS3 = 0;
		int countS4 = 0;

		for (Sample sample : samples.values()) {

			Collection<Variant> variants = sample.getVariants();

			if (sample.getId().equals("s1")) {
				for (Variant pos : variants) {
					posArray.add(pos.getPos());
					countS1++;
				}
			}

			if (sample.getId().equals("s3")) {
				for (Variant pos : variants) {
					posArray.add(pos.getPos());
					countS3++;
				}
			}

			if (sample.getId().equals("s4")) {
				for (Variant pos : variants) {
					posArray.add(pos.getPos());
					countS4++;
				}
			}
		}
		assertEquals(3, countS1);
		assertEquals(2, countS3);
		assertEquals(1, countS4);
	}

}
