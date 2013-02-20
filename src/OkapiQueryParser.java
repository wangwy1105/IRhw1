import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/*
 * Usage: implement Okapi Query model
 */
public class OkapiQueryParser extends QueryParser {

	@Override
	public Node readUnstructuredQuery(String[] str) {
		ArrayList<Node> children = new ArrayList<Node>();

		for (String s : str) {
			children.add(new Node(s));

		}
		Node root = new Node("#SUM");
		root.setChildren(children);
		return root;
	}

	public static void main(String[] args){
		QueryParser qp = new OkapiQueryParser();
		qp.parse("queries.txt");
		qp.evaluate();
		//qp.evaluate(16);
		qp.output();
	}

	@Override
	public void doOperation(Node n, String op) {
		if(op.equals("#SUM")){
			for(Node c: n.getChildren()){
				if(!c.getData().startsWith("#")){
					c.setScores(toScore(c.getList()));
					//System.out.println(c.getScores());
				}
				
			}
			
			sumScores(n);
			//System.out.println(n.getScores());
		}else if(op.startsWith("#NEAR/")){
			opNear(n, op);
		}
	}

	/*
	 * sum scores of different terms
	 */
	public void sumScores(Node n) {
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		InvertedList list = new InvertedList(n.getData());
		
		for(Node c: n.getChildren()){
			HashMap<Integer, Double> score = c.getScores();
			for(Iterator<Entry<Integer, Double>> it = score.entrySet().iterator(); it.hasNext(); ){
				Entry<Integer, Double> e = it.next();
				if(res.containsKey(e.getKey())){
					//System.out.println(e.getKey());
					res.put(e.getKey(), res.get(e.getKey()) +e.getValue());
				}
				else{
					res.put(e.getKey(), e.getValue());
					list.addDoc(e.getKey());
				}
			}
		}
		
		n.setScores(res);
		n.setList(list);
	}
	
	
	/*
	 * convert inverted list to scores
	 */
	@Override
	public HashMap<Integer, Double> toScore(InvertedList list) {
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		double df = list.getDf();
		//System.out.println(df);
		double idf = Math.log(((double)N -df + 0.5)/(df+0.5));
		//System.out.println(idf);
		double avgDocLength = getAvgDocLength(list);
		//System.out.println(avgDocLength);
		for(Document doc: list.getDocs()){
			double tf = doc.getTf();
			//System.out.println(tf);
			double tf2 = tf / (tf+1.2*(0.25+0.75*(doc.getDocLength()/avgDocLength)));
			res.put(doc.getDocID(), idf*tf2);
		}
		return res;
	}

	public double getAvgDocLength(InvertedList list) {
		double sum = 0;
		for(Document doc: list.getDocs()){
			sum += (double) doc.getDocLength();
		}
		return sum/(double)list.getDf();
	}
}
