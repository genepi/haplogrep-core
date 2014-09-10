import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;


public class readHaplogroups {

	String haplgroup;
	HashSet<String> profile;
	
	public static void main(String[] args) {
		BufferedReader br;
	
		try {
			br = new BufferedReader(new FileReader("testDataFiles/phylotree16_blank.hsd.txt"));
		 
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	    	BufferedReader brfluct = new BufferedReader(new FileReader("../HaplogrepServer/weights/weights16.txt"));
	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        String everything = sb.toString();
	        System.out.println(everything);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}

}
