package ghost;

public class Int implements Word {
	
	private final Integer value;
	
	public Int(Integer i) {
		value = i;
	}
	
	@Override
	public Integer value() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}
