package tests;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.jdom.JDOMException;
import org.junit.BeforeClass;

import search.ClusteredSearchResult;
import search.HaploSearchHamming;
import search.HaploSearchManager;
import search.IHaploSearch;
import core.Haplogroup;
import core.SampleFile;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class GenericTest {
	static IHaploSearch newSearch = null;
	static HashMap<String,SampleFile> testSampleFiles = 
		new HashMap<String,SampleFile>();
	
	static HashMap<String,TestSample> testProbes = new HashMap<String,TestSample>();
	
	static boolean allowSubgroupsHits = true;
	
	@BeforeClass
	public static void Init()
	{
		try
		{
			/*File polyWeightsFile = new File("polyGeneticWeights/fluctRatesML.txt");
			FileInputStream polyWeightsFileStream = new FileInputStream(polyWeightsFile);
			
			File phylorTreeFile = new File("phylotree/phylotree.xml");
			FileInputStream phylorTreeFileStream = new FileInputStream(phylorTreeFile);
			
			newSearch = new HaplogroupClassifier(polyWeightsFileStream, phylorTreeFileStream);*/
			HaploSearchManager h1 = new HaploSearchManager("phylotree14.xml","fluctRates14.txt");
			newSearch = new HaploSearchHamming(h1);
			
			
			File dir = new File("../docs/testSamples/hsdFiles/DataNew");
			
			String[] children = dir.list();
			FilenameFilter filter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return !name.startsWith(".");
			}
		};
		children = dir.list(filter);
			for (int i = 0; i < 1; i++)
			{
				SampleFile newSampleFile = new SampleFile(dir.getAbsolutePath()+File.separatorChar+children[i],true);
				testSampleFiles.put(children[i], newSampleFile);
				System.out.println("aaa"+children[i]);
				for(TestSample currentTestSample : newSampleFile.getTestSamples())
				{
					testProbes.put(currentTestSample.getSampleID(), currentTestSample);
				}
			}

		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	
	public void runCompleteTestfile(String filename) throws IOException, JDOMException, NumberFormatException, InvalidPolymorphismException
	{
		int correctHits = 0;
		int i = 0;
		double sumFinalRanks = 0;
		double sumBestRankClusterSize = 0;
		
		
		ArrayList<String> filesToCheck = new ArrayList<String>();
		if(filename.equals("*.*"))
			filesToCheck.addAll(testSampleFiles.keySet());
		else
			filesToCheck.add(filename);
		for(String currentFilename : filesToCheck)
		{
			System.out.println(testSampleFiles.get(currentFilename));
			for(TestSample currentSample : testSampleFiles.get(currentFilename).getTestSamples())
			{
				
				List<ClusteredSearchResult> results = newSearch.search(currentSample);
				boolean isCorrectHit = results.get(0).containsSuperhaplogroup(currentSample.getExpectedHaplogroup());//checkIfCorrectHaplogroup(results.get(0).getHaplogroup(),currentHaplogroup);
				 
				sumBestRankClusterSize += results.get(0).getCluster().size();
				
				sumFinalRanks += results.get(0).getCluster().get(0).getRank();
				
				if(isCorrectHit)
				{
					 //System.out.println(i + ". Found correct haplogroup "+ currentHaplogroup + " : " + currentProbe+ " \nExact result: "+results.get(0).getHaplogroup()+"\n");
					 //System.out.println("Correct"+ currentHaplogroup + " : " + currentSample); 
					/*if(0.5 > results.get(0).getCluster().get(0).getCorrectPolyInTestSampleRatio())
					{
					sumBestTestSampleRank += results.get(0).getCluster().get(0).getCorrectPolyInTestSampleRatio();
					System.out.println(results.get(0).getCluster().get(0).getCorrectPolyInTestSampleRatio());
					System.out.println("Correct"+ currentHaplogroup + " : " + currentSample); 
					}*/
					correctHits++;
				}
				else
				{
					 if(/*results.get(0).getCluster().size() >= 1*/true)//results.get(0).getHaplogroup().equals("P**16176"))
					 {
					 System.out.println(i + ". Failed to query "+ currentSample.getExpectedHaplogroup() + " : " + currentSample);
					 System.out.println( "\t Best Result "+results.get(0).getHaplogroup()+"("+results.get(0).getCluster().get(0).getRank() +")\n");
					 System.out.println( "\t TestSample Rank "+results.get(0).getCluster().get(0).getCorrectPolyInTestSampleRatio());
					 System.out.println("\t Cluster size: " +results.get(0).getCluster().size() );
					 
					 int rank = 1;
					 boolean found = false;
					 for(ClusteredSearchResult currentcluster : results)
					 {
						 if(currentcluster.containsSuperhaplogroup(currentSample.getExpectedHaplogroup()))
						 {
							 System.out.println( "\t Expected haplogroup "+currentSample.getExpectedHaplogroup() +" at rank:" + rank);
							 System.out.println();
							 found = true;
							 break;
						 }
						 rank++;
					 }
					 
					 if(!found)
					 {
						 System.out.println( "\t Expected haplogroup not found!");
						 System.out.println();
					 }
					 
				}
				}
				i++;
			}
			
		}
		DecimalFormat df = new DecimalFormat( "0.00" );
		
		System.out.println();
		System.out.println("Correct results: " + correctHits + " / " + i + " = " + df.format(((double)correctHits/(double)i)*100.0) + "%");
		System.out.println("Average best rank: " +( sumFinalRanks / (double)i));
		System.out.println("Average best rank cluster size: " +( sumBestRankClusterSize / (double)i));
		//System.out.println("Average best testsample rank : " + (sumBestTestSampleRank / (double)correctHits));
	//	Assert.assertEquals("Not all haplogroup search results correct", correctHits ,i);
		
	}
	

	public GenericTest() {
		super();
	}

	public boolean checkIfCorrectHaplogroup(String haplogroupToCheck, String expectedHaplogroup) {
		if(allowSubgroupsHits)
		{
			return haplogroupToCheck.startsWith(expectedHaplogroup);
		}
		
		else 
		{
			if(!haplogroupToCheck.equals(expectedHaplogroup))
			return false;
			
			else
				return true;
		}
		
	}

	private void outputResultDetailed(Haplogroup haplogroup, List<ClusteredSearchResult> results, TestSample currentSample) {
		int i = 1;
		int posHaplogroupToFind=-1;
		
		System.out.println("\n\n-----------------------------------------------------------------------");
		System.out.println("-----------------------New Query---------------------------------------");
		System.out.println("-----------------------------------------------------------------------");
		
		System.out.println("Search for haplogroup: " + haplogroup);
		
		for(ClusteredSearchResult currentResult : results)
		{
			if(currentResult.containsSuperhaplogroup(haplogroup) && i > 10)
			{
				System.out.println(i + "."+currentResult.toString());
				posHaplogroupToFind = i;			
				break;
			}
			
			if( i <= 10)
			{
				System.out.println("Test Sample: " + currentSample);
				System.out.println(i + "."+currentResult.toString());
			}
			else
				break;
			
			
			i++;
		}
		
		System.out.println("Summary:");
		 
		if(posHaplogroupToFind >= 0)
			System.out.println("Found expected haplogroup at position " + posHaplogroupToFind + "/" + i);
		else
			System.out.println("The expected haplogroup hasn`t been found!");
		
		System.out.println("------------------------------------------------------------------------");
	}

	

	protected void TestSampleDetailed( String string)
	throws NumberFormatException, JDOMException, IOException,InvalidPolymorphismException {
		
		List<ClusteredSearchResult> results = newSearch.search(testProbes.get(string));
	
		
		boolean isCorrectHit = results.get(0).containsSuperhaplogroup(testProbes.get(string).getExpectedHaplogroup());
		
		outputResultDetailed(testProbes.get(string).getExpectedHaplogroup(),results,testProbes.get(string));	
		
		if(!isCorrectHit)
		{
			
			Assert.fail("Haplogroup hasn`t been ranked first!");
		}
		//Assert.assertEquals(isCorrectHit, true);
		else
			Assert.assertTrue("Right haplogroup detected",true);
	
	}

}