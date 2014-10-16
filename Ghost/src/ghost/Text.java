package ghost;

public class Text implements Word {
	
	private final String value;
	
	public Text(String i) {
		value = i;
	}
	
	@Override
	public String value() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public Text copy() {
		return new Text(value());
	}
}
