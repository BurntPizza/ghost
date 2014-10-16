package ghost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Ghost {
	
	public static Map<String, Consumer<Stack>> functions = builtins();
	
	//TODO actual interpreter infrastructure
	public static void main(String[] args) {
		try (Scanner s = new Scanner(System.in)) {
			Stack stack = new Stack();
			while (true) {
				parse(s.nextLine()).forEach(word -> word.accept(stack));
			}
		}
	}
	
	public static List<Word> parse(String string) {
		List<Word> words = new ArrayList<>();
		
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
					words.add(new Text(str));
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
					words.add(new Quote(string.substring(start, --i)));
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
							words.add(new Int(Integer.parseInt(str)));
							break;
						}
					}
					
					Consumer<Stack> f = functions.get(str);
					if (f == null)
						words.add(new Text(str));
					else
						words.add(new Function(str, f));
			}
		}
		
		return words;
	}
	
	public static Map<String, Consumer<Stack>> builtins() {
		Map<String, Consumer<Stack>> map = new HashMap<>();
		
		map.put("dup", s -> s.push(s.peek()));
		map.put("drop", s -> s.pop());
		map.put("print", s -> System.out.println(s.peek()));
		map.put("dump", s -> System.out.println(s));
		map.put("exit", s -> System.exit(0));
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
		map.put("foreach", s -> {
			List<Word> func = s.popQuote().value();
			List<Word> words = s.popQuote().value();
			for (int i = 0; i < words.size(); i++) {
				s.push(words.get(i));
				func.forEach(word -> word.accept(s));
				words.set(i, s.pop());
			}
			s.push(new Quote(words));
			
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
			List<Word> q2 = s.popQuote().value(), q1 = s.popQuote().value();
			q1.addAll(q2);
			s.push(new Quote(q1));
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
		map.put("def", s -> {
			String name = s.popText().value();
			List<Word> words = s.popQuote().value();
			functions.put(name, stack -> words.forEach(word -> word.accept(stack)));
		});
		
		return map;
	}
	
	//TODO: be useful
	public static void error(String msg) {
		throw new RuntimeException(msg);
	}
}
