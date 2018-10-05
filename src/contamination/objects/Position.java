package contamination.objects;

public class Position {

	private int pos;
	private char ref;
	private char variant;
	private double level;
	private char major;
	private char minor;
	private double majorLevel;
	private double minorLevel;
	private int coverage;
	private int type;

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public char getRef() {
		return ref;
	}

	public void setRef(char ref) {
		this.ref = ref;
	}

	public char getVariant() {
		return variant;
	}

	public void setVariant(char variant) {
		this.variant = variant;
	}

	public double getLevel() {
		return level;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	public char getMajor() {
		return major;
	}

	public void setMajor(char major) {
		this.major = major;
	}

	public char getMinor() {
		return minor;
	}

	public void setMinor(char minor) {
		this.minor = minor;
	}

	public double getMajorLevel() {
		return majorLevel;
	}

	public void setMajorLevel(double majorLevel) {
		this.majorLevel = majorLevel;
	}

	public double getMinorLevel() {
		return minorLevel;
	}

	public void setMinorLevel(double minorLevel) {
		this.minorLevel = minorLevel;
	}

	public int getCoverage() {
		return coverage;
	}

	public void setCoverage(int coverage) {
		this.coverage = coverage;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return pos + "\t" + ref + "\t" + variant + "\t" + level + "\t" + major + "\t" + majorLevel + "\t" + minor + "\t" + minorLevel + "\t"
				+ coverage + "\t" + type;
	}
}
