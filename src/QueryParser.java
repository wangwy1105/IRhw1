import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
/*
 * Usage: Super class to parse query and evaluate
 */
public abstract class QueryParser {
	//store stop words
	private HashSet<String> stopList;
	//store each query
	private HashMap<Integer, Node> queries;
	// private HashMap<String, InvertedList> lists;

	public static final int N = 890630;

	// public static final int N = 10;

	public QueryParser() {
		queries = new HashMap<Integer, Node>();
		stopList = new HashSet<String>(); 
		// lists = new HashMap<String, InvertedList>();
	}

	public void makeStoplist() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("stoplist.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				stopList.add(line);
			}
//			System.out.println(stopWords);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//parse each query and build the parse tree.
	public void parse(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				String[] str1 = line.split(":");
				int queryID = Integer.parseInt(str1[0]);
//				System.out.println(str1[1]);
				String query = str1[1];  //save text query here
				String[] str = query.split(" "); //and turn it into arrays

				Node root;
				if (str[0].startsWith("#")) {
					root = readStructuredQuery(str);

				} else {
					root = readUnstructuredQuery(str);
				}
				queries.put(queryID, root);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public abstract Node readUnstructuredQuery(String[] str);

	public Node readStructuredQuery(String[] str) {
		Stack<Node> s = new Stack<Node>();
		for (int i = 0; i < str.length; i++) {
			if (!str[i].equals(")")) {
				if (!stopList.contains(str[i])) {
					s.push(new Node(str[i]));
					System.out.println(str[i]);
				}
			} else {
				ArrayList<Node> children = new ArrayList<Node>();
				while (!s.peek().getData().equals("(")) {
					children.add(s.pop());
				}
				s.pop();
				s.peek().setChildren(children);
			}
		}
		return s.pop();
	}

	
	//post order traverse
	public void evaluate() {
		// System.out.println("evaluate");
		for (Iterator<Entry<Integer, Node>> it = queries.entrySet().iterator(); it
				.hasNext();) {
			Entry<Integer, Node> e = it.next();

			System.out.println(e.getKey());
			Node n = e.getValue();
			postOrderEvaluate(n);
			// System.out.println(n.getScores());
			Collections.sort(n.getList().getDocs(), new DocComparator(n.getScores()));
		}
		// System.out.println("evaluate finish");
	}

	public void output() {
		try {
			PrintWriter pr = new PrintWriter("output.txt");
			for (Iterator<Entry<Integer, Node>> it = queries.entrySet()
					.iterator(); it.hasNext();) {
				Entry<Integer, Node> e = it.next();
				int queryID = e.getKey();
				Node n = e.getValue();

				ArrayList<Document> docs = n.getList().getDocs();
				HashMap<Integer, Double> scores = n.getScores();
				for (int i = 1; i <= 100; i++) {
					pr.println(queryID + "\tQ0\t" + docs.get(i - 1).getDocID()
							+ "\t" + i + "\t"
							+ scores.get(docs.get(i - 1).getDocID())
							+ "\trun-1");
				}
			}

			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void postOrderEvaluate(Node n) {

		if (n != null) {
			for (Node c : n.getChildren()) {
				postOrderEvaluate(c);
			}
			String data = n.getData();
			// System.out.println(data);
			if (!data.startsWith("#")) {
				n.setList(readInvertedList(data));
				// System.out.println(n.getList());

			} else {
				doOperation(n, data);
			}
		}
	}

	public abstract void doOperation(Node n, String op);

	public InvertedList readInvertedList(String term) {
		InvertedList res = null;
		try {
			File invFile = new File("inv/" + term + ".inv");
			if (invFile.exists()) {
				// System.out.println("file exists");
				BufferedReader br = new BufferedReader(new FileReader(invFile));
				String line1 = br.readLine();
				String[] str1 = line1.split(" ");
				String stemmed = str1[1];
				int ctf = Integer.parseInt(str1[2]);
				int ttc = Integer.parseInt(str1[3]);

				res = new InvertedList(term, stemmed, ctf, ttc);

				String line;
				while ((line = br.readLine()) != null) {
					res.addDoc(line);
				}

				// System.out.println(res);
				// lists.put(term, inv);

				br.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public InvertedList opNear(Node n, String op) {
		System.out.println(op);
		System.out.println(n.getChildren());

		int num = Integer.parseInt(op.substring(6));
		// System.out.println(num);
		ArrayList<InvertedList> lists = new ArrayList<InvertedList>();
		for (Node c : n.getChildren()) {
			if (!c.getData().startsWith("#")) {
				lists.add(c.getList());
			}
		}

		return intersect( lists.get(1), lists.get(0), num);	
	}

	public InvertedList intersect(InvertedList invertedList1, InvertedList invertedList2, int num) {
		String t1 = invertedList1.getTerm();
		String t2 = invertedList2.getTerm();
		//System.out.println(t1);
		InvertedList res = new InvertedList("NEAR/" + num);
		ArrayList<Document> docs1 = invertedList1.getDocs();
		ArrayList<Document> docs2 = invertedList2.getDocs();
		
		int pd1 = 0;
		int pd2 = 0;
		
		while(pd1 < docs1.size() && pd2 < docs2.size()){
			if(docs1.get(pd1).getDocID() == docs2.get(pd2).getDocID()){
				int tf = 0;
				
				Document doc1 = docs1.get(pd1);
				Document doc2 = docs2.get(pd2);
				
				ArrayList<Integer> pos1 = doc1.getPositions();
				ArrayList<Integer> pos2 = doc2.getPositions();
				
				int pp1 = 0;
				int pp2 = 1;
				
				while(pp1 < pos1.size() && pp2 < pos2.size()){
					if(pos2.get(pp2) - pos1.get(pp1) <= num ){
						tf ++;
						pp1 = pp2+1;
						pp2 = pp1 +1;
					}
					
				}
				if(tf != 0){
					res.addDoc(pd1);
					
				}
				pd1++;
				pd2++;
				
			}
			else if(docs1.get(pd1).getDocID() < docs2.get(pd2).getDocID()){
				pd1 ++;
			}
			else {
				pd2 ++;
			}
		}
		return res;
	}

	public abstract HashMap<Integer, Double> toScore(InvertedList list);

	public void evaluate(int id) {
		Node n = queries.get(id);
		postOrderEvaluate(n);

		// System.out.println(n.getScores());

		Collections.sort(n.getList().getDocs(),
				new DocComparator(n.getScores()));
		ArrayList<Document> docs = n.getList().getDocs();
		HashMap<Integer, Double> scores = n.getScores();

		for (int i = 1; i <= 100; i++) {
			System.out.println(id + "\tQ0\t" + docs.get(i - 1).getDocID()
					+ "\t" + i + "\t" + scores.get(docs.get(i - 1).getDocID())
					+ "\trun-1");
		}
	}

}
