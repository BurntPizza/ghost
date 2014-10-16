package ghost;

import java.util.List;
import java.util.stream.Collectors;

public class Quote implements Word {
	
	private List<Word> value;
	private String unparsed;
	
	public Quote(String i) {
		unparsed = i;
	}
	
	public Quote(List<Word> words) {
		value = words;
	}
	
	@Override
	public List<Word> value() {
		if (value == null) {
			value = Ghost.parse(unparsed);
			unparsed = null;
		}
		return value;
	}
	
	@Override
	public String toString() {
		return "[" + value().stream().map(w -> w.toString()).collect(Collectors.joining(" ")) + "]";
	}
	
	@Override
	public Quote copy() {
		return new Quote(value());
	}
}
