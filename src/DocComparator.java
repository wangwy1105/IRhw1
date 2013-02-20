import java.util.Comparator;
import java.util.HashMap;
/*
 * Help to sort the document according to the scores
 */
public class DocComparator implements Comparator<Document> {

	private HashMap<Integer, Double> scores;

	public DocComparator(HashMap<Integer, Double> scores) {
		this.scores = scores;
	}

	@Override
	public int compare(Document o1, Document o2) {

		double a = scores.get(o1.getDocID());
		double b = scores.get(o2.getDocID());
		if (a > b)
			return -1;
		else if (a < b)
			return 1;
		else
			return 0;
	}

}
