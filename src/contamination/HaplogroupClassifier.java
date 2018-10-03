package contamination;

import java.util.ArrayList;

import core.SampleFile;
import exceptions.parse.HsdFileException;
import phylotree.Phylotree;
import search.ranking.KulczynskiRanking;

public class HaplogroupClassifier {

	public SampleFile calculateHaplogrops(Phylotree phylotree, ArrayList<String> profiles) {

		SampleFile samples = null;
		try {
			samples = new SampleFile(profiles);

			samples.updateClassificationResults(phylotree, new KulczynskiRanking(1));

			return samples;

		} catch (HsdFileException e) {
			e.printStackTrace();
		}
		return samples;

	}
}
