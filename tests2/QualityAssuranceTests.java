import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.core.Every;
import org.junit.Assert;
import org.junit.Test;

import phylotree.PhyloTreeNode;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import qualityAssurance.QualityAssistent;
import qualityAssurance.RuleSet;
import qualityAssurance.rules.CheckForRecombinationRule;
import qualityAssurance.rules.CheckForSampleRCRSAligned;
import qualityAssurance.rules.CheckForSampleRSRSAligned;
import qualityAssurance.rules.CheckForSampleRange;
import qualityAssurance.rules.CheckForTooManyGlobalPrivateMutations;
import search.ranking.HammingRanking;
import sun.org.mozilla.javascript.internal.regexp.SubString;
import core.Haplogroup;
import core.Polymorphism;
import core.SampleFile;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.HsdFileException;


public class QualityAssuranceTests<K> {

	@Test
	public void testMatchingRulesDetectedExcpectedHg() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/Burma_44GG.txt",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree16.xml","weights16.txt");
		
		RuleSet rules = new RuleSet();
		rules.addStandardRules();
		
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		testFile.updateClassificationResults(phyoTree, new HammingRanking());
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testCheckTooManyPrivateMutations() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/Burma_44GG.txt",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree16.xml","weights16.txt");
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree16.xml","weights16.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForTooManyGlobalPrivateMutations(0));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testForMetaboChip() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/selected-samples-metabochip.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForSampleRange(0));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testForRSRS() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/RSRS.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForSampleRSRSAligned(0));
		rules.addRule(new CheckForSampleRCRSAligned(0));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(4, newQualityAssistent.getNumIssuedErrors());
	}

	@Test
	public void testCompleteRuleSet() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/qualityIssuesTestFile.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree16.xml","weights16.txt");
		
		RuleSet rules = new RuleSet();
		rules.addStandardRules();
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		
		newQualityAssistent.getAllIssuesJSON();
		//assertEquals(2, preChecksQualityAssistent.getNumIssuedErrors(testFile.getTestSample("5019773")));
		//assertEquals(3, preChecksQualityAssistent.getNumIssuedErrors());
			
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
		
		
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testCompleteRangeDetectionRule() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/completeRangeDetection.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
		
		RuleSet rules = new RuleSet();
		rules.addStandardRules();
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
		
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testRecombinationControlRegion() throws Exception {
		SampleFile testFile = new  SampleFile("/testDataFiles/Bandelt.txt",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
//	ArrayList<TestSample> file = generateRecombination(phyoTree,testFile);
//	file.addAll(testFile.getTestSamples());
//		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForRecombinationRule(4));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
//		newQualityAssistent.getIssueByID(0).getDescription()
//		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testRecombinationRule() throws Exception {
		SampleFile testFile = new  SampleFile("/testDataFiles/test4Recombination.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
	ArrayList<TestSample> file = generateRecombination(phyoTree,testFile);
	file.addAll(testFile.getTestSamples());
//		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForRecombinationRule(4));
		QualityAssistent newQualityAssistent = new QualityAssistent(file,rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
//		newQualityAssistent.getIssueByID(0).getDescription()
//		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void test() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/zeroPolysFound.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
		testFile.updateClassificationResults(phyoTree, new HammingRanking());
		testFile.getTestSample("5019784").getSample().getSampleRanges().clear();
		testFile.getTestSample("5019784").getSample().getSampleRanges().addMetaboChipRange();
		System.out.println();
		System.out.println(testFile.getTestSample("5019784").getClusteredSearchResults());
	}
	
	@Test
	public void testRecombinationGenerator() throws Exception{
		SampleFile testFile = new  SampleFile("/testDataFiles/test4recombination.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
		ArrayList<TestSample> file = generateRecombination(phyoTree,testFile);
		
		SampleFile newFile = new SampleFile();
		newFile.setTestSamples(file);
		FileWriter fileWriter = new FileWriter("GeneratedRecombination.hsd");
		fileWriter.write(newFile.toHSDFileString());
		fileWriter.close();

		System.out.println();
		for(TestSample currentSample : file){
			System.out.println(currentSample);
		}
	}
	
	private ArrayList<TestSample> generateRecombination(Phylotree phylotee, SampleFile samplesToRecombinate) throws Exception{
		if(samplesToRecombinate.getTestSamples().size() < 2){
			throw new Exception("The recombination generator requries at least two testSamples as input");
		}
		
		SampleRanges ranges  = new SampleRanges();
		ranges.addCustomRange(2488, 10858);
		ranges.addCustomRange(10898, 2687);
//		ranges.addCustomRange(0, 1000);
//		ranges.addCustomRange(1000, 16569);
		
		ArrayList<TestSample> recombiantedTestSamples = new ArrayList<TestSample>();
		
		
		ArrayList<ArrayList<TestSample>> fragments = new ArrayList<ArrayList<TestSample>>();
		ArrayList<Haplogroup> haplogroupsOfFragments = new ArrayList<Haplogroup>();
 		
		for(TestSample currentSample : samplesToRecombinate.getTestSamples()){
			Haplogroup haplogroup = phylotee.search(currentSample, new HammingRanking(1)).get(0).getHaplogroup();
			haplogroupsOfFragments.add(haplogroup);
			fragments.add(currentSample.createFragments(ranges));
		}
		
		Random r = new Random(5);
		
		
		
		String recombinatedSampleID = "";
		for(int i = 0; i < samplesToRecombinate.getTestSamples().size();i++){
			int nextRandom = r.nextInt(samplesToRecombinate.getTestSamples().size());
			TestSample currentSample = samplesToRecombinate.getTestSamples().get(i);
			ArrayList<Polymorphism> newRecombiantedPolys = new ArrayList<Polymorphism>();
			
			recombinatedSampleID = currentSample.getSampleID();
			recombinatedSampleID += "_" + haplogroupsOfFragments.get(i);
			newRecombiantedPolys.addAll(fragments.get(i).get(0).getSample().getPolymorphisms());
			
			while(true){
				nextRandom = r.nextInt(samplesToRecombinate.getTestSamples().size());		
				if(i != nextRandom)
					break;
			}
			
			currentSample = samplesToRecombinate.getTestSamples().get(nextRandom);
			recombinatedSampleID += "_" + currentSample.getSampleID();
			recombinatedSampleID += "_" + haplogroupsOfFragments.get(nextRandom);
			
			newRecombiantedPolys.addAll(fragments.get(nextRandom).get(1).getSample().getPolymorphisms());
			
			recombiantedTestSamples.add(new TestSample(recombinatedSampleID, newRecombiantedPolys, currentSample.getSample().getSampleRanges()));
		}
		
		return recombiantedTestSamples;
	}
	
	@Test
	public void testSampleRange() throws Exception{
		SampleRanges ranges  = new SampleRanges();
		ranges.addCustomRange(2488, 10858);
		ranges.addCustomRange(10898, 2687);
		
		Assert.assertTrue(ranges.contains(new Polymorphism("73G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("16512G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("10898G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("2687G")));
		
		ranges  = new SampleRanges();
		ranges.addCustomRange(16024, 16569);
		ranges.addCustomRange(74, 576);
			
		Assert.assertFalse(ranges.contains(new Polymorphism("73G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("16512G")));
		Assert.assertFalse(ranges.contains(new Polymorphism("10898G")));
		Assert.assertFalse(ranges.contains(new Polymorphism("2687G")));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSuperHaploGroup() throws Exception{
		Phylotree phyloTree = PhylotreeManager.getInstance().getPhylotree("phylotree16.xml","weights16.txt");
        
		HashMap<String, HashSet<String>> phylotreeMemory = new HashMap<String, HashSet<String>>();
		BufferedReader br = new BufferedReader(new FileReader("testDataFiles/phylotree16_blank.hsd.txt"));
		BufferedReader brfluct = new BufferedReader(new FileReader("../HaplogrepServer/weights/weights16.txt"));
		HashMap<String, Double> weights= new HashMap<String, Double>();
	    try {
	    	       
	       // System.out.println(brfluct.readLine());
	        String line1 = brfluct.readLine();
	        while (line1 != null) {
	        	  weights.put(line1.split("\t")[0], Double.valueOf(line1.split("\t")[1].split(" ")[0]));
	        	  line1 = brfluct.readLine();
	        }
	        
	        
	        String line = br.readLine();

	        while (line != null) {
	           // sb.append(line);
	           // sb.append(System.lineSeparator());
	          
	            HashSet<String> values = new HashSet<String>();
	            StringTokenizer st = new StringTokenizer(line);
	            String key = st.nextToken();
	            st.nextToken(); //Range not needed
	            while (st.hasMoreTokens())
	            {
	            	String value = st.nextToken();
	            
	            	if (!value.contains("!"))
	            		values.add(value);
	            }
	            phylotreeMemory.put(key, values);
	            line = br.readLine();
	        	}
	        	
	    
	//   System.out.println(phylotreeMemory);
	    } finally {
	        br.close();
	        brfluct.close();
	    }
	    
	    BufferedReader br2 = new BufferedReader(new FileReader("testDataFiles/exportStudy_Illumina_Vanessa.txt"));
	    String line = br2.readLine();
	    line = br2.readLine(); //skip header
       
	     System.out.println("Expected\tExp.Super\tExp.Macro\tResulting\tRes.SuperGroup\tRes.Macro\tRes=Macro(Exp)\tdistance to Super HG\tDifferences\tWeight\tDetails Differences");
		  
	    while (line != null) {
        	String ex = line.split("\t")[0];
       	String res= line.split("\t")[2].replace("  (", "(").replace("  ", "_");
   

        Haplogroup expected = new Haplogroup(ex);
		Haplogroup result = new Haplogroup(res);
		HashSet<String> expectedPolys = phylotreeMemory.get(ex);
		HashSet<String> resultingPolys = phylotreeMemory.get(res);
//	System.out.println(expectedPolys);
//	System.out.println(resultingPolys);

		double level =0;
	//	System.out.println(resultingPolys.size());
	
		String macroRes = getMajorGroup(result.toString());
		String macroExp = getMajorGroup(expected.toString());
		if (resultingPolys.size()>0){
			
			HashSet<String> temp = symDifference(expectedPolys, resultingPolys);

			Iterator<String> iter = temp.iterator();
			while (iter.hasNext()) {
			  level+=weights.get(iter.next());
			}
		int isMacro=(phyloTree.isSuperHaplogroup(expected, result))?1:0;
	        System.out.println(expected +  "\t" +  macroExp + "\t" +  getMacroGroup(expected.toString())+"\t" + result + "\t" +  macroRes  + "\t" +  getMacroGroup(result.toString()) + "\t" + isMacro + "\t"+phyloTree.distanceToSuperHaplogroup(expected, result)+ "\t"  +temp.size() + "\t" +level/10 + "\t" + temp );
	        line = br2.readLine();
		}else{
			Iterator<String> iter = expectedPolys.iterator();
			while (iter.hasNext()) {
			  level+=weights.get(iter.next());
			}
			int isMacro=(phyloTree.isSuperHaplogroup(expected, result))?1:0;
				System.out.println(expected + "\t"  +  macroExp +  "\t" + getMacroGroup(expected.toString())+ "\t" + result + "\t" +  macroRes + "\t" +  getMacroGroup(result.toString()) + "\t" + isMacro + "\t"+phyloTree.distanceToSuperHaplogroup(expected, result)+ "\t" + expectedPolys.size() + "\t" +level/10 + "\t" + expectedPolys);
			line = br2.readLine();
		}

		
        }
	
		Haplogroup Macro = new Haplogroup("H12");
		Haplogroup Test = new Haplogroup("H");
	//	System.out.println("IS_SUPER1 " + phyloTree.isSuperHaplogroup(Macro, Test));
	
	//	System.out.println(Macro);
		HashSet<String> a = phylotreeMemory.get("A");
		HashSet<String> b = phylotreeMemory.get("A");
	//	System.out.println(a.size());
	//	System.out.println(b.size());
		
	//	System.out.println("Union: " + union(a, b));
	//	System.out.println("Intersection: " + intersection(a, b));
	//	System.out.println("Difference (set1 - set2): " + difference(b, a));
	//	System.out.println("Symmetric Difference: " + symDifference(b, a));

		double level =0;
		HashSet<String> temp = symDifference(a, b);
		Iterator<String> iter = symDifference(a, b).iterator();
	
		while (iter.hasNext()) {
		  level+=weights.get(iter.next());
		}
	//	System.out.println(level/10 + " " +temp.size() );
	//	System.out.println("IS_SUPER2 " + phyloTree.isSuperHaplogroup(Test, Macro));
	//	System.out.println("distance "  +phyloTree.distanceToSuperHaplogroup(Macro, Test));

	//	System.out.println(phyloTree.getPhyloTree().getHaplogroup().toString());
		Assert.assertTrue(phyloTree.isSuperHaplogroup(Macro, Test));
		//Assert.assertFalse(phyloTree.isSuperHaplogroup(Macro, Test ));
	
	}
	
	
	
	 	
	
	private String getMajorGroup(String string) {
		String result = "";
		String refine = "A2	A5	B4	B5	D4	D5	H1	H2	H3	H5	H6	H7	HV	J1	J2	JT	K1	K2	M1	M2	M5	M7	M8	M9	N1	N2	R0	T1	T2	U3	U5	L1	L2	L3	L0	L2";
		if (string.contains("+"))
			string = string.split("\\+")[0];

		if (string.contains("'"))
			string = string.split("\\'")[0];

		String pattern = "[A-Z]*\\d+['\\d]*";

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(string);
		if (m.find()) {
			if (refine.contains(m.group()))
				result = m.group(0);
			else {
				if ((string.contains("HV")) || (string.contains("R0")) || (string.contains("JT")))
					result = string.substring(0, 2);
				else
					result = string.substring(0, 1);
			}
		} else {
			if ((string.contains("HV")) || (string.contains("R0")) || (string.contains("JT")))
				result = string.substring(0, 2);
			else
				result = string.substring(0, 1);
		}
		return result;
	}

	private String getMacroGroup(String string) {
		if (string.contains("+"))
			string = string.split("\\+")[0];

		if (string.contains("'"))
			string = string.split("\\'")[0];

		String M = "M	C	E	G	Q	Z";
		String N = "N	A	I	O	S	W	X	Y";
		String R = "R	B	F	P";
		String R0 = "HV	H	V";

		if (string.contains("HV"))
			return "R0";
		if (string.contains("R0"))
			return "R0";
		if (string.contains("JT"))
			return "JT";
		String compare = string.substring(0, 1);
		if (M.contains(compare))
			return "M*";
		else if (N.contains(compare))
			return "N*";
		else if (R.contains(compare))
			return "R*";
		else if (R.contains(compare))
			return "R0";
		else
			return compare;
	}

	public static <T> HashSet<T> union(HashSet<T> setA, HashSet<T> setB) {
		    HashSet<T> tmp = new HashSet<T>(setA);
		    tmp.addAll(setB);
		    return tmp;
		  }

		  public static <T> HashSet<T> intersection(HashSet<T> setA, HashSet<T> setB) {
			  HashSet<T> tmp = new HashSet<T>();
		    for (T x : setA)
		      if (setB.contains(x))
		        tmp.add(x);
		    return tmp;
		  }

		  public static <T> HashSet<T> difference(HashSet<T> setA, HashSet<T> setB) {
			  HashSet<T> tmp = new HashSet<T>(setA);
		    tmp.removeAll(setB);
		    return tmp;
		  }

		  public static <T> HashSet<T> symDifference(HashSet<T> setA, HashSet<T> setB) {
			  HashSet<T> tmpA;
			  HashSet<T> tmpB;

		    tmpA = union(setA, setB);
		    tmpB = intersection(setA, setB);
		    return difference(tmpA, tmpB);
		  }

		  public static <T> boolean isSubset(HashSet<T> setA, HashSet<T> setB) {
		    return setB.containsAll(setA);
		  }

		  public static <T> boolean isSuperset(HashSet<T> setA, HashSet<T> setB) {
		    return setA.containsAll(setB);
		  }
		  
		  
			@Test
			public void testDanish() throws Exception{
				  BufferedReader br2 = new BufferedReader(new FileReader("testDataFiles/danish2000.txt"));
				    String line = br2.readLine();
				    line = br2.readLine(); //skip header
				    String HG="";
			  
				    while (line != null) {
				           // sb.append(line);
				           // sb.append(System.lineSeparator());
				            HashSet<String> values = new HashSet<String>();
				            StringTokenizer st = new StringTokenizer(line, "\t");
				            String key = st.nextToken();
				            String profiles ="";
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            st.nextToken(); //Range not needed
				            String pr = st.nextToken();
				            
				            StringTokenizer st2 = new StringTokenizer(pr, ";");
				            while (st2.hasMoreTokens())
				            {
				            	String value = st2.nextToken();
				            //	System.out.println(value);
				            //	System.out.println(value.split("\\|")[0]);
				            	if (!value.contains("-") && !value.contains("N") )
				            	profiles+=value.split("\\|")[0]+value.split("\\|")[1].split("/")[1]+"\t";
				             }
				           // System.out.println(profiles);
				            HG= st.nextToken();
				        System.out.println(key+"_"+HG+"\t1-16569\t"+HG+"\t"+profiles);
				            line = br2.readLine();
				        	}
				        	
				    
				//   System.out.println(phylotreeMemory);
				
				        br2.close();
				      
				    
			}
			
			
			@Test
			public void testMitoToolGroups() throws Exception{
				  BufferedReader br2 = new BufferedReader(new FileReader("testDataFiles/HG_1000G.txt"));
				    String line = br2.readLine();
				    line = br2.readLine(); //skip header
				    String HG="";
			  
				    while (line != null) {
				           // sb.append(line);
				           // sb.append(System.lineSeparator());
				    System.out.println(line+"\t" + getMajorGroup(line)+"\t"+getMacroGroup(line));  
				    line = br2.readLine();
				    }
				        	
				    
				//   System.out.println(phylotreeMemory);
				
				        br2.close();
				      
				    
			}
		  
		  
		  
}
