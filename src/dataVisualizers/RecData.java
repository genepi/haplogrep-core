package dataVisualizers;

/**
 * Helper class for tree recursion and rendering of the phlotreeRenderer.class
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class RecData {
	private int currentPosX;
	private int centerPosX;
	private int maxWidth;
	private int maxHeight;
	
	public RecData(int centerPosX, int currentPosX,int maxWidth,int maxHeight) {
		this.centerPosX = centerPosX;
		this.currentPosX = currentPosX;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}
	public int getCurrentPos() {
		return currentPosX;
	}
	
	public int getCenter() {
		return centerPosX ;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}
	public void setMaxHeight(int maxHeight) {
		if(this.maxHeight <  maxHeight)
		this.maxHeight = maxHeight;
	}
	
}
