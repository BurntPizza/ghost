package ghost;

import java.util.ArrayDeque;

public class Stack {
	
	private final ArrayDeque<Word> stack;
	
	public Stack() {
		stack = new ArrayDeque<>();
	}
	
	public Word peek() {
		return stack.peek();
	}
	
	public Word pop() {
		return stack.pop();
	}
	
	public void clear() {
		stack.clear();
	}
	
	public Function popFunction() {
		Word w = stack.pop();
		if (!(w instanceof Function))
			Ghost.error(w + " is not a function!");
		return (Function) w;
	}
	
	public Int popInt() {
		Word w = stack.pop();
		if (!(w instanceof Int))
			Ghost.error(w + " is not a number!");
		return (Int) w;
	}
	
	public Quote popQuote() {
		Word w = stack.pop();
		if (!(w instanceof Quote))
			Ghost.error(w + " is not a quote!");
		return (Quote) w;
	}
	
	public Text popText() {
		Word w = stack.pop();
		if (!(w instanceof Text))
			Ghost.error(w + " is not text!");
		return (Text) w;
	}
	
	public void push(Word w) {
		stack.push(w);
	}
	
	@Override
	public String toString() {
		return stack.toString();
	}
}
