package core;

public class AnnotationAAC {

	private String position;
	private String gene;
	private short codon;
	private String aminoAcidChange;

	
	public AnnotationAAC(String pos, String gen, short cod, String aac){
		position= pos;
		gene = gen;
		codon = cod;
		aminoAcidChange = aac;
	}
	
	public String getPosition() {
		return position;
	}
	public String getGene() {
		return gene;
	}
	public short getCodon() {
		return codon;
	}
	public String getAminoAcidChange() {
		return aminoAcidChange;
	}
	
	
}
