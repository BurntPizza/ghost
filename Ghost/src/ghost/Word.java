package ghost;

import java.util.function.Consumer;

public interface Word extends Consumer<Stack> {
	
	@Override
	public default void accept(Stack s) {
		s.push(this);
	}
	
	public Object value();
	
	public Word copy();
}
