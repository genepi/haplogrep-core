package haploClassification;

import genetools.Polymorphism;

import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MissingPolysIterator implements Iterator<Polymorphism> {
	SearchResult searchResult;
	Iterator<Polymorphism> iterExpectedPolysIter;
	Polymorphism nextMissingPoly = null;
	boolean hasNext = false;
	public MissingPolysIterator(SearchResult searchResult) {
		this.searchResult = searchResult;
		this.iterExpectedPolysIter = searchResult.getIterExpectedPolys();
		
		searchNextMissingPoly();
	}

	@Override
	public boolean hasNext() {
		if(nextMissingPoly == null)
			return false;
		else
			return true;
	}

	@Override
	public Polymorphism next() {
		Polymorphism tmp = nextMissingPoly;
		searchNextMissingPoly();
		return tmp;
	}

	private void searchNextMissingPoly(){
		nextMissingPoly = null;
		while(iterExpectedPolysIter.hasNext()){
			Polymorphism nextExpectedPoly = iterExpectedPolysIter.next();
			if(!searchResult.getFoundPolys().contains(nextExpectedPoly)){
				nextMissingPoly = nextExpectedPoly;
				return;
			}
		}
	}
	@Override
	public void remove() {
		throw new NotImplementedException();

	}

}
