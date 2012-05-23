package genetools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Haplogroup {
	ArrayList<Object> subParts = new  ArrayList<Object>();
	String haplogroup;
	public Haplogroup(String _haploGroup){
		this.haplogroup=_haploGroup;
		subParts.add(_haploGroup);
		
	}
	
	public void changeHaplogroupFormat(String haploGroup) {
		int position = 0;
		Pattern p = Pattern.compile("\\d+|'");
		boolean toggle = true;
	
		int pos = haploGroup.indexOf("+");
		String specialEnd = "";
		if(pos > -1)
		{
			specialEnd = haploGroup.substring(pos);
			haploGroup = haploGroup.substring(0, pos);
		}
		
		while(position < haploGroup.length())
		{
			if(toggle)
			{
				char c = haploGroup.charAt(position);
				if(haploGroup.length() > position + 1 && c == 'H' && haploGroup.charAt(position + 1) == 'V')
				{
					subParts.add("HV");
					position += 2;
					toggle = false;
				}
				
				else if(haploGroup.length() > position + 1 && c == 'C' && haploGroup.charAt(position + 1) == 'Z')
				{
					subParts.add("CZ");
					position += 2;
					toggle = false;
				}
				
				else
				{
					subParts.add(haploGroup.charAt(position));
					position++;
					toggle = false;
				}
			}
			
			else
			{
				
				Matcher m = p.matcher(haploGroup.substring(position, haploGroup.length()));
				
				if(m.find())
				{
					Character c = haploGroup.charAt(position + m.start());
					if(c.toString().equals("'"))
						subParts.add("'");
					else
					{
						subParts.add(Integer.parseInt(haploGroup.substring(position + m.start(),position + m.end())));
						
					}
					position = position + m.end();
					toggle = true;
				}
				else 
				{
					subParts.add("'");
					toggle = true;
				}
				
				
			}
		}	
		
		if(pos > -1)
			subParts.add(specialEnd);
		
	}
	
	public boolean equals(Object haplogroup)
	{
		if(!(haplogroup instanceof Haplogroup))
			return false;
		
		Haplogroup c = (Haplogroup)haplogroup;
		if(!this.haplogroup.equals(c.haplogroup))
			return false;
		
		/*
		 *  needed for subParts distinction:
		if(this.subParts.size() != c.subParts.size())
			return false;
		
		int i = 0;
		for(Object current : subParts)
		{
			if(!current.equals(c.subParts.get(i)))
				return false;
			
			i++;
		}
		*/
		return true;
	}
	public boolean isSuperHaplogroup(Haplogroup hgToCheck)
	{
		if(!(hgToCheck instanceof Haplogroup))
			return false;
		
		Haplogroup c = (Haplogroup)hgToCheck;
		if(!c.haplogroup.contains(this.haplogroup))
			return false;
		/*
		 needed for subParts distinction:
		 
		if(this.subParts.size() > c.subParts.size())
			return false;
		
		int i = 0;
		for(Object current : subParts)
		{
			if(!current.equals(c.subParts.get(i)))
				return false;		
			
			i++;
		}*/
		
		return true;
	}
	
	public String toString()
	{
		String result = "";
		
		for(Object current : subParts)
		{
			result += current.toString();
		}
		
		return result;
	}
}
