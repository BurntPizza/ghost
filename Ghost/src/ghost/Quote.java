package ghost;

import java.util.List;
import java.util.stream.Collectors;

public class Quote implements Word {
	
	private final List<Word> value;
	
	public Quote(List<Word> i) {
		value = i;
	}
	
	@Override
	public List<Word> value() {
		return value;
	}
	
	@Override
	public String toString() {
		return "[" + value.stream().map(w -> w.toString()).collect(Collectors.joining(" ")) + "]";
	}
}
