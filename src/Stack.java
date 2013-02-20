import java.util.ArrayList;

public class Stack<T> {
	private ArrayList<T> s;

	public Stack() {
		s = new ArrayList<T>();
	}

	public boolean isEmpty() {
		return s.size() == 0;
	}

	public void push(T x) {
		s.add(x);
	}

	public T pop(){
		if (isEmpty()) 
			System.out.println("empty stack");

		return s.remove(s.size() - 1);
	}

	public T peek(){
		if (isEmpty())
			System.out.println("empty stack");
		return s.get(s.size() - 1);
	}
	
	public String toString(){
		return s.toString();
	}
}
