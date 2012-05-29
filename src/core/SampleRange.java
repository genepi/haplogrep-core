package core;
import exceptions.parse.sample.InvalidRangeException;

import java.util.ArrayList;




public class SampleRange {
	public ArrayList<Integer> getStarts() {
		return starts;
	}

	public ArrayList<Integer> getEnds() {
		return ends;
	}
	ArrayList<Integer> starts = new ArrayList<Integer>();
	ArrayList<Integer> ends = new ArrayList<Integer>();
	
	public SampleRange()
	{
		
	}
	
	public SampleRange(SampleRange rangeToCopy)
	{
		starts.addAll(rangeToCopy.starts);
		ends.addAll(rangeToCopy.ends);
	}
	
	public SampleRange(String rangesToParse) throws InvalidRangeException {
		if(rangesToParse.equals(""))
			return;
		
		String[] ranges = rangesToParse.split(";");
		
		for (int i = 0; i < ranges.length; i++) {
			//A range was defined
			if(ranges[i].contains("-"))
			{
				String[] rangeParts = ranges[i].split("-");
				if(rangeParts.length != 2)
					throw new InvalidRangeException(rangesToParse);
				
				//parse range if first number is greater than second
				int from = 0;
				int to = 0;
				try {
					from = Integer.valueOf(rangeParts[0].trim());
					to = Integer.valueOf(rangeParts[1].trim());
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					throw new InvalidRangeException(rangeParts[0]+" "+rangeParts[1]);
				}
				if(to >16570 || from > 16570)
					throw new InvalidRangeException();
				
				try {
					//make one range to two ranges
					if (from > to){
						this.addCustomRange(from,16569);
						this.addCustomRange(1,to);
					}
					//standard
					else 
					this.addCustomRange(Integer.parseInt(rangeParts[0].trim()),Integer.parseInt(rangeParts[1].trim()));
				} 
				catch (NumberFormatException e) {
					throw new InvalidRangeException(rangesToParse);
				}
				}
			
			
			//Only one position was defined
			else
			{
				try {
					this.addCustomRange(Integer.parseInt(ranges[i].trim()),Integer.parseInt(ranges[i].trim()));
				} 
				catch (NumberFormatException e) {
					throw new InvalidRangeException(rangesToParse);
				}
			}
		}
	}

	public boolean contains(Polymorphism polyToCheck)
	{
		for(int i = 0; i < starts.size(); i++)
		{
			if(starts.get(i) <= polyToCheck.getPosition() 
					&& ends.get(i) >=polyToCheck.getPosition())
				return true;
		}
		
		return false;
	}
	
	public void addCompleteRange()
	{
		starts.add(1);
		ends.add(16569);
	}
	
	public void addCustomRange(int newRangeStart,int newRangeEnd)
	{
		starts.add(newRangeStart);
		ends.add(newRangeEnd);
	}
	
	
	public String toString(){
		String result="";
		
		
		for(int i=0;i<starts.size();i++)
		{
		if (starts.get(i) == ends.get(i)){
			result+= starts.get(i)+" ; ";
		}
		else
			result+= starts.get(i)+"-"+ends.get(i) + " ; ";
		}
		return result.trim();
		
	}
}
