package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.w3c.dom.svg.GetSVGDocument;

import core.Haplogroup;
import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;
import genepi.io.table.writer.CsvTableWriter;
import importer.FastaImporter.References;
import search.SearchResult;
import search.SearchResultTreeNode;
import search.ranking.HammingRanking;
import search.ranking.JaccardRanking;
import search.ranking.KulczynskiRanking;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;
import vcf.Sample;
import vcf.Variant;

public class ExportUtils {

	public static ArrayList<String> vcfTohsd(HashMap<String, Sample> samples) {
		return ExportUtils.vcfTohsd(samples, 0.9);
	}

	public static ArrayList<String> vcfTohsd(HashMap<String, Sample> samples, double hetLevel) {
		ArrayList<String> lines = new ArrayList<String>();
		for (Sample sam : samples.values()) {

			StringBuilder build = new StringBuilder();
			build.append(sam.getId() + "\t" + sam.getRange() + "\t" + "?");

			for (Variant var : sam.getVariants()) {
				if (var.getType() == 1 || var.getType() == 4 || (var.getType() == 2 && var.getLevel() >= hetLevel)) {
					build.append("\t" + var.getPos() + "" + var.getVariant());
				} else if (var.getType() == 5) {
					build.append("\t" + var.getInsertion());
				}
			}
			build.append("\n");

			lines.add(build.toString());
		}
		return lines;
	}

	public static void createReport(Collection<TestSample> sampleCollection, String outFilename, boolean extended) throws IOException {

		CsvTableWriter writer = new CsvTableWriter(outFilename, '\t');

		Collections.sort((List<TestSample>) sampleCollection);

		if (!extended) {

			writer.setColumns(new String[] { "SampleID", "Haplogroup", "Rank", "Quality", "Range" });

		} else {

			writer.setColumns(new String[] { "SampleID", "Haplogroup", "Rank", "Quality", "Range", "Not_Found_Polys", "Found_Polys", "Remaining_Polys",
					"AAC_In_Remainings", "Input_Sample" });

		}

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				int rank = 0;

				for (RankedResult currentResult : sample.getResults()) {

					rank++;

					SampleRanges range = sample.getSample().getSampleRanges();

					ArrayList<Integer> startRange = range.getStarts();

					ArrayList<Integer> endRange = range.getEnds();

					String resultRange = "";

					for (int i = 0; i < startRange.size(); i++) {

						if (i != 0) {
							resultRange += " ";
						}
						if (startRange.get(i).equals(endRange.get(i))) {
							resultRange += startRange.get(i);
						} else {
							resultRange += startRange.get(i) + "-" + endRange.get(i);
						}
					}

					writer.setString("SampleID", sample.getSampleID());

					writer.setString("Range", resultRange);

					writer.setString("Haplogroup", currentResult.getHaplogroup().toString());

					writer.setString("Rank", rank + "");

					writer.setString("Quality", String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

					if (extended) {

						ArrayList<Polymorphism> foundPolys = currentResult.getSearchResult().getDetailedResult().getFoundPolys();

						ArrayList<Polymorphism> expectedPolys = currentResult.getSearchResult().getDetailedResult().getExpectedPolys();

						Collections.sort(foundPolys);

						Collections.sort(expectedPolys);

						StringBuffer result = new StringBuffer();
						for (Polymorphism expected : expectedPolys) {
							if (!foundPolys.contains(expected)) {
								result.append(" " + expected.toString());
							}
						}

						writer.setString("Not_Found_Polys", result.toString().trim());

						result = new StringBuffer();
						for (Polymorphism currentPoly : foundPolys) {
							result.append(" " + currentPoly);
						}

						writer.setString("Found_Polys", result.toString().trim());

						ArrayList<Polymorphism> allChecked = currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample();
						Collections.sort(allChecked);

						result = new StringBuffer();
						for (Polymorphism currentPoly : allChecked) {
							String type = getTypeRemaining(currentPoly, currentResult.getSearchResult());
							result.append(" " + currentPoly + " (" + type + ")");
						}

						writer.setString("Remaining_Polys", result.toString().trim());

						ArrayList<Polymorphism> aac = currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample();
						Collections.sort(aac);

						result = new StringBuffer();
						for (Polymorphism currentPoly : aac) {
							if (currentPoly.getAnnotation() != null)
								result.append(" " + currentPoly + " [" + currentPoly.getAnnotation().getAminoAcidChange() + "| Codon "
										+ currentPoly.getAnnotation().getCodon() + " | " + currentPoly.getAnnotation().getGene() + " ]");
						}

						writer.setString("AAC_In_Remainings", result.toString().trim());

						ArrayList<Polymorphism> inputPolys = sample.getSample().getPolymorphisms();

						Collections.sort(inputPolys);

						result = new StringBuffer();
						for (Polymorphism input : inputPolys) {
							result.append(" " + input);
						}

						writer.setString("Input_Sample", result.toString().trim());

					}

					writer.next();

				}
			}
		}

