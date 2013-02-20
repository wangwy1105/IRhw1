import java.util.ArrayList;
/*
 * store document and positions in the inverted list
 */
public class Document {
	private int docID;
	private int tf;
	private int docLength;
	private ArrayList<Integer> positions;
	
	public Document(int docID, int tf, int docLength) {
		this.docID = docID;
		this.tf = tf;
		this.docLength = docLength;	
		positions = new ArrayList<Integer>();
	}

	public Document(Integer key) {
		this.docID = key;
		positions = new ArrayList<Integer>();
	}

	public void addPos(String pos) {
		positions.add(Integer.parseInt(pos));
	}

	public String toString(){
		String res = "doc: "+docID + " " + tf + " " + docLength+"\npos: " + positions+"\n"; 
		return res;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}

	public int getDocLength() {
		return docLength;
	}

	public void setDocLength(int docLength) {
		this.docLength = docLength;
	}

	public ArrayList<Integer> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Integer> positions) {
		this.positions = positions;
	}
}
