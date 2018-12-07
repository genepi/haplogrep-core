package contamination;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import contamination.objects.Sample;
import contamination.objects.Variant;
import importer.VcfImporter;

public class VcfHeteroplasmyTests {

	@Test
	public void testVcfWithHeteroplasmies() throws Exception {

		 VcfImporter reader2 = new VcfImporter();
		 HashMap<String, Sample> mutationServerSamples = reader2.load(new File("test-data/vcf/NA20877.vcf"), false);
		 
		 for (Sample sam : mutationServerSamples.values()){
			 System.out.println(sam.getId());
			for(Variant var : sam.getVariants()) {
				
				if(var.getPos() == 1438) {
					assertEquals(var.getVariant(), 'G');
					assertEquals(var.getRef(), 'A');
					assertEquals(var.getMajor(), 'G');
					assertEquals(var.getMinor(), 'A');
					assertEquals(var.getLevel(), 0.964, 0.00);
					assertEquals(var.getMajorLevel(), 0.964,0.00);
					assertEquals(var.getMinorLevel(), 0.036,0.00);
				}
				
				if(var.getPos() == 3427) {
					assertEquals(var.getVariant(), 'A');
					assertEquals(var.getRef(), 'G');
					assertEquals(var.getMajor(), 'G');
					assertEquals(var.getMinor(), 'A');
					assertEquals(var.getLevel(), 0.184, 0.00);
					assertEquals(var.getMajorLevel(), 0.816,0.00);
					assertEquals(var.getMinorLevel(), 0.184,0.00);
				}
				
				if(var.getPos() == 3107) {
					assertEquals(var.getVariant(), 'T');
					assertEquals(var.getRef(), 'N');
					assertEquals(var.getMajor(), 'T');
					assertEquals(var.getMinor(), 'C');
					assertEquals(var.getLevel(), 0.655, 0.00);
					assertEquals(var.getMajorLevel(), 0.655,0.00);
					assertEquals(var.getMinorLevel(), 0.345,0.00);
				}
			}
			 
		 }
	}
}
