package ghost;

import java.util.*;
import java.util.function.Consumer;

public class Ghost {
	
	public static Map<String, Consumer<Stack>> functions = builtins();
	
	//TODO actual interpreter infrastructure
	public static void main(String[] args) {
		try (Scanner s = new Scanner(System.in)) {
			Stack stack = new Stack();
			while (true) {
				interpret(s.nextLine(), w -> w.accept(stack));
			}
		}
	}
	
	public static void interpret(String string, Consumer<Word> pipe) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (Character.isWhitespace(c))
				continue;
			switch (c) {
				case '"': {
					int start = ++i;
					while (!(string.charAt(i) == '\"' && string.charAt(i - 1) != '\\'))
						// add error handling
						i++;
					String str = string.substring(start, i);
					if (str.contains("\\\""))
						str = str.replace("\\\"", "\"");
					pipe.accept(new Text(str));
					break;
				}
				case '[': {
					int start = ++i;
					int nestLevel = 1;
					while (nestLevel > 0) {
						switch (string.charAt(i++)) {
							case '[':
								nestLevel++;
								break;
							case ']':
								nestLevel--;
								break;
						}
					}
					pipe.accept(new Quote(string.substring(start, --i)));
					break;
				}
				default:
					int start = i;
					while (i < string.length() && !Character.isWhitespace(string.charAt(i)) && string.charAt(i) != '[')
						// add error handling
						i++;
					String str = string.substring(start, i--);
					
					if (Character.isDigit(str.charAt(0)) || str.charAt(0) == '-' && str.length() > 1) {
						boolean isInt = true;
						for (int d = 1; d < str.length(); d++)
							if (!Character.isDigit(str.charAt(d))) {
								isInt = false;
								break;
							}
						if (isInt) {
							pipe.accept(new Int(Integer.parseInt(str)));
							break;
						}
					}
					
					Consumer<Stack> f = functions.get(str);
					if (f == null)
						pipe.accept(new Text(str));
					else
						pipe.accept(new Function(str, f));
			}
		}
	}
	
	public static Map<String, Consumer<Stack>> builtins() {
		functions = new HashMap<>();
		Map<String, Consumer<Stack>> map = functions;
		
		map.put("dup", s -> s.push(s.peek().copy()));
		map.put("drop", s -> s.pop());
		map.put("print", s -> System.out.println(s.peek()));
		map.put("dump", s -> System.out.println(s));
		map.put("exit", s -> System.exit(0));
		map.put("clear", s -> s.clear());
		map.put("apply", s -> s.popQuote().value().forEach(word -> word.accept(s)));
		map.put("+", s -> s.push(new Int(s.popInt().value() + s.popInt().value())));
		map.put("*", s -> s.push(new Int(s.popInt().value() * s.popInt().value())));
		map.put("quote", s -> s.push(new Quote(Arrays.asList(s.pop()))));
		map.put("if", map.get("apply"));
		map.put("typeof", s -> s.push(new Text(s.pop().getClass().getSimpleName())));
		map.put("call", s -> {
			String name = s.popText().value();
			Consumer<Stack> c = functions.get(name);
			if (c == null)
				error("Unknown identifier: " + name);
			c.accept(s);
		});
		map.put("reverse", s -> {
			Quote q = s.popQuote();
			Collections.reverse(q.value());
			s.push(q);
		});
		map.put("zip", s -> {
			Quote q2 = s.popQuote(), q1 = s.popQuote();
			if (q1.value().size() != q2.value().size())
				error("Quotes must have same number of elements to zip");
			for (int i = 0; i < q1.value().size(); i++) {
				q1.value().set(i, new Quote(Arrays.asList(q1.value().get(i), q2.value().get(i))));
			}
			s.push(q1);
		});
		map.put("foreach", s -> {
			List<Word> func = s.popQuote().value();
			Quote q = s.popQuote();
			for (int i = 0; i < q.value().size(); i++) {
				s.push(q.value().get(i));
				func.forEach(word -> word.accept(s));
				q.value().set(i, s.pop());
			}
			s.push(q);
		});
		map.put("rot3", s -> {
			Word w3 = s.pop(), w2 = s.pop(), w1 = s.pop();
			s.push(w2);
			s.push(w3);
			s.push(w1);
		});
		
		Quote _true = new Quote("drop apply");
		Quote _false = new Quote("swap drop apply");
		
		map.put("true", s -> s.push(_true));
		map.put("false", s -> s.push(_false));
		
		map.put(">", s -> {
			Integer i2 = s.popInt().value(), i1 = s.popInt().value();
			(i1 > i2 ? _true : _false).accept(s);
		});
		map.put("<", s -> {
			Integer i2 = s.popInt().value(), i1 = s.popInt().value();
			(i1 < i2 ? _true : _false).accept(s);
		});
		map.put("compose", s -> {
			Quote q2 = s.popQuote(), q1 = s.popQuote();
			List<Word> list = new ArrayList<>();
			list.addAll(q1.value());
			list.addAll(q2.value());
			s.push(new Quote(list));
		});
		map.put("join", s -> {
			Word w2 = s.pop(), w1 = s.pop();
			s.push(new Quote(Arrays.asList(w1, w2)));
		});
		map.put("-", s -> {
			Integer i2 = s.popInt().value(), i1 = s.popInt().value();
			s.push(new Int(i1 - i2));
		});
		map.put("/", s -> {
			Integer i2 = s.popInt().value(), i1 = s.popInt().value();
			s.push(new Int(i1 / i2));
		});
		map.put("swap", s -> {
			Word w1 = s.pop(), w2 = s.pop();
			s.push(w1);
			s.push(w2);
		});
		Consumer<Stack> apply = map.get("apply");
		map.put("filter", s -> {
			Quote pred = s.popQuote();
			Quote data = s.popQuote();
			List<Word> list = data.value();
			int pos = 0;
			for (int i = 0; i < list.size(); i++) {
				Word w = list.get(i);
				s.push(w);
				s.push(pred);
				apply.accept(s);
				if (s.pop() == _true)
					list.set(pos++, w);
			}
			for (int i = list.size(); --i >= pos;)
				list.remove(i);
			s.push(data);
		});
		map.put("def", s -> {
			String name = s.popText().value();
			List<Word> words = s.popQuote().value();
			map.put(name, stack -> words.forEach(word -> word.accept(stack)));
		});
		
		Stack s = new Stack();
		interpret("[[quote] foreach] split def", w -> w.accept(s));
		interpret("[[] swap [compose 0] foreach drop] merge def", w -> w.accept(s));
		//interpret("", w -> w.accept(s)); // add more defs
		
		return map;
	}
	
	//TODO: be useful
	public static void error(String msg) {
		throw new RuntimeException(msg);
	}
}