		writer.close();

	}
	
	private static String getTypeRemaining(Polymorphism p, SearchResult result) {

		if (result.getPhyloTree().getMutationRate(p) == 0) {
			if (p.isMTHotspot()) {
				return "hotspot";
			} else {
				return "globalPrivateMutation";
			}
		} else {
			return "localPrivateMutation";
		}
	}

	public static void createHsdInput(List<TestSample> sampleCollection, String out) throws IOException {

		StringBuffer result = new StringBuffer();

		Collections.sort((List<TestSample>) sampleCollection);

		result.append("SampleID\tRange\tHaplogroup\tInput_Sample\n");

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				result.append(sample.getSampleID() + "\t");

				for (RankedResult currentResult : sample.getResults()) {

					SampleRanges range = sample.getSample().getSampleRanges();

					ArrayList<Integer> startRange = range.getStarts();

					ArrayList<Integer> endRange = range.getEnds();

					String resultRange = "";

					for (int i = 0; i < startRange.size(); i++) {
						if (startRange.get(i).equals(endRange.get(i))) {
							resultRange += startRange.get(i) + ";";
						} else {
							resultRange += startRange.get(i) + "-" + endRange.get(i) + ";";
						}
					}
					result.append(resultRange);

					result.append("\t" + currentResult.getHaplogroup());

					result.append("\t");

					ArrayList<Polymorphism> input = sample.getSample().getPolymorphisms();

					Collections.sort(input);

					for (Polymorphism currentPoly : input) {
						result.append(" " + currentPoly);
					}
					result.append("\n");

				}
			}
		}

		FileWriter fileWriter = new FileWriter(out);

		fileWriter.write(result.toString().replace("\t ", "\t"));

		fileWriter.close();

	}

	public static void calcLineage(Collection<TestSample> sampleCollection, int tree, String out) throws IOException {

		if (tree == 0)
			return;

		Collections.sort((List<TestSample>) sampleCollection);

		MultiValueMap remainingSet = new MultiValueMap();

		if (out.endsWith(".txt")) {
			out = out.substring(0, out.lastIndexOf("."));
		}

		HashSet<String> set = new HashSet<String>();
		String tmpNode = "";

		String graphViz = out + ".dot";

		FileWriter graphVizWriter = new FileWriter(graphViz);

		graphVizWriter.write("digraph {  label=\"Sample File: " + out + "\"\n");
		if (tree == 1 || tree == 3 ) {
			graphVizWriter.write("graph [layout = dot, rankdir = TB]\n");
		} else if (tree == 2) {
			graphVizWriter.write("graph [layout = dot, rankdir = LR]\n");
		}
		graphVizWriter.write("node [shape = oval,style = filled,color = lightblue]\n");

		// iterate through result samples
		for (TestSample sample : sampleCollection) {
			String notfound = "";
			String remaining = "";
			notfound = "";
			for (RankedResult currentResult : sample.getResults()) {
				ArrayList<SearchResultTreeNode> currentPath = currentResult.getSearchResult().getDetailedResult().getPhyloTreePath();

				for (int i = 0; i < currentPath.size(); i++) {
					Haplogroup currentHg = currentPath.get(i).getHaplogroup();

					if (i == 0) {
						tmpNode = "\"" + currentHg + "\" -> ";
					}

					else {

						StringBuilder polys = new StringBuilder();

						if (currentPath.get(i).getExpectedPolys().size() == 0) {
							polys.append("");
						} else {

							for (Polymorphism currentPoly : currentPath.get(i).getExpectedPolys()) {

								polys.append(currentPoly + " ");

								if (!currentPath.get(i).getFoundPolys().contains(currentPoly)) {
									notfound += currentPoly + "@ ";
									notfound += ("\n");
								}
								polys.append("\n");
							}
						}
						String node = "";
						if (tree == 1) {
							node = "\"" + currentHg + "\"[label=\"" + polys.toString().trim() + "\"" + "];\n";
						}
						// write empty edge labels
						else if (tree >= 2) {
							node = "\"" + currentHg + "\"[label=\"" + "\"" + "];\n";
						}

						if (!set.contains(tmpNode + node)) {
							graphVizWriter.write(tmpNode + node);
							set.add(tmpNode + node);
							tmpNode = "";
						}

						// Write currentHG also in new line for next iteration, but don't do this for
						// last element
						if (i != (currentPath.size() - 1)) {
							tmpNode = "\"" + currentHg + "\" -> ";
						}
					}

				}
				// append the remaining SNPS

				for (int i = 0; i < currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample().size(); i++) {
					Polymorphism poly = currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample().get(i);
					if (!polyToExclude(poly)) {
						remaining += poly + " ";
						remaining += "\n";
					}
				}

			}

			remainingSet.put(notfound + " " + remaining, sample);

		}
		HashMap<String, Integer> groupedMap = new HashMap<>();

		SortedSet<String> keys = new TreeSet<>(remainingSet.keySet());

		for (String key : keys) {
			String sampleLabels = "";
			List<TestSample> nodes = (List<TestSample>) remainingSet.get(key);
			for (int j = 0; j < nodes.size(); j++) {
				TestSample s1 = nodes.get(j);
				if (j > 0) {
					if (tree == 1)
						sampleLabels += "\n";
					else if (tree == 2)
						sampleLabels += " ";
				}

				sampleLabels += s1.getSampleID();
				String HG = s1.getDetectedHaplogroup().toString();

				if (groupedMap.containsKey(HG))
					groupedMap.put(HG, Integer.valueOf(groupedMap.get(HG)) + 1);
				else
					groupedMap.put(HG, 1);
			}

			graphVizWriter.write("\"" + nodes.get(0).getDetectedHaplogroup() + "\"[color=deepskyblue]\n");
			if (tree == 1) {
				graphVizWriter.write(
						"\"" + nodes.get(0).getDetectedHaplogroup() + "\" -> " + "\"" + sampleLabels + " \"" + "[color=steelblue, label=\"" + key + "\"]\n");
				graphVizWriter.write("\"" + sampleLabels + " \"" + "[shape=rectangle, color=steelblue]\n");
			} else if (tree == 2) {
				graphVizWriter
						.write("\"" + nodes.get(0).getDetectedHaplogroup() + "\" -> " + "\"" + sampleLabels + " \"" + "[color=steelblue, label=\"" + "\"]\n");
				graphVizWriter.write("\"" + sampleLabels + " \"" + "[shape=rectangle, color=steelblue]\n");
			}
		}

		if (tree == 3) {
			Iterator it = groupedMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				
				int value =  (Integer) pair.getValue(); 
				graphVizWriter.write("\"" + pair.getKey() + "\" -> " + "\"" + pair.getKey()+" = "+value + " \"" + "[color=steelblue, label=\"" + "\"]\n");
				if (value <2 )
					graphVizWriter.write("\"" + pair.getKey()+" = "+value  + " \"" + "[shape=rectangle, color=steelblue]\n");
				else if (value >=2 && value <10)
					graphVizWriter.write("\"" + pair.getKey()+" = "+value  + " \"" + "[shape=rectangle, color=darkorange]\n");
				else 
					graphVizWriter.write("\"" + pair.getKey()+" = "+value  + " \"" + "[shape=rectangle, color=firebrick1]\n");

				it.remove(); // avoids a ConcurrentModificationException
			}
			graphVizWriter.write("subgraph cluster_legend {\n"); 
			graphVizWriter.write("label = \"Legend\"\n");
			graphVizWriter.write("shape=rectangle\n") ;
			graphVizWriter.write("color = black\n");
			graphVizWriter.write("\"Intermediate haplogroup\" [color=lightblue]\n");
			graphVizWriter.write("\"Terminal haplogroup\"  [color=deepskyblue]\n");
			graphVizWriter.write("\"1 sample \\n in Haplogroup\"  [shape=rectangle, color=steelblue]\n");
			graphVizWriter.write("\">= 2 Samples \\n in Haplogroup\"  [shape=rectangle, color=darkorange]\n");
			graphVizWriter.write("\">=10 Samples \\n in Haplogroup\"  [shape=rectangle, color=firebrick1]\n");
			graphVizWriter.write("}\n");	
		}

		graphVizWriter.write("}");
		graphVizWriter.close();

	}

	// see Phylotree.org: The mutations 309.1C(C), 315.1C, AC indels at 515-522,
	// A16182c, A16183c, 16193.1C(C) and C16519T/T16519C
	// were not considered for phylogenetic reconstruction and are therefore
	// excluded from the tree.
	private static boolean polyToExclude(Polymorphism polymorphism) {
		int pos = polymorphism.getPosition();
		switch (pos) {
		case 309:
			return true;
		case 315:
			return true;
		case 523:
			return true;
		case 524:
			return true;
		case 525:
			return true;
		case 3107:
			return true;
		case 16182:
			return true;
		case 16183:
			return true;
		case 16193:
			return true;
		case 16519:
			return true;
		default:
			return false;
		}
	}
	
	public static void generateFasta( Collection<TestSample> sampleCollection, String out) throws IOException {
		String fastafile = out + "_haplogrep2.fasta";
		FileWriter fasta = new FileWriter(fastafile);
		
		
		for (TestSample sample : sampleCollection) {
			
			Collections.sort((List<Polymorphism>) sample.getSample().getPolymorphisms());
			String fastaResult = Polymorphism.rCRS;
			int insertions=0;
			int deletions=0;
			System.out.println("sample " + sample.getSampleID());
			for (Polymorphism poly : sample.getSample().getPolymorphisms()) {
			
						poly.getPosition();
						if (poly.getMutation().toString().equals("INS"))
						{
							for (int i=0; i<poly.getInsertedPolys().length(); i++){
								insertions++;
								fastaResult=insertChar(fastaResult, poly.getInsertedPolys().toString().charAt(i), poly.getPosition()-1+insertions-deletions);
								//log.debug
								System.out.println("!ins " + poly.getPosition()+" "  + poly.getMutation() +  " "+ poly.getInsertedPolys().toString());
							}
							}
						else if (poly.getMutation().toString().equals("DEL"))
						{
							System.out.println(poly + " poly " + poly.getMutation());
							fastaResult=deleteChar(fastaResult, poly.getPosition()-1+insertions-deletions);
							deletions++;
						}
						else
						{
						fastaResult=replaceChar(fastaResult, poly.getMutation().toString().charAt(0), poly.getPosition()-1+insertions-deletions);
						}
					}
			
			fasta.write(">"+sample.getSampleID()+"\n"+fastaResult+"\n");
			
			}
		fasta.close();
	}
	
	
	public static void generateFastaMSA(Collection<TestSample> sampleCollection, String out) throws IOException {
		String fasta = out + "_haplogrep2_MSA.fasta";
		FileWriter fastaMSA = new FileWriter(fasta);
		
		String result = "";

		long start = new java.util.Date().getTime();

		StringBuffer sbresult = new StringBuffer();

		Vector<Polymorphism> vectorPolys = new Vector<Polymorphism>();
		Vector vectorhelp = new Vector();
		Vector samplepoly = new Vector();

		Collections.sort((List<TestSample>) sampleCollection);

		// GET DISTINCT POLYMORPHISMS
		if (sampleCollection != null) {
			for (TestSample sample : sampleCollection) {
				Vector<Polymorphism> vDistinct = new Vector<Polymorphism>();
				for (Polymorphism poly : sample.getSample().getPolymorphisms()) {

					if (poly.getMutation().toString().length() == 1) {
						vDistinct.add(poly);
					} else {

						if (poly.getMutation().toString().contains("DEL")) {
							vDistinct.add(poly);
						}

						else 					
						{

							for (int i = 0; i < poly.getInsertedPolys()
									.length(); i++) {

								try {
									System.out.println("--" + poly);
									Polymorphism p1 = new Polymorphism(
											poly.getPosition()
													+ ".1"
													+ poly.getInsertedPolys()
															.substring(1, i + 1)
													+ poly.getInsertedPolys()
															.charAt(i));
									vDistinct.add(p1);
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InvalidPolymorphismException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

					if (!vectorhelp.contains(poly.toString())) {

						if (poly.getMutation().toString().contains("INS")) {

							String h = poly.getInsertedPolys().toString();
							System.out.println("inserted polys " +h);
							for (int i = 0; i < h.length(); i++) {
								Polymorphism p1 = null;
								try {

									p1 = new Polymorphism(poly.getPosition()
											+ ".1" + h.substring(0, i + 1));

									if (!vectorhelp.contains(p1.toString())) {
										vectorhelp.add(p1.toString());
										vectorPolys.add(p1);
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						} else {
							vectorhelp.add(poly.toString());
							vectorPolys.add(poly);
						}
					}
				}
				
				Collections.sort(vDistinct);
				System.out.println( sample.getSampleID()+ " "  + vDistinct);
				samplepoly.add(vDistinct);
			}

			Collections.sort(vectorPolys);

			int count = 0;

			Vector<Polymorphism> vsamplep = new Vector<Polymorphism>();
			for (TestSample sample : sampleCollection) {
			
				fastaMSA.write(">"+sample.getSampleID()+"_"+sample.getDetectedHaplogroup() + "\n");

				vsamplep.clear();
				vsamplep = (Vector<Polymorphism>) samplepoly.get(count);

				count++;

				int j = 0;
				String fastaResult = Polymorphism.rCRS;
		
				int insertion=0;
				for (int i = 0; i < vectorPolys.size(); i++) {
					
					if (j < vsamplep.size()) {
						if (vsamplep.get(j).getPosition() == (vectorPolys.get(i).getPosition()) && (vsamplep.get(j).getMutation() == vectorPolys.get(i).getMutation()))
						 {
							String help = vectorPolys.get(i).getMutation().toString();
								if (help.contains("DEL")) {
				
									if (vsamplep.get(j).getMutation().toString().charAt(0)==('D')){
							    		fastaResult=replaceChar(fastaResult, '-', vectorPolys.get(i).getPosition()-1+insertion);
									}
								}
								else if (help.equals("INS")) {
									fastaResult=insertChar(fastaResult, vectorPolys.get(i).getInsertedPolys().charAt(vectorPolys.get(i).getInsertedPolys().length()-1), vectorPolys.get(i).getPosition()+insertion);
									insertion++;
								}
						
								else {
									if (vsamplep.get(j).getMutation().toString().charAt(0)!=('I')){
										fastaResult=replaceChar(fastaResult, vsamplep.get(j).getMutation().toString().charAt(0), vectorPolys.get(i).getPosition()-1+insertion);
									}
								}
								j++;	
						}
				
						else if (vectorPolys.get(i).getMutation().toString().equals("INS")){

							fastaResult=insertChar(fastaResult, '-', vectorPolys.get(i).getPosition()+insertion);
							insertion++;
						}
					}
					else if (vectorPolys.get(i).getMutation().toString().equals("INS")){
						fastaResult=insertChar(fastaResult, '-', vectorPolys.get(i).getPosition()+insertion);
						insertion++;
					}
				}
				fastaMSA.write(fastaResult+"\n");	
				}
		}
		fastaMSA.close();
	}
	
	/**
	 * 
	 * @param data
	 * @param ch
	 * @param myIndex
	 * @return
	 */
	public static String replaceChar( String data, char ch, int myIndex)
	{
	StringBuilder builderString = new StringBuilder(data);
	builderString.setCharAt(myIndex, ch);
	return builderString.toString();
	}
	
	/**
	 * 
	 * @param data
	 * @param ch
	 * @param myIndex
	 * @return
	 */
	public static String insertChar(String data, char ch, int myIndex ){
		return 	 new StringBuffer(data).insert(myIndex, ch).toString();
	}
	
	/**
	 * 
	 * @param data
	 * @param myIndex
	 * @return
	 */
	public static String deleteChar(String data, int myIndex ){
	    return  data.substring(0,myIndex) + data.substring(myIndex+1);
	}

}
