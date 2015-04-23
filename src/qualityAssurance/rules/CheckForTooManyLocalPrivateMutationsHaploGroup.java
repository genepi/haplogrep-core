package qualityAssurance.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForTooManyLocalPrivateMutationsHaploGroup extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyLocalPrivateMutationsHaploGroup.class);
	HashMap<String, String> polyHG = null;
	
	public CheckForTooManyLocalPrivateMutationsHaploGroup(int priority) {
		super(priority);
		polyHG= new HashMap<String, String>();
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		if(currentSample.getResults().size() != 0){
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		int numLocalPrivateMuations = 0;
		StringBuffer sb = new StringBuffer();
		Set <String> addHG = new HashSet<String>();
	
		File fileHSD = new File("../HaploGrepServer/weights/phylotree16.hsd.byPOS.txt");
		try {
			if (polyHG.isEmpty())
				readHSD(fileHSD);

			
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
	
		 
		Map<String, String> map = new HashMap<String, String>();
		String result="";
		int max=0;
		log.debug("testsample  " + currentSample.getSampleID());
		StringBuffer helpHGs=new StringBuffer();
		for(Polymorphism currentRemainingPoly : topResult.getDetailedResult().getRemainingPolysInSample()){

			if(!currentRemainingPoly.isMTHotspot() && !(qualityAssistent.getUsedPhyloTree().getMutationRate(currentRemainingPoly) == 0) && !(currentRemainingPoly.equalsReference()))
				{
				numLocalPrivateMuations++;
				sb.append(currentRemainingPoly.toString()+" ");
				if (polyHG.containsKey(currentRemainingPoly.toString())){
				StringTokenizer st = new StringTokenizer(polyHG.get(currentRemainingPoly.toString()), ",");
				while (st.hasMoreTokens()){
					String HG = st.nextToken();
			//		System.out.println("////////\\///// "+HG);
					if (map.containsKey(HG))
					{
					String count = map.get(HG);
					map.put(HG, count + ","+currentRemainingPoly.toString());
					}
					else 
						map.put(HG, currentRemainingPoly.toString());
					}
				}
		
				for (Map.Entry<String, String> entry : map.entrySet()) {
			
					int count = StringUtils.countMatches(entry.getValue(), ",");
			
					if (count>=1) //at least 2 identical haplogroups per SNP
						{
						if (max<count){
						result=" - "+(count+1)+" in "+entry.getKey()+" ["+entry.getValue()+"] ";
						max=count;
						}
						}
				}
		}
		}
		System.out.println("RESULLLLT  " + result);
		if(result.length() > 0)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, numLocalPrivateMuations + " local private " +
					"mutation(s) found " + result));
		}
	}

	private void readHSD(File fileHSD) throws IOException {
		polyHG= new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(fileHSD));
		String line;
		br.readLine(); //skip Header
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, "\t");
		   System.out.println(line);
		   String pos = st.nextToken();
		   st.nextToken();
		    st.nextToken();
		   polyHG.put(pos, st.nextToken());
		}
		br.close();
		
	
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
