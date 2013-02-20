import java.util.ArrayList;
/*
 * store inverted list
 */
public class InvertedList {
	private String term;
	private String stemmed;
	private int ctf;
	private int ttc;
	private int df;
	private ArrayList<Document> docs;

	public InvertedList(String term, String stemmed, int ctf, int ttc) {
		this.term = term;
		this.stemmed = stemmed;
		this.ctf = ctf;
		this.ttc = ttc;
		df = 0;
		docs = new ArrayList<Document>();
	}

	public InvertedList(String data) {
		this.term = data;
		df = 0;
		docs = new ArrayList<Document>();
	}

	public void addDoc(String line) {
		//System.out.println(line);
		String[] str = line.split(" ");
		int docID = Integer.parseInt(str[0]);
		int tf = Integer.parseInt(str[1]);
		int docLength = Integer.parseInt(str[2]);
		
		Document doc = new Document(docID, tf, docLength);
		
		for(int i = 3; i< str.length; i++){
			doc.addPos(str[i]);
		}
		
		docs.add(doc);
		df++;
	}

	public String toString(){
		String res = term + ": " + stemmed + " " + ctf + " " + ttc + " " + df + "\n" + docs+"\n";
		return res;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getStemmed() {
		return stemmed;
	}

	public void setStemmed(String stemmed) {
		this.stemmed = stemmed;
	}

	public int getCtf() {
		return ctf;
	}

	public void setCtf(int ctf) {
		this.ctf = ctf;
	}

	public int getTtc() {
		return ttc;
	}

	public void setTtc(int ttc) {
		this.ttc = ttc;
	}

	public int getDf() {
		return df;
	}

	public void setDf(int df) {
		this.df = df;
	}

	public ArrayList<Document> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<Document> docs) {
		this.docs = docs;
	}

	public void addDoc(Integer key) {
		Document doc = new Document(key);
		docs.add(doc);
		df ++;
	}
}
