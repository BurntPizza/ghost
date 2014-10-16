package ghost;

import java.util.function.Consumer;

public class Function implements Word {
	
	private final Consumer<Stack> value;
	private final String name;
	
	public Function(String name, Consumer<Stack> i) {
		value = i;
		this.name = name;
	}
	
	@Override
	public void accept(Stack t) {
		value.accept(t);
	}
	
	@Override
	public Consumer<Stack> value() {
		return value;
	}
	
	@Override
	public String toString() {
		return "@" + name;
	}
	
	@Override
	public Function copy() {
		return new Function(name, value());
	}
}
