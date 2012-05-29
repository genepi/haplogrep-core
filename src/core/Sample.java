package core;

import exceptions.parse.sample.InvalidPolymorphismException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sample {
	public ArrayList<Polymorphism> sample = new ArrayList<Polymorphism>();
	//callMethod defines the call. callMethod=1 call from PhyloTree
	public Sample(String sampleToParse,int callMethod) throws InvalidPolymorphismException {
	
		String[] polyTokens = sampleToParse.trim().split("\\s+");
		ArrayList<String> listOfSampleTokens = new ArrayList<String>(Arrays.asList(polyTokens));
		
		this.sample = parseSample(listOfSampleTokens, callMethod);
	}
	
	
	public ArrayList<Polymorphism> getPolymorphismn() {
		return sample;
	}
	
	public boolean contains(Polymorphism polyToCheck) {
		return sample.contains(polyToCheck);
	}

	public String toString()
	{
		String result = "";
		for(Polymorphism currentPoly : sample)
		{
			result += currentPoly + " ";
		}
		
		return result.trim();
	}
	/**
	 * Filters  polys in sample which do NOT appear in postivePolys
	 * @param postivePolys
	 */
	public void filter(ArrayList<Polymorphism> postivePolys)
	{
		ArrayList<Polymorphism> filteredSample = new ArrayList<Polymorphism>();
		for (Polymorphism currentPoly : sample) {
			if (postivePolys.contains(currentPoly)) {
				filteredSample.add(currentPoly);
			}
		}
		
		this.sample = filteredSample;
	}

	private ArrayList<Polymorphism> parseSample(ArrayList<String> sample, int callMethod) throws   InvalidPolymorphismException {

		ArrayList<Polymorphism> filteredSample = new ArrayList<Polymorphism>();
		for (String currentPoly : sample) {
			//TODO check this 2 special cases 
			if (!currentPoly.contains("5899.1d!")&&!currentPoly.contains("65.1T(T)")) {
				//poly in brackets (receiving from phylotree) are selected and handled as standard polys. 
				currentPoly = currentPoly.replace("(", "");
				currentPoly = currentPoly.replace(")", "");
				
				if (currentPoly.contains(".")) {
					//callMethod 0 means SearchEngine
					if (callMethod==0) {
						//CASE1: .1 insertion with one base: 523.1C (NOT 523.1CC -> case2)
					if (currentPoly.contains(".1") && currentPoly.matches("\\d+\\.\\d\\w")) {//means: [0-9]+.[0-9][0-9A-Za-z_]
						StringTokenizer st1 = new StringTokenizer(currentPoly, ".");
						String position = st1.nextToken();
						String token1 = st1.nextToken().trim();
						String newInsert = position + "." + token1;
						StringBuffer buffer = new StringBuffer();
						//check for a further insertion on 523.1C -> e.g. 523.2C and sum them up to 523.1CC etc.
						for (String currentPoly2 : sample) {

							if (!currentPoly2.equals(currentPoly) && currentPoly2.contains(position+".")) {
								st1 = new StringTokenizer(currentPoly2, ".");
								String token = st1.nextToken();
								if (position.equals(token)) {
									token1 = st1.nextToken().trim();
									// for(int i = 0; i <
									// Integer.parseInt(String.valueOf(token1.charAt(0)));i++)
									Pattern p = Pattern.compile("\\d+");
									Matcher m = p.matcher(token1);
									m.find();
									int ipos = Integer.parseInt(token1.substring(m.start(), m.end()));// String.valueOf(token1.charAt(0)));
									{
										if (buffer.length() < ipos - 1)
											buffer.setLength(ipos - 1);

										buffer.setCharAt(ipos - 2, token1.charAt(m.end()));

									}

								}

							}
						}
						filteredSample.add(new Polymorphism(newInsert + buffer.toString()));
						
					}
					
					//format is already correct: 523.1ACCCCCC, use it as it is
					else if (currentPoly.matches("\\d+\\.1[a-zA-Z]{2,}")){
						System.out.println(currentPoly.toString());
						Polymorphism newPoly = new Polymorphism(currentPoly);
						filteredSample.add(newPoly);
					}
				}
					//CASE 3 rename in HaploSearchManager: if PhyloTree delivers 523.2C we accept it but only from PHYLOTREE
					//call method 1 means PHYLOTREE
					else {
						Polymorphism newPoly = new Polymorphism(currentPoly);
						filteredSample.add(newPoly);
					}
					

				}

				// Resolve deletation ranges e.g. 1800-1804d
				else if (currentPoly.contains("-")) {
					StringTokenizer st1 = new StringTokenizer(currentPoly, "-");
					String token = st1.nextToken();
					int startPosition = Integer.valueOf(token);
					token = st1.nextToken();
					int endPosition = Integer.valueOf(token.substring(0, token.length() - 1));
					for (int i = startPosition; i <= endPosition; i++) {
						// phyloString = firstInt + "del";
						filteredSample.add(new Polymorphism(i, Mutations.DEL));
						startPosition++;
					}
				}

				else {
						Polymorphism newPoly = new Polymorphism(currentPoly);
						filteredSample.add(newPoly);
					

				}
			}
		}
		//System.out.println(filteredSample);
		return filteredSample;
	}
	
}