import java.util.ArrayList;
import java.util.HashMap;

/*
 * Tree node
 */
public class Node {
	private String data;
	private ArrayList<Node> children;
	private InvertedList list;
	private HashMap<Integer, Double> scores;
	
	public Node(String data) {
		this.data = data;
		children = new ArrayList<Node>();
		list = null;
		scores = null;
		
	}

	public String toString(){
		return data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public InvertedList getList() {
		return list;
	}

	public void setList(InvertedList list) {
		this.list = list;
	}

	public HashMap<Integer, Double> getScores() {
		return scores;
	}

	public void setScores(HashMap<Integer, Double> scores) {
		this.scores = scores;
	}
	
}
