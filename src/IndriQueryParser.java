import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/*
 * Usage: implement Indri retrieval model
 */
public class IndriQueryParser extends QueryParser {

	@Override
	public Node readUnstructuredQuery(String[] str) {
		ArrayList<Node> children = new ArrayList<Node>();
		for (String s : str) {
			children.add(new Node(s));
			System.out.println(s);
		}
		Node root = new Node("#OR");
		root.setChildren(children);
		System.out.println(children);
		return root;
	}

	public static void main(String[] args) {
		QueryParser qp = new IndriQueryParser();
		qp.parse("queries.txt");
		qp.evaluate();
		qp.output();

	}

	@Override
	public void doOperation(Node n, String op) {
		if (op.equals("#AND")) {
			for (Node c : n.getChildren()) {
				c.setScores(toScore(c.getList()));
				//System.out.println(c.getScores());

			}
			
			multiplyScores(n);
			//System.out.println(n.getScores());

		} else if (op.equals("#OR")) {
			for(Node c: n.getChildren()){
				c.setScores(toScore(c.getList()));
				System.out.println(c.getScores());
			}
			
			orScores(n);
			System.out.println(n.getScores());
		} else if (op.startsWith("#NEAR/")) {
			// opNear();
		}
	}

	// calculate score for OR operator
	public void orScores(Node n) {
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		InvertedList list = new InvertedList(n.getData());
		
		for(int i = 1; i <= N; i++){
			res.put(i, 1.0);
		}
		
		for(Node c: n.getChildren()){
			HashMap<Integer, Double> score = c.getScores();
			
			
			for(Iterator<Entry<Integer, Double>> it = score.entrySet().iterator(); it.hasNext(); ){
				Entry<Integer, Double> e = it.next();
				res.put(e.getKey(), res.get(e.getKey())* (1-e.getValue()));
			}
		}
		
		for(int i = 1; i <= N; i++){
			res.put(i, 1-res.get(i));
		}
		
		for(int i = 1; i <= N; i++){
			list.addDoc(i);
		}
		
		n.setScores(res);
		n.setList(list);
	}

	
	//calulate score for AND operator
	public void multiplyScores(Node n) {
		
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		InvertedList list = new InvertedList(n.getData());
		
		for(int i = 1; i <= N; i++){
			res.put(i, 1.0);
		}
		for(Node c: n.getChildren()){
			HashMap<Integer, Double> score = c.getScores();
			for(Iterator<Entry<Integer, Double>> it = score.entrySet().iterator(); it.hasNext(); ){
				Entry<Integer, Double> e = it.next();
				res.put(e.getKey(), res.get(e.getKey()) * e.getValue());
				
			}
		}
	
		for(int i = 1; i <= N; i++){
			list.addDoc(i);
		}
		n.setScores(res);
		n.setList(list);
	}

	@Override
	public HashMap<Integer, Double> toScore(InvertedList list) {
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		for(int i = 1; i <= N; i++){
			res.put(i, 0.0);
		}
		
		double pmle = (double) list.getCtf() / (double) list.getTtc();
		// System.out.println(pmle);
		double u = 1500.0;
		for (Document doc : list.getDocs()) {
			double p = (doc.getTf() + u * pmle) / (doc.getDocLength() + u);
			res.put(doc.getDocID(), p);
		}

		return res;
	}
}
